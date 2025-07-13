package com.github.huymaster.campusexpensemanager.database.dao;

import android.util.Log;

import com.github.huymaster.campusexpensemanager.database.DatabaseCore;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

import io.realm.Realm;
import io.realm.RealmModel;

public abstract class BaseDAO<T extends RealmModel> implements AutoCloseable {
    private static final String SUPER_TAG = "BaseDAO";
    protected final DatabaseCore core;
    private final String TAG;
    protected Realm realm = null;

    protected BaseDAO(DatabaseCore core) {
        this.TAG = getClass().getSimpleName();
        this.core = core;
        this.realm = this.core.getRealm();
        Log.d(SUPER_TAG, "New realm instance created for " + TAG);
    }

    public abstract Class<T> getObjectClass();

    protected Stream<T> getAll() {
        try {
            checkValid();
            List<T> objects = new LinkedList<>();
            _getAll(objects);
            return objects.parallelStream();
        } catch (Exception e) {
            error("Failed to get all objects", e);
            return Stream.empty();
        }
    }

    public boolean insert(T object) {
        try {
            checkValid();
            return _insert(object);
        } catch (Exception e) {
            error("Failed to create object", e);
            return false;
        }
    }

    public boolean update(T object) {
        try {
            checkValid();
            return _update(object);
        } catch (Exception e) {
            error("Failed to update object", e);
            return false;
        }
    }

    public boolean delete(T object) {
        try {
            checkValid();
            return _delete(object);
        } catch (Exception e) {
            error("Failed to delete object", e);
            return false;
        }
    }

    public <O> boolean exists(O i, Function<T, O> selector) {
        try {
            checkValid();
            Stream<T> stream = getAll();
            return stream.anyMatch(t -> {
                var o = selector.apply(t);
                return o != null && o.equals(i) || o == i;
            });
        } catch (Exception e) {
            error("Failed to check if object exists", e);
            return false;
        }
    }

    public <O> T get(O i, Function<T, O> selector) {
        if (!exists(i, selector)) return null;
        try {
            checkValid();
            Stream<T> stream = getAll();
            return stream.filter(t -> {
                var o = selector.apply(t);
                return o != null && o.equals(i) || o == i;
            }).findFirst().orElse(null);
        } catch (Exception e) {
            error("Failed to get object", e);
            return null;
        }
    }

    protected abstract void _getAll(Collection<T> collection) throws Exception;

    protected abstract boolean _insert(T object) throws Exception;

    protected abstract boolean _update(T object) throws Exception;

    protected abstract boolean _delete(T object) throws Exception;

    private void error(String message) {
        error(message, null);
    }

    private void error(Exception e) {
        error(e.getMessage(), e);
    }

    private void error(String message, Exception e) {
        Log.w(TAG, message, e);
    }

    private void checkValid() {
        if (realm == null || realm.isClosed()) {
            throw new IllegalStateException("Realm is not initialized or it is closed");
        }
    }

    @Override
    public void close() {
        if (realm != null) {
            realm.close();
            realm = null;
            Log.d(SUPER_TAG, "Realm instance of " + TAG + " closed");
        }
    }
}