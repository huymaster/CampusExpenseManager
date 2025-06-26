package com.github.huymaster.campusexpensemanager.database.dao;

import com.github.huymaster.campusexpensemanager.database.DAO;
import com.github.huymaster.campusexpensemanager.database.TableProxy;
import com.github.huymaster.campusexpensemanager.database.type.Credential;

public class CredentialDAO extends DAO<Credential> {
    protected CredentialDAO(TableProxy<Credential> proxy) {
        super(proxy);
    }
}
