package com.github.huymaster.campusexpensemanager.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import org.jetbrains.annotations.NotNull;

public abstract class DataSourceImpl extends SQLiteOpenHelper implements IDataSource {
    private static final int VERSION = 1;

    public DataSourceImpl(@NotNull Context context) {
        super(context, "database.db", null, VERSION);
    }

    public DataSourceImpl(@NotNull Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }
}
