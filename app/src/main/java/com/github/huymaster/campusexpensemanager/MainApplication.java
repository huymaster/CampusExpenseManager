package com.github.huymaster.campusexpensemanager;

import android.app.Application;

import com.github.huymaster.campusexpensemanager.core.ApplicationPreferences;
import com.github.huymaster.campusexpensemanager.core.NotificationUtils;
import com.github.huymaster.campusexpensemanager.data.DataSourceImpl;
import com.github.huymaster.campusexpensemanager.data.LocalDataSource;

public class MainApplication extends Application {
    public static MainApplication INSTANCE;
    private final DataSourceImpl local = new LocalDataSource(this);
    private ApplicationPreferences preferences;
    private NotificationUtils notification;

    @Override
    public void onCreate() {
        INSTANCE = this;
        super.onCreate();
        preferences = new ApplicationPreferences(this);
        notification = new NotificationUtils(this);
    }

    public DataSourceImpl getLocalDataSource() {
        return local;
    }

    public ApplicationPreferences getPreferences() {
        return preferences;
    }

    public NotificationUtils getNotificationUtils() {
        return notification;
    }
}