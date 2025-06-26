package com.github.huymaster.campusexpensemanager;

import android.app.Application;
import android.util.Log;

import com.github.huymaster.campusexpensemanager.core.ApplicationPreferences;
import com.github.huymaster.campusexpensemanager.core.NotificationUtils;
import com.github.huymaster.campusexpensemanager.database.DatabaseCore;

import java.util.function.Consumer;
import java.util.function.Function;

public class MainApplication extends Application {
    private static final String TAG = "MainApplication";
    public static MainApplication INSTANCE;
    private ApplicationPreferences preferences;
    private NotificationUtils notification;

    public static ApplicationPreferences getPreferences() {
        return INSTANCE.preferences;
    }

    public static NotificationUtils getNotificationUtils() {
        return INSTANCE.notification;
    }

    public static void useDatabase(Consumer<DatabaseCore> consumer) {
        useDatabase(database -> {
            consumer.accept(database);
            return null;
        });
    }

    public static <O> O useDatabase(Function<DatabaseCore, O> function) {
        try (DatabaseCore database = new DatabaseCore(INSTANCE)) {
            database.init();
            return function.apply(database);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
        return null;
    }

    @Override
    public void onCreate() {
        INSTANCE = this;
        super.onCreate();
        preferences = new ApplicationPreferences(this);
        notification = new NotificationUtils(this);
    }
}