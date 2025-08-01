package com.github.huymaster.campusexpensemanager.fragment;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.github.huymaster.campusexpensemanager.core.ResourceFunctions;
import com.github.huymaster.campusexpensemanager.databinding.SettingsFragmentBinding;

public class SettingsFragment extends BaseFragment {
	private SettingsFragmentBinding binding;

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		binding = SettingsFragmentBinding.inflate(inflater, container, false);
		return binding.getRoot();
	}

	@Override
	public void onResume() {
		super.onResume();
		initComponents();
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		binding = null;
	}

	private void initComponents() {
		binding.settingsDarkTheme.setChecked(ResourceFunctions.isDarkTheme());
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU)
			binding.settingNotification.setChecked(true);
		else
			binding.settingNotification.setChecked(getMainActivity().checkCallingOrSelfPermission(Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED);
	}
}
