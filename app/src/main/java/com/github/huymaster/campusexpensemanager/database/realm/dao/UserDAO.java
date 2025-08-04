package com.github.huymaster.campusexpensemanager.database.realm.dao;

import androidx.lifecycle.Lifecycle;

import com.github.huymaster.campusexpensemanager.database.realm.DatabaseCore;
import com.github.huymaster.campusexpensemanager.database.realm.type.Budget;
import com.github.huymaster.campusexpensemanager.database.realm.type.Category;
import com.github.huymaster.campusexpensemanager.database.realm.type.Expense;
import com.github.huymaster.campusexpensemanager.database.realm.type.User;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class UserDAO extends BaseDAO<User> {
	protected UserDAO(DatabaseCore databaseCore, Lifecycle lifecycle) {
		super(databaseCore, lifecycle);
	}

	@Override
	public Class<User> getType() {
		return User.class;
	}

	public List<User> getUsers() {
		return getAll(LinkedList::new);
	}

	public void addUser(String username) {
		create(u -> {
		}, username);
	}

	public User getUser(String username) {
		return get(query -> query.equalTo("username", username));
	}

	public void removeUser(String username) {
		delete(query -> query.equalTo("username", username));
	}

	public boolean exists(String username) {
		return getUser(username) != null;
	}

	public List<Category> getCategories(String username) {
		User user = getUser(username);
		if (user == null) return new LinkedList<>();
		return user.getDefinedCategories();
	}

	public Category getCategory(String username, UUID categoryID) {
		return getCategories(username).stream().filter(c -> c.getId().equals(categoryID)).findFirst().orElse(null);
	}

	public void addCategory(String username, Category category) {
		if (category == null) return;
		List<Category> categories = getCategories(username);
		if (exists(username))
			if (categories.stream().noneMatch(c -> c.getName().equalsIgnoreCase(category.getName())))
				update(
						query -> query.equalTo("username", username),
						user -> user.getDefinedCategories().add(category)
				);
	}

	public void updateCategory(String username, UUID categoryID, Consumer<Category> updator) {
		if (categoryID == null || updator == null) return;
		List<Category> categories = getCategories(username);
		if (exists(username))
			if (categories.stream().anyMatch(c -> c.getId().equals(categoryID)))
				update(
						query -> query.equalTo("username", username),
						user -> user.getDefinedCategories().forEach(c -> {
							if (c.getId().equals(categoryID))
								updator.accept(c);
						})
				);
	}

	public void removeCategory(String username, UUID categoryID) {
		if (categoryID == null) return;
		List<Category> categories = getCategories(username);
		if (exists(username))
			if (categories.stream().anyMatch(c -> c.getId().equals(categoryID)))
				update(
						query -> query.equalTo("username", username),
						user -> user.getDefinedCategories().removeIf(c -> c.getId().equals(categoryID))
				);
	}

	public List<Expense> getExpenses(String username) {
		User user = getUser(username);
		if (user == null) return new LinkedList<>();
		return user.getExpenses();
	}

	public List<Expense> getExpensesFiltered(String username, Predicate<Expense> filter) {
		if (filter == null) return getExpenses(username);
		List<Expense> expenses = getExpenses(username);
		List<Expense> filtered = new LinkedList<>();
		expenses.stream().filter(filter).forEach(filtered::add);
		return filtered;
	}

	public List<Expense> getExpensesSorted(String username, Comparator<Expense> comparator) {
		if (comparator == null) return getExpenses(username);
		List<Expense> expenses = getExpenses(username);
		expenses.sort(comparator);
		return expenses;
	}

	public List<Expense> getExpensesFilteredSorted(String username, Predicate<Expense> filter, Comparator<Expense> comparator) {
		if (filter == null || comparator == null) return getExpenses(username);
		List<Expense> expenses = getExpensesFiltered(username, filter);
		expenses.sort(comparator);
		return expenses;
	}

	public Expense getExpense(String username, UUID expenseID) {
		if (expenseID == null) return null;
		return getExpenses(username).stream().filter(e -> e.getId().equals(expenseID)).findFirst().orElse(null);
	}

	public void addExpense(String username, Expense expense) {
		if (expense == null) return;
		update(
				query -> query.equalTo("username", username),
				user -> user.getExpenses().add(expense)
		);
	}

	public void updateExpense(String username, UUID expenseID, Consumer<Expense> updator) {
		if (expenseID == null || updator == null) return;
		List<Expense> expenses = getExpenses(username);
		if (exists(username))
			if (expenses.stream().anyMatch(e -> e.getId().equals(expenseID)))
				update(
						query -> query.equalTo("username", username),
						user -> user.getExpenses().forEach(e -> {
							if (e.getId().equals(expenseID))
								updator.accept(e);
						})
				);
	}

	public void removeExpense(String username, UUID expenseID) {
		if (expenseID == null) return;
		List<Expense> expenses = getExpenses(username);
		if (exists(username))
			if (expenses.stream().anyMatch(e -> e.getId().equals(expenseID)))
				update(
						query -> query.equalTo("username", username),
						user -> user.getExpenses().removeIf(e -> e.getId().equals(expenseID))
				);
	}

	public List<Budget> getBudgets(String username) {
		User user = getUser(username);
		if (user == null) return new LinkedList<>();
		return user.getBudgets();
	}

	public void addBudget(String username, Budget budget) {
		if (budget == null) return;
		update(
				query -> query.equalTo("username", username),
				user -> {
					if (user.getBudgets().stream().noneMatch(b -> b.getName().equalsIgnoreCase(budget.getName())))
						if (user.getBudgets().stream().noneMatch(b -> budget.getStartDate().after(b.getStartDate()) && budget.getStartDate().before(b.getEndDate())))
							user.getBudgets().add(budget);
				}
		);
	}

	public void updateBudget(String username, UUID budgetID, Consumer<Budget> updator) {
		if (budgetID == null || updator == null) return;
		List<Budget> budgets = getBudgets(username);
		if (exists(username))
			if (budgets.stream().anyMatch(b -> b.getId().equals(budgetID)))
				update(
						query -> query.equalTo("username", username),
						user -> user.getBudgets().forEach(b -> {
							if (b.getId().equals(budgetID))
								updator.accept(b);
						})
				);
	}

	public void removeBudget(String username, UUID budgetID) {
		if (budgetID == null) return;
		List<Budget> budgets = getBudgets(username);
		if (exists(username))
			if (budgets.stream().anyMatch(b -> b.getId().equals(budgetID)))
				update(
						query -> query.equalTo("username", username),
						user -> user.getBudgets().removeIf(b -> b.getId().equals(budgetID))
				);
	}
}
