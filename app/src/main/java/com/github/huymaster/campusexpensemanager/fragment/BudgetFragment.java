package com.github.huymaster.campusexpensemanager.fragment;

import android.content.res.Resources;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.huymaster.campusexpensemanager.R;
import com.github.huymaster.campusexpensemanager.core.ItemTouchHelperAdapter;
import com.github.huymaster.campusexpensemanager.core.SimpleItemTouchHelper;
import com.github.huymaster.campusexpensemanager.database.realm.DatabaseCore;
import com.github.huymaster.campusexpensemanager.database.realm.dao.UserDAO;
import com.github.huymaster.campusexpensemanager.database.realm.type.Budget;
import com.github.huymaster.campusexpensemanager.database.realm.type.Expense;
import com.github.huymaster.campusexpensemanager.databinding.BudgetFragmentBinding;
import com.github.huymaster.campusexpensemanager.databinding.BudgetViewHolderBinding;
import com.github.huymaster.campusexpensemanager.fragment.bottomsheet.BudgetAddBottomSheetFragment;
import com.github.huymaster.campusexpensemanager.fragment.bottomsheet.BudgetEditBottomSheetFragment;
import com.github.huymaster.campusexpensemanager.viewmodel.UserViewModel;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.textview.MaterialTextView;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class BudgetFragment extends BaseFragment {
	@Inject
	DatabaseCore core;

	@Inject
	UserViewModel viewModel;
	private UserDAO dao;
	private BudgetFragmentBinding binding;

	private ItemTouchHelper itemTouchHelper;

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		binding = BudgetFragmentBinding.inflate(inflater, container, false);
		return binding.getRoot();
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		dao = core.getDAO(UserDAO.class);
		initComponents();
		initListeners();
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		binding = null;
	}

	private void initComponents() {
		binding.budgetList.setLayoutManager(new LinearLayoutManager(getContext()));
		initLegends();
		updateBudgets();
	}

	private void initLegends() {
		Resources.Theme theme = binding.getRoot().getContext().getTheme();
		TypedValue colorPrimary = new TypedValue();
		TypedValue colorAccent = new TypedValue();
		TypedValue colorTertiary = new TypedValue();

		theme.resolveAttribute(androidx.appcompat.R.attr.colorPrimary, colorPrimary, true);
		theme.resolveAttribute(androidx.appcompat.R.attr.colorAccent, colorAccent, true);
		theme.resolveAttribute(com.google.android.material.R.attr.colorTertiary, colorTertiary, true);

		binding.budgetPast.setBackgroundColor(colorAccent.data);
		binding.budgetCurrent.setBackgroundColor(colorPrimary.data);
		binding.budgetFuture.setBackgroundColor(colorTertiary.data);
	}

	private void initListeners() {
		binding.budgetAddButton.setOnClickListener(v -> showAddBudgetDialog());
	}

	private void updateBudgets() {
		BudgetAdapter adapter = new BudgetAdapter(
				dao.getBudgets(viewModel.getLoggedInUsername()),
				dao.getExpenses(viewModel.getLoggedInUsername()),
				this::showEditBudgetDialog,
				this::showRemoveBudgetDialog
		);
		binding.budgetList.setAdapter(adapter);

		if (itemTouchHelper != null) itemTouchHelper.attachToRecyclerView(null);
		itemTouchHelper = new ItemTouchHelper(new SimpleItemTouchHelper(adapter));
		itemTouchHelper.attachToRecyclerView(binding.budgetList);
	}

	private void showAddBudgetDialog() {
		BudgetAddBottomSheetFragment fragment = new BudgetAddBottomSheetFragment(budget -> {
			dao.addBudget(viewModel.getLoggedInUsername(), budget);
			updateBudgets();
		});
		fragment.show(getParentFragmentManager(), "BudgetAddBottomSheetFragment");
	}

	private void showEditBudgetDialog(Budget budget) {
		if (budget == null) return;
		BudgetEditBottomSheetFragment fragment = new BudgetEditBottomSheetFragment(budget, budget1 -> {
			dao.updateBudget(viewModel.getLoggedInUsername(), budget.getId(), b -> {
				b.setName(budget1.getName());
				b.setAmount(budget1.getAmount());
			});
			updateBudgets();
		});
		fragment.show(getParentFragmentManager(), "BudgetEditBottomSheetFragment");
		updateBudgets();
	}

	private void showRemoveBudgetDialog(Budget budget) {
		MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireActivity());
		builder.setTitle("Delete budget?");
		builder.setMessage(getString(R.string.dialog_delete_budget, budget.getName()));
		builder.setPositiveButton(R.string.dialog_yes, (dialog, which) -> {
			dao.removeBudget(viewModel.getLoggedInUsername(), budget.getId());
			updateBudgets();
		});
		builder.setNegativeButton(R.string.dialog_no, (dialog, which) -> dialog.dismiss());
		builder.show();
		updateBudgets();
	}
}

class BudgetAdapter extends RecyclerView.Adapter<BudgetVH> implements ItemTouchHelperAdapter {
	private final Calendar calendar = Calendar.getInstance();
	private final List<Budget> budgets;
	private final List<Expense> expenses;
	private final Consumer<Budget> editCallback, removeCallback;

	public BudgetAdapter(List<Budget> budgets, List<Expense> expenses, Consumer<Budget> editCallback, Consumer<Budget> removeCallback) {
		this.budgets = budgets;
		budgets.sort((b1, b2) -> b2.getStartDate().compareTo(b1.getStartDate()));
		this.expenses = expenses;
		this.editCallback = editCallback;
		this.removeCallback = removeCallback;
	}

	@NonNull
	@Override
	public BudgetVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.budget_view_holder, parent, false);
		return new BudgetVH(view);
	}

	@Override
	public void onBindViewHolder(@NonNull BudgetVH holder, int position) {
		Resources resources = holder.itemView.getResources();
		Resources.Theme theme = holder.itemView.getContext().getTheme();
		DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
		Budget budget = budgets.get(position);
		int progress = calculateProgress(budget);

		holder.budgetName.setText(budget.getName());
		holder.budgetAmount.setText(resources.getString(R.string.budget_card_total, budget.getAmount(), "VND"));
		holder.budgetSpent.setText(resources.getString(R.string.budget_card_spent, calculateSpent(budget), "VND"));
		holder.budgetProgress.setProgress(progress, true);
		holder.budgetFrom.setText(resources.getString(R.string.budget_card_from, dateFormat.format(budget.getStartDate())));
		holder.budgetEnd.setText(resources.getString(R.string.budget_card_end, dateFormat.format(budget.getEndDate())));

		TypedValue colorPrimary = new TypedValue();
		TypedValue colorAccent = new TypedValue();
		TypedValue colorTertiary = new TypedValue();

		theme.resolveAttribute(androidx.appcompat.R.attr.colorPrimary, colorPrimary, true);
		theme.resolveAttribute(androidx.appcompat.R.attr.colorAccent, colorAccent, true);
		theme.resolveAttribute(com.google.android.material.R.attr.colorTertiary, colorTertiary, true);

		Calendar c = Calendar.getInstance();
		if (c.getTimeInMillis() > budget.getEndDate().getTime())
			holder.budgetColor.setBackgroundColor(colorAccent.data);
		else if (c.getTimeInMillis() >= budget.getStartDate().getTime() && c.getTimeInMillis() < budget.getEndDate().getTime())
			holder.budgetColor.setBackgroundColor(colorPrimary.data);
		else
			holder.budgetColor.setBackgroundColor(colorTertiary.data);
	}

	private int calculateProgress(Budget budget) {
		var spent = calculateSpent(budget);
		var amount = budget.getAmount();
		return BigDecimal.valueOf(spent)
				.divide(BigDecimal.valueOf(amount), 2, RoundingMode.HALF_EVEN)
				.multiply(BigDecimal.valueOf(100))
				.intValue();
	}

	private double calculateSpent(Budget budget) {
		List<Expense> filtered = new LinkedList<>();
		expenses.stream().filter(e -> {
			long timestamp = e.getTimestamp();
			Date date = new Date(timestamp);
			return date.after(budget.getStartDate()) && date.before(budget.getEndDate());
		}).forEach(filtered::add);
		return filtered.stream().mapToDouble(Expense::getAmount).sum();
	}

	@Override
	public int getItemCount() {
		return budgets.size();
	}

	@Override
	public void onSwipeLeft(int position) {
		if (position < 0 || position >= budgets.size()) return;
		Budget budget = budgets.get(position);
		removeCallback.accept(budget);
	}

	@Override
	public void onSwipeRight(int position) {
		if (position < 0 || position >= budgets.size()) return;
		Budget budget = budgets.get(position);
		editCallback.accept(budget);
	}
}

class BudgetVH extends RecyclerView.ViewHolder {

	private final BudgetViewHolderBinding binding;

	public View budgetColor;
	public MaterialTextView budgetName;
	public MaterialTextView budgetSpent;
	public MaterialTextView budgetAmount;
	public LinearProgressIndicator budgetProgress;
	public MaterialTextView budgetFrom;
	public MaterialTextView budgetEnd;

	public BudgetVH(@NonNull View itemView) {
		super(itemView);
		binding = BudgetViewHolderBinding.bind(itemView);
		budgetColor = binding.budgetColor;
		budgetName = binding.budgetName;
		budgetSpent = binding.budgetSpent;
		budgetAmount = binding.budgetAmount;
		budgetProgress = binding.budgetProgress;
		budgetFrom = binding.budgetFrom;
		budgetEnd = binding.budgetEnd;
	}
}