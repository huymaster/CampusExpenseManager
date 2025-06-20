package com.github.huymaster.campusexpensemanager.core;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.content.ContextCompat;

import com.github.huymaster.campusexpensemanager.MainApplication;

public class NotificationUtils {
    private final Context context;

    public NotificationUtils(MainApplication context) {
        this.context = context;
    }

    private void init() {

    }

    private boolean checkPermission() {
        String permission = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU ? Manifest.permission.POST_NOTIFICATIONS : "android.permission.POST_NOTIFICATIONS";
        return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED;
    }

    public enum NotificationType {
        ERROR,
        NORMAL,
        SILENT;
    }
}
