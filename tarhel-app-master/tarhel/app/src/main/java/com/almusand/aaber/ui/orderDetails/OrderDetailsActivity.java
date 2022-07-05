package com.almusand.aaber.ui.orderDetails;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.almusand.aaber.R;
import com.almusand.aaber.model.Order;
import com.almusand.aaber.model.User;
import com.almusand.aaber.ui.base.BaseActivity;
import com.almusand.aaber.ui.orderOffers.OrderOffersActivity;
import com.almusand.aaber.ui.sendOffer.SendOfferActivity;
import com.almusand.aaber.utils.AppPreferences;
import com.almusand.aaber.utils.LocaleManager;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.makeramen.roundedimageview.RoundedImageView;

public class OrderDetailsActivity extends BaseActivity implements View.OnClickListener {

    private LinearLayout layout;
    private TextView tvOrderTittle;
    private ImageView imgBack;
    private RoundedImageView imgOrder;
    private EditText etCategory;
    private EditText etWeight;
    private EditText etNotes;
    private EditText etTime;
    private EditText etArrivalDate;
    private TextView tvOffers;
    private Button btSendOffer;
    private Button btPickupLocation;
    private Button btDropoffLocation;

    private Order order;
    private String type;
    private String fromChat;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);

        order = (Order) getIntent().getExtras().getSerializable("order");
        type = getIntent().getExtras().getString("type");
        fromChat = getIntent().getExtras().getString("chat");
        user = new Gson().fromJson(AppPreferences.getUser(OrderDetailsActivity.this), User.class);

        initViews();

        prepareData();
    }

    private void prepareData() {

        Glide.with(OrderDetailsActivity.this).load(order.getImage()).centerCrop().error(R.drawable.aaber_logo).into(imgOrder);
        tvOrderTittle.append(String.valueOf(order.getOrderCode()));

        if (LocaleManager.getLocale(OrderDetailsActivity.this.getResources()).getLanguage().equals("ar")) {
            etCategory.setText(order.getCategory().getName().getAr());
        } else {
            etCategory.setText(order.getCategory().getName().getEn());
        }
        etNotes.setText(order.getNote());
        etArrivalDate.setText(order.getArrivalDate());
        etTime.setText(order.getTime());
        etWeight.setText(order.getWeight());

        btDropoffLocation.setText(order.getLocationDropoff());
        btPickupLocation.setText(order.getLocation());

    }

    private void initViews() {
        layout = findViewById(R.id.layout);
        tvOrderTittle = findViewById(R.id.tv_order_tittle);
        imgBack = findViewById(R.id.img_back);
        imgOrder = findViewById(R.id.img_order);
        etCategory = findViewById(R.id.et_category);
        etWeight = findViewById(R.id.et_weight);
        etNotes = findViewById(R.id.et_notes);
        etTime = findViewById(R.id.et_time);
        etArrivalDate = findViewById(R.id.et_arrival_date);
        tvOffers = findViewById(R.id.tv_offers);
        btSendOffer = findViewById(R.id.bt_send_offer);
        btPickupLocation = findViewById(R.id.bt_pickup_location);
        btDropoffLocation = findViewById(R.id.bt_dropoff_location);

        if (!user.getType().equals("user")) {
            tvOrderTittle.setEnabled(false);
            etCategory.setEnabled(false);
            etWeight.setEnabled(false);
            etNotes.setEnabled(false);
            etTime.setEnabled(false);
            etArrivalDate.setEnabled(false);
            tvOffers.setEnabled(false);
        }

        if (type != null) {
            btSendOffer.setVisibility(View.VISIBLE);
            tvOffers.setVisibility(View.GONE);
        }

        if (fromChat != null) {
            tvOffers.setVisibility(View.GONE);
            btSendOffer.setVisibility(View.GONE);
        }

        imgBack.setOnClickListener(this);
        tvOffers.setOnClickListener(this);
        btSendOffer.setOnClickListener(this);
        btDropoffLocation.setOnClickListener(this);
        btPickupLocation.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.img_back:
                finish();
                break;

            case R.id.tv_offers:
                startActivity(new Intent(OrderDetailsActivity.this, OrderOffersActivity.class)
                        .putExtra("order", order));
                break;

            case R.id.bt_send_offer:
                startActivity(new Intent(OrderDetailsActivity.this, SendOfferActivity.class)
                        .putExtra("order", order));
                break;

            case R.id.bt_dropoff_location:
                intentToMap(order.getDropoffLat(), order.getDropoffLng());
                break;

            case R.id.bt_pickup_location:
                intentToMap(order.getPickupLat(), order.getPickupLng());
                break;
        }
    }

    private void intentToMap(String lat, String lng) {
        Intent i = new Intent(Intent.ACTION_VIEW,Uri.parse("geo:"+ lat + "," + lng));
        i.setClassName("com.google.android.apps.maps",
                "com.google.android.maps.MapsActivity");
        startActivity(i);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleManager.setLocale(base));
    }

}

