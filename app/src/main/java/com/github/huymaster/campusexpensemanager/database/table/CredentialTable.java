package com.github.huymaster.campusexpensemanager.database.table;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import androidx.annotation.NonNull;

import com.github.huymaster.campusexpensemanager.database.Table;
import com.github.huymaster.campusexpensemanager.database.type.Credential;

import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Predicate;

public class CredentialTable extends Table<Credential> {
    @Override
    public String getName() {
        return Credential.TABLE_NAME;
    }

    @Override
    public void create(@NonNull SQLiteDatabase database) {
        String sql = String.format(
                "CREATE TABLE IF NOT EXISTS %s (%s TEXT PRIMARY KEY NOT NULL, %s BLOB NOT NULL);",
                Credential.TABLE_NAME,
                Credential.COLUMN_USERNAME,
                Credential.COLUMN_PASSWORD
        );
        database.execSQL(sql);
    }

    @Override
    public void drop(@NonNull SQLiteDatabase database) {
        String sql = String.format("DROP TABLE IF EXISTS %s;", Credential.TABLE_NAME);
        database.execSQL(sql);
    }

    @Override
    protected Credential get(@NonNull Cursor cursor) {
        int usernameIndex = cursor.getColumnIndex(Credential.COLUMN_USERNAME);
        int passwordIndex = cursor.getColumnIndex(Credential.COLUMN_PASSWORD);
        if (usernameIndex == -1 || passwordIndex == -1) return null;

        return new Credential(cursor.getString(usernameIndex), cursor.getBlob(passwordIndex));
    }

    @Override
    public long insert(@NonNull SQLiteDatabase database, @NonNull Credential credential) {
        if (exists(database, c -> c.username.equals(credential.username))) return 0;
        return database.insert(Credential.TABLE_NAME, null, credential.toContentValues());
    }

    @Override
    public long update(@NonNull SQLiteDatabase database, @NonNull Predicate<Credential> selector, @NonNull Credential credential) {
        if (!exists(database, selector)) return 0;
        return database.update(Credential.TABLE_NAME, credential.toContentValues(), Credential.COLUMN_USERNAME + " = ?", new String[]{credential.username});
    }

    @Override
    public long delete(@NonNull SQLiteDatabase database, @NonNull Predicate<Credential> selector) {
        if (!exists(database, selector)) return 0;
        var stream = getAll(database);
        var count = new AtomicLong(0L);
        stream.filter(selector)
                .map(c -> database.delete(Credential.TABLE_NAME, Credential.COLUMN_USERNAME + " = ?", new String[]{c.username}))
                .forEach(count::addAndGet);
        return count.get();
    }
}
