package com.github.huymaster.campusexpensemanager.database.realm.dao;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.util.Consumer;
import androidx.core.util.Predicate;
import androidx.core.util.Supplier;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;

import com.github.huymaster.campusexpensemanager.database.realm.DatabaseCore;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicReference;

import io.realm.Realm;
import io.realm.RealmModel;
import io.realm.RealmObject;
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

    public Future<T> getAsync(@NonNull Predicate<T> selector) {
        return IO_SERVICE.submit(() -> {
            semaphore.acquireUninterruptibly();
            try (Realm realm = databaseCore.getRealm()) {
                RealmResults<T> ts = realm.where(getType()).findAll();
                if (ts == null) return null;
                T managed = ts.stream()
                        .filter(selector::test)
                        .findFirst()
                        .orElse(null);
                return getUnmanaged(managed);
            } catch (Exception e) {
                Log.w(TAG, "Error while getting object", e);
                return null;
            } finally {
                semaphore.release();
            }
        });
    }

    public T get(@NonNull Predicate<T> selector) {
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

    public <C extends Collection<T>> Future<C> getAllAsync(@NonNull Supplier<C> supplier, @NonNull Predicate<T> selector) {
        return IO_SERVICE.submit(() -> {
            semaphore.acquireUninterruptibly();
            try (Realm realm = databaseCore.getRealm()) {
                C container = supplier.get();
                RealmResults<T> ts = realm.where(getType()).findAll();
                if (ts != null) {
                    var unmanaged = getUnmanagedI(ts);
                    unmanaged.removeIf(t -> !selector.test(t));
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
        return null;
    }

    public <C extends Collection<T>> C getAll(@NonNull Supplier<C> supplier, @NonNull Predicate<T> selector) {
        try {
            return getAllAsync(supplier, selector).get();
        } catch (Exception e) {
            Log.w(TAG, "Error while getting all objects", e);
        }
        return null;
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

    public Future<T> updateAsync(@NonNull Predicate<T> selector, Consumer<T> updater) {
        return IO_SERVICE.submit(() -> {
            T unmanaged = getUnmanaged(get(selector));
            semaphore.acquireUninterruptibly();
            try (Realm realm = databaseCore.getRealm()) {
                final AtomicReference<T> managedRef = new AtomicReference<>();
                realm.executeTransaction(r -> {
                    if (unmanaged == null) return;
                    updater.accept(unmanaged);
                    r.insertOrUpdate(unmanaged);
                    managedRef.set(unmanaged);
                });
                return getUnmanaged(managedRef.get());
            } catch (Exception e) {
                Log.w(TAG, "Error while updating object [" + unmanaged + "]", e);
                return null;
            } finally {
                semaphore.release();
            }
        });
    }

    public T update(@NonNull Predicate<T> selector, Consumer<T> updater) {
        try {
            return updateAsync(selector, updater).get();
        } catch (Exception e) {
            Log.w(TAG, "Error while updating object", e);
        }
        return null;
    }

    public Future<Long> deleteAsync(@NonNull Predicate<T> selector) {
        return IO_SERVICE.submit(() -> {
            List<T> unmanaged = getUnmanagedI(getAll(LinkedList::new, selector));
            semaphore.acquireUninterruptibly();
            try (Realm realm = databaseCore.getRealm()) {
                final AtomicReference<Long> deleted = new AtomicReference<>(0L);
                if (unmanaged != null) {
                    realm.executeTransaction(r -> {
                        for (T obj : unmanaged) {
                            try {
                                RealmObject.deleteFromRealm(obj);
                                deleted.set(deleted.get() + 1);
                            } catch (Exception e) {
                                Log.w(TAG, "Error while deleting object [" + obj + "]", e);
                            }
                        }
                    });
                }
                return deleted.get();
            } catch (Exception e) {
                Log.w(TAG, "Error while deleting object", e);
                return -1L;
            } finally {
                semaphore.release();
            }
        });
    }

    public boolean exists(Predicate<T> selector) {
        return get(selector) != null;
    }

    @Override
    public void onDestroy(@NonNull LifecycleOwner owner) {
        IO_SERVICE.shutdown();
        lifecycle.removeObserver(this);
        DefaultLifecycleObserver.super.onDestroy(owner);
    }
}
