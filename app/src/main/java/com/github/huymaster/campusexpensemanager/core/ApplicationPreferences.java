package com.github.huymaster.campusexpensemanager.core;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.Nullable;

import com.github.huymaster.campusexpensemanager.MainApplication;

public class ApplicationPreferences {
	// Static preferences
	public static final BooleanPreference rememberLogin = new BooleanPreference("remember_login");
	public static final StringPreference rememberUsername = new StringPreference("remember_username");
	public static final StringPreference rememberPassword = new StringPreference("remember_password");
	public static final StringPreference loggedInUsername = new StringPreference("logged_in_username");
	private final MainApplication context;
	private final SharedPreferences preferences;

	public ApplicationPreferences(MainApplication context) {
		this.context = context;
		this.preferences = context.getSharedPreferences("config", Context.MODE_PRIVATE);
	}

	public <T> void set(Preference<T> key, @Nullable T value) {
		SharedPreferences.Editor editor = preferences.edit();
		key.set(editor, value);
		editor.apply();
	}

	@Nullable
	public <T> T get(Preference<T> key) {
		return key.get(preferences);
	}

	public <T> T get(Preference<T> key, T defaultValue) {
		T value = key.get(preferences);
		return value != null ? value : defaultValue;
	}

	public abstract static class Preference<T> {
		protected final String key;

		protected Preference(String key) {
			this.key = key;
		}


		public abstract void set(SharedPreferences.Editor editor, @Nullable T value);

		public abstract T get(SharedPreferences preferences);
	}

	public static class StringPreference extends Preference<String> {
		public StringPreference(String key) {
			super(key);
		}


		@Override
		public void set(SharedPreferences.Editor editor, @Nullable String value) {
			if (value == null) {
				editor.remove(key);
			} else {
				editor.putString(key, value);
			}
		}

		@Override
		@Nullable
		public String get(SharedPreferences preferences) {
			return preferences.getString(key, null);
		}
	}

	public static class BooleanPreference extends Preference<Boolean> {
		public BooleanPreference(String key) {
			super(key);
		}

		@Override
		public void set(SharedPreferences.Editor editor, @Nullable Boolean value) {
			if (value == null) {
				editor.remove(key);
			} else {
				editor.putBoolean(key, value);
			}
		}

		@Override
		@Nullable
		public Boolean get(SharedPreferences preferences) {
			if (!preferences.contains(key)) {
				return null;
			}
			return preferences.getBoolean(key, false);
		}
	}

	public static class IntPreference extends Preference<Integer> {
		public IntPreference(String key) {
			super(key);
		}


		@Override
		public void set(SharedPreferences.Editor editor, @Nullable Integer value) {
			if (value == null) {
				editor.remove(key);
			} else {
				editor.putInt(key, value);
			}
		}


		@Override
		public Integer get(SharedPreferences preferences) {
			return preferences.getInt(key, 0);
		}
	}

	public static class LongPreference extends Preference<Long> {
		public LongPreference(String key) {
			super(key);
		}


		@Override
		public void set(SharedPreferences.Editor editor, @Nullable Long value) {
			if (value == null) {
				editor.remove(key);
			} else {
				editor.putLong(key, value);
			}
		}


		@Override
		@Nullable
		public Long get(SharedPreferences preferences) {
			if (!preferences.contains(key)) {
				return null;
			}
			return preferences.getLong(key, 0L);
		}
	}

	public static class FloatPreference extends Preference<Float> {
		public FloatPreference(String key) {
			super(key);
		}


		@Override
		public void set(SharedPreferences.Editor editor, @Nullable Float value) {
			if (value == null) {
				editor.remove(key);
			} else {
				editor.putFloat(key, value);
			}
		}


		@Override
		@Nullable
		public Float get(SharedPreferences preferences) {
			if (!preferences.contains(key)) {
				return null;
			}
			return preferences.getFloat(key, 0f);
		}
	}
}
