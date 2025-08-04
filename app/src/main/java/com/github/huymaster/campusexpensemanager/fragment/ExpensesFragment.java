package com.github.huymaster.campusexpensemanager.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.github.huymaster.campusexpensemanager.database.realm.DatabaseCore;
import com.github.huymaster.campusexpensemanager.database.realm.dao.UserDAO;
import com.github.huymaster.campusexpensemanager.databinding.ExpensesFragmentBinding;
import com.github.huymaster.campusexpensemanager.fragment.bottomsheet.ExpenseAddBottomSheetFragment;
import com.github.huymaster.campusexpensemanager.fragment.page.ExpenseHistoryPage;
import com.github.huymaster.campusexpensemanager.fragment.page.ExpenseOverviewPage;
import com.github.huymaster.campusexpensemanager.fragment.page.ExpenseStatisticsPage;
import com.github.huymaster.campusexpensemanager.viewmodel.UserViewModel;
import com.google.android.material.tabs.TabLayout;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ExpensesFragment extends BaseFragment {
	public static final String EXPENSES_ADD_BOTTOM_SHEET_FRAGMENT_TAG = "expenses_add_bottom_sheet_fragment_tag";

	@Inject
	UserViewModel viewModel;
	@Inject
	DatabaseCore core;
	private ExpensesPagerAdapter adapter;
	private ExpensesFragmentBinding binding;
	private UserDAO dao;

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		binding = ExpensesFragmentBinding.inflate(inflater, container, false);
		return binding.getRoot();
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		dao = core.getDAO(UserDAO.class);
		adapter = new ExpensesPagerAdapter(getChildFragmentManager(), getLifecycle());
		initComponents();
		initListeners();
	}

	private void initComponents() {
		binding.expensesViewPager.setAdapter(adapter);
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
		binding.expensesAdd.setOnClickListener(v -> showAddExpenseDialog(false));
	}

	@Override
	public void onResume() {
		super.onResume();
		if (getArguments() != null && getArguments().getBoolean(EXPENSES_ADD_BOTTOM_SHEET_FRAGMENT_TAG))
			showAddExpenseDialog(true);
	}

	private void showAddExpenseDialog(boolean finishOnSubmit) {
		ExpenseAddBottomSheetFragment fragment = new ExpenseAddBottomSheetFragment(expense -> {
			dao.addExpense(viewModel.getLoggedInUsername(), expense);
			if (finishOnSubmit) getMainActivity().finishAndRemoveTask();
		}, dao.getCategories(viewModel.getLoggedInUsername()));
		fragment.show(getParentFragmentManager(), "ExpenseAddBottomSheetFragment");
	}
}

class ExpensesPagerAdapter extends FragmentStateAdapter {
	private static final int PAGE_COUNT = 3;

	public ExpensesPagerAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
		super(fragmentManager, lifecycle);
	}

	@NonNull
	@Override
	public Fragment createFragment(int position) {
		return switch (position) {
			case 1 -> new ExpenseHistoryPage();
			case 2 -> new ExpenseStatisticsPage();
			default -> new ExpenseOverviewPage();
		};
	}

	@Override
	public int getItemCount() {
		return PAGE_COUNT;
	}
}