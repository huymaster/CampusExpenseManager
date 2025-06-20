package com.github.huymaster.campusexpensemanager.type;

import android.util.Log;

import androidx.annotation.NonNull;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

public class Credential {
    private static final String TAG = "Credential";
    public final String id;
    public String username;
    public byte[] password;

    public Credential(String username, byte[] password) {
        this.id = UUID.randomUUID().toString();
        this.username = username;
        this.password = password;
    }

    public Credential(String id, String username, byte[] password) {
        this.id = id;
        this.username = username;
        this.password = password;
    }

    public static byte[] hashPassword(@NonNull String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            return md.digest(password.getBytes());
        } catch (NoSuchAlgorithmException e) {
            Log.w(TAG, "Failed to hash password", e);
            return new byte[0];
        }
    }
}
