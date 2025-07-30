package com.github.huymaster.campusexpensemanager.database.realm.dao;

import androidx.lifecycle.Lifecycle;

import com.github.huymaster.campusexpensemanager.database.realm.DatabaseCore;
import com.github.huymaster.campusexpensemanager.database.realm.type.User;

import java.util.LinkedList;
import java.util.List;

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

}
