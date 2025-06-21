package com.github.huymaster.campusexpensemanager;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.github.huymaster.campusexpensemanager.databinding.MainActivityBinding;

public class MainActivity extends AppCompatActivity {
    private final OnBackPressedCallback onBackPressedCallback = new OnBackPressedCallback(true) {
        @Override
        public void handleOnBackPressed() {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setMessage("Are you sure you want to exit?");
            builder.setPositiveButton("Yes", (dialog, which) -> finish());
            builder.setNegativeButton("No", (dialog, which) -> dialog.dismiss());
            Dialog dialog = builder.create();
            dialog.show();
        }
    };
    private MainActivityBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(new View(this));

        if (getIntent().hasExtra("permissionGranted")) {
            binding = MainActivityBinding.inflate(getLayoutInflater());
            Functions.setupInsets(binding);
            setContentView(binding.getRoot());
        } else {
            Intent intent = new Intent(this, PermissionActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        onBackPressedCallback.setEnabled(true);
    }

    @Override
    protected void onPause() {
        super.onPause();
        onBackPressedCallback.setEnabled(false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}