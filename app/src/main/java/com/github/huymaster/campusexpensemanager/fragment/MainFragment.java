package com.github.huymaster.campusexpensemanager.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.github.huymaster.campusexpensemanager.R;
import com.github.huymaster.campusexpensemanager.databinding.MainFragmentBinding;
import com.github.huymaster.campusexpensemanager.databinding.NavigationHeaderBinding;
import com.github.huymaster.campusexpensemanager.viewmodel.UserViewModel;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MainFragment extends BaseFragment {
	public static final String FRAGMENT_TAG = "MainFragment_TAG";
	@Inject
	UserViewModel viewModel;
	private MainFragmentBinding binding;
	private NavigationHeaderBinding navigationHeaderBinding;


	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		binding = MainFragmentBinding.inflate(inflater, container, false);
		navigationHeaderBinding = NavigationHeaderBinding.bind(binding.mainNavigationView.getHeaderView(0));
		return binding.getRoot();
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		initComponents();
		initListeners();
	}

	private void initListeners() {
		binding.mainToolbar.setNavigationOnClickListener(v -> binding.mainDrawerLayout.open());
		binding.mainNavigationView.setNavigationItemSelectedListener(this::menuItemListener);
		if (getArguments() != null && getArguments().containsKey(ExpensesFragment.EXPENSES_ADD_BOTTOM_SHEET_FRAGMENT_TAG)) {
			binding.mainNavigationView.setCheckedItem(R.id.navigation_expenses);
			ExpensesFragment fragment = new ExpensesFragment();
			fragment.setArguments(getArguments());
			setFragment(fragment);
		}
	}

	private void initComponents() {
		binding.mainNavigationView.getMenu().setGroupCheckable(R.id.navigation_group, true, true);
		binding.mainNavigationView.setCheckedItem(R.id.navigation_expenses);
		setFragment(new ExpensesFragment());
		binding.mainFrameLayout.removeAllViews();
	}

	private boolean menuItemListener(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.navigation_expenses) {
			binding.mainNavigationView.setCheckedItem(R.id.navigation_expenses);
			setFragment(new ExpensesFragment());
			closeDrawer();
			return true;
		} else if (id == R.id.navigation_categories) {
			binding.mainNavigationView.setCheckedItem(R.id.navigation_categories);
			setFragment(new CategoriesFragment());
			closeDrawer();
			return true;
		} else if (id == R.id.navigation_budget) {
			binding.mainNavigationView.setCheckedItem(R.id.navigation_budget);
			setFragment(new BudgetFragment());
			closeDrawer();
			return true;
		} else if (id == R.id.navigation_settings) {
			binding.mainNavigationView.setCheckedItem(R.id.navigation_settings);
			setFragment(new SettingsFragment());
			closeDrawer();
			return true;
		}
		if (id == R.id.navigation_logout) {
			MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getMainActivity());
			builder.setTitle("Logout");
			builder.setMessage(R.string.dialog_logout);
			builder.setPositiveButton(R.string.dialog_ok, (dialog, which) -> viewModel.logout());
			builder.setNegativeButton(R.string.dialog_cancel, (dialog, which) -> dialog.dismiss());
			builder.show();
			return true;
		}
		return false;
	}

	private void setFragment(Fragment fragment) {
		if (fragment != null) {
			FrameLayout frameLayout = binding.mainFrameLayout;
			FragmentManager manager = getChildFragmentManager();
			manager.beginTransaction().replace(R.id.main_frame_layout, fragment).commit();
		}
	}

	private void closeDrawer() {
		binding.mainDrawerLayout.close();
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		binding = null;
	}
}
