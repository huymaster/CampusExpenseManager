package com.github.huymaster.campusexpensemanager.database;

import android.content.Context;
import android.util.Log;

import java.util.function.Function;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class DatabaseCore {
    private static final String TAG = "DatabaseCore";
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

    public <T> T useRealm(Function<Realm, T> action) {
        return useRealm(action, getDefaultConfiguration());
    }

    public <T> T useRealm(Function<Realm, T> action, RealmConfiguration configuration) {
        try (Realm realm = Realm.getInstance(configuration)) {
            return action.apply(realm);
        } catch (Exception e) {
            Log.w(TAG, "Failed when executing action", e);
            return null;
        }
    }

    public <T> T useTransaction(Function<Realm, T> action) {
        return useTransaction(action, getDefaultConfiguration());
    }

    public <T> T useTransaction(Function<Realm, T> action, RealmConfiguration configuration) {
        return useRealm(realm -> {
            realm.beginTransaction();
            T result = action.apply(realm);
            realm.commitTransaction();
            return result;
        }, configuration);
    }
}
