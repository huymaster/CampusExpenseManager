package com.github.huymaster.campusexpensemanager.core;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.widget.Toast;

import com.github.huymaster.campusexpensemanager.MainApplication;

import java.util.List;

public class Utils {
    private Utils() {
    }

    @SuppressLint("QueryPermissionsNeeded")
    public static boolean existsActivityCanHandleIntent(Intent intent) {
        PackageManager pm = MainApplication.INSTANCE.getPackageManager();
        List<ResolveInfo> activities = pm.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        return activities.size() > 0;
    }

    public static void toastShort(Context context, String s) {
        Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
    }

    public static void toastLong(Context context, String s) {
        Toast.makeText(context, s, Toast.LENGTH_LONG).show();
    }

    public static void toast(Context context, String s, int duration) {
        Toast.makeText(context, s, duration).show();
    }
}
