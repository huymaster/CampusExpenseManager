package com.github.huymaster.campusexpensemanager.database.type;

import android.util.Log;

import androidx.annotation.NonNull;

import java.security.MessageDigest;
import java.util.Arrays;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

public class Credential extends RealmObject {
    private static final String TAG = "Credential";
    @PrimaryKey
    @Required
    private String username;

    @Required
    private byte[] password;

    public Credential() {
    }

    public Credential(String username, String password) {
        this(username, hash(password));
    }

    public Credential(String username, byte[] password) {
        this.username = username;
        this.password = password;
    }

    public static byte[] hash(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            return md.digest(password.getBytes());
        } catch (Exception e) {
            Log.w(TAG, "Failed to hash password. !!! Returning raw password !!!", e);
            return password.getBytes();
        }
    }

    public static boolean verify(@NonNull Credential credential, String password) {
        return verify(password, credential.password);
    }

    public static boolean verify(String password, byte[] hash) {
        return verify(hash(password), hash);
    }

    public static boolean verify(byte[] password, byte[] hash) {
        return Arrays.equals(password, hash);
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public byte[] getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = hash(password);
    }

    public void setPassword(byte[] password) {
        this.password = password;
    }
}
