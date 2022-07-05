package com.almusand.aaber.ui.main;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.almusand.aaber.R;
import com.almusand.aaber.model.Categories;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

/**
 * Developed by Anas Elshwaf
 * anaselshawaf357@gmail.com
 */
public class MainCategoriesAdapter extends RecyclerView.Adapter<MainCategoriesAdapter.CategoriesViewHolder> {

    private final onItemClick onItemClick;
    private List<Categories> categoriesList = new ArrayList<>();
    private String lang;

    public interface onItemClick {
        void onItemClick(Categories categories, LinearLayout lyContainer);

    }

    public MainCategoriesAdapter(onItemClick onItemClick) {
        this.onItemClick = onItemClick;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public void setList(List<Categories> categoriesList) {
        this.categoriesList = categoriesList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CategoriesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_home_categories, parent, false);

        return new CategoriesViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoriesViewHolder holder, int position) {

        Categories categories = categoriesList.get(position);

        holder.bind(categories, onItemClick);

    }

    @Override
    public int getItemCount() {
        return categoriesList.size();
    }

    public class CategoriesViewHolder extends RecyclerView.ViewHolder {

        private ImageView imgCategoryImage;
        private TextView tvCategoryName;
        private LinearLayout lyContainer;

        CategoriesViewHolder(@NonNull View itemView) {
            super(itemView);

            imgCategoryImage = itemView.findViewById(R.id.img_category_image);
            tvCategoryName = itemView.findViewById(R.id.tv_category_name);
            lyContainer = itemView.findViewById(R.id.ly_container);


        }

        void bind(Categories categories, final onItemClick onItemClick) {

            Glide.with(itemView.getContext())
                    .load(categories.getImage())
                    .error(R.drawable.aaber_logo)
                    .into(imgCategoryImage);

            if (lang.equals("ar")) {
                tvCategoryName.setText(categories.getName().getAr());
            } else {
                tvCategoryName.setText(categories.getName().getEn());
            }

            lyContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClick.onItemClick(categories, lyContainer);
                }
            });
        }

    }


}
