package com.almusand.aaber.custom.categories;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.almusand.aaber.R;
import com.almusand.aaber.model.Categories;
import com.almusand.aaber.ui.main.MainCategoriesAdapter;
import com.google.android.material.checkbox.MaterialCheckBox;

import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;

/**
 * Developed by Anas Elshwaf
 * anaselshawaf357@gmail.com
 */
public class CategoriesDialog extends Dialog {

    private List<Categories> categoriesList;
    private List<Categories> selectedCategoriesList;
    private RecyclerView rvCategories;
    private Button btSave;
    private LinearLayoutManager layoutManager;
    private CategoriesAdapter categoriesAdapter;

    public OnCategorysaved onCategorysaved;

    public interface OnCategorysaved {
        void onCategorySaved(List<Categories> list);
    }

    public CategoriesDialog(@NonNull Context context, List<Categories> categoriesList, OnCategorysaved onCategorysaved) {
        super(context);
        this.categoriesList = categoriesList;
        this.onCategorysaved = onCategorysaved;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        this.setCancelable(true);
        this.setContentView(R.layout.layout_categories_dialog);
        getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

        initialViews();

        setUpRecyclerCategories();

        setUpCategories();

        btSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedCategoriesList.size() > 0) {
                    onCategorysaved.onCategorySaved(selectedCategoriesList);
                    dismiss();
                } else {
                    Toasty.info(getContext(), getContext().getResources().getString(R.string.msg_choose_category), Toasty.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void setUpCategories() {
        if (categoriesList != null && categoriesList.size() > 0) {
            categoriesAdapter.setList(categoriesList);
        }
    }

    private void setUpRecyclerCategories() {
        categoriesAdapter = new CategoriesAdapter(new CategoriesAdapter.onItemClick() {
            @Override
            public void onItemClick(Categories categories, int position, MaterialCheckBox CheckBox, String status) {
                if (status.equals("add")) {
                    selectedCategoriesList.add(categories);
                } else {
                    selectedCategoriesList.remove(categories);
                }

            }
        });
        rvCategories.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext());
        rvCategories.setLayoutManager(layoutManager);
        rvCategories.setAdapter(categoriesAdapter);

    }

    private void initialViews() {
        rvCategories = findViewById(R.id.rv_categories);
        btSave = findViewById(R.id.bt_save);
        selectedCategoriesList = new ArrayList<>();
    }

}
