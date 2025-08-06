package com.github.huymaster.campusexpensemanager.fragment;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.github.huymaster.campusexpensemanager.databinding.SimpleViewHolderBinding;
import com.google.android.material.textview.MaterialTextView;

public class SimpleVH extends RecyclerView.ViewHolder {

	public final MaterialTextView simpleViewHolderName;
	private final SimpleViewHolderBinding binding;

	public SimpleVH(@NonNull View itemView) {
		super(itemView);
		binding = SimpleViewHolderBinding.bind(itemView);
		simpleViewHolderName = binding.simpleViewHolderName;
	}
}
