package com.github.huymaster.campusexpensemanager.database;

import com.github.huymaster.campusexpensemanager.database.type.ContentType;

public abstract class DAO<T extends ContentType> {
    protected final TableProxy<? extends T> proxy;

    protected DAO(TableProxy<? extends T> proxy) {
        this.proxy = proxy;
    }
}
