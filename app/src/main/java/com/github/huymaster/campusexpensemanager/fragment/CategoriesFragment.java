package com.github.huymaster.campusexpensemanager.fragment;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.huymaster.campusexpensemanager.R;
import com.github.huymaster.campusexpensemanager.core.ItemTouchHelperAdapter;
import com.github.huymaster.campusexpensemanager.core.ResourceFunctions;
import com.github.huymaster.campusexpensemanager.database.realm.DatabaseCore;
import com.github.huymaster.campusexpensemanager.database.realm.dao.UserDAO;
import com.github.huymaster.campusexpensemanager.database.realm.type.Category;
import com.github.huymaster.campusexpensemanager.databinding.CategoriesFragmentBinding;
import com.github.huymaster.campusexpensemanager.databinding.CategoryViewHolderBinding;
import com.github.huymaster.campusexpensemanager.fragment.bottomsheet.CategoryAddBottomSheetFragment;
import com.github.huymaster.campusexpensemanager.fragment.bottomsheet.CategoryEditBottomSheetFragment;
import com.github.huymaster.campusexpensemanager.viewmodel.UserViewModel;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.List;
import java.util.function.Consumer;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class CategoriesFragment extends BaseFragment {
	@Inject
	UserViewModel viewModel;
	@Inject
	DatabaseCore databaseCore;
	private CategoriesFragmentBinding binding;
	private UserDAO dao;
	private ItemTouchHelper itemTouchHelper = null;

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		binding = CategoriesFragmentBinding.inflate(inflater, container, false);
		dao = databaseCore.getDAO(UserDAO.class);
		return binding.getRoot();
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		initComponents();
		initListeners();
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	private void initComponents() {
		updateCategories();
		binding.categoriesList.setLayoutManager(new LinearLayoutManager(requireContext()));
	}

	private void initListeners() {
		binding.categoriesAddButton.setOnClickListener(v -> {
			CategoryAddBottomSheetFragment fragment = new CategoryAddBottomSheetFragment(
					category -> {
						dao.addCategory(viewModel.getLoggedInUsername(), category);
						updateCategories();
					}
			);
			fragment.show(getParentFragmentManager(), "CategoryAddBottomSheetFragment");
		});
	}

	private void updateCategories() {
		List<Category> categories = dao.getCategories(viewModel.getLoggedInUsername());
		CategoryAdapter adapter = new CategoryAdapter(categories, this::editCategory, this::removeCategory);
		binding.categoriesList.setAdapter(adapter);

		if (itemTouchHelper != null) itemTouchHelper.attachToRecyclerView(null);
		itemTouchHelper = new ItemTouchHelper(new CategoryTouchHelper(adapter));
		itemTouchHelper.attachToRecyclerView(binding.categoriesList);
	}

	private void editCategory(@NonNull Category category) {
		CategoryEditBottomSheetFragment fragment = new CategoryEditBottomSheetFragment(category, category2 -> {
			if (category2 != null)
				dao.updateCategory(viewModel.getLoggedInUsername(), category.getId(), cat -> {
					cat.setName(category2.getName());
					cat.setDescription(category2.getDescription());
				});
			updateCategories();
		});
		fragment.show(getParentFragmentManager(), "CategoryEditBottomSheetFragment");
	}

	private void removeCategory(@NonNull Category category) {
		MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireContext());
		builder.setTitle("Delete category?");
		builder.setMessage(getString(R.string.dialog_delete_category, category.getName()));
		builder.setPositiveButton(R.string.dialog_yes, (dialog, which) ->
		{
			dao.removeCategory(viewModel.getLoggedInUsername(), category.getId());
			updateCategories();
		});
		builder.setNegativeButton(R.string.dialog_no, (dialog, which) -> {
			dialog.dismiss();
			updateCategories();
		});
		builder.setOnCancelListener(dialog -> updateCategories());
		builder.show();
	}
}

class CategoryAdapter extends RecyclerView.Adapter<CategoryVH> implements ItemTouchHelperAdapter {
	private final Handler handler = new Handler();
	private final List<Category> categories;
	private final Consumer<Category> editCallback;
	private final Consumer<Category> removeCallback;

	protected CategoryAdapter(List<Category> categories, @NonNull Consumer<Category> editCallback, @NonNull Consumer<Category> removeCallback) {
		this.categories = categories;
		this.editCallback = editCallback;
		this.removeCallback = removeCallback;
	}

	@NonNull
	@Override
	public CategoryVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_view_holder, parent, false);
		return new CategoryVH(view);
	}

	@Override
	public void onBindViewHolder(@NonNull CategoryVH holder, int position) {
		Category category = categories.get(position);
		holder.categoryViewHolderName.setText(category.getName() == null ? "<no name>" : category.getName());
		holder.categoryViewHolderDescription.setText(category.getDescription() == null ? "<no description>" : category.getDescription());
	}

	@Override
	public int getItemCount() {
		return categories.size();
	}

	@Override
	public void onSwipeRight(int position) {
		if (position < 0 || position >= categories.size()) return;
		Category category = categories.get(position);
		editCallback.accept(category);
	}

	@Override
	public void onSwipeLeft(int position) {
		if (position < 0 || position >= categories.size()) return;
		Category category = categories.get(position);
		removeCallback.accept(category);
	}

	private void onItemHold(int position) {
		if (position < 0 || position >= categories.size()) return;
		Category category = categories.get(position);
		editCallback.accept(category);
	}
}

class CategoryVH extends RecyclerView.ViewHolder {
	private final CategoryViewHolderBinding binding;
	public MaterialCardView categoryViewHolderCard;
	public TextView categoryViewHolderName;
	public TextView categoryViewHolderDescription;

	public CategoryVH(@NonNull View itemView) {
		super(itemView);
		binding = CategoryViewHolderBinding.bind(itemView);
		categoryViewHolderCard = binding.categoryViewHolderCard;
		categoryViewHolderName = binding.categoryViewHolderName;
		categoryViewHolderDescription = binding.categoryViewHolderDescription;
	}
}

class CategoryTouchHelper extends ItemTouchHelper.SimpleCallback {
	private static final int dragDirs = 0;
	private static final int swipeDirs = ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;

	private final ItemTouchHelperAdapter adapter;

	public CategoryTouchHelper(ItemTouchHelperAdapter adapter) {
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
		if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE)
			if (dX < 0) {
				TypedValue colorError = new TypedValue();
				TypedValue colorOnError = new TypedValue();
				Resources.Theme theme = recyclerView.getContext().getTheme();
				theme.resolveAttribute(androidx.appcompat.R.attr.colorError, colorError, true);
				theme.resolveAttribute(com.google.android.material.R.attr.colorOnError, colorOnError, true);

				View view = viewHolder.itemView;
				float left = view.getRight() + dX;
				float right = view.getRight();
				float top = view.getTop();
				float bottom = view.getBottom();


				Paint p = new Paint();
				p.setStyle(Paint.Style.FILL);
				p.setColor(colorError.data);
				c.drawRect(left, top, right, bottom, p);
				p.setColor(colorOnError.data);
				p.setAntiAlias(true);

				int iconSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, recyclerView.getResources().getDisplayMetrics());
				int paddingRight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, recyclerView.getResources().getDisplayMetrics());
				int postionTop = (int) (top) + (view.getHeight() - iconSize) / 2;
				int postionLeft = view.getWidth() - (iconSize + paddingRight);

				Drawable icon = ResourceFunctions.getDrawable(R.drawable.ic_delete);
				icon.setBounds(postionLeft, postionTop, postionLeft + iconSize, postionTop + iconSize);
				icon.draw(c);
			} else {
				TypedValue colorPrimary = new TypedValue();
				TypedValue colorOnPrimary = new TypedValue();
				Resources.Theme theme = recyclerView.getContext().getTheme();
				theme.resolveAttribute(androidx.appcompat.R.attr.colorPrimary, colorPrimary, true);
				theme.resolveAttribute(com.google.android.material.R.attr.colorOnPrimary, colorOnPrimary, true);

				View view = viewHolder.itemView;
				float left = view.getLeft() + dX;
				float right = view.getLeft();
				float top = view.getTop();
				float bottom = view.getBottom();

				Paint p = new Paint();
				p.setStyle(Paint.Style.FILL);
				p.setColor(colorPrimary.data);
				c.drawRect(left, top, right, bottom, p);
				p.setColor(colorOnPrimary.data);
				p.setAntiAlias(true);

				int iconSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, recyclerView.getResources().getDisplayMetrics());
				int paddingLeft = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, recyclerView.getResources().getDisplayMetrics());
				int postionTop = (int) (top) + (view.getHeight() - iconSize) / 2;

				Drawable icon = ResourceFunctions.getDrawable(R.drawable.ic_edit);
				icon.setBounds(paddingLeft, postionTop, paddingLeft + iconSize, postionTop + iconSize);
				icon.draw(c);
			}
		super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
	}
}