package com.almusand.aaber.ui.orderOffers;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.almusand.aaber.R;
import com.almusand.aaber.model.NotifyFcm;
import com.almusand.aaber.model.Offer;
import com.almusand.aaber.model.Order;
import com.almusand.aaber.ui.base.BaseActivity;
import com.almusand.aaber.ui.offerDetails.OfferDetailsActivity;
import com.almusand.aaber.utils.LocaleManager;
import com.cooltechworks.views.shimmer.ShimmerRecyclerView;
import com.google.gson.Gson;

public class OrderOffersActivity extends BaseActivity implements View.OnClickListener {

    private TextView tvOrderTittle;
    private ImageView imgBack;
    private ShimmerRecyclerView rvOffers;
    private LinearLayout lyNoOffers;

    private OrderOfferViewModel viewModel;
    private OrderOffersAdapter orderOffersAdapter;

    private Order mOrder;
    private int orderId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_offers);

        viewModel = new ViewModelProvider(this).get(OrderOfferViewModel.class);

        prpareDataFromIntent();

        initViews();

        setUpRecyclerViewOffers();

        getOffers(orderId);

    }

    private void initViews() {
        tvOrderTittle = findViewById(R.id.tv_order_tittle);
        imgBack = findViewById(R.id.img_back);
        rvOffers = findViewById(R.id.rv_offers);
        lyNoOffers = findViewById(R.id.ly_no_offers);

        if (mOrder != null) {
            tvOrderTittle.append(String.valueOf(mOrder.getOrderCode()));
        } else {
            tvOrderTittle.setText(OrderOffersActivity.this.getResources().getText(R.string.offers));
        }

        imgBack.setOnClickListener(this);
    }

    private void prpareDataFromIntent() {
        if (getIntent().getExtras() != null) {
            for (String key : getIntent().getExtras().keySet()) {
                Log.e("keyIntent",key);
                if (key.equals("order")) {
                    mOrder = (Order) getIntent().getExtras().get(key);
                    orderId = mOrder.getId();
                } else if (key.equals("notifyFcm")) {
                    NotifyFcm notifyFcm = (NotifyFcm) getIntent().getExtras().get(key);
                    orderId = notifyFcm.getOrderId();
                }
            }
        }
    }

    private void setUpRecyclerViewOffers() {
        orderOffersAdapter = new OrderOffersAdapter(new OrderOffersAdapter.onItemClick() {
            @Override
            public void onItemClick(Offer offer, int position, LinearLayout textView) {
                startActivity(new Intent(OrderOffersActivity.this, OfferDetailsActivity.class)
                        .putExtra("offer", offer)
                        .putExtra("order_id", orderId));
            }
        });

        rvOffers.setHasFixedSize(true);
        rvOffers.setLayoutManager(new LinearLayoutManager(OrderOffersActivity.this));
        rvOffers.setAdapter(orderOffersAdapter);
        rvOffers.showShimmerAdapter();
    }

    private void getOffers(int id) {
        viewModel.getSingleOrder(OrderOffersActivity.this, String.valueOf(id));
        viewModel.mutableLiveData.observe(this, new Observer<Order>() {
            @Override
            public void onChanged(Order order) {
                if (order != null && order.getOffers().size() > 0) {
                    lyNoOffers.setVisibility(View.GONE);
                    rvOffers.setVisibility(View.VISIBLE);
                    orderOffersAdapter.setList(order.getOffers());
                    rvOffers.hideShimmerAdapter();
                } else {
                    lyNoOffers.setVisibility(View.VISIBLE);
                    rvOffers.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.img_back:
                finish();
                break;

        }
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleManager.setLocale(base));
    }

}
