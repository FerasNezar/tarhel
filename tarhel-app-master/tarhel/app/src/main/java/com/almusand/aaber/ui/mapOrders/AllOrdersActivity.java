package com.almusand.aaber.ui.mapOrders;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.almusand.aaber.R;
import com.almusand.aaber.model.Order;
import com.almusand.aaber.model.User;
import com.almusand.aaber.ui.base.BaseActivity;
import com.almusand.aaber.ui.orderDetails.OrderDetailsActivity;
import com.almusand.aaber.utils.AppPreferences;
import com.almusand.aaber.utils.EndlessRecyclerViewScrollListener;
import com.almusand.aaber.utils.LocaleManager;
import com.cooltechworks.views.shimmer.ShimmerRecyclerView;
import com.google.gson.Gson;

import java.util.List;

public class AllOrdersActivity extends BaseActivity implements View.OnClickListener {

    private ShimmerRecyclerView rvOrders;
    private ImageView imgBack;
    private LinearLayout lyNoRoders;

    private NearbyOrdersViewModel nearbyOrdersViewModel;
    private List<Order> ordersList;

    private LinearLayoutManager layoutManager;

    private NearbyOrdersAdapter ordersAdapter;
    private boolean havePagination = false;

    private String currentLat, currentLng;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_orders);

        nearbyOrdersViewModel = new ViewModelProvider(this).get(NearbyOrdersViewModel.class);
        user = new Gson().fromJson(AppPreferences.getUser(this), User.class);

        initialViews();

        setUpRecyclerViewOrders();

        getNearbyOrders();

    }

    private void setUpRecyclerViewOrders() {
        ordersAdapter = new NearbyOrdersAdapter(new NearbyOrdersAdapter.onItemClick() {
            @Override
            public void onItemClick(Order order, LinearLayout layout) {
                startActivity(new Intent(AllOrdersActivity.this, OrderDetailsActivity.class)
                        .putExtra("order", order).putExtra("type", "provider"));
            }
        });
        ordersAdapter.setLang(LocaleManager.getLocale(AllOrdersActivity.this.getResources()).getLanguage());
        rvOrders.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        rvOrders.setLayoutManager(layoutManager);
        rvOrders.setAdapter(ordersAdapter);
        rvOrders.showShimmerAdapter();
    }

    private void initialViews() {
        imgBack = findViewById(R.id.img_back);
        rvOrders = findViewById(R.id.rv_orders);
        lyNoRoders = findViewById(R.id.ly_no_roders);

        currentLat = getIntent().getExtras().getString("currentLat");
        currentLng = getIntent().getExtras().getString("currentLng");

        if (currentLat == null) {
            currentLat = user.getLat();
            currentLng = user.getLng();
        }
        imgBack.setOnClickListener(this);
    }

    private void getNearbyOrders() {
        nearbyOrdersViewModel.getOrders(this, 1, currentLat, currentLng);
        nearbyOrdersViewModel.mutableLivePagination.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                havePagination = aBoolean;
            }
        });
        nearbyOrdersViewModel.mutableLiveData.observe(this, new Observer<List<Order>>() {
            @Override
            public void onChanged(List<Order> orders) {
                if (orders != null) {
                    if (orders.size() > 0) {
                        lyNoRoders.setVisibility(View.GONE);
                        rvOrders.setVisibility(View.VISIBLE);
                        ordersList = orders;
                        ordersAdapter.setList(ordersList);
                        rvOrders.hideShimmerAdapter();
                        rvOrders.addOnScrollListener(new EndlessRecyclerViewScrollListener(layoutManager) {
                            @Override
                            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                                if (havePagination) {
                                    loadMoreOrders(page + 1);
                                }
                            }
                        });
                    } else {
                        lyNoRoders.setVisibility(View.VISIBLE);
                        rvOrders.setVisibility(View.GONE);
                    }
                } else {
                    rvOrders.hideShimmerAdapter();
                }
            }
        });
    }

    private void loadMoreOrders(int i) {
        nearbyOrdersViewModel.getOrders(AllOrdersActivity.this, i, "", "");
        nearbyOrdersViewModel.mutableLiveData.observe(this, new Observer<List<Order>>() {
            @Override
            public void onChanged(List<Order> orders) {
                ordersList.addAll(orders);
                ordersAdapter.setList(ordersList);
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
