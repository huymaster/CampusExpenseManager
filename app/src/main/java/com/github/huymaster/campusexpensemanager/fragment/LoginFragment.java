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
import com.github.huymaster.campusexpensemanager.database.dao.CredentialDAO;
import com.github.huymaster.campusexpensemanager.database.type.Credential;
import com.github.huymaster.campusexpensemanager.databinding.LoginFragmentBinding;

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
        var username = binding.loginUsername.getText() == null ? "" : binding.loginUsername.getText().toString();
        var password = binding.loginPassword.getText() == null ? "" : binding.loginPassword.getText().toString();
        preferences.set(ApplicationPreferences.rememberLogin, binding.loginRemember.isChecked());
        preferences.set(ApplicationPreferences.rememberUsername, username.toLowerCase());
        preferences.set(ApplicationPreferences.rememberPassword, password);
    }

    private void buttonClick() {
        var username = binding.loginUsername.getText() == null ? "" : binding.loginUsername.getText().toString();
        var password = binding.loginPassword.getText() == null ? "" : binding.loginPassword.getText().toString();
    }
}