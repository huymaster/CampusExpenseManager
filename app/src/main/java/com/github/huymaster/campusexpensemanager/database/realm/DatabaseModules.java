package com.github.huymaster.campusexpensemanager.database.realm;

import com.github.huymaster.campusexpensemanager.database.realm.type.Budget;
import com.github.huymaster.campusexpensemanager.database.realm.type.Category;
import com.github.huymaster.campusexpensemanager.database.realm.type.Expense;
import com.github.huymaster.campusexpensemanager.database.realm.type.User;
import com.github.huymaster.campusexpensemanager.database.realm.type.UserInfo;

import io.realm.annotations.RealmModule;

@RealmModule(classes = {User.class, UserInfo.class, Expense.class, Budget.class, Category.class})
public class DatabaseModules {
    public static final DatabaseModules INSTANCE = new DatabaseModules();

    private DatabaseModules() {
    }
}
