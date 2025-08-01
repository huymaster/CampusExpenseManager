package com.github.huymaster.campusexpensemanager.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.github.huymaster.campusexpensemanager.R;
import com.github.huymaster.campusexpensemanager.databinding.ExpensesFragmentBinding;
import com.github.huymaster.campusexpensemanager.databinding.ExpensesSubviewContainerBinding;
import com.google.android.material.tabs.TabLayout;

public class ExpensesFragment extends BaseFragment {
	private ExpensesFragmentBinding binding;

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		binding = ExpensesFragmentBinding.inflate(inflater, container, false);
		return binding.getRoot();
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		initComponents();
		initListeners();
	}

	private void initComponents() {
		binding.expensesViewPager.setAdapter(new ExpensesPagerAdapter());
	}

	private void initListeners() {
		binding.expensesTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
			@Override
			public void onTabSelected(TabLayout.Tab tab) {
				binding.expensesViewPager.setCurrentItem(tab.getPosition());
			}

			@Override
			public void onTabUnselected(TabLayout.Tab tab) {

			}

			@Override
			public void onTabReselected(TabLayout.Tab tab) {

			}
		});
		binding.expensesViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {

			@Override
			public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
				super.onPageScrolled(position, positionOffset, positionOffsetPixels);
				binding.expensesTabLayout.setScrollPosition(position, positionOffset, true);
			}

			@Override
			public void onPageSelected(int position) {
				super.onPageSelected(position);
				binding.expensesTabLayout.selectTab(binding.expensesTabLayout.getTabAt(position));
			}
		});
	}
}

class ExpensesPagerAdapter extends RecyclerView.Adapter<ExpensesSubVH> {

	private final int[] layouts = new int[]{
			R.layout.expenses_overview,
			R.layout.expenses_history,
			R.layout.expenses_statistics
	};

	@NonNull
	@Override
	public ExpensesSubVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.expenses_subview_container, parent, false);
		return new ExpensesSubVH(view);
	}

	@Override
	public void onBindViewHolder(@NonNull ExpensesSubVH holder, int position) {
		View view = LayoutInflater.from(holder.itemView.getContext()).inflate(layouts[position], holder.container, false);
		holder.container.addView(view);
	}

	@Override
	public int getItemCount() {
		return layouts.length;
	}
}

class ExpensesSubVH extends RecyclerView.ViewHolder {

	public FrameLayout container;

	public ExpensesSubVH(@NonNull View itemView) {
		super(itemView);
		ExpensesSubviewContainerBinding binding = ExpensesSubviewContainerBinding.bind(itemView);
		container = binding.expensesSubviewContainer;
	}
}