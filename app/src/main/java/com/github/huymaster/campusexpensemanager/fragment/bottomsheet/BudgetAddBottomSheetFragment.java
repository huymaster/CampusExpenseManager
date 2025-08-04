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
import com.github.huymaster.campusexpensemanager.databinding.BudgetAddBottomSheetBinding;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.textfield.TextInputEditText;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.function.Consumer;

public class BudgetAddBottomSheetFragment extends BottomSheetDialogFragment {
	private final Consumer<Budget> callback;
	private final DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
	private BudgetAddBottomSheetBinding binding;

	public BudgetAddBottomSheetFragment(@NonNull Consumer<Budget> callback) {
		this.callback = callback;
	}

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		binding = BudgetAddBottomSheetBinding.inflate(inflater, container, false);
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
	}

	private void initListeners() {
		binding.budgetAddBottomSheetStartDate.setOnFocusChangeListener((v, hasFocus) -> {
			if (hasFocus)
				showTimePicker(binding.budgetAddBottomSheetStartDate, binding.budgetAddBottomSheetEndDate);
			v.clearFocus();
		});
		binding.budgetAddBottomSheetAdd.setOnClickListener(v -> {
			if (checkInputs()) submit();
		});
	}

	private boolean checkInputs() {
		boolean valid = true;
		Editable name = binding.budgetAddBottomSheetName.getText();
		Editable amount = binding.budgetAddBottomSheetAmount.getText();
		Editable startDate = binding.budgetAddBottomSheetStartDate.getText();
		if (name != null && amount != null && startDate != null) {
			if (name.length() == 0) {
				binding.budgetAddBottomSheetNameLayout.setError("Name cannot be empty");
				valid = false;
			} else binding.budgetAddBottomSheetNameLayout.setError(null);
			if (amount.length() == 0) {
				binding.budgetAddBottomSheetAmountLayout.setError("Amount cannot be empty");
				valid = false;
			} else binding.budgetAddBottomSheetAmountLayout.setError(null);
			try {
				double parseDouble = Double.parseDouble(amount.toString());
				if (parseDouble < 0) {
					binding.budgetAddBottomSheetAmountLayout.setError("Amount cannot be negative");
					valid = false;
				} else
					binding.budgetAddBottomSheetAmountLayout.setError(null);
			} catch (NumberFormatException e) {
				binding.budgetAddBottomSheetAmountLayout.setError("Amount must be a number");
				valid = false;
			}
			if (startDate.length() == 0) {
				binding.budgetAddBottomSheetStartDateLayout.setError("Start date cannot be empty");
				valid = false;
			} else if (parseDate(startDate) == null) {
				binding.budgetAddBottomSheetStartDateLayout.setError("Invalid date format");
				valid = false;
			} else binding.budgetAddBottomSheetStartDateLayout.setError(null);
		}
		return valid;
	}

	private void submit() {
		Editable name = binding.budgetAddBottomSheetName.getText();
		Editable amount = binding.budgetAddBottomSheetAmount.getText();
		Editable startDate = binding.budgetAddBottomSheetStartDate.getText();
		if (name != null && amount != null && startDate != null) {
			Budget budget = new Budget(name.toString().trim(), Double.parseDouble(amount.toString()), parseDate(startDate));
			Log.d("BudgetAddBottomSheetFragment", "Submitted budget: " + budget);
			callback.accept(budget);
			dismiss();
		}
	}

	private void showTimePicker(TextInputEditText startTextView, TextInputEditText endTextView) {
		MaterialDatePicker<Long> picker = MaterialDatePicker.Builder.datePicker().build();
		picker.show(getParentFragmentManager(), picker.toString());
		picker.addOnPositiveButtonClickListener(selection -> {
			Date date = new Date(selection);
			startTextView.setText(dateFormat.format(date));
			endTextView.setText(dateFormat.format(getEndDate(date)));
		});
	}

	private Date getEndDate(Date startDate) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(startDate);
		calendar.add(Calendar.MONTH, 1);
		return calendar.getTime();
	}

	private Date parseDate(Editable date) {
		try {
			return dateFormat.parse(date.toString());
		} catch (Exception ignore) {
			return null;
		}
	}
}
