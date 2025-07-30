package com.github.huymaster.campusexpensemanager.database.realm;

import android.content.Context;

import androidx.annotation.Nullable;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.ProcessLifecycleOwner;

import com.github.huymaster.campusexpensemanager.database.realm.dao.BaseDAO;

import java.lang.reflect.Constructor;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmModel;

public class DatabaseCore {
    private static final String TAG = "RealmDatabaseCore";
    private final Context context;

    public DatabaseCore(Context context) {
        this.context = context;
        Realm.init(context);
        Realm realm = Realm.getInstance(getDefaultConfiguration());
        realm.close();
    }

    private RealmConfiguration getDefaultConfiguration() {
        return new RealmConfiguration.Builder()
                .modules(DatabaseModules.INSTANCE)
                .deleteRealmIfMigrationNeeded()
                .name("database.realm")
                .build();
    }

    public Realm getRealm() {
        return Realm.getInstance(getDefaultConfiguration());
    }

    public <T extends RealmModel, V extends BaseDAO<T>> V getDAO(Class<V> clazz) {
        return getDAO(clazz, null);
    }

    public <T extends RealmModel, V extends BaseDAO<T>> V getDAO(Class<V> clazz, @Nullable Lifecycle lifecycle) {
        if (lifecycle == null) lifecycle = ProcessLifecycleOwner.get().getLifecycle();
        try {
            Constructor<V> constructor = clazz.getDeclaredConstructor(DatabaseCore.class, Lifecycle.class);
            constructor.setAccessible(true);
            if (constructor.isAccessible())
                return constructor.newInstance(this, lifecycle);
            else
                throw new IllegalStateException("Can't create " + clazz.getSimpleName() + " instance: constructor is not accessible");
        } catch (Exception e) {
            throw new IllegalStateException("Can't create " + clazz.getSimpleName() + " instance: " + e.getMessage(), e);
        }
    }
}
