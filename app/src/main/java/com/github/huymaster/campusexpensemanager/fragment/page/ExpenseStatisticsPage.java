package com.github.huymaster.campusexpensemanager.fragment.page;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.github.huymaster.campusexpensemanager.database.realm.DatabaseCore;
import com.github.huymaster.campusexpensemanager.database.realm.type.Category;
import com.github.huymaster.campusexpensemanager.database.realm.type.Expense;
import com.github.huymaster.campusexpensemanager.database.realm.type.User;
import com.github.huymaster.campusexpensemanager.databinding.ExpensesStatisticsBinding;
import com.github.huymaster.campusexpensemanager.fragment.BaseFragment;
import com.github.huymaster.campusexpensemanager.viewmodel.UserViewModel;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmQuery;

@AndroidEntryPoint
public class ExpenseStatisticsPage extends BaseFragment {
	private final ExecutorService executor = Executors.newSingleThreadExecutor();
	private final Handler handler = new Handler(Looper.getMainLooper());
	@Inject
	DatabaseCore core;
	private Realm realm;
	private RealmList<Expense> r;
	private ExpensesStatisticsBinding binding;

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		binding = ExpensesStatisticsBinding.inflate(inflater, container, false);
		return binding.getRoot();
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		realm = core.getRealm();
		RealmQuery<User> query = realm.where(User.class).equalTo("username", UserViewModel.INSTANCE.getLoggedInUsername());
		User user = query.findFirst();
		if (user == null) return;
		r = user.getExpenses();
		r.addChangeListener(this::updateUI);
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
		updateUI(r);
	}

	private void initListeners() {

	}

	private void updateUI(RealmList<Expense> e) {
		if (e == null) return;
		List<Expense> list = realm.copyFromRealm(e);
		executor.execute(() -> _updateUI(list));
	}

	private void _updateUI(List<Expense> es) {
		Map<Category, List<Expense>> categoried = es.stream()
				.filter(e -> e.getCategory() != null)
				.collect(Collectors.groupingBy(Expense::getCategory));

		List<Expense> nullCategory = es.stream().filter(e -> e.getCategory() == null).collect(LinkedList::new, LinkedList::add, LinkedList::addAll);
		categoried.put(null, nullCategory);

		Map<Category, List<Expense>> sorted = categoried.entrySet()
				.stream()
				.sorted((entry1, entry2) -> {
					var e1 = entry1.getValue().stream().mapToDouble(Expense::getAmount).sum();
					var e2 = entry2.getValue().stream().mapToDouble(Expense::getAmount).sum();
					return Double.compare(e2, e1);
				})
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2, LinkedHashMap::new));

		sorted.forEach((key, value) -> {
			Log.d("ExpenseStatisticsPage", "Category: " + (key == null ? "null" : key.getName()) + " Count: " + value.size() + " Total: " + value.stream().mapToDouble(Expense::getAmount).sum());
		});
		updateChart(sorted);
	}

	private void updateChart(Map<Category, List<Expense>> categoried) {
		double totalAmount = categoried.values().stream().mapToDouble(expenses -> expenses.stream().mapToDouble(Expense::getAmount).sum()).sum();
		var total = 1f;
		handler.post(() -> binding.donutProgress.setCap(total));
		for (Category category : categoried.keySet()) {
			var categoryGroup = categoried.get(category);
			if (categoryGroup == null) continue;
			var amount = categoryGroup.stream().mapToDouble(Expense::getAmount).sum();
			var percent = (float) (amount / totalAmount);
			handler.post(() -> binding.donutProgress.addAmount(category == null ? "<Other>" : category.getName(), percent, getRandomColor().toArgb()));
		}
	}

	private Color getRandomColor() {
		Random random = new Random();
		return Color.valueOf(random.nextFloat(), random.nextFloat(), random.nextFloat());
	}
}
