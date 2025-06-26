package com.github.huymaster.campusexpensemanager.database;

import com.github.huymaster.campusexpensemanager.database.type.ContentType;

public abstract class DAO<T extends ContentType> {
    protected final TableProxy<T> proxy;

    protected DAO(TableProxy<T> proxy) {
        this.proxy = proxy;
    }
}
