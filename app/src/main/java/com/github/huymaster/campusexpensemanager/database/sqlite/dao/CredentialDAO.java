package com.github.huymaster.campusexpensemanager.database.sqlite.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import androidx.annotation.NonNull;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;

import com.github.huymaster.campusexpensemanager.MainApplication;
import com.github.huymaster.campusexpensemanager.database.sqlite.helper.CredentialDbHelper;
import com.github.huymaster.campusexpensemanager.database.sqlite.type.Credential;

import java.util.ArrayList;
import java.util.List;

public class CredentialDAO implements DefaultLifecycleObserver {
    private final CredentialDbHelper credentialDbHelper;

    public CredentialDAO(@NonNull LifecycleOwner owner) {
        this(owner.getLifecycle());
    }

    public CredentialDAO(@NonNull Lifecycle lifecycle) {
        this(MainApplication.getSqliteDatabaseCore().getCredentialDbHelper(), lifecycle);
    }

    public CredentialDAO(@NonNull CredentialDbHelper credentialDbHelper, @NonNull Lifecycle lifecycle) {
        this.credentialDbHelper = credentialDbHelper;
        lifecycle.addObserver(this);
    }

    @Override
    public void onDestroy(@NonNull LifecycleOwner owner) {
        DefaultLifecycleObserver.super.onDestroy(owner);
        owner.getLifecycle().removeObserver(this);
        credentialDbHelper.close();
    }

    public List<Credential> getAll() {
        SQLiteDatabase db = credentialDbHelper.getReadableDatabase();
        String[] columns = {Credential.CredentialEntry.COLUMN_USERNAME, Credential.CredentialEntry.COLUMN_PASSWORD};

        Cursor cursor = db.query(Credential.CredentialEntry.TABLE_NAME, columns, null, null, null, null, null);
        int usernameIndex = cursor.getColumnIndex(Credential.CredentialEntry.COLUMN_USERNAME);
        int passwordIndex = cursor.getColumnIndex(Credential.CredentialEntry.COLUMN_PASSWORD);

        List<Credential> credentials = new ArrayList<>();
        while (cursor.moveToNext() && usernameIndex != -1 && passwordIndex != -1) {
            String username = cursor.getString(usernameIndex);
            String password = cursor.getString(passwordIndex);
            credentials.add(new Credential(username, password));
        }

        cursor.close();
        db.close();
        return credentials;
    }

    public Credential get(String u) {
        String username = u.toLowerCase();
        String[] columns = {Credential.CredentialEntry.COLUMN_USERNAME, Credential.CredentialEntry.COLUMN_PASSWORD};
        String selection = Credential.CredentialEntry.COLUMN_USERNAME + " = ?";
        String[] selectionArgs = {username};

        SQLiteDatabase db = credentialDbHelper.getReadableDatabase();
        Cursor cursor = db.query(Credential.CredentialEntry.TABLE_NAME, columns, selection, selectionArgs, null, null, null);
        int passwordIndex = cursor.getColumnIndex(Credential.CredentialEntry.COLUMN_PASSWORD);

        if (cursor.moveToFirst() && passwordIndex != -1) {
            byte[] password = cursor.getBlob(passwordIndex);
            cursor.close();
            db.close();
            return new Credential(username, password);
        }

        cursor.close();
        db.close();
        return null;
    }

    public boolean insert(String u, String password) {
        String username = u.toLowerCase();
        if (exists(username)) return false;

        ContentValues values = new ContentValues();
        values.put(Credential.CredentialEntry.COLUMN_USERNAME, username);
        values.put(Credential.CredentialEntry.COLUMN_PASSWORD, Credential.hashPassword(password));

        SQLiteDatabase db = credentialDbHelper.getWritableDatabase();
        boolean insert = db.insert(Credential.CredentialEntry.TABLE_NAME, null, values) != -1;
        db.close();

        return insert;
    }

    public boolean updateCredential(String u, String oldPassword, String newPassword) {
        String username = u.toLowerCase();
        if (!exists(username)) return false;

        Credential credential = get(username);
        if (!credential.checkPassword(oldPassword)) return false;
        credential.changePassword(newPassword);

        String whereClause = Credential.CredentialEntry.COLUMN_USERNAME + " = ?";
        String[] whereArgs = {username};

        ContentValues values = new ContentValues();
        values.put(Credential.CredentialEntry.COLUMN_PASSWORD, credential.getPasswordHash());

        SQLiteDatabase database = credentialDbHelper.getWritableDatabase();
        boolean update = database.update(Credential.CredentialEntry.TABLE_NAME, values, whereClause, whereArgs) > 0;
        database.close();

        return update;
    }

    public boolean deleteCredential(String u) {
        String username = u.toLowerCase();
        if (!exists(username)) return false;

        String whereClause = Credential.CredentialEntry.COLUMN_USERNAME + " = ?";
        String[] whereArgs = {username};

        SQLiteDatabase database = credentialDbHelper.getWritableDatabase();
        boolean delete = database.delete(Credential.CredentialEntry.TABLE_NAME, whereClause, whereArgs) > 0;
        database.close();

        return delete;
    }

    public boolean exists(String u) {
        String username = u.toLowerCase();
        return get(username) != null;
    }
}
