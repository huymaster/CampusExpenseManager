package com.github.huymaster.campusexpensemanager.database.sqlite.helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.NonNull;

import com.github.huymaster.campusexpensemanager.database.sqlite.type.Credential;

public class CredentialDbHelper extends SQLiteOpenHelper {
    private static final String TAG = "CredentialDbHelper";

    private static final String DATABASE_NAME = "credential.db";
    private static final int DATABASE_VERSION = 1;

    private static final String SQL_CREATE_TABLE = String.format(
            "CREATE TABLE IF NOT EXISTS %s (%s TEXT PRIMARY KEY, %s BLOB);",
            Credential.CredentialEntry.TABLE_NAME,
            Credential.CredentialEntry.COLUMN_USERNAME,
            Credential.CredentialEntry.COLUMN_PASSWORD
    );

    private static final String SQL_DELETE_TABLE = String.format(
            "DROP TABLE IF EXISTS %s;",
            Credential.CredentialEntry.TABLE_NAME
    );

    public CredentialDbHelper(@NonNull Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TABLE);
        db.setVersion(DATABASE_VERSION);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion >= newVersion) return;
        db.execSQL(SQL_DELETE_TABLE);
        onCreate(db);
    }
}
