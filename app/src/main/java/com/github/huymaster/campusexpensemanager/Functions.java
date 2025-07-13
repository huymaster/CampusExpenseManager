package com.github.huymaster.campusexpensemanager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.view.View;
import android.widget.Toast;

import androidx.core.graphics.Insets;
import androidx.core.view.OnApplyWindowInsetsListener;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewbinding.ViewBinding;

import java.util.List;

public class Functions {
    public static void setupInsets(ViewBinding binding) {
        Thread thread = new Thread(() -> {
            View root = binding.getRoot();
            OnApplyWindowInsetsListener listener = (v, insets) -> {
                Insets systemBar = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                Insets ime = insets.getInsets(WindowInsetsCompat.Type.ime());
                root.setPadding(systemBar.left, systemBar.top + ime.top, systemBar.right, systemBar.bottom + ime.bottom);
                return insets;
            };
            ViewCompat.setOnApplyWindowInsetsListener(root, listener);
        }, "Insets setup");
        thread.start();
    }

    public static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException ignore) {
        }
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