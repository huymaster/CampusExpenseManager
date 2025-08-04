package com.github.huymaster.campusexpensemanager.core;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.util.TypedValue;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.github.huymaster.campusexpensemanager.R;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class SimpleItemTouchHelper extends ItemTouchHelper.SimpleCallback {
	private static final int dragDirs = 0;
	private static final int swipeDirs = ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;

	private final ItemTouchHelperAdapter adapter;

	public SimpleItemTouchHelper(ItemTouchHelperAdapter adapter) {
		super(dragDirs, swipeDirs);
		this.adapter = adapter;
	}

	@Override
	public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
		return false;
	}

	@Override
	public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
		if (direction == ItemTouchHelper.LEFT)
			adapter.onSwipeLeft(viewHolder.getAbsoluteAdapterPosition());
		else if (direction == ItemTouchHelper.RIGHT)
			adapter.onSwipeRight(viewHolder.getAbsoluteAdapterPosition());
	}

	@Override
	public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
		Resources.Theme theme = recyclerView.getContext().getTheme();
		TypedValue colorError = new TypedValue();
		TypedValue colorOnError = new TypedValue();
		TypedValue colorPrimary = new TypedValue();
		TypedValue colorOnPrimary = new TypedValue();
		theme.resolveAttribute(androidx.appcompat.R.attr.colorError, colorError, true);
		theme.resolveAttribute(com.google.android.material.R.attr.colorOnError, colorOnError, true);
		theme.resolveAttribute(androidx.appcompat.R.attr.colorPrimary, colorPrimary, true);
		theme.resolveAttribute(com.google.android.material.R.attr.colorOnPrimary, colorOnPrimary, true);

		RecyclerViewSwipeDecorator.Builder builder = new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
		if (dX > 0)
			builder.addSwipeRightActionIcon(R.drawable.ic_edit)
					.addBackgroundColor(colorPrimary.data)
					.setActionIconTint(colorOnPrimary.data);
		else if (dX < 0)
			builder.addSwipeLeftActionIcon(R.drawable.ic_delete)
					.addBackgroundColor(colorError.data)
					.setActionIconTint(colorOnError.data);
		if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE)
			builder.create().decorate();
		super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
	}

	@Override
	public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
		super.clearView(recyclerView, viewHolder);
	}
}