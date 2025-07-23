package com.github.huymaster.campusexpensemanager.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.Objects;

public class UserViewModel extends ViewModel {
    public static UserViewModel INSTANCE = new UserViewModel();
    private final MutableLiveData<String> loggedInUsername = new MutableLiveData<>(null);

    private UserViewModel() {
    }

    public LiveData<String> getLoggedInState() {
        return loggedInUsername;
    }

    public void login(String username) {
        if (!Objects.equals(loggedInUsername.getValue(), username))
            loggedInUsername.setValue(username);
    }

    public void logout() {
        if (loggedInUsername.getValue() != null)
            loggedInUsername.setValue(null);
    }
}
