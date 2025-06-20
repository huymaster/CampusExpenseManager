package com.github.huymaster.campusexpensemanager.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import org.jetbrains.annotations.NotNull;

public class LocalDataSource extends DataSourceImpl {

    public LocalDataSource(@NotNull Context context) {
        super(context);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
