package com.github.huymaster.campusexpensemanager.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.github.huymaster.campusexpensemanager.databinding.MainFragmentBinding;
import com.github.huymaster.campusexpensemanager.databinding.NavigationHeaderBinding;

public class MainFragment extends BaseFragment {
    private MainFragmentBinding binding;
    private NavigationHeaderBinding navigationHeaderBinding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = MainFragmentBinding.inflate(inflater, container, false);
        binding.mainToolbar.setNavigationOnClickListener(v -> binding.mainDrawerLayout.open());
        navigationHeaderBinding = NavigationHeaderBinding.bind(binding.mainNavigationView.getHeaderView(0));
        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
