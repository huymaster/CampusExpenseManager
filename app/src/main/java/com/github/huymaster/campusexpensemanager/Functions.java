package com.github.huymaster.campusexpensemanager;

import android.view.View;

import androidx.core.graphics.Insets;
import androidx.core.view.OnApplyWindowInsetsListener;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewbinding.ViewBinding;

public class Functions {
	public static void setupInsets(ViewBinding binding) {
		View root = binding.getRoot();
		OnApplyWindowInsetsListener listener = (v, insets) -> {
			Insets systemBar = insets.getInsets(WindowInsetsCompat.Type.systemBars());
			Insets ime = insets.getInsets(WindowInsetsCompat.Type.ime());
			root.setPadding(systemBar.left, systemBar.top + ime.top, systemBar.right, systemBar.bottom + ime.bottom);
			return insets;
		};
		ViewCompat.setOnApplyWindowInsetsListener(root, listener);
	}

	public static void sleep(long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException ignore) {
		}
	}
}