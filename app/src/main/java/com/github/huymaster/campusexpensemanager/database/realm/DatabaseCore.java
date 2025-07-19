package com.github.huymaster.campusexpensemanager.database.realm;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.Lifecycle;

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
        Log.d(TAG, "Realm version: " + realm.getVersion());
        Log.d(TAG, "Realm path: " + realm.getPath());
        realm.close();
    }

    private RealmConfiguration getDefaultConfiguration() {
        return new RealmConfiguration.Builder()
                .directory(context.getFilesDir())
                .name("database.realm")
                .deleteRealmIfMigrationNeeded()
                .build();
    }

    public Realm getRealm() {
        return Realm.getInstance(getDefaultConfiguration());
    }

    public <T extends RealmModel, V extends BaseDAO<T>> V getDAO(Class<V> clazz, Lifecycle lifecycle) {
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
