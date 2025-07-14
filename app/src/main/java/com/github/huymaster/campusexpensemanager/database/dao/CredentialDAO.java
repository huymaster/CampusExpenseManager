package com.github.huymaster.campusexpensemanager.database.dao;

import com.github.huymaster.campusexpensemanager.database.DatabaseCore;
import com.github.huymaster.campusexpensemanager.database.type.Credential;

import java.util.Collection;

import io.realm.Realm;

public class CredentialDAO extends BaseDAO<Credential> {
    public CredentialDAO(DatabaseCore core) {
        super(core);
    }

    @Override
    public Class<Credential> getObjectClass() {
        return Credential.class;
    }

    @Override
    protected void _getAll(Collection<Credential> collection) throws Exception {
        Realm realm = core.getRealm();
        collection.addAll(realm.where(Credential.class).findAll());
        realm.close();
    }

    @Override
    protected boolean _insert(Credential object) throws Exception {
        try (Realm realm = core.getRealm()) {
            realm.beginTransaction();
            realm.insertOrUpdate(object);
            realm.commitTransaction();
            return true;
        }
    }

    @Override
    protected boolean _update(Credential object) throws Exception {
        try (Realm realm = core.getRealm()) {
            realm.beginTransaction();
            realm.insertOrUpdate(object);
            realm.commitTransaction();
            return true;
        }
    }

    @Override
    protected boolean _delete(Credential object) throws Exception {
        try (Realm realm = core.getRealm()) {
            realm.beginTransaction();
            object.deleteFromRealm();
            realm.commitTransaction();
            return true;
        }
    }

    public boolean checkValid(String username, String password) {
        try (Realm realm = core.getRealm()) {
            Credential credential = realm.where(Credential.class).equalTo("username", username).findFirst();
            if (credential == null) return false;
            return Credential.verify(credential, password);
        }
    }

    public boolean add(String username, String password) {
        if (exists(username, Credential::getUsername)) return false;
        Credential credential = new Credential(username, password);
        return insert(credential);
    }

    public boolean remove(String username) {
        Credential credential = realm.where(Credential.class).equalTo("username", username).findFirst();
        if (credential == null) return false;
        return delete(credential);
    }
}
