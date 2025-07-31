package com.github.huymaster.campusexpensemanager.fragment;

import android.os.Handler;
import android.os.Looper;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.github.huymaster.campusexpensemanager.MainActivity;
import com.github.huymaster.campusexpensemanager.R;

import java.util.function.Function;

public abstract class BaseFragment extends Fragment {
	protected String TAG = getClass().getSimpleName();

	protected MainActivity getMainActivity() {
		try {
			return (MainActivity) requireActivity();
		} catch (Exception e) {
			return null;
		}
	}

	protected void runOnMainThread(Runnable runnable) {
		Handler handler = new Handler(Looper.getMainLooper());
		handler.post(runnable);
	}

	protected void runOnUiThread(Runnable runnable) {
		getMainActivity().runOnUiThread(runnable);
	}

	protected NavController getNavController() {
		try {
			MainActivity activity = getMainActivity();
			if (activity == null) return null;
			return Navigation.findNavController(activity, R.id.main_fragment_container);
		} catch (Exception e) {
			return null;
		}
	}

	protected FragmentManager getMainFragmentManager() {
		try {
			MainActivity activity = getMainActivity();
			if (activity == null) return null;
			return activity.getSupportFragmentManager();
		} catch (Exception e) {
			return null;
		}
	}

	public boolean onBackPressed() {
		return false;
	}

	public <T> T useMainActivity(Function<MainActivity, T> function) {
		MainActivity activity = getMainActivity();
		if (activity == null) return null;
		return function.apply(activity);
	}
}