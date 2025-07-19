package com.github.huymaster.campusexpensemanager.database.sqlite;

import android.content.Context;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;

import com.github.huymaster.campusexpensemanager.database.sqlite.dao.CredentialDAO;
import com.github.huymaster.campusexpensemanager.database.sqlite.helper.CredentialDbHelper;

public class DatabaseCore {
    private static final String TAG = "SQLiteDatabaseCore";
    private final Context context;

    public DatabaseCore(Context context) {
        this.context = context;
    }

    public CredentialDbHelper getCredentialDbHelper() {
        return new CredentialDbHelper(context);
    }

    public CredentialDAO getCredentialDAO(Lifecycle lifecycle) {
        return new CredentialDAO(getCredentialDbHelper(), lifecycle);
    }

    public CredentialDAO getCredentialDAO(LifecycleOwner owner) {
        return getCredentialDAO(owner.getLifecycle());
    }
}
