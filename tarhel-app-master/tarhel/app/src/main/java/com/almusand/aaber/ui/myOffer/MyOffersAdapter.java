package com.almusand.aaber.ui.myOffer;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.almusand.aaber.R;
import com.almusand.aaber.model.Offer;
import com.almusand.aaber.utils.LocaleManager;
import com.bumptech.glide.Glide;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;
import java.util.List;

/**
 * Developed by Anas Elshwaf
 * anaselshawaf357@gmail.com
 */
public class MyOffersAdapter extends RecyclerView.Adapter<MyOffersAdapter.StoresViewHolder> {

    private final onItemClick onItemClick;
    private List<Offer> offerList = new ArrayList<>();

    public interface onItemClick {
        void onItemClick(Offer offer, LinearLayout textView);

        void onItemCancel(Offer offer, int position, ImageView imageView);


    }

    public MyOffersAdapter(onItemClick onItemClick) {
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
                .inflate(R.layout.item_my_offer, parent, false);

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
        private TextView tvOfferTittle;
        private TextView tvOfferPrice;
        private TextView tvOfferStatus;
        private ImageView imgOptions;

        StoresViewHolder(@NonNull View itemView) {
            super(itemView);

            layout = itemView.findViewById(R.id.layout);
            imgOrder = itemView.findViewById(R.id.img_order);
            tvOfferTittle = itemView.findViewById(R.id.tv_offer_tittle);
            tvOfferPrice = itemView.findViewById(R.id.tv_offer_price);
            tvOfferStatus = itemView.findViewById(R.id.tv_offer_status);
            imgOptions = itemView.findViewById(R.id.img_options);

            lang = LocaleManager.getLocale(itemView.getContext().getResources()).getLanguage();
        }

        void bind(final Offer offer, int position, final onItemClick onItemClick) {

            tvOfferTittle.setText(new StringBuilder().append(itemView.getContext().getResources().getText(R.string.offer_hash)).append("").append(offer.getId()).toString());
            tvOfferPrice.setText(new StringBuilder().append(offer.getPrice()).append(" ").append(itemView.getContext().getResources().getString(R.string.sar)).toString());

            switch (offer.getStatus()){

                case "pending":
                    tvOfferStatus.setBackground(itemView.getContext().getResources().getDrawable(R.drawable.background_pending));
                    if (lang.equals("ar")){
                        tvOfferStatus.setText("معلق");
                    }else {
                        tvOfferStatus.setText(offer.getStatus());
                    }

                    break;

                case "cancelled":
                    tvOfferStatus.setBackground(itemView.getContext().getResources().getDrawable(R.drawable.background_button));
                    imgOptions.setVisibility(View.INVISIBLE);
                    if (lang.equals("ar")){
                        tvOfferStatus.setText("ملغى");
                    }else {
                        tvOfferStatus.setText(offer.getStatus());
                    }
                    imgOptions.setVisibility(View.GONE);
                    break;

                case "confirmed":
                    tvOfferStatus.setBackground(itemView.getContext().getResources().getDrawable(R.drawable.background_confirmed));
                    if (lang.equals("ar")){
                        tvOfferStatus.setText("مقبول");
                    }else {
                        tvOfferStatus.setText(offer.getStatus());
                    }
                    break;

            }

            if (offer.getOrderDetails().getStatus().equals("paid")){
                imgOptions.setVisibility(View.GONE);
            }

            Glide.with(itemView.getContext())
                    .load(R.drawable.aaber_logo).into(imgOrder);

            imgOptions.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClick.onItemCancel(offer, position, imgOptions);
                }
            });

            layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onItemClick.onItemClick(offer, layout);
                }
            });

        }


    }


}
