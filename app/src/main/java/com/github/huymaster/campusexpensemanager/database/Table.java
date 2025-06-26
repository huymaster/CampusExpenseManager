package com.github.huymaster.campusexpensemanager.database;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.github.huymaster.campusexpensemanager.type.ContentType;

import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

public abstract class Table<T extends ContentType> {
    protected Table() {
    }

    public abstract String getName();

    public abstract void create(@NotNull SQLiteDatabase database);

    public abstract void drop(@NotNull SQLiteDatabase database);

    protected abstract T get(@NotNull Cursor cursor);

    public Stream<T> getAll(@NotNull SQLiteDatabase database) {
        List<T> list = new LinkedList<>();
        try (Cursor cursor = database.query(getName(), null, null, null, null, null, null)) {
            if (cursor.moveToFirst()) {
                do {
                    T t = get(cursor);
                    list.add(t);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.w(getClass().getSimpleName(), e.getMessage(), e);
        }
        return list.stream();
    }

    public abstract void insert(@NotNull SQLiteDatabase database, @NotNull T t);

    public abstract void update(@NotNull SQLiteDatabase database, @NotNull Predicate<T> selector, @NotNull T t);

    public abstract void delete(@NotNull SQLiteDatabase database, @NotNull Predicate<T> selector);

    protected boolean exists(@NotNull SQLiteDatabase database, @NotNull Predicate<T> predicate) {
        return getAll(database).anyMatch(predicate);
    }
}
