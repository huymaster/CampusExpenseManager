package com.github.huymaster.campusexpensemanager.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.github.huymaster.campusexpensemanager.R;
import com.github.huymaster.campusexpensemanager.databinding.MainFragmentBinding;
import com.github.huymaster.campusexpensemanager.databinding.NavigationHeaderBinding;
import com.github.huymaster.campusexpensemanager.viewmodel.UserViewModel;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MainFragment extends BaseFragment {
    @Inject
    UserViewModel viewModel;
    private MainFragmentBinding binding;
    private NavigationHeaderBinding navigationHeaderBinding;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = MainFragmentBinding.inflate(inflater, container, false);
        navigationHeaderBinding = NavigationHeaderBinding.bind(binding.mainNavigationView.getHeaderView(0));
        initListeners();
        return binding.getRoot();
    }

    private void initListeners() {
        binding.mainToolbar.setNavigationOnClickListener(v -> binding.mainDrawerLayout.open());
        binding.mainNavigationView.setNavigationItemSelectedListener(this::menuItemListener);
    }

    private boolean menuItemListener(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.navigation_logout) {
            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getMainActivity());
            builder.setTitle("Logout");
            builder.setMessage(R.string.dialog_logout);
            builder.setPositiveButton(R.string.dialog_ok, (dialog, which) -> {
                viewModel.logout();
                getNavController().navigate(R.id.action_mainFragment_to_loginFragment);
            });
            builder.setNegativeButton(R.string.dialog_cancel, (dialog, which) -> dialog.dismiss());
            builder.show();
            return true;
        }
        return false;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
