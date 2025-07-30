package com.github.huymaster.campusexpensemanager;

import android.app.Instrumentation;
import android.content.Context;
import android.util.Log;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.github.huymaster.campusexpensemanager.database.realm.DatabaseCore;
import com.github.huymaster.campusexpensemanager.database.realm.DatabaseModules;
import com.github.huymaster.campusexpensemanager.database.realm.dao.UserDAO;
import com.github.huymaster.campusexpensemanager.database.realm.type.User;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.LinkedList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;

@RunWith(AndroidJUnit4.class)
public class InstrumentedTest {
    private static final String TAG = "InstrumentedTest";

    private Context context;
    private DatabaseCore core;

    @Before
    public void setUp() {
        Instrumentation instrumentation = InstrumentationRegistry.getInstrumentation();
        context = instrumentation.getTargetContext();

        Realm.init(context);
        RealmConfiguration testConfig = new RealmConfiguration.Builder()
                .modules(DatabaseModules.INSTANCE)
                .name("test.realm")
                .inMemory()
                .build();

        Log.d(TAG, "Setting up InstrumentedTest: " + context.getPackageName());
        core = new DatabaseCore(context, testConfig);
        Log.d(TAG, "DatabaseCore: " + core.getRealm().getPath());
        clearAll();
    }

    @Test
    public void checkUserInfo() {
        UserDAO dao = core.getDAO(UserDAO.class);

        for (int i = 'a'; i < 'z'; i++) {
            for (int j = '0'; j < '9'; j++) {
                dao.create(u -> {
                }, String.valueOf((char) i) + (char) j);
            }
        }
        List<User> users = dao.getAll(LinkedList::new);
        Log.d(TAG, "Users: " + users);
        Log.d(TAG, "Deleted user start with 'a': " + dao.delete(u -> u.getUsername().startsWith("a")));
        dao.getAll(() -> users);
        Log.d(TAG, "Users: " + users);
    }

    @After
    public void tearDown() {
        Log.d(TAG, "Tearing down InstrumentedTest: " + context.getPackageName());
        clearAll();
    }

    private void clearAll() {
        try (Realm realm = Realm.getDefaultInstance()) {
            realm.executeTransaction(r -> r.deleteAll());
        }
    }
}