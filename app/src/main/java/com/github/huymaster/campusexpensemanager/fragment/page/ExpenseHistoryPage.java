package com.github.huymaster.campusexpensemanager.fragment.page;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.github.huymaster.campusexpensemanager.databinding.ExpensesHistoryBinding;
import com.github.huymaster.campusexpensemanager.fragment.BaseFragment;

public class ExpenseHistoryPage extends BaseFragment {
	private ExpensesHistoryBinding binding;

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		binding = ExpensesHistoryBinding.inflate(inflater, container, false);
		return binding.getRoot();
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		initComponents();
		initListeners();
	}

	private void initComponents() {

	}

	private void initListeners() {

	}
}
