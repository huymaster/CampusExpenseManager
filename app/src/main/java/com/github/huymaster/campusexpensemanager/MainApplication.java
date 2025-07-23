package com.github.huymaster.campusexpensemanager;

import android.app.Application;

import com.github.huymaster.campusexpensemanager.core.ApplicationPreferences;
import com.github.huymaster.campusexpensemanager.core.NotificationUtils;
import com.google.android.material.color.DynamicColors;

import dagger.hilt.android.HiltAndroidApp;

@HiltAndroidApp
public class MainApplication extends Application {
    private static final String TAG = "MainApplication";
    public static MainApplication INSTANCE;
    private boolean isDebug = false;
    private ApplicationPreferences preferences;
    private NotificationUtils notification;
    private com.github.huymaster.campusexpensemanager.database.realm.DatabaseCore realmDatabaseCore;
    private com.github.huymaster.campusexpensemanager.database.sqlite.DatabaseCore sqliteDatabaseCore;


    public static boolean isDebug() {
        return INSTANCE.isDebug;
    }

    public static ApplicationPreferences getPreferences() {
        return INSTANCE.preferences;
    }

    public static NotificationUtils getNotificationUtils() {
        return INSTANCE.notification;
    }

    public static com.github.huymaster.campusexpensemanager.database.realm.DatabaseCore getRealmDatabaseCore() {
        return INSTANCE.realmDatabaseCore;
    }

    public static com.github.huymaster.campusexpensemanager.database.sqlite.DatabaseCore getSqliteDatabaseCore() {
        return INSTANCE.sqliteDatabaseCore;
    }

    @Override
    public void onCreate() {
        isDebug = BuildConfig.DEBUG;
        INSTANCE = this;
        super.onCreate();
        DynamicColors.applyToActivitiesIfAvailable(this);
        preferences = new ApplicationPreferences(this);
        notification = new NotificationUtils(this);
        realmDatabaseCore = new com.github.huymaster.campusexpensemanager.database.realm.DatabaseCore(this);
        sqliteDatabaseCore = new com.github.huymaster.campusexpensemanager.database.sqlite.DatabaseCore(this);
    }
}