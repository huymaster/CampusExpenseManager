package com.github.huymaster.campusexpensemanager.fragment.bottomsheet;

import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.github.huymaster.campusexpensemanager.database.realm.type.Budget;
import com.github.huymaster.campusexpensemanager.databinding.BudgetEditBottomSheetBinding;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.function.Consumer;

public class BudgetEditBottomSheetFragment extends BottomSheetDialogFragment {
	private final Consumer<Budget> callback;
	private final DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
	private final Budget budget;
	private BudgetEditBottomSheetBinding binding;

	public BudgetEditBottomSheetFragment(@NonNull Budget budget, @NonNull Consumer<Budget> callback) {
		this.budget = budget;
		this.callback = callback;
	}

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		binding = BudgetEditBottomSheetBinding.inflate(inflater, container, false);
		return binding.getRoot();
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		initComponents();
		initListeners();
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		callback.accept(null);
		binding = null;
	}

	private void initComponents() {
		binding.budgetEditBottomSheetName.setText(budget.getName().trim());
		binding.budgetEditBottomSheetAmount.setText(String.valueOf(budget.getAmount()));
		binding.budgetEditBottomSheetStartDate.setText(dateFormat.format(budget.getStartDate()));
		binding.budgetEditBottomSheetEndDate.setText(dateFormat.format(budget.getEndDate()));
	}

	private void initListeners() {
		binding.budgetEditBottomSheetEdit.setOnClickListener(v -> {
			if (checkInputs()) submit();
		});
	}

	private boolean checkInputs() {
		boolean valid = true;
		Editable name = binding.budgetEditBottomSheetName.getText();
		Editable amount = binding.budgetEditBottomSheetAmount.getText();
		if (name != null && amount != null) {
			if (name.length() == 0) {
				binding.budgetEditBottomSheetNameLayout.setError("Name cannot be empty");
				valid = false;
			} else binding.budgetEditBottomSheetNameLayout.setError(null);
			if (amount.length() == 0) {
				binding.budgetEditBottomSheetAmountLayout.setError("Amount cannot be empty");
				valid = false;
			} else binding.budgetEditBottomSheetAmountLayout.setError(null);
			try {
				double parseDouble = Double.parseDouble(amount.toString());
				if (parseDouble < 0) {
					binding.budgetEditBottomSheetAmountLayout.setError("Amount cannot be negative");
					valid = false;
				} else
					binding.budgetEditBottomSheetAmountLayout.setError(null);
			} catch (NumberFormatException e) {
				binding.budgetEditBottomSheetAmountLayout.setError("Amount must be a number");
				valid = false;
			}
		}
		return valid;
	}

	private void submit() {
		Editable name = binding.budgetEditBottomSheetName.getText();
		Editable amount = binding.budgetEditBottomSheetAmount.getText();
		if (name != null && amount != null) {
			Budget budget = new Budget();
			budget.setName(name.toString().trim());
			budget.setAmount(Double.parseDouble(amount.toString()));
			Log.d("BudgetAddBottomSheetFragment", "Submitted budget: " + budget);
			callback.accept(budget);
			dismiss();
		}
	}
}
