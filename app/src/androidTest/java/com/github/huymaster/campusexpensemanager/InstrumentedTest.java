package com.github.huymaster.campusexpensemanager;

import android.app.Instrumentation;
import android.content.Context;
import android.util.Log;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.github.huymaster.campusexpensemanager.database.realm.DatabaseCore;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class InstrumentedTest {
    private static final String TAG = "InstrumentedTest";

    private Context context;
    private DatabaseCore core;

    @Before
    public void setUp() {
        Instrumentation instrumentation = InstrumentationRegistry.getInstrumentation();

        context = instrumentation.getContext();
        Log.d(TAG, "Setting up InstrumentedTest: " + context.getPackageName());
        core = new DatabaseCore(context);
        Log.d(TAG, "DatabaseCore: " + core.getRealm().getPath());
    }

    @Test
    public void checkUserInfo() {

    }

    @After
    public void tearDown() {
        Log.d(TAG, "Tearing down InstrumentedTest: " + context.getPackageName());
    }
}