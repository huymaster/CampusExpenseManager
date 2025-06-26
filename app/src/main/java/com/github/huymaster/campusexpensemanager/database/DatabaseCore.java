package com.github.huymaster.campusexpensemanager.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.github.huymaster.campusexpensemanager.database.table.CredentialTable;
import com.github.huymaster.campusexpensemanager.database.type.ContentType;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;
import java.util.List;

/**
 *
 */
public class DatabaseCore implements AutoCloseable {
    private static final String TAG = "DatabaseCore";
    private static final String DATABASE_NAME = "database.db";
    private static final int DATABASE_VERSION = 1;
    private final List<Class<? extends Table<? extends ContentType>>> tables = new LinkedList<>();
    private final SQLiteDatabase database;
    private boolean initialized = false;

    public DatabaseCore(@NotNull Context context) {
        this(context.openOrCreateDatabase(DATABASE_NAME, Context.MODE_PRIVATE, null));
    }

    public DatabaseCore(@NotNull SQLiteDatabase database) {
        this.database = database;
        prepareTable();
    }

    private void prepareTable() {
        tables.add(CredentialTable.class);

    }

    private <T extends Table<? extends ContentType>> T get(@NotNull Class<T> tableClass) {
        if (!tables.contains(tableClass))
            return null;
        try {
            Constructor<T> constructor = tableClass.getConstructor();
            constructor.setAccessible(true);
            return constructor.newInstance();
        } catch (NoSuchMethodException e) {
            Log.w(TAG, "Class " + tableClass.getName() + " does not have a default constructor", e);
        } catch (SecurityException e) {
            Log.w(TAG, "Can't access default constructor of class " + tableClass.getName(), e);
        } catch (InvocationTargetException e) {
            Log.w(TAG, "Error while instantiating class " + tableClass.getName(), e);
        } catch (IllegalAccessException e) {
            Log.w(TAG, "Can't access default constructor of class " + tableClass.getName() + ". Maybe it's private", e);
        } catch (InstantiationException e) {
            Log.w(TAG, tableClass.getName() + " is an abstract class. Can't instantiate it", e);
        }
        return null;
    }

    public <T extends ContentType> TableProxy<T> getProxy(@NotNull Class<? extends Table<T>> tableClass) {
        var table = get(tableClass);
        if (table == null) return null;
        return new TableProxy<>(database, table);
    }

    public SQLiteDatabase getDatabase() {
        return database;
    }

    public void init() {
        if (initialized) {
            Log.d(TAG, "Database already initialized.");
        } else {
            initialized = true;
            initialize();
            updateIfNeeded();
        }
    }

    @Override
    public void close() {
        if (database != null)
            database.close();
    }

    private void initialize() {
        for (var tableClass : tables) {
            if (tableClass == null)
                continue;
            Table<?> table = get(tableClass);
            if (table != null)
                table.create(database);
        }
    }

    private void updateIfNeeded() {
        if (database.getVersion() < DATABASE_VERSION) {
            update();
            database.setVersion(DATABASE_VERSION);
        }
    }

    private void update() {
        for (var tableClass : tables) {
            if (tableClass == null)
                continue;
            Table<?> table = get(tableClass);
            if (table != null)
                table.drop(database);
        }
        initialize();
    }
}
