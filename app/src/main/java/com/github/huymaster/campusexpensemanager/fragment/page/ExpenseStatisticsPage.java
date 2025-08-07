package com.github.huymaster.campusexpensemanager.fragment.page;

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
import com.github.huymaster.campusexpensemanager.databinding.ExpensesStatisticsBinding;
import com.github.huymaster.campusexpensemanager.fragment.BaseFragment;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import io.realm.Realm;
import io.realm.RealmResults;

@AndroidEntryPoint
public class ExpenseStatisticsPage extends BaseFragment {
	private final ExecutorService executor = Executors.newSingleThreadExecutor();
	private final Handler handler = new Handler(Looper.getMainLooper());
	@Inject
	DatabaseCore core;
	private Realm realm;
	private RealmResults<Expense> r;
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
		r = realm.where(Expense.class).findAll();
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

	private void updateUI(RealmResults<Expense> e) {
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
				.sorted((entry1, entry2) -> entry2.getValue().size() - entry1.getValue().size())
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2, LinkedHashMap::new));

		sorted.forEach((key, value) -> {
			Log.d("ExpenseStatisticsPage", "Category: " + key);
			Log.d("ExpenseStatisticsPage", "Count: " + value.size());
		});
	}
}
