package com.github.huymaster.campusexpensemanager.fragment.bottomsheet;

import android.os.Bundle;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.github.huymaster.campusexpensemanager.database.realm.type.Category;
import com.github.huymaster.campusexpensemanager.database.realm.type.Expense;
import com.github.huymaster.campusexpensemanager.databinding.ExpenseEditBottomSheetBinding;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.List;
import java.util.function.Consumer;

public class ExpenseEditBottomSheetFragment extends BottomSheetDialogFragment {

	private final Expense expense;
	private final Consumer<Expense> callback;
	private final List<Category> categories;

	private ExpenseEditBottomSheetBinding binding;

	public ExpenseEditBottomSheetFragment(@NonNull Expense expense, @NonNull Consumer<Expense> callback, @Nullable List<Category> categories) {
		this.expense = expense;
		this.callback = callback;
		this.categories = categories == null ? List.of() : categories;
	}

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		binding = ExpenseEditBottomSheetBinding.inflate(inflater, container, false);
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
		String[] strings = this.categories.stream().map(Category::getName).toArray(String[]::new);
		binding.expenseEditBottomSheetCategory.setSimpleItems(strings);
		binding.expenseEditBottomSheetName.setText(expense.getName());
		binding.expenseEditBottomSheetAmount.setText(String.valueOf(expense.getAmount()));
		binding.expenseEditBottomSheetCategory.setSelected(expense.getCategory() != null);
		if (expense.getCategory() != null)
			binding.expenseEditBottomSheetCategory.setSelection(categories.indexOf(expense.getCategory()));
	}

	private void initListeners() {
		binding.expenseEditBottomSheetEdit.setOnClickListener(v -> {
			if (checkInputs()) {
				submit();
			}
		});
	}

	private boolean checkInputs() {
		boolean valid = true;
		Editable name = binding.expenseEditBottomSheetName.getText();
		Editable amount = binding.expenseEditBottomSheetAmount.getText();
		if (name != null && amount != null) {
			if (name.length() == 0) {
				binding.expenseEditBottomSheetNameLayout.setError("Name cannot be empty");
				valid = false;
			} else binding.expenseEditBottomSheetNameLayout.setError(null);
			if (amount.length() == 0) {
				binding.expenseEditBottomSheetAmountLayout.setError("Amount cannot be empty");
				valid = false;
			} else binding.expenseEditBottomSheetAmountLayout.setError(null);
			try {
				double parseDouble = Double.parseDouble(amount.toString());
				if (parseDouble < 0) {
					binding.expenseEditBottomSheetAmountLayout.setError("Amount cannot be negative");
					valid = false;
				} else
					binding.expenseEditBottomSheetAmountLayout.setError(null);
			} catch (NumberFormatException e) {
				binding.expenseEditBottomSheetAmountLayout.setError("Amount must be a number");
				valid = false;
			}
		}
		return valid;
	}

	private void submit() {
	}
}
