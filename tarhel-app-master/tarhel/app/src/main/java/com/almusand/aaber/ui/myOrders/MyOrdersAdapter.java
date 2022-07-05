package com.almusand.aaber.ui.myOrders;

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

import java.util.ArrayList;
import java.util.List;

/**
 * Developed by Anas Elshwaf
 * anaselshawaf357@gmail.com
 */
public class MyOrdersAdapter extends RecyclerView.Adapter<MyOrdersAdapter.StoresViewHolder> {

    private final onItemClick onItemClick;
    private Boolean isPending = true;
    private List<Order> ordersList = new ArrayList<>();
    private String lang;

    public interface onItemClick {
        void onItemClick(Order order, int i, ImageView view);

        void onItemClick(Order order, LinearLayout view);
    }

    public MyOrdersAdapter(onItemClick onItemClick) {
        this.onItemClick = onItemClick;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public void setList(List<Order> ordersList) {
        this.ordersList = ordersList;
        notifyDataSetChanged();
    }

    public void setPending(Boolean pending) {
        isPending = pending;
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

        holder.bind(order, position, onItemClick);

    }

    @Override
    public int getItemCount() {
        return ordersList.size();
    }

    public class StoresViewHolder extends RecyclerView.ViewHolder {

        private ImageView imgOrder;
        private TextView tvOrderTittle;
        private TextView tvCategoryName;
        private ImageView imgCancel;
        private LinearLayout layout;

        StoresViewHolder(@NonNull View itemView) {
            super(itemView);

            imgOrder = itemView.findViewById(R.id.img_order);
            tvOrderTittle = itemView.findViewById(R.id.tv_order_tittle);
            tvCategoryName = itemView.findViewById(R.id.tv_category_name);
            imgCancel = itemView.findViewById(R.id.img_cancel);
            layout = itemView.findViewById(R.id.layout);

            if (!isPending) {
                imgCancel.setVisibility(View.GONE);
            }

        }

        void bind(final Order order, int position, final onItemClick onItemClick) {

            tvOrderTittle.setText(new StringBuilder().append(itemView.getContext().getResources().getString(R.string.order_hash)).append(order.getOrderCode()).toString());

            if (order.getCategory() != null) {
                if (lang.equals("ar")) {
                    tvCategoryName.setText(order.getCategory().getName().getAr());
                } else {
                    tvCategoryName.setText(order.getCategory().getName().getEn());
                }

                if (order.getImage() != null) {
                    Glide.with(itemView.getContext()).load(order.getImage()).error(R.drawable.aaber_logo).into(imgOrder);
                } else if (order.getCategory().getImage() != null) {
                    Glide.with(itemView.getContext()).load(order.getCategory().getImage()).error(R.drawable.aaber_logo).into(imgOrder);
                }
            }

            imgCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClick.onItemClick(order, position, imgCancel);
                }
            });

            layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClick.onItemClick(order, layout);
                }
            });
        }


    }


}
