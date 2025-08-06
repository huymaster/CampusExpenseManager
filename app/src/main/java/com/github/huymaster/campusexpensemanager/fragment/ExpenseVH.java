package com.github.huymaster.campusexpensemanager.fragment;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.github.huymaster.campusexpensemanager.databinding.ExpenseViewHolderBinding;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.chip.Chip;
import com.google.android.material.textview.MaterialTextView;

public class ExpenseVH extends RecyclerView.ViewHolder {

	public final MaterialCardView expenseViewHolderCard;
	public final MaterialTextView expenseViewHolderName;
	public final MaterialTextView expenseViewHolderAmount;
	public final MaterialTextView expenseViewHolderDate;
	public final Chip expenseViewHolderCategory;
	private final ExpenseViewHolderBinding binding;

	public ExpenseVH(@NonNull View itemView) {
		super(itemView);
		binding = ExpenseViewHolderBinding.bind(itemView);
		expenseViewHolderCard = binding.expenseViewHolderCard;
		expenseViewHolderName = binding.expenseViewHolderName;
		expenseViewHolderAmount = binding.expenseViewHolderAmount;
		expenseViewHolderDate = binding.expenseViewHolderDate;
		expenseViewHolderCategory = binding.expenseViewHolderCategory;
	}
}
