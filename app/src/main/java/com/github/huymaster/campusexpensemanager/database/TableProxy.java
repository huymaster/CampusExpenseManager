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

    public long insert(T t) {
        return table.insert(database, t);
    }

    public long update(Predicate<T> selector, T t) {
        return table.update(database, selector, t);
    }

    public long delete(Predicate<T> selector) {
        return table.delete(database, selector);
    }

    @Override
    public void close() {
        if (database != null)
            database.close();
    }
}
