package com.github.huymaster.campusexpensemanager.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.github.huymaster.campusexpensemanager.MainApplication;
import com.github.huymaster.campusexpensemanager.R;
import com.github.huymaster.campusexpensemanager.databinding.InitializationFragmentBinding;

public class InitializationFragment extends BaseFragment {
    private InitializationFragmentBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = InitializationFragmentBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        MainApplication.createLibraries();
    }

    @Override
    public void onResume() {
        super.onResume();
        Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (MainApplication.allInstancesCreated()) {
                    getNavController().navigate(R.id.action_initializationFragment_to_loginFragment);
                } else {
                    handler.postDelayed(this, 100);
                }
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}