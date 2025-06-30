package com.github.huymaster.campusexpensemanager.fragment;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.github.huymaster.campusexpensemanager.MainApplication;
import com.github.huymaster.campusexpensemanager.R;
import com.github.huymaster.campusexpensemanager.core.ApplicationPreferences;
import com.github.huymaster.campusexpensemanager.database.DatabaseCore;
import com.github.huymaster.campusexpensemanager.database.TableProxy;
import com.github.huymaster.campusexpensemanager.database.dao.CredentialDAO;
import com.github.huymaster.campusexpensemanager.database.table.CredentialTable;
import com.github.huymaster.campusexpensemanager.database.type.Credential;
import com.github.huymaster.campusexpensemanager.databinding.LoginFragmentBinding;

import java.util.Locale;

public class LoginFragment extends BaseFragment {
    private LoginFragmentBinding binding;
    private CredentialDAO credentialDAO;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = LoginFragmentBinding.inflate(inflater, container, false);
        DatabaseCore core = MainApplication.getDatabaseCore();
        TableProxy<Credential> proxy = core.getProxy(CredentialTable.class);
        credentialDAO = new CredentialDAO(proxy);
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
        credentialDAO.close();
        binding = null;
    }

    private void init() {
        ApplicationPreferences preferences = MainApplication.getPreferences();
        var checked = preferences.get(ApplicationPreferences.rememberLogin, false);
        binding.loginRemember.setChecked(checked);
        if (checked) {
            var username = preferences.get(ApplicationPreferences.rememberUsername, "");
            var password = preferences.get(ApplicationPreferences.rememberUsername, "");
            binding.loginUsername.setText(username);
            binding.loginPassword.setText(password);
        }
        binding.loginButton.setOnClickListener((v) -> buttonClick());
        if (binding.loginUsername.getText() == null || binding.loginUsername.getText().length() == 0 || binding.loginPassword.getText() == null || binding.loginPassword.getText().length() == 0) {
            binding.loginButton.setEnabled(false);
        }
        binding.loginUsername.addTextChangedListener(textWatcher());
    }

    private TextWatcher textWatcher() {
        return new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                var username = (binding.loginUsername.getText() == null ? "" : binding.loginUsername.getText().toString());
                var lower = username.toLowerCase(Locale.ROOT);
                if (!lower.equals(username))
                    binding.loginUsername.setText(lower);
                binding.loginButton.setEnabled(lower.length() > 0);
                binding.loginButton.setText(credentialDAO.checkUsername(lower) ? R.string.login_button_login : R.string.login_button_signup);
            }
        };
    }

    private void save() {
        ApplicationPreferences preferences = MainApplication.getPreferences();
        var username = binding.loginUsername.getText() == null ? "" : binding.loginUsername.getText().toString();
        var password = binding.loginPassword.getText() == null ? "" : binding.loginPassword.getText().toString();
        preferences.set(ApplicationPreferences.rememberLogin, binding.loginRemember.isChecked());
        preferences.set(ApplicationPreferences.rememberUsername, username);
        preferences.set(ApplicationPreferences.rememberPassword, password);
    }

    private void buttonClick() {
        var username = binding.loginUsername.getText() == null ? "" : binding.loginUsername.getText().toString();
        var password = binding.loginPassword.getText() == null ? "" : binding.loginPassword.getText().toString();
        if (credentialDAO.checkUsername(username)) {
            login(username, password);
        } else {
            signup(username, password);
        }
    }

    private void login(String username, String password) {
        if (credentialDAO.checkPassword(username, password)) {
            Toast.makeText(getMainActivity(), "Successfully logged in!", Toast.LENGTH_LONG).show();
            getMainActivity().finish();
        } else {
            Toast.makeText(getMainActivity(), "Wrong password!", Toast.LENGTH_LONG).show();
        }
    }

    private void signup(String username, String password) {
        if (credentialDAO.insert(username, password)) {
            Toast.makeText(getMainActivity(), "Successfully registered!", Toast.LENGTH_LONG).show();
            binding.loginUsername.setText("");
            binding.loginPassword.setText("");
        }
    }
}