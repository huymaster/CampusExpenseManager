package com.github.huymaster.campusexpensemanager;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.github.huymaster.campusexpensemanager.core.Utils;
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
    private MediaPlayer player;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (player == null) {
            new Thread(() -> player = MediaPlayer.create(this, R.raw.time_make_heroes), "player initializer").start();
        }

        setContentView(new View(this));
        if (getIntent().hasExtra("permissionGranted")) {
            getOnBackPressedDispatcher().addCallback(onBackPressedCallback);
            binding = MainActivityBinding.inflate(getLayoutInflater());
            Functions.setupInsets(binding);
            var view = binding.getRoot();
            view.setLongClickable(true);
            view.setOnLongClickListener(this::togglePlayer);
            setContentView(view);
        } else {
            Intent intent = new Intent(this, PermissionActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }
    }

    private boolean togglePlayer(View ignored) {
        if (player == null) return false;
        if (player.isPlaying()) {
            Utils.toastShort(this, "Paused");
            player.pause();
        } else {
            Utils.toastShort(this, "Playing");
            player.start();
        }
        return true;
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
        player.stop();
        player.release();
        player = null;
        binding = null;
    }
}