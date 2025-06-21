package com.github.huymaster.campusexpensemanager;

import android.app.Application;

import com.github.huymaster.campusexpensemanager.core.ApplicationPreferences;
import com.github.huymaster.campusexpensemanager.core.NotificationUtils;

public class MainApplication extends Application {
    public static MainApplication INSTANCE;
    private ApplicationPreferences preferences;
    private NotificationUtils notification;

    @Override
    public void onCreate() {
        INSTANCE = this;
        super.onCreate();
        preferences = new ApplicationPreferences(this);
        notification = new NotificationUtils(this);
    }

    public ApplicationPreferences getPreferences() {
        return preferences;
    }

    public NotificationUtils getNotificationUtils() {
        return notification;
    }
}