package com.github.huymaster.campusexpensemanager.fragment.bottomsheet;

import android.os.Bundle;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.github.huymaster.campusexpensemanager.database.realm.type.Category;
import com.github.huymaster.campusexpensemanager.databinding.CategoryEditBottomSheetBinding;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.function.Consumer;

public class CategoryEditBottomSheetFragment extends BottomSheetDialogFragment {
	private final Consumer<Category> callback;
	private final Category category;
	private CategoryEditBottomSheetBinding binding;

	public CategoryEditBottomSheetFragment(Category category, @NonNull Consumer<Category> callback) {
		this.category = category;
		this.callback = callback;
	}

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		binding = CategoryEditBottomSheetBinding.inflate(inflater, container, false);
		return binding.getRoot();
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		if (category == null) dismiss();
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
		binding.categoryEditBottomSheetName.setText(category.getName().trim());
		binding.categoryEditBottomSheetDescription.setText(category.getDescription() == null ? "" : category.getDescription().trim());
	}

	private void initListeners() {
		binding.categoryEditBottomSheetSaveButton.setOnClickListener(v -> saveCategory());
	}

	private void saveCategory() {
		Editable name = binding.categoryEditBottomSheetName.getText();
		Editable description = binding.categoryEditBottomSheetDescription.getText();
		if (name != null && description != null) {
			if (name.length() == 0) {
				binding.categoryEditBottomSheetNameLayout.setError("Name cannot be empty");
				return;
			}
			category.setName(name.toString().trim());
			category.setDescription(description.length() > 0 ? description.toString().trim() : null);
			callback.accept(category);
			dismiss();
		}
	}
}