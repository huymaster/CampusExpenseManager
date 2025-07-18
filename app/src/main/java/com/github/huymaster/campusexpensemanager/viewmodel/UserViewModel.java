package com.github.huymaster.campusexpensemanager.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.github.huymaster.campusexpensemanager.MainApplication;
import com.github.huymaster.campusexpensemanager.database.dao.CredentialDAO;

public class UserViewModel extends ViewModel {
    private final MutableLiveData<String> loggedInUsername = new MutableLiveData<>(null);

    public LiveData<String> getLoggedInState() {
        return loggedInUsername;
    }

    public void login(String username, String password) {
        CredentialDAO dao = MainApplication.getDatabaseCore().getDAO(CredentialDAO.class);
        try {

        } finally {
            dao.close();
        }
    }
}
