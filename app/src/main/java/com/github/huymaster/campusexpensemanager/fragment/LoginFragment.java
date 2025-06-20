package com.github.huymaster.campusexpensemanager.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.github.huymaster.campusexpensemanager.MainApplication;
import com.github.huymaster.campusexpensemanager.core.ApplicationPreferences;
import com.github.huymaster.campusexpensemanager.databinding.LoginFragmentBinding;

public class LoginFragment extends BaseFragment {
    private LoginFragmentBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = LoginFragmentBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        init();
    }

    @Override
    public void onPause() {
        save();
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void init() {
        ApplicationPreferences preferences = MainApplication.INSTANCE.getPreferences();
        var checked = preferences.get(ApplicationPreferences.rememberLogin, false);
        binding.loginRemember.setChecked(checked);
        if (checked) {
            var username = preferences.get(ApplicationPreferences.rememberUsername, "");
            var password = preferences.get(ApplicationPreferences.rememberUsername, "");
            binding.loginUsername.setText(username);
            binding.loginPassword.setText(password);
        }
        binding.loginButton.setOnClickListener((v) -> login());
    }

    private void save() {
        ApplicationPreferences preferences = MainApplication.INSTANCE.getPreferences();
        var username = binding.loginUsername.getText() == null ? "" : binding.loginUsername.getText().toString();
        var password = binding.loginPassword.getText() == null ? "" : binding.loginPassword.getText().toString();
        preferences.set(ApplicationPreferences.rememberLogin, binding.loginRemember.isChecked());
        preferences.set(ApplicationPreferences.rememberUsername, username);
        preferences.set(ApplicationPreferences.rememberPassword, password);
    }

    private void login() {
        var username = binding.loginUsername.getText() == null ? "" : binding.loginUsername.getText().toString();
        var password = binding.loginPassword.getText() == null ? "" : binding.loginPassword.getText().toString();
    }
}