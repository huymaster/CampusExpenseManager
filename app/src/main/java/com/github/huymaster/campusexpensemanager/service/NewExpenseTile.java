package com.github.huymaster.campusexpensemanager.service;

import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;

import com.github.huymaster.campusexpensemanager.MainActivity;
import com.github.huymaster.campusexpensemanager.fragment.ExpensesFragment;

public class NewExpenseTile extends TileService {

	@Override
	public void onClick() {
		super.onClick();
		Intent i = new Intent(this, MainActivity.class);
		i.putExtra(ExpensesFragment.EXPENSES_ADD_BOTTOM_SHEET_FRAGMENT_TAG, true);
		PendingIntent pi = PendingIntent.getActivity(this, 0, i, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
		unlockAndRun(() -> {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
				startActivityAndCollapse(pi);
			else
				startActivity(i);
		});
	}

	@Override
	public void onStartListening() {
		super.onStartListening();
		Tile tile = getQsTile();
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
			tile.setSubtitle("Add new expense");
		tile.updateTile();
	}
}
