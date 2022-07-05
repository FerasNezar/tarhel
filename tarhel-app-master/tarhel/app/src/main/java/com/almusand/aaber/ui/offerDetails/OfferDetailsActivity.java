package com.almusand.aaber.ui.offerDetails;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.almusand.aaber.R;
import com.almusand.aaber.model.Offer;
import com.almusand.aaber.ui.base.BaseActivity;
import com.almusand.aaber.ui.chat.ChatActivity;
import com.almusand.aaber.ui.chat.MessagesActivity;
import com.almusand.aaber.utils.LocaleManager;
import com.almusand.aaber.utils.ProgressButton;
import com.bumptech.glide.Glide;
import com.makeramen.roundedimageview.RoundedImageView;
import com.willy.ratingbar.ScaleRatingBar;

public class OfferDetailsActivity extends BaseActivity implements View.OnClickListener {

    private TextView tvOfferHash;
    private ImageView imgBack;
    private RoundedImageView imgProvider;
    private ScaleRatingBar rtProvider;
    private TextView tvUsername;
    private TextView tvOfferPrice;
    private TextView tvNotes;
    private Button btConversation;
    private ProgressButton progressButton;
    private View btAcceptOffer;

    private Offer offer;
    private OfferDetailsViewModel viewModel;
    private int orderId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offer_details);

        viewModel = new ViewModelProvider(this).get(OfferDetailsViewModel.class);

        offer = (Offer) getIntent().getExtras().get("offer");
        if (offer.getOrderDetails() == null) {
            orderId = getIntent().getExtras().getInt("order_id");
        } else {
            orderId = offer.getOrderDetails().getId();
        }

        initViews();

        prepareData();

    }

    private void prepareData() {
        tvOfferHash.append(String.valueOf(offer.getId()));
        Glide.with(this)
                .load(offer.getOwnerOfferDetails()
                        .getAvatar()).centerCrop().error(R.drawable.aaber_logo)
                .into(imgProvider);

        tvUsername.setText(offer.getOwnerOfferDetails().getUserName());
        tvOfferPrice.setText(new StringBuilder().append(offer.getPrice()).append(" ").append(OfferDetailsActivity.this.getResources().getString(R.string.sar)).toString());
        tvNotes.setText(offer.getNote());

        if (offer.getOwnerOfferDetails().getAverageRating() != null)
            rtProvider.setRating((Float.parseFloat(offer.getOwnerOfferDetails().getAverageRating().toString())));

    }

    private void initViews() {
        tvOfferHash = findViewById(R.id.tv_offer_hash);
        imgBack = findViewById(R.id.img_back);
        imgProvider = findViewById(R.id.img_provider);
        rtProvider = findViewById(R.id.rt_provider);
        tvUsername = findViewById(R.id.tv_username);
        tvOfferPrice = findViewById(R.id.tv_offer_price);
        tvNotes = findViewById(R.id.tv_notes);
        btConversation = findViewById(R.id.bt_conversation);
        btAcceptOffer = findViewById(R.id.bt_accept);

        if (offer.getConversation() == null) {
            btConversation.setVisibility(View.GONE);
            btAcceptOffer.setVisibility(View.VISIBLE);
        } else {
            btAcceptOffer.setVisibility(View.GONE);
            btConversation.setVisibility(View.VISIBLE);
        }

        progressButton = new ProgressButton(OfferDetailsActivity.this, btAcceptOffer);
        progressButton.setButtonTittle(OfferDetailsActivity.this.getResources().getString(R.string.accept_offer));

        btConversation.setOnClickListener(this);
        imgBack.setOnClickListener(this);
        btAcceptOffer.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.bt_conversation:

                if (offer.getConversation() != null) {
                    if (offer.getOrderDetails().getStatus().equals("cancelled") ||
                            offer.getOrderDetails().getStatus().equals("delivered") ||
                            offer.getStatus().equals("cancelled")) {
                        startActivity(new Intent(OfferDetailsActivity.this, MessagesActivity.class)
                                .putExtra("offer", offer).putExtra("order_id", orderId));
                    } else {
                        startActivity(new Intent(OfferDetailsActivity.this, ChatActivity.class)
                                .putExtra("offer", offer).putExtra("order_id", orderId));
                    }
                }

                break;

            case R.id.img_back:
                finish();
                break;

            case R.id.bt_accept:
                progressButton.buttonActivated();
                acceptOffer();
                break;
        }
    }

    private void acceptOffer() {
        viewModel.acceptOffer(this, String.valueOf(offer.getId()));
        viewModel.mutableLiveData.observe(this, new Observer<Offer>() {
            @Override
            public void onChanged(Offer offer) {
                if (offer != null) {
                    progressButton.buttonFinished();
                    startActivity(new Intent(OfferDetailsActivity.this, ChatActivity.class)
                            .putExtra("offer", offer).putExtra("order_id", offer.getOrderDetails().getId()));
                    finish();
                } else {
                    progressButton.buttonDactivated();
                }
            }
        });

    }


    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleManager.setLocale(base));
    }
}
