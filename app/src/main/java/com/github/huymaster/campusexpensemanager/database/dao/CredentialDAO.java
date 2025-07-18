package com.github.huymaster.campusexpensemanager.database.dao;

import com.github.huymaster.campusexpensemanager.database.DatabaseCore;
import com.github.huymaster.campusexpensemanager.database.type.Credential;

public class CredentialDAO extends BaseDAO<Credential> {
    public CredentialDAO(DatabaseCore databaseCore) {
        super(databaseCore);
        getRealm();
        releaseRealm();
    }
}
