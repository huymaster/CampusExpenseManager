package com.github.huymaster.campusexpensemanager.database.realm.type;

import androidx.annotation.NonNull;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

public class User extends RealmObject {
    @Required
    @PrimaryKey
    private String username;

    private UserInfo info = new UserInfo();

    private RealmList<Category> definedCategories = new RealmList<>();

    private RealmList<Budget> budgets = new RealmList<>();

    private RealmList<Expense> expenses = new RealmList<>();

    public User() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public UserInfo getInfo() {
        return info;
    }

    public void setInfo(UserInfo info) {
        this.info = info;
    }

    public RealmList<Category> getDefinedCategories() {
        return definedCategories;
    }

    public void setDefinedCategories(RealmList<Category> definedCategories) {
        this.definedCategories = definedCategories;
    }

    public RealmList<Budget> getBudgets() {
        return budgets;
    }

    public void setBudgets(RealmList<Budget> budgets) {
        this.budgets = budgets;
    }

    public RealmList<Expense> getExpenses() {
        return expenses;
    }

    public void setExpenses(RealmList<Expense> expenses) {
        this.expenses = expenses;
    }

    @NonNull
    @Override
    public String toString() {
        return "User[" + username + "]";
    }
}
