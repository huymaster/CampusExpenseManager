package com.github.huymaster.campusexpensemanager.fragment.page;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.util.Pair;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.huymaster.campusexpensemanager.R;
import com.github.huymaster.campusexpensemanager.database.realm.DatabaseCore;
import com.github.huymaster.campusexpensemanager.database.realm.dao.UserDAO;
import com.github.huymaster.campusexpensemanager.database.realm.type.Expense;
import com.github.huymaster.campusexpensemanager.database.realm.type.User;
import com.github.huymaster.campusexpensemanager.databinding.ExpensesHistoryBinding;
import com.github.huymaster.campusexpensemanager.fragment.BaseFragment;
import com.github.huymaster.campusexpensemanager.fragment.ExpenseVH;
import com.github.huymaster.campusexpensemanager.fragment.SimpleVH;
import com.github.huymaster.campusexpensemanager.viewmodel.UserViewModel;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmQuery;

@AndroidEntryPoint
public class ExpenseHistoryPage extends BaseFragment {
	private final ExecutorService executor = Executors.newSingleThreadExecutor();
	private final Handler handler = new Handler(Looper.getMainLooper());
	private final Map<Integer, UUID> ids = new HashMap<>();
	private final ExpensesHistoryAdapter adapter = new ExpensesHistoryAdapter(this::registerContextMenu);
	@Inject
	DatabaseCore core;
	private ExpensesHistoryBinding binding;
	private UserDAO dao;
	private Realm realm;
	private RealmList<Expense> r;

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		binding = ExpensesHistoryBinding.inflate(inflater, container, false);
		return binding.getRoot();
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		dao = core.getDAO(UserDAO.class);
		realm = core.getRealm();
		RealmQuery<User> query = realm.where(User.class).equalTo("username", UserViewModel.INSTANCE.getLoggedInUsername());
		User user = query.findFirst();
		if (user == null) return;
		r = user.getExpenses();
		r.addChangeListener(r -> updateUI(realm.copyFromRealm(r)));

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
		binding.expensesHistoryList.setLayoutManager(new LinearLayoutManager(requireContext()));
		binding.expensesHistoryList.setHasFixedSize(true);
		binding.expensesHistoryList.setAdapter(adapter);
		updateUI(dao.getExpenses(UserViewModel.INSTANCE.getLoggedInUsername()));
	}

	private void initListeners() {

	}

	private void registerContextMenu(Pair<View, UUID> consumer) {
		View view = consumer.first;
		UUID id = consumer.second;
		if (!(view instanceof MaterialCardView) || id == null) return;
		view.setId(id.hashCode());
		if (!ids.containsKey(view.getId()))
			ids.put(view.getId(), id);
		registerForContextMenu(view);
		view.setOnLongClickListener(v -> view.showContextMenu());
	}

	@Override
	public void onCreateContextMenu(@NonNull ContextMenu menu, @NonNull View v, @Nullable ContextMenu.ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.add(0, v.getId(), 0, "Edit");
		menu.add(0, v.getId(), 0, "Delete");
	}

	@Override
	public boolean onContextItemSelected(@NonNull MenuItem item) {
		if (item.getTitle() == null) return false;
		if (item.getTitle().equals("Edit")) {
			editExpense(ids.get(item.getItemId()));
		} else if (item.getTitle().equals("Delete")) {
			confirmDelete(() -> dao.removeExpense(UserViewModel.INSTANCE.getLoggedInUsername(), ids.get(item.getItemId())));
		}
		return super.onContextItemSelected(item);
	}

	private void editExpense(UUID id) {
		MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireActivity());
		builder.setTitle("Edit expense?");
		builder.setMessage("Funtion not implemented yet.");
		builder.setPositiveButton(R.string.dialog_yes, (dialog, which) -> dialog.dismiss());
		builder.show();
	}

	private void confirmDelete(Runnable callback) {
		MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireActivity());
		builder.setTitle("Delete expense?");
		builder.setMessage(getString(R.string.dialog_delete_expense));
		builder.setPositiveButton(R.string.dialog_yes, (dialog, which) -> callback.run());
		builder.setNegativeButton(R.string.dialog_no, (dialog, which) -> dialog.dismiss());
		builder.show();
	}

	private void updateUI(List<Expense> e) {
		executor.execute(() -> {
			e.sort((e1, e2) -> Long.compare(e2.getTimestamp(), e1.getTimestamp()));
			Log.d("ExpenseHistoryPage", "updateUI: " + e.stream().map(Expense::getTimestamp).collect(LinkedList::new, LinkedList::add, LinkedList::addAll));
			DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
			List<Object> objects = new LinkedList<>();
			Calendar cal = Calendar.getInstance();
			String lastDate = "";
			for (Expense expense : e) {
				cal.setTimeInMillis(expense.getTimestamp());
				String date = dateFormat.format(cal.getTime());
				if (!date.equals(lastDate)) {
					objects.add(date);
					lastDate = date;
				}
				objects.add(expense);
			}

			handler.post(() -> adapter.setData(objects));
		});
	}
}

class ExpensesHistoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

	private static final Integer VIEW_TYPE_DATE = 0;
	private static final Integer VIEW_TYPE_EXPENSE = 1;
	private final DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy", Locale.getDefault());
	private final List<Object> objects = new LinkedList<>();
	private final Consumer<Pair<View, UUID>> contextMenuRequester;

	public ExpensesHistoryAdapter(Consumer<Pair<View, UUID>> contextMenuRequester) {
		this.contextMenuRequester = contextMenuRequester;
	}

	public void setData(@NonNull List<Object> expenses) {
		DiffUtil.Callback callback = new ExpensesDiffCallback(this.objects, expenses);
		DiffUtil.DiffResult result = DiffUtil.calculateDiff(callback);

		this.objects.clear();
		this.objects.addAll(expenses);

		result.dispatchUpdatesTo(this);
	}

	@Override
	public int getItemViewType(int position) {
		if (position < 0 || position >= objects.size()) return -1;
		Object object = objects.get(position);
		if (object instanceof String) return VIEW_TYPE_DATE;
		if (object instanceof Expense) return VIEW_TYPE_EXPENSE;
		return -1;
	}

	@NonNull
	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		View view;
		if (viewType == VIEW_TYPE_DATE) {
			view = LayoutInflater.from(parent.getContext()).inflate(R.layout.simple_view_holder, parent, false);
			return new SimpleVH(view);
		} else if (viewType == VIEW_TYPE_EXPENSE) {
			view = LayoutInflater.from(parent.getContext()).inflate(R.layout.expense_view_holder, parent, false);
			return new ExpenseVH(view);
		} else {
			view = LayoutInflater.from(parent.getContext()).inflate(R.layout.simple_view_holder, parent, false);
		}
		return new SimpleVH(view);
	}

	@Override
	public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
		if (position < 0 || position >= objects.size()) return;
		Object object = objects.get(position);
		if (object instanceof String && holder instanceof SimpleVH) {
			bindDate((SimpleVH) holder, (String) object);
		} else if (object instanceof Expense && holder instanceof ExpenseVH)
			bindExpense((ExpenseVH) holder, (Expense) object);
		else if (holder instanceof SimpleVH)
			bindDate((SimpleVH) holder, null);
	}

	private void bindDate(@NonNull SimpleVH holder, String date) {
		holder.simpleViewHolderName.setText(date == null ? "<unknown>" : date);
	}

	private void bindExpense(@NonNull ExpenseVH holder, @NonNull Expense expense) {
		contextMenuRequester.accept(new Pair<>(holder.expenseViewHolderCard, expense.getId()));
		holder.expenseViewHolderName.setText(expense.getName() == null ? "<no name>" : expense.getName());
		holder.expenseViewHolderAmount.setText(String.format(Locale.getDefault(), "%,.2f", expense.getAmount()));
		holder.expenseViewHolderDate.setText(dateFormat.format(new Date(expense.getTimestamp())));
		holder.expenseViewHolderCategory.setText(expense.getCategory() == null ? "<no category>" : expense.getCategory().getName());
	}

	@Override
	public int getItemCount() {
		return objects.size();
	}
}

class ExpensesDiffCallback extends DiffUtil.Callback {

	private final List<Object> oldList;
	private final List<Object> newList;

	public ExpensesDiffCallback(List<Object> oldList, List<Object> newList) {
		this.oldList = oldList;
		this.newList = newList;
	}

	@Override
	public int getOldListSize() {
		return oldList.size();
	}

	@Override
	public int getNewListSize() {
		return newList.size();
	}

	@Override
	public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
		Object oldItem = oldList.get(oldItemPosition);
		Object newItem = newList.get(newItemPosition);

		if (oldItem instanceof String && newItem instanceof String) {
			return oldItem.equals(newItem);
		}
		if (oldItem instanceof Expense && newItem instanceof Expense) {
			return ((Expense) oldItem).getId().equals(((Expense) newItem).getId());
		}
		return false;
	}

	@Override
	public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
		Object oldItem = oldList.get(oldItemPosition);
		Object newItem = newList.get(newItemPosition);

		if (oldItem instanceof String && newItem instanceof String) {
			return oldItem.equals(newItem);
		}
		if (oldItem instanceof Expense oldExpense && newItem instanceof Expense newExpense) {
			return oldExpense.equals(newExpense);
		}
		return false;
	}
}