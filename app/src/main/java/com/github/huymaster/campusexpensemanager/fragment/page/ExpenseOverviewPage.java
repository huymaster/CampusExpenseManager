package com.github.huymaster.campusexpensemanager.fragment.page;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.github.huymaster.campusexpensemanager.R;
import com.github.huymaster.campusexpensemanager.database.realm.DatabaseCore;
import com.github.huymaster.campusexpensemanager.database.realm.dao.UserDAO;
import com.github.huymaster.campusexpensemanager.database.realm.type.Budget;
import com.github.huymaster.campusexpensemanager.database.realm.type.Expense;
import com.github.huymaster.campusexpensemanager.databinding.ExpensesOverviewBinding;
import com.github.huymaster.campusexpensemanager.fragment.BaseFragment;
import com.github.huymaster.campusexpensemanager.viewmodel.UserViewModel;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Calendar;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import io.realm.Realm;
import io.realm.RealmResults;

@AndroidEntryPoint
public class ExpenseOverviewPage extends BaseFragment {
	@Inject
	UserViewModel viewModel;
	@Inject
	DatabaseCore core;

	private UserDAO dao;

	private Realm realm;

	private RealmResults<Expense> r;

	private ExpensesOverviewBinding binding;

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		binding = ExpensesOverviewBinding.inflate(inflater, container, false);
		return binding.getRoot();
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		dao = core.getDAO(UserDAO.class);
		realm = core.getRealm();
		r = realm.where(Expense.class).findAll();
		r.addChangeListener(e -> updateUI());
		initComponents();
		initListeners();
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		r.removeAllChangeListeners();
		realm.close();
		binding = null;
	}

	private void initComponents() {
	}

	private void initListeners() {
	}

	@Override
	public void onResume() {
		super.onResume();
		updateUI();
	}

	@SuppressLint("DefaultLocale")
	private void updateUI() {
		Budget budget = getWorkingBudget();
		String spent = String.format("%,.2f", getSpent());
		String total = String.format("%,.2f", budget == null ? 0.0 : budget.getAmount());
		binding.expensesOverviewSummaryName.setText(budget == null ? getString(R.string.expense_overview_summary_no_budget) : budget.getName());
		binding.expensesOverviewSummaryAmount.setText(getString(R.string.expense_overview_summary_amount, spent, total));
		double progress = getProgress(budget, getSpent());
		binding.expensesOverviewSummaryProgress.setProgress((int) progress, true);
	}

	@Nullable
	private Budget getWorkingBudget() {
		List<Budget> budgets = dao.getBudgets(viewModel.getLoggedInUsername());
		Calendar c = Calendar.getInstance();
		return budgets.stream()
				.filter(b -> c.getTimeInMillis() >= b.getStartDate().getTime() && c.getTimeInMillis() < b.getEndDate().getTime())
				.findFirst()
				.orElse(null);
	}

	private double getSpent() {
		Budget budget = getWorkingBudget();
		if (budget != null) {
			List<Expense> expenses = dao.getExpensesFiltered(viewModel.getLoggedInUsername(), e -> e.getTimestamp() >= budget.getStartDate().getTime() && e.getTimestamp() < budget.getEndDate().getTime());
			return expenses.stream().mapToDouble(Expense::getAmount).sum();
		}
		return 0.0;
	}

	private double getProgress(Budget budget, double spent) {
		if (budget == null) return 0.0;
		try {
			return BigDecimal.valueOf(spent)
					.divide(BigDecimal.valueOf(budget.getAmount()), 2, RoundingMode.HALF_EVEN)
					.multiply(BigDecimal.valueOf(100))
					.doubleValue();
		} catch (ArithmeticException e) {
			return 0.0;
		}
	}
}
