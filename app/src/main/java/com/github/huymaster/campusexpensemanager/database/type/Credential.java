package com.github.huymaster.campusexpensemanager.database.type;

import android.content.ContentValues;
import android.util.Log;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class Credential implements ContentType {
    public static final String TABLE_NAME = "credential";
    public static final String COLUMN_USERNAME = "username";
    public static final String COLUMN_PASSWORD = "password";
    private static final String TAG = "Credential";
    public String username;
    public byte[] password;

    public Credential() {
    }

    public Credential(String username, byte[] password) {
        this();
        this.username = username;
        this.password = password;
    }

    public Credential(String username, String password) {
        this(username, hash(password));
    }

    public static byte[] hash(String password) {
        String[] digests = {"SHA-512", "SHA-256", "SHA-1", "MD5"};
        MessageDigest md;
        for (String digest : digests) {
            try {
                md = MessageDigest.getInstance(digest);
                Log.d(TAG, "Hashing password with " + digest);
                return md.digest(password.getBytes());
            } catch (NoSuchAlgorithmException e) {
                Log.w(TAG, "No such algorithm: " + digest, e);
            }
        }
        return password.getBytes();
    }

    public static boolean check(byte[] hash, String password) {
        return Arrays.equals(hash, hash(password));
    }

    @Override
    public ContentValues toContentValues() {
        ContentValues values = new ContentValues();
        values.put(COLUMN_USERNAME, username);
        values.put(COLUMN_PASSWORD, password);
        return values;
    }
}
