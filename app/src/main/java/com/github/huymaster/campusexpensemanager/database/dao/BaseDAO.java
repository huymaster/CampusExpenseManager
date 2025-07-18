package com.github.huymaster.campusexpensemanager.database.dao;

import android.util.Log;

import com.github.huymaster.campusexpensemanager.database.DatabaseCore;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.realm.Realm;
import io.realm.RealmModel;

public abstract class BaseDAO<T extends RealmModel> implements AutoCloseable {
    protected static final String SUPER_TAG = "BaseDAO";
    protected final String TAG = getClass().getSimpleName();
    protected final ExecutorService IO_SERVICE = Executors.newSingleThreadExecutor();
    private final DatabaseCore databaseCore;
    private final Map<Long, Realm> workingRealms = new HashMap<>();

    protected BaseDAO(DatabaseCore databaseCore) {
        this.databaseCore = databaseCore;
    }

    protected Realm getRealm() {
        Realm realm = workingRealms.get(Thread.currentThread().getId());
        if (realm == null) {
            Log.d(SUPER_TAG, "Creating new Realm instance for thread " + String.format("%02X", Thread.currentThread().getId()));
            realm = databaseCore.getRealm();
            workingRealms.put(Thread.currentThread().getId(), realm);
        }
        return realm;
    }

    protected void releaseRealm() {
        Log.d(SUPER_TAG, "Releasing Realm instance for thread " + String.format("%02X", Thread.currentThread().getId()));
        Realm realm = workingRealms.remove(Thread.currentThread().getId());
        if (realm == null)
            return;
        if (realm.isInTransaction())
            realm.cancelTransaction();
        if (!realm.isClosed())
            realm.close();
    }

    @Override
    public void close() {
        IO_SERVICE.shutdown();
        for (Realm realm : workingRealms.values()) {
            if (realm.isInTransaction())
                realm.commitTransaction();
            if (!realm.isClosed())
                realm.close();
        }
    }
}
