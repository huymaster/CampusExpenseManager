package com.github.huymaster.campusexpensemanager.fragment.bottomsheet;

import android.os.Bundle;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.github.huymaster.campusexpensemanager.database.realm.type.Category;
import com.github.huymaster.campusexpensemanager.databinding.CategoryAddBottomSheetBinding;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.function.Consumer;

public class CategoryAddBottomSheetFragment extends BottomSheetDialogFragment {
	private final Consumer<Category> callback;
	private CategoryAddBottomSheetBinding binding;

	public CategoryAddBottomSheetFragment(@NonNull Consumer<Category> callback) {
		this.callback = callback;
	}

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		binding = CategoryAddBottomSheetBinding.inflate(inflater, container, false);
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
		binding.categoryAddBottomSheetAddButton.setOnClickListener(v -> {
			if (checkInputs()) insertCategory();
		});
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		callback.accept(null);
		binding = null;
	}

	private boolean checkInputs() {
		boolean valid = true;
		Editable name = binding.categoryAddBottomSheetName.getText();
		if (name != null) {
			if (name.length() == 0) {
				binding.categoryAddBottomSheetNameLayout.setError("Name cannot be empty");
				valid = false;
			} else if (name.length() <= 16) binding.categoryAddBottomSheetNameLayout.setError(null);
			else
				binding.categoryAddBottomSheetNameLayout.setError("Name cannot be longer than 16 characters");
		}
		return valid;
	}

	private void insertCategory() {
		Editable name = binding.categoryAddBottomSheetName.getText();
		Editable description = binding.categoryAddBottomSheetDescription.getText();
		if (name != null && description != null) {
			if (name.length() == 0) {
				binding.categoryAddBottomSheetNameLayout.setError("Name cannot be empty");
				return;
			}
			Category category = new Category(name.toString().trim(), description.length() > 0 ? description.toString().trim() : null);
			callback.accept(category);
			dismiss();
		}
	}
}