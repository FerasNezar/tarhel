package com.almusand.aaber.ui.orderOffers;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.almusand.aaber.R;
import com.almusand.aaber.model.Offer;
import com.almusand.aaber.utils.LocaleManager;
import com.bumptech.glide.Glide;
import com.makeramen.roundedimageview.RoundedImageView;
import com.willy.ratingbar.ScaleRatingBar;

import java.util.ArrayList;
import java.util.List;

/**
 * Developed by Anas Elshwaf
 * anaselshawaf357@gmail.com
 */
public class OrderOffersAdapter extends RecyclerView.Adapter<OrderOffersAdapter.StoresViewHolder> {

    private final onItemClick onItemClick;
    private List<Offer> offerList = new ArrayList<>();

    public interface onItemClick {
        void onItemClick(Offer offer, int position, LinearLayout textView);
    }

    public OrderOffersAdapter(onItemClick onItemClick) {
        this.onItemClick = onItemClick;
    }

    public void setList(List<Offer> offerList) {
        this.offerList = offerList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public StoresViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_order_offer, parent, false);

        return new StoresViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull StoresViewHolder holder, int position) {

        Offer offer = offerList.get(position);

        holder.bind(offer, position, onItemClick);

    }

    @Override
    public int getItemCount() {
        return offerList.size();
    }

    public class StoresViewHolder extends RecyclerView.ViewHolder {

        private final String lang;
        private LinearLayout layout;
        private RoundedImageView imgOrder;
        private TextView tvProviderName;
        private TextView tvOfferPrice;
        private ScaleRatingBar rtProvider;
        private TextView tvOfferStatus;

        StoresViewHolder(@NonNull View itemView) {
            super(itemView);

            layout = itemView.findViewById(R.id.layout);
            imgOrder = itemView.findViewById(R.id.img_order);
            tvProviderName = itemView.findViewById(R.id.tv_provider_name);
            tvOfferPrice = itemView.findViewById(R.id.tv_offer_price);
            rtProvider = itemView.findViewById(R.id.rt_provider);
            tvOfferStatus = itemView.findViewById(R.id.tv_offer_status);

            lang = LocaleManager.getLocale(itemView.getContext().getResources()).getLanguage();


        }

        void bind(final Offer offer, int position, final onItemClick onItemClick) {

            tvProviderName.setText(offer.getOwnerOfferDetails().getUserName());
            tvOfferPrice.setText(new StringBuilder().append(offer.getPrice()).append(" ").append(itemView.getContext().getResources().getString(R.string.sar)).toString());

            switch (offer.getStatus()) {

                case "pending":
                    if (lang.equals("ar")){
                        tvOfferStatus.setText("معلق");
                    }else {
                        tvOfferStatus.setText(offer.getStatus());
                    }

                    break;

                case "delivered":
                    if (lang.equals("ar")){
                        tvOfferStatus.setText("تم التوصيل");
                    }else {
                        tvOfferStatus.setText(offer.getStatus());
                    }
                    break;
                case "confirmed":
                    if (lang.equals("ar")){
                        tvOfferStatus.setText("مؤكد");
                    }else {
                        tvOfferStatus.setText(offer.getStatus());
                    }

                    break;
            }


            Glide.with(itemView.getContext())
                    .load(offer.getOwnerOfferDetails().getAvatar())
                    .error(R.drawable.aaber_logo)
                    .into(imgOrder);

            if (offer.getOwnerOfferDetails().getAverageRating() != null)
                rtProvider.setRating((Float.parseFloat( offer.getOwnerOfferDetails().getAverageRating().toString())));

            layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClick.onItemClick(offer, position, layout);
                }
            });
        }


    }


}
