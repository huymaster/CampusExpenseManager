package com.github.huymaster.campusexpensemanager;

import android.os.Bundle;
import android.view.View;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.github.huymaster.campusexpensemanager.core.ApplicationPreferences;
import com.github.huymaster.campusexpensemanager.databinding.MainActivityBinding;
import com.github.huymaster.campusexpensemanager.fragment.LoginFragment;
import com.github.huymaster.campusexpensemanager.fragment.MainFragment;
import com.github.huymaster.campusexpensemanager.viewmodel.UserViewModel;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import javax.inject.Inject;

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
				finish();
			});
			builder.setNegativeButton(R.string.dialog_cancel, (dialog, which) -> dialog.dismiss());
			builder.show();
		}
	};

	@Inject
	UserViewModel viewModel;
	@Inject
	ApplicationPreferences preferences;

	private MainActivityBinding binding;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		SplashScreen.installSplashScreen(this);
		super.onCreate(savedInstanceState);

		binding = MainActivityBinding.inflate(getLayoutInflater());
		View root = binding.getRoot();
		Functions.setupInsets(root);
		setContentView(root);

		getOnBackPressedDispatcher().addCallback(onBackPressedCallback);
		String u = preferences.get(ApplicationPreferences.loggedInUsername, null);
		viewModel.login(u);
		viewModel.getLoggedInState().observe(this, username -> {
			preferences.set(ApplicationPreferences.loggedInUsername, username);
			FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
			Fragment fragment = username == null ? new LoginFragment() : new MainFragment();
			fragment.setArguments(getIntent().getExtras());
			transaction.replace(R.id.main_fragment_container, fragment);
			transaction.commit();
		});
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