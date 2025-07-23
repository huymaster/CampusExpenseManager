package com.github.huymaster.campusexpensemanager.fragment;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.github.huymaster.campusexpensemanager.MainApplication;
import com.github.huymaster.campusexpensemanager.R;
import com.github.huymaster.campusexpensemanager.core.ApplicationPreferences;
import com.github.huymaster.campusexpensemanager.core.ResourceFunctions;
import com.github.huymaster.campusexpensemanager.core.ViewFunctions;
import com.github.huymaster.campusexpensemanager.database.sqlite.dao.CredentialDAO;
import com.github.huymaster.campusexpensemanager.databinding.LoginFragmentBinding;
import com.github.huymaster.campusexpensemanager.viewmodel.UserViewModel;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;

public class LoginFragment extends BaseFragment {
    private static final String TAG = "LoginFragment";
    private LoginFragmentBinding binding;
    private CredentialDAO dao;
    private final TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void afterTextChanged(Editable s) {
            for (int i = 0; i < s.length(); i++) {
                if (s.charAt(i) == ' ') {
                    s.delete(i, i + 1);
                }
            }
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            var username = s.toString();
            if (username.length() == 0) return;
            var exists = dao.exists(username);
            binding.loginButton.setText(exists ? R.string.login_button_login : R.string.login_button_signup);
            binding.loginButton.setIcon(exists ? ResourceFunctions.getDrawable(R.drawable.ic_login) : ResourceFunctions.getDrawable(R.drawable.ic_person_add));
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = LoginFragmentBinding.inflate(inflater, container, false);
        dao = MainApplication.getSqliteDatabaseCore().getCredentialDAO(this);
        init();
        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        save();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void init() {
        binding.loginButton.setOnClickListener((v) -> buttonClick());
        binding.loginButtonForgot.setOnClickListener((v) -> forgotPassword());
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
        UserViewModel.INSTANCE.getLoggedInState().observe(getViewLifecycleOwner(), username -> {
            binding.loginButton.setEnabled(username == null);
            if (username != null) {
                getNavController().navigate(R.id.action_loginFragment_to_mainFragment);
            }
        });
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
            if (dao.exists(username)) {
                login(username, password);
            } else {
                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireActivity());
                builder.setTitle("New Account");
                builder.setMessage(getString(R.string.dialog_create_account, username));
                builder.setPositiveButton(R.string.dialog_yes, (dialog, which) -> signup(username, password));
                builder.setNegativeButton(R.string.dialog_no, (dialog, which) -> dialog.dismiss());
                builder.show();
            }
        } catch (Exception e) {
            ViewFunctions.showSnackbar(binding, R.string.login_error_unknown, Snackbar.LENGTH_SHORT);
            Log.w(TAG, e);
        }
    }

    private void forgotPassword() {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireActivity());
        builder.setTitle("Forgot Password");
        builder.setMessage("This feature is not implemented yet. Contact the developer for manual password reset.");
        builder.setPositiveButton(R.string.dialog_ok, (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    private void login(String username, String password) {
        if (!dao.exists(username)) {
            ViewFunctions.showSnackbar(binding, R.string.login_error_notfound, Snackbar.LENGTH_SHORT);
            return;
        }
        var result = dao.get(username).checkPassword(password);
        if (result) {
            UserViewModel.INSTANCE.login(username);
            ViewFunctions.showSnackbar(binding, R.string.login_success, Snackbar.LENGTH_SHORT);
        } else {
            ViewFunctions.showSnackbar(binding, R.string.login_error_invalid, Snackbar.LENGTH_SHORT);
        }
    }

    private void signup(String username, String password) {
        if (dao.exists(username)) {
            ViewFunctions.showSnackbar(binding, R.string.login_signup_exists, Snackbar.LENGTH_SHORT);
            return;
        }
        var result = dao.insert(username, password);
        if (result) {
            binding.loginUsername.requestFocus();
            binding.loginUsername.setText("");
            binding.loginPassword.setText("");
            ViewFunctions.showSnackbar(binding, R.string.login_signup_success, Snackbar.LENGTH_SHORT);
        } else {
            ViewFunctions.showSnackbar(binding, R.string.login_signup_failed, Snackbar.LENGTH_SHORT);
        }
    }
}