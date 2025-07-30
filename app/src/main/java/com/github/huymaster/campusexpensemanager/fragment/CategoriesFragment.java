package com.github.huymaster.campusexpensemanager.fragment;

import android.os.Bundle;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.github.huymaster.campusexpensemanager.MainApplication;
import com.github.huymaster.campusexpensemanager.R;
import com.github.huymaster.campusexpensemanager.database.realm.dao.UserDAO;
import com.github.huymaster.campusexpensemanager.database.realm.type.Category;
import com.github.huymaster.campusexpensemanager.databinding.CategoriesFragmentBinding;
import com.github.huymaster.campusexpensemanager.databinding.CategoryViewHolderBinding;
import com.github.huymaster.campusexpensemanager.viewmodel.UserViewModel;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class CategoriesFragment extends BaseFragment {
    @Inject
    UserViewModel viewModel;
    private CategoriesFragmentBinding binding;
    private UserDAO dao;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = CategoriesFragmentBinding.inflate(inflater, container, false);
        dao = MainApplication.getRealmDatabaseCore().getDAO(UserDAO.class);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initComponents();
        initListeners();
    }

    private void initComponents() {
        updateCategories();
    }

    private void initListeners() {
        binding.categoriesButtonAdd.setOnClickListener(v -> {
            Editable name = binding.categoriesInputName.getText();
            if (name != null) {
                updateCategories();
            }
        });
    }

    private void updateCategories() {
    }
}

class CategoryAdapter extends RecyclerView.Adapter<CategoryVH> {
    private final List<Category> categories;

    public CategoryAdapter(List<Category> categories) {
        this.categories = categories;
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
        holder.binding.categoryViewHolderName.setText(category.getName() == null ? "<no name>" : category.getName());
        holder.binding.categoryViewHolderDescription.setText(category.getDescription() == null ? "<no description>" : category.getDescription());
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }
}

class CategoryVH extends RecyclerView.ViewHolder {
    public final CategoryViewHolderBinding binding;

    public CategoryVH(@NonNull View itemView) {
        super(itemView);
        binding = CategoryViewHolderBinding.bind(itemView);
    }
}