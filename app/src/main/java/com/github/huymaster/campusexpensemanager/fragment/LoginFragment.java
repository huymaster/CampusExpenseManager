package com.github.huymaster.campusexpensemanager.fragment;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.github.huymaster.campusexpensemanager.MainApplication;
import com.github.huymaster.campusexpensemanager.R;
import com.github.huymaster.campusexpensemanager.core.ApplicationPreferences;
import com.github.huymaster.campusexpensemanager.core.ViewFunctions;
import com.github.huymaster.campusexpensemanager.database.dao.CredentialDAO;
import com.github.huymaster.campusexpensemanager.database.type.Credential;
import com.github.huymaster.campusexpensemanager.databinding.LoginFragmentBinding;
import com.google.android.material.snackbar.Snackbar;

public class LoginFragment extends BaseFragment {
    private LoginFragmentBinding binding;
    private CredentialDAO credentialDAO;
    private final TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void afterTextChanged(Editable s) {
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            var username = s.toString();
            if (username.length() == 0) return;
            binding.loginButton.setText(credentialDAO.exists(username, Credential::getUsername) ? R.string.login_button_login : R.string.login_button_signup);
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = LoginFragmentBinding.inflate(inflater, container, false);
        credentialDAO = MainApplication.getDatabaseCore().getDAO(CredentialDAO.class);
        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        init();
    }

    @Override
    public void onPause() {
        super.onPause();
        save();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        credentialDAO.close();
        credentialDAO = null;
        binding = null;
    }

    private void init() {
        binding.loginButton.setOnClickListener((v) -> buttonClick());
        binding.loginUsername.addTextChangedListener(textWatcher);
        ApplicationPreferences preferences = MainApplication.getPreferences();
        var checked = preferences.get(ApplicationPreferences.rememberLogin, false);
        binding.loginRemember.setChecked(checked);
        if (checked) {
            var username = preferences.get(ApplicationPreferences.rememberUsername, "");
            var password = preferences.get(ApplicationPreferences.rememberPassword, "");
            binding.loginUsername.setText(username);
            binding.loginPassword.setText(password);
        }
    }

    private void save() {
        ApplicationPreferences preferences = MainApplication.getPreferences();
        var username = ViewFunctions.getTextOrEmpty(binding.loginUsername);
        var password = ViewFunctions.getTextOrEmpty(binding.loginPassword);
        preferences.set(ApplicationPreferences.rememberLogin, binding.loginRemember.isChecked());
        preferences.set(ApplicationPreferences.rememberUsername, username.toLowerCase());
        preferences.set(ApplicationPreferences.rememberPassword, password);
    }

    private void buttonClick() {
        try {
            var username = ViewFunctions.getTextOrEmpty(binding.loginUsername);
            var password = ViewFunctions.getTextOrEmpty(binding.loginPassword);
            if (username.length() == 0 || password.length() == 0) {
                ViewFunctions.showSnackbar(binding, R.string.login_error_empty, Snackbar.LENGTH_SHORT);
                return;
            }
            if (credentialDAO.exists(username, Credential::getUsername)) {
                login(username, password);
            } else {
                signup(username, password);
            }
        } catch (Exception ignored) {
            ViewFunctions.showSnackbar(binding, R.string.login_error_unknown, Snackbar.LENGTH_SHORT);
        }
    }

    private void login(String username, String password) {
        if (credentialDAO.exists(username, Credential::getUsername) && credentialDAO.checkValid(username, password)) {
            ViewFunctions.showSnackbar(binding, R.string.login_success, Snackbar.LENGTH_SHORT);
        } else {
            ViewFunctions.showSnackbar(binding, R.string.login_error_invalid, Snackbar.LENGTH_SHORT);
        }
    }

    private void signup(String username, String password) {
        if (credentialDAO.exists(username, Credential::getUsername)) {
            ViewFunctions.showSnackbar(binding, R.string.login_signup_exists, Snackbar.LENGTH_SHORT);
        } else if (password.length() < 6) {
            ViewFunctions.showSnackbar(binding, R.string.login_signup_password_length, Snackbar.LENGTH_SHORT);
        } else {
            if (credentialDAO.add(username, password)) {
                ViewFunctions.showSnackbar(binding, R.string.login_signup_success, Snackbar.LENGTH_SHORT);
                binding.loginUsername.setText("");
                binding.loginPassword.setText("");
                binding.loginUsername.requestFocus();
            } else
                ViewFunctions.showSnackbar(binding, R.string.login_signup_failed, Snackbar.LENGTH_SHORT);
        }
    }
}