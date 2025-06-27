package com.github.huymaster.campusexpensemanager;

import android.app.Application;

import com.github.huymaster.campusexpensemanager.core.ApplicationPreferences;
import com.github.huymaster.campusexpensemanager.core.NotificationUtils;
import com.github.huymaster.campusexpensemanager.database.DatabaseCore;

public class MainApplication extends Application {
    private static final String TAG = "MainApplication";
    public static MainApplication INSTANCE;
    private ApplicationPreferences preferences;
    private NotificationUtils notification;
    private DatabaseCore databaseCore;

    public static ApplicationPreferences getPreferences() {
        return INSTANCE.preferences;
    }

    public static NotificationUtils getNotificationUtils() {
        return INSTANCE.notification;
    }

    public static DatabaseCore getDatabaseCore() {
        return INSTANCE.databaseCore;
    }

    @Override
    public void onCreate() {
        INSTANCE = this;
        super.onCreate();
        preferences = new ApplicationPreferences(this);
        notification = new NotificationUtils(this);
        databaseCore = new DatabaseCore(this);
    }
}