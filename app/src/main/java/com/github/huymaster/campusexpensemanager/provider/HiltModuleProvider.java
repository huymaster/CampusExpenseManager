package com.github.huymaster.campusexpensemanager.provider;

import com.github.huymaster.campusexpensemanager.MainApplication;
import com.github.huymaster.campusexpensemanager.core.ApplicationPreferences;
import com.github.huymaster.campusexpensemanager.core.NotificationUtils;
import com.github.huymaster.campusexpensemanager.database.sqlite.helper.CredentialDbHelper;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;

@Module
@InstallIn(SingletonComponent.class)
public class HiltModuleProvider {

	@Provides
	public static MainApplication provideMainApplication() {
		return MainApplication.INSTANCE;
	}

	@Provides
	public static ApplicationPreferences provideApplicationPreferences(MainApplication application) {
		return new ApplicationPreferences(application);
	}

	@Provides
	public static NotificationUtils provideNotificationUtils(MainApplication application) {
		return new NotificationUtils(application);
	}

	@Provides
	public static com.github.huymaster.campusexpensemanager.database.realm.DatabaseCore provideDatabaseCore(MainApplication application) {
		return new com.github.huymaster.campusexpensemanager.database.realm.DatabaseCore(application);
	}

	@Provides
	public static com.github.huymaster.campusexpensemanager.database.sqlite.DatabaseCore provideSqliteDatabaseCore(MainApplication application) {
		return new com.github.huymaster.campusexpensemanager.database.sqlite.DatabaseCore(application);
	}

	@Provides
	public static CredentialDbHelper provideCredentialDbHelper(MainApplication application) {
		return new CredentialDbHelper(application);
	}
}
