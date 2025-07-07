package com.github.huymaster.campusexpensemanager;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.PowerManager;
import android.provider.Settings;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.github.huymaster.campusexpensemanager.databinding.PermissionActivityBinding;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class PermissionActivity extends AppCompatActivity {
    private PermissionActivityBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = PermissionActivityBinding.inflate(getLayoutInflater());
        Functions.setupInsets(binding);
        setContentView(binding.getRoot());

        binding.permissionOpenSettings.setOnClickListener(v -> requestPermission());
        binding.permissionSdk.setText(getString(R.string.permission_sdk, Build.VERSION.SDK_INT, Build.VERSION.RELEASE));
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onResume() {
        super.onResume();
        checkPermissions();
    }

    private void requestPermission() {
        requestOverlayPermission();
        requestIgnoreBatteryOptimization();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            requestPostNotification();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
            requestStoragePermission();
    }

    private void requestPostNotification() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU || checkPermission(Manifest.permission.POST_NOTIFICATIONS))
            return;
        if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
            Intent intent = new Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
            intent.putExtra(Settings.EXTRA_APP_PACKAGE, getPackageName());
            startActivity(intent);
        } else
            requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1);
    }

    private void requestStoragePermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R || checkPermission(Manifest.permission.MANAGE_EXTERNAL_STORAGE))
            return;
        Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
        intent.setData(Uri.parse("package:" + getPackageName()));
        startActivity(intent);
    }

    private void requestOverlayPermission() {
        if (checkPermission(Manifest.permission.SYSTEM_ALERT_WINDOW)) return;
        PackageManager pm = getPackageManager();

        Intent applicationDetailsSettings = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        applicationDetailsSettings.setData(Uri.parse("package:" + getPackageName()));

        Intent overlaySetting = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
//        overlaySetting.setData(Uri.parse("package:" + getPackageName()));

        ResolveInfo info = pm.resolveActivity(overlaySetting, PackageManager.MATCH_SYSTEM_ONLY);
        if (info != null) {
            startActivity(overlaySetting);
            Toast.makeText(this, "Please allow overlay permission for " + getString(R.string.app_name), Toast.LENGTH_LONG).show();
        } else {
            startActivity(applicationDetailsSettings);
        }
    }

    @SuppressLint("BatteryLife")
    private void requestIgnoreBatteryOptimization() {
        if (checkPermission(Manifest.permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS))
            return;
        Intent intent = new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
        intent.setData(Uri.parse("package:" + getPackageName()));
        startActivity(intent);
    }

    private void checkPermissions() {
        Map<String, Boolean> permissionMap = new HashMap<>();
        for (String permission : getPermissions()) {
            permissionMap.put(permission, checkPermission(permission));
        }

        var notGrant = permissionMap.entrySet().stream().filter(e -> !e.getValue()).map(Map.Entry::getKey).iterator();
        var iterable = new Iterable<String>() {
            @NonNull
            @Override
            public Iterator<String> iterator() {
                return notGrant;
            }
        };

        Handler handler = new Handler(Looper.getMainLooper());
        String text = getString(R.string.permission_error, String.join("\n", iterable));
        if (permissionMap.values().stream().allMatch(Boolean::booleanValue)) {
            handler.post(() -> {
                binding.permissionOpenSettings.setVisibility(View.GONE);
                binding.permissionText.setText(R.string.permission_ok);
            });
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
            intent.putExtra("permissionGranted", true);
            startActivity(intent);
            finish();
        } else {
            handler.post(() -> {
                binding.permissionOpenSettings.setVisibility(View.VISIBLE);
                binding.permissionText.setText(text);
            });
        }
    }

    private List<String> getPermissions() {
        List<String> permissions = new LinkedList<>();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            permissions.add(Manifest.permission.POST_NOTIFICATIONS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
            permissions.add(Manifest.permission.MANAGE_EXTERNAL_STORAGE);
        permissions.add(Manifest.permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
        permissions.add(Manifest.permission.SYSTEM_ALERT_WINDOW);
        return permissions;
    }

    private boolean checkPermission(String permission) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && permission.equals(Manifest.permission.POST_NOTIFICATIONS))
            return checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && permission.equals(Manifest.permission.MANAGE_EXTERNAL_STORAGE))
            return Environment.isExternalStorageManager();
        if (permission.equals(Manifest.permission.SYSTEM_ALERT_WINDOW))
            return Settings.canDrawOverlays(this);
        if (permission.equals(Manifest.permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS)) {
            PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
            return powerManager != null && powerManager.isIgnoringBatteryOptimizations(getPackageName());
        }
        return false;
    }
}
