package com.github.huymaster.campusexpensemanager.database.realm.dao;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.util.Consumer;
import androidx.core.util.Supplier;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;

import com.github.huymaster.campusexpensemanager.database.realm.DatabaseCore;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicReference;

import io.realm.Realm;
import io.realm.RealmModel;
import io.realm.RealmObject;
import io.realm.RealmQuery;
import io.realm.RealmResults;

public abstract class BaseDAO<T extends RealmModel> implements DefaultLifecycleObserver {
    protected static final String SUPER_TAG = "BaseDAO";
    protected final ExecutorService IO_SERVICE = Executors.newSingleThreadExecutor();
    protected final String TAG = getClass().getSimpleName();
    private final Semaphore semaphore = new Semaphore(1);
    private final DatabaseCore databaseCore;
    private final Lifecycle lifecycle;

    protected BaseDAO(DatabaseCore databaseCore, Lifecycle lifecycle) {
        this.databaseCore = databaseCore;
        this.lifecycle = lifecycle;
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(() -> lifecycle.addObserver(this));
    }

    public abstract Class<T> getType();

    protected T getUnmanaged(T managed) {
        if (managed == null) return null;
        try (Realm realm = databaseCore.getRealm()) {
            if (!RealmObject.isValid(managed)) {
                Log.w(TAG, "Object [" + managed + "] is not valid");
                return null;
            }
            if (!RealmObject.isManaged(managed))
                return managed;
            return realm.copyFromRealm(managed);
        } catch (Exception e) {
            Log.w(TAG, "Error while getting unmanaged object", e);
        }
        return null;
    }

    protected List<T> getUnmanagedI(Iterable<T> managed) {
        if (managed == null) return null;
        try (Realm realm = databaseCore.getRealm()) {
            return realm.copyFromRealm(managed);
        } catch (Exception e) {
            Log.w(TAG, "Error while getting unmanaged objects", e);
        }
        return null;
    }

    public Future<T> getAsync(@NonNull Consumer<RealmQuery<T>> selector) {
        return IO_SERVICE.submit(() -> {
            semaphore.acquireUninterruptibly();
            try (Realm realm = databaseCore.getRealm()) {
                RealmQuery<T> query = realm.where(getType());
                selector.accept(query);
                RealmResults<T> ts = query.findAll();
                if (ts == null) return null;
                T managed = ts.first(null);
                return getUnmanaged(managed);
            } catch (Exception e) {
                Log.w(TAG, "Error while getting object", e);
                return null;
            } finally {
                semaphore.release();
            }
        });
    }

    public T get(@NonNull Consumer<RealmQuery<T>> selector) {
        try {
            return getAsync(selector).get();
        } catch (Exception e) {
            Log.w(TAG, "Error while getting object", e);
        }
        return null;
    }

    public <C extends Collection<T>> Future<C> getAllAsync(@NonNull Supplier<C> supplier) {
        return IO_SERVICE.submit(() -> {
            semaphore.acquireUninterruptibly();
            try (Realm realm = databaseCore.getRealm()) {
                C container = supplier.get();
                RealmResults<T> ts = realm.where(getType()).findAll();
                if (ts != null) {
                    var unmanaged = getUnmanagedI(ts);
                    container.clear();
                    container.addAll(unmanaged);
                }
                return container;
            } catch (Exception e) {
                Log.w(TAG, "Error while getting all objects", e);
                return supplier.get();
            } finally {
                semaphore.release();
            }
        });
    }

    public <C extends Collection<T>> Future<C> getAllAsync(@NonNull Supplier<C> supplier, @NonNull Consumer<RealmQuery<T>> selector) {
        return IO_SERVICE.submit(() -> {
            semaphore.acquireUninterruptibly();
            try (Realm realm = databaseCore.getRealm()) {
                C container = supplier.get();
                RealmQuery<T> query = realm.where(getType());
                selector.accept(query);
                RealmResults<T> ts = query.findAll();
                if (ts != null) {
                    var unmanaged = getUnmanagedI(ts);
                    container.clear();
                    container.addAll(unmanaged);
                }
                return container;
            } catch (Exception e) {
                Log.w(TAG, "Error while getting all objects", e);
                return supplier.get();
            } finally {
                semaphore.release();
            }
        });
    }

    public <C extends Collection<T>> C getAll(@NonNull Supplier<C> supplier) {
        try {
            return getAllAsync(supplier).get();
        } catch (Exception e) {
            Log.w(TAG, "Error while getting all objects", e);
        }
        return supplier.get();
    }

    public <C extends Collection<T>> C getAll(@NonNull Supplier<C> supplier, @NonNull Consumer<RealmQuery<T>> selector) {
        try {
            return getAllAsync(supplier, selector).get();
        } catch (Exception e) {
            Log.w(TAG, "Error while getting all objects", e);
        }
        return supplier.get();
    }

    public Future<T> createAsync(Consumer<T> creator) {
        return IO_SERVICE.submit(() -> {
            semaphore.acquireUninterruptibly();
            try (Realm realm = databaseCore.getRealm()) {
                final AtomicReference<T> managedRef = new AtomicReference<>();
                realm.executeTransaction(r -> {
                    T managed = r.createObject(getType());
                    creator.accept(managed);
                    managedRef.set(managed);
                });
                return getUnmanaged(managedRef.get());
            } finally {
                semaphore.release();
            }
        });
    }

    public Future<T> createAsync(Consumer<T> creator, Object primaryKey) {
        return IO_SERVICE.submit(() -> {
            semaphore.acquireUninterruptibly();
            try (Realm realm = databaseCore.getRealm()) {
                final AtomicReference<T> managedRef = new AtomicReference<>();
                realm.executeTransaction(r -> {
                    T managed = r.createObject(getType(), primaryKey);
                    creator.accept(managed);
                    managedRef.set(managed);
                });
                return getUnmanaged(managedRef.get());
            } finally {
                semaphore.release();
            }
        });
    }

    public T create(Consumer<T> creator) {
        try {
            return createAsync(creator).get();
        } catch (Exception e) {
            Log.w(TAG, "Error while creating object", e);
        }
        return null;
    }

    public T create(Consumer<T> creator, Object primaryKey) {
        try {
            return createAsync(creator, primaryKey).get();
        } catch (Exception e) {
            Log.w(TAG, "Error while creating object with primary key", e);
        }
        return null;
    }

    public Future<T> updateAsync(@NonNull Consumer<RealmQuery<T>> selector, Consumer<T> updater) {
        return IO_SERVICE.submit(() -> {
            semaphore.acquireUninterruptibly();
            try (Realm realm = databaseCore.getRealm()) {
                final AtomicReference<T> managedRef = new AtomicReference<>();
                realm.executeTransaction(r -> {
                    RealmQuery<T> query = r.where(getType());
                    selector.accept(query);
                    RealmResults<T> ts = query.findAll();
                    if (ts == null) return;
                    T managed = ts.stream()
                            .findFirst()
                            .orElse(null);
                    if (managed == null) return;
                    updater.accept(managed);
                    managedRef.set(managed);
                });
                return getUnmanaged(managedRef.get());
            } catch (Exception e) {
                Log.w(TAG, "Error while updating object", e);
                return null;
            } finally {
                semaphore.release();
            }
        });
    }

    public T update(@NonNull Consumer<RealmQuery<T>> selector, Consumer<T> updater) {
        try {
            return updateAsync(selector, updater).get();
        } catch (Exception e) {
            Log.w(TAG, "Error while updating object", e);
        }
        return null;
    }

    public Future<Long> deleteAsync(@NonNull Consumer<RealmQuery<T>> selector) {
        return IO_SERVICE.submit(() -> {
            semaphore.acquireUninterruptibly();
            try (Realm realm = databaseCore.getRealm()) {
                final AtomicReference<Long> deleted = new AtomicReference<>(0L);
                realm.executeTransaction(r -> {
                    RealmQuery<T> query = r.where(getType());
                    selector.accept(query);
                    RealmResults<T> ts = query.findAll();
                    ts.forEach(obj -> {
                        try {
                            RealmObject.deleteFromRealm(obj);
                            deleted.set(deleted.get() + 1);
                        } catch (Exception e) {
                            Log.w(TAG, "Error while deleting object [" + obj + "]", e);
                        }
                    });
                });
                return deleted.get();
            } catch (Exception e) {
                Log.w(TAG, "Error while deleting object", e);
                return -1L;
            } finally {
                semaphore.release();
            }
        });
    }

    public long delete(@NonNull Consumer<RealmQuery<T>> selector) {
        try {
            return deleteAsync(selector).get();
        } catch (Exception e) {
            Log.w(TAG, "Error while deleting object", e);
        }
        return -1L;
    }

    public Future<Long> deleteAllAsync() {
        return IO_SERVICE.submit(() -> {
            semaphore.acquireUninterruptibly();
            try (Realm realm = databaseCore.getRealm()) {
                final AtomicReference<Long> deleted = new AtomicReference<>(0L);
                realm.executeTransaction(r -> {
                    RealmResults<T> ts = realm.where(getType()).findAll();
                    deleted.set((long) ts.size());
                    ts.deleteAllFromRealm();
                });
                return deleted.get();
            } catch (Exception e) {
                Log.w(TAG, "Error while deleting all objects", e);
                return -1L;
            } finally {
                semaphore.release();
            }
        });
    }

    public long deleteAll() {
        try {
            return deleteAllAsync().get();
        } catch (Exception e) {
            Log.w(TAG, "Error while deleting all objects", e);
        }
        return -1L;
    }

    public boolean exists(Consumer<RealmQuery<T>> selector) {
        return get(selector) != null;
    }

    @Override
    public void onDestroy(@NonNull LifecycleOwner owner) {
        IO_SERVICE.shutdown();
        lifecycle.removeObserver(this);
        DefaultLifecycleObserver.super.onDestroy(owner);
    }
}
