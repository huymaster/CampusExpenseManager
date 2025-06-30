package com.github.huymaster.campusexpensemanager.database.dao;

import com.github.huymaster.campusexpensemanager.database.DAO;
import com.github.huymaster.campusexpensemanager.database.TableProxy;
import com.github.huymaster.campusexpensemanager.database.type.Credential;

import java.util.Arrays;

public class CredentialDAO extends DAO<Credential> {
    public CredentialDAO(TableProxy<Credential> proxy) {
        super(proxy);
    }

    public boolean insert(String username, String password) {
        if (checkUsername(username)) return false;
        if (password.length() == 0) return false;
        Credential credential = new Credential(username, password);
        return proxy.insert(credential) > 0;
    }

    public boolean changePassword(String username, String oldPassword, String newPassword) {
        if (checkUsername(username) && checkPassword(username, oldPassword)) {
            if (newPassword.length() == 0) return false;
            Credential credential = new Credential(username, newPassword);
            return proxy.update(c -> c.username.equals(username), credential) > 0;
        }
        return false;
    }

    public boolean checkUsername(String username) {
        return proxy.getAll().anyMatch(c -> c.username.equals(username));
    }

    public boolean checkPassword(String username, String password) {
        return checkPassword(username, Credential.hash(password));
    }

    public boolean checkPassword(String username, byte[] password) {
        return proxy.getAll().anyMatch(c -> c.username.equals(username) && Arrays.equals(c.password, password));
    }
}
