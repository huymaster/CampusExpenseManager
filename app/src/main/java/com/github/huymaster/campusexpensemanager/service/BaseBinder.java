package com.github.huymaster.campusexpensemanager.service;

import android.app.Service;
import android.os.Binder;

public abstract class BaseBinder<T extends Service> extends Binder {
    public abstract T getService();
}
