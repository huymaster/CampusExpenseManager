package com.github.huymaster.campusexpensemanager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class EntryActivity extends AppCompatActivity {

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(new View(this));
	}

	@Override
	protected void onResume() {
		super.onResume();
		startActivity(new Intent(EntryActivity.this, MainActivity.class));
		finish();
	}
}
