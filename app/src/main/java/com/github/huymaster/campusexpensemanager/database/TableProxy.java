package com.github.huymaster.campusexpensemanager.database;

import android.database.sqlite.SQLiteDatabase;

import com.github.huymaster.campusexpensemanager.database.type.ContentType;

import java.util.function.Predicate;
import java.util.stream.Stream;

public class TableProxy<T extends ContentType> implements AutoCloseable {
    protected final Table<T> table;
    private final SQLiteDatabase database;

    public TableProxy(SQLiteDatabase database, Table<T> table) {
        this.database = database;
        this.table = table;
    }

    public Stream<T> getAll() {
        return table.getAll(database);
    }

    public void insert(T t) {
        table.insert(database, t);
    }

    public void update(Predicate<T> selector, T t) {
        table.update(database, selector, t);
    }

    public void delete(Predicate<T> selector) {
        table.delete(database, selector);
    }

    @Override
    public void close() {
        if (database != null)
            database.close();
    }
}
