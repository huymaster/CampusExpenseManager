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

import com.github.huymaster.campusexpensemanager.R;
import com.github.huymaster.campusexpensemanager.core.ApplicationPreferences;
import com.github.huymaster.campusexpensemanager.core.ResourceFunctions;
import com.github.huymaster.campusexpensemanager.core.ViewFunctions;
import com.github.huymaster.campusexpensemanager.database.realm.DatabaseCore;
import com.github.huymaster.campusexpensemanager.database.realm.dao.UserDAO;
import com.github.huymaster.campusexpensemanager.database.sqlite.dao.CredentialDAO;
import com.github.huymaster.campusexpensemanager.databinding.LoginFragmentBinding;
import com.github.huymaster.campusexpensemanager.viewmodel.UserViewModel;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class LoginFragment extends BaseFragment {
	@Inject
	UserViewModel viewModel;
	@Inject
	ApplicationPreferences preferences;
	@Inject
	DatabaseCore realmDatabaseCore;
	@Inject
	CredentialDAO dao;
	private LoginFragmentBinding binding;
	private final TextWatcher textWatcher = new TextWatcher() {
		@Override
		public void afterTextChanged(Editable s) {
			if (s.length() == 0) return;
			var u = s.charAt(s.length() - 1);
			if (u >= 'A' && u <= 'Z') {
				s.delete(s.length() - 1, s.length());
				s.append(Character.toLowerCase(u));
			}
			if (u == ' ') {
				s.delete(s.length() - 1, s.length());
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
	private UserDAO userDao;

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		binding = LoginFragmentBinding.inflate(inflater, container, false);
		userDao = realmDatabaseCore.getDAO(UserDAO.class);
		return binding.getRoot();
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		init();
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onPause() {
		super.onPause();
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
		var checked = preferences.get(ApplicationPreferences.rememberLogin, false);
		binding.loginRemember.setChecked(checked);
		if (checked) {
			var username = preferences.get(ApplicationPreferences.rememberUsername, "");
			var password = preferences.get(ApplicationPreferences.rememberPassword, "");
			binding.loginUsername.setText(username);
			binding.loginPassword.setText(password);
		}
		viewModel.getLoggedInState().observe(getViewLifecycleOwner(), username -> {
			binding.loginButton.setEnabled(username == null);
			if (username != null)
				getNavController().navigate(R.id.action_loginFragment_to_mainFragment);
		});
	}

	private void save() {
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
			if (!userDao.exists(username))
				userDao.addUser(username);
			viewModel.login(username);
			ViewFunctions.showSnackbar(binding, R.string.login_success, Snackbar.LENGTH_SHORT);
			save();
		} else {
			ViewFunctions.showSnackbar(binding, R.string.login_error_invalid, Snackbar.LENGTH_SHORT);
		}
	}

	private void signup(String username, String password) {
		if (dao.exists(username)) {
			ViewFunctions.showSnackbar(binding, R.string.login_signup_exists, Snackbar.LENGTH_SHORT);
			return;
		}
		if (password.length() < 6) {
			ViewFunctions.showSnackbar(binding, R.string.login_signup_password_length, Snackbar.LENGTH_SHORT);
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