package com.github.huymaster.campusexpensemanager.database.sqlite.type;

import android.provider.BaseColumns;
import android.util.Base64;
import android.util.Log;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Arrays;

public class Credential {
    private static final String TAG = "Credential";
    private String username;
    private byte[] passwordHash;

    public Credential(String username, String password) {
        this(username, hashPassword(password));
    }

    public Credential(String username, byte[] passwordHash) {
        this.username = username;
        this.passwordHash = passwordHash;
    }

    public static byte[] hashPassword(String password) {
        byte[] passwordBytes = password.getBytes(StandardCharsets.UTF_8);
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return digest.digest(passwordBytes);
        } catch (Exception e) {
            Log.w(TAG, "Error while hashing password. Returning Base64 encoded password", e);
            return Base64.encode(passwordBytes, Base64.DEFAULT);
        }
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public byte[] getPasswordHash() {
        return passwordHash;
    }

    public void changePassword(String password) {
        this.passwordHash = hashPassword(password);
    }

    public boolean checkPassword(String password) {
        return Arrays.equals(hashPassword(password), passwordHash);
    }

    public boolean checkPassword(byte[] passwordHash) {
        return Arrays.equals(passwordHash, this.passwordHash);
    }

    public static class CredentialEntry implements BaseColumns {
        public static final String TABLE_NAME = "credential";
        public static final String COLUMN_USERNAME = "username";
        public static final String COLUMN_PASSWORD = "password";
    }
}
