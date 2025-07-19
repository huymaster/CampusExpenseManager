package com.github.huymaster.campusexpensemanager.database.realm.dao;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;

import com.github.huymaster.campusexpensemanager.database.realm.DatabaseCore;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Function;
import java.util.function.Supplier;

import io.realm.Realm;
import io.realm.RealmModel;
import io.realm.RealmResults;

public abstract class BaseDAO<T extends RealmModel> implements DefaultLifecycleObserver {
    protected static final String SUPER_TAG = "BaseDAO";
    protected static final ExecutorService IO_SERVICE = Executors.newSingleThreadExecutor();
    protected final String TAG = getClass().getSimpleName();
    private final DatabaseCore databaseCore;
    private final Lifecycle lifecycle;
    private final Map<Long, Realm> workingRealms = new HashMap<>();

    protected BaseDAO(DatabaseCore databaseCore, Lifecycle lifecycle) {
        this.databaseCore = databaseCore;
        this.lifecycle = lifecycle;
        lifecycle.addObserver(this);
    }

    protected Realm getRealm() {
        Realm realm = workingRealms.get(Thread.currentThread().getId());
        if (realm == null) {
            Log.d(SUPER_TAG, String.format("Creating new Realm instance for thread id %02X (%d)", Thread.currentThread().getId(), Thread.currentThread().getId()));
            realm = databaseCore.getRealm();
            workingRealms.put(Thread.currentThread().getId(), realm);
        }
        return realm;
    }

    protected void releaseRealm() {
        Realm realm = workingRealms.remove(Thread.currentThread().getId());
        if (realm == null) {
            Log.w(SUPER_TAG, String.format("Realm instance for thread id %02X (%d) not found", Thread.currentThread().getId(), Thread.currentThread().getId()));
            return;
        }
        Log.d(SUPER_TAG, String.format("Releasing Realm instance for thread id %02X (%d)", Thread.currentThread().getId(), Thread.currentThread().getId()));
        if (realm.isInTransaction())
            realm.cancelTransaction();
        if (!realm.isClosed())
            realm.close();
    }

    public abstract Class<T> getType();

    protected abstract <O extends Collection<T>> O _getAll(Realm realm, Supplier<O> supplier);

    public List<T> getAll() {
        try {
            return getAllAsync().get();
        } catch (ExecutionException e) {
            Log.w(TAG, "Error while getting all objects", e);
        } catch (InterruptedException e) {
            Log.w(TAG, "Getting all objects was interrupted", e);
        }
        return null;
    }

    public Future<List<T>> getAllAsync() {
        return getAllAsync(LinkedList::new);
    }

    public <O extends Collection<T>> O getAll(Supplier<O> supplier) {
        try {
            return getAllAsync(supplier).get();
        } catch (ExecutionException e) {
            Log.w(TAG, "Error while getting all objects", e);
        } catch (InterruptedException e) {
            Log.w(TAG, "Getting all objects was interrupted", e);
        }
        return null;
    }

    public <O extends Collection<T>> Future<O> getAllAsync(Supplier<O> supplier) {
        return IO_SERVICE.submit(() -> {
            Realm realm = getRealm();
            O out = _getAll(realm, supplier);
            releaseRealm();
            return out;
        });
    }

    public <I> T get(I value, Function<T, I> selector) {
        try {
            return getAsync(value, selector).get();
        } catch (ExecutionException e) {
            Log.w(TAG, "Error while getting object", e);
        } catch (InterruptedException e) {
            Log.w(TAG, "Getting object was interrupted", e);
        }
        return null;
    }

    public <I> Future<T> getAsync(I value, Function<T, I> selector) {
        return IO_SERVICE.submit(() -> {
            Realm realm = getRealm();
            RealmResults<T> query = realm.where(getType()).findAll();
            T out = query.stream().filter(t -> selector.apply(t).equals(value)).findFirst().orElse(null);
            releaseRealm();
            return out;
        });
    }

    @Override
    public void onDestroy(@NonNull LifecycleOwner owner) {
        DefaultLifecycleObserver.super.onDestroy(owner);
        lifecycle.removeObserver(this);
        IO_SERVICE.shutdown();
        for (Realm realm : workingRealms.values()) {
            if (realm.isInTransaction())
                realm.commitTransaction();
            if (!realm.isClosed())
                realm.close();
        }
    }
}
