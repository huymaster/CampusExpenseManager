package com.github.huymaster.campusexpensemanager;

import android.app.Application;

import com.google.android.material.color.DynamicColors;

import dagger.hilt.android.HiltAndroidApp;

@HiltAndroidApp
public class MainApplication extends Application {
	private static final String TAG = "MainApplication";
	public static MainApplication INSTANCE;
	private final boolean isDebug;

	public MainApplication() {
		super();
		INSTANCE = this;
		isDebug = BuildConfig.DEBUG;
	}

	public static boolean isDebug() {
		return INSTANCE.isDebug;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		DynamicColors.applyToActivitiesIfAvailable(this);
	}
}