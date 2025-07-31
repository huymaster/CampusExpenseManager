package com.github.huymaster.campusexpensemanager.database.realm.dao;

import androidx.lifecycle.Lifecycle;

import com.github.huymaster.campusexpensemanager.database.realm.DatabaseCore;
import com.github.huymaster.campusexpensemanager.database.realm.type.Category;
import com.github.huymaster.campusexpensemanager.database.realm.type.User;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

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
		return getUser(username).getDefinedCategories();
	}

	public Category getCategory(String username, UUID categoryID) {
		return getCategories(username).stream().filter(c -> c.getId().equals(categoryID)).findFirst().orElse(null);
	}

	public void addCategory(String username, Category category) {
		List<Category> categories = getCategories(username);
		if (exists(username))
			if (categories.stream().noneMatch(c -> c.getName().equalsIgnoreCase(category.getName())))
				update(
						query -> query.equalTo("username", username),
						user -> user.getDefinedCategories().add(category)
				);
	}

	public void updateCategory(String username, UUID categoryID, Consumer<Category> updator) {
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
		List<Category> categories = getCategories(username);
		if (exists(username))
			if (categories.stream().anyMatch(c -> c.getId().equals(categoryID)))
				update(
						query -> query.equalTo("username", username),
						user -> user.getDefinedCategories().removeIf(c -> c.getId().equals(categoryID))
				);
	}
}
