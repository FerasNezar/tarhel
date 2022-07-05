package com.almusand.aaber.custom.categories;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.almusand.aaber.R;
import com.almusand.aaber.model.Categories;
import com.almusand.aaber.utils.LocaleManager;
import com.google.android.material.checkbox.MaterialCheckBox;

import java.util.ArrayList;
import java.util.List;

/**
 * Developed by Anas Elshwaf
 * anaselshawaf357@gmail.com
 */
public class CategoriesAdapter extends RecyclerView.Adapter<CategoriesAdapter.CategoriesViewHolder> {

    private final onItemClick onItemClick;
    private List<Categories> categoriesList = new ArrayList<>();

    public interface onItemClick {
        void onItemClick(Categories categories, int position, MaterialCheckBox animCheckBox, String status);

    }

    public CategoriesAdapter(onItemClick onItemClick) {
        this.onItemClick = onItemClick;
    }

    public void setList(List<Categories> categoriesList) {
        this.categoriesList = categoriesList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CategoriesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_category, parent, false);

        return new CategoriesViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoriesViewHolder holder, int position) {

        Categories categories = categoriesList.get(position);

        holder.bind(categories, position, onItemClick);

    }

    @Override
    public int getItemCount() {
        return categoriesList.size();
    }

    public class CategoriesViewHolder extends RecyclerView.ViewHolder {

        private MaterialCheckBox chCategory;
        private String lang;

        CategoriesViewHolder(@NonNull View itemView) {
            super(itemView);

            chCategory = itemView.findViewById(R.id.ch_category);

            lang= LocaleManager.getLocale(itemView.getResources()).getLanguage();
        }

        void bind(Categories categories, int position, final onItemClick onItemClick) {

            if (lang.equals("ar")) {
                chCategory.setText(categories.getName().getAr());
            } else {
                chCategory.setText(categories.getName().getEn());
            }

            chCategory.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        onItemClick.onItemClick(categories, position, chCategory, "add");
                    } else {
                        onItemClick.onItemClick(categories, position, chCategory, "remove");
                    }
                }
            });

        }


    }


}
