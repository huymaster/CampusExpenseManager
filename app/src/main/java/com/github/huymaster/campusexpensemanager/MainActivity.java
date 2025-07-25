package com.github.huymaster.campusexpensemanager;

import android.os.Bundle;
import android.view.WindowManager;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.github.huymaster.campusexpensemanager.databinding.MainActivityBinding;
import com.github.huymaster.campusexpensemanager.viewmodel.UserViewModel;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity {
    private final OnBackPressedCallback onBackPressedCallback = new OnBackPressedCallback(true) {
        @Override
        public void handleOnBackPressed() {
            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(MainActivity.this);
            builder.setTitle("Exit");
            builder.setMessage(R.string.dialog_exit);
            builder.setPositiveButton(R.string.dialog_ok, (dialog, which) -> {
                UserViewModel.INSTANCE.logout();
                finish();
            });
            builder.setNegativeButton(R.string.dialog_cancel, (dialog, which) -> dialog.dismiss());
            builder.show();
        }
    };
    private MainActivityBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SECURE);
        getOnBackPressedDispatcher().addCallback(onBackPressedCallback);

        binding = MainActivityBinding.inflate(getLayoutInflater());
        Functions.setupInsets(binding);
        setContentView(binding.getRoot());
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