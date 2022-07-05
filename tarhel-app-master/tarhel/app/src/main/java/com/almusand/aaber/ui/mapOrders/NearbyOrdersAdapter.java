package com.almusand.aaber.ui.mapOrders;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.almusand.aaber.R;
import com.almusand.aaber.model.Order;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;
import java.util.List;

/**
 * Developed by Anas Elshwaf
 * anaselshawaf357@gmail.com
 */
public class NearbyOrdersAdapter extends RecyclerView.Adapter<NearbyOrdersAdapter.StoresViewHolder> {

    private final onItemClick onItemClick;
    private List<Order> ordersList = new ArrayList<>();
    private String lang;

    public interface onItemClick {
        void onItemClick(Order order, LinearLayout layout);

    }

    public NearbyOrdersAdapter(onItemClick onItemClick) {
        this.onItemClick = onItemClick;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public void setList(List<Order> ordersList) {
        this.ordersList = ordersList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public StoresViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_my_order, parent, false);

        return new StoresViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull StoresViewHolder holder, int position) {

        Order order = ordersList.get(position);

        holder.bind(order, onItemClick);

    }

    @Override
    public int getItemCount() {
        return ordersList.size();
    }

    public class StoresViewHolder extends RecyclerView.ViewHolder {

        private LinearLayout layout;
        private RoundedImageView imgOrder;
        private TextView tvOrderTittle;
        private TextView tvCategoryName;
        private ImageView imgCancel;


        StoresViewHolder(@NonNull View itemView) {
            super(itemView);

            layout = itemView.findViewById(R.id.layout);
            imgOrder = itemView.findViewById(R.id.img_order);
            tvOrderTittle = itemView.findViewById(R.id.tv_order_tittle);
            tvCategoryName = itemView.findViewById(R.id.tv_category_name);
            imgCancel = itemView.findViewById(R.id.img_cancel);

            imgCancel.setVisibility(View.GONE);

        }

        void bind(final Order order, final onItemClick onItemClick) {

            tvOrderTittle.setText(order.getNote());

            if (lang.equals("ar")) {
                tvCategoryName.setText(order.getCategory().getName().getAr());
            } else {
                tvCategoryName.setText(order.getCategory().getName().getEn());
            }

            if (order.getImage() != null) {
                Glide.with(itemView.getContext())
                        .load(order.getImage())
                        .apply(new RequestOptions()).fitCenter().error(R.drawable.aaber_logo)
                        .into(imgOrder);
            } else {
                Glide.with(itemView.getContext())
                        .load(R.drawable.aaber_logo)
                        .into(imgOrder);
            }


            layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClick.onItemClick(order, layout);
                }
            });
        }


    }


}
