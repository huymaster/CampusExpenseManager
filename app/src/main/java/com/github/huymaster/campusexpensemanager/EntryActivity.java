package com.github.huymaster.campusexpensemanager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;

public class EntryActivity extends AppCompatActivity {

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		SplashScreen.installSplashScreen(this);
		super.onCreate(savedInstanceState);
		Animation fadeOut = new AlphaAnimation(1f, 0f);
		fadeOut.setDuration(350);
		fadeOut.setAnimationListener(new Animation.AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
				startActivity(new Intent(EntryActivity.this, MainActivity.class));
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				finish();
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}
		});
		View view = new View(this);
		setContentView(view);
		view.startAnimation(fadeOut);
	}

	@Override
	protected void onResume() {
		super.onResume();
	}
}
