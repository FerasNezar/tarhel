package com.almusand.aaber.ui.myOrders;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
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
import com.almusand.aaber.ui.base.BaseActivity;
import com.almusand.aaber.ui.main.MainActivityClient;
import com.almusand.aaber.ui.orderDetails.OrderDetailsActivity;
import com.almusand.aaber.utils.AppPreferences;
import com.almusand.aaber.utils.EndlessRecyclerViewScrollListener;
import com.almusand.aaber.utils.LocaleManager;
import com.cooltechworks.views.shimmer.ShimmerRecyclerView;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;
import java.util.List;

public class MyOrdersActivity extends BaseActivity implements View.OnClickListener {

    private OrdersViewModel viewModel;
    private ShimmerRecyclerView rvPendingOrders;
    private ShimmerRecyclerView rvCompleteOrders;
    private TextView tvOrderTittle;
    private TextView tvCancelOrder;
    private TextView tvCancel;

    private MyOrdersAdapter ordersAdapter;
    private List<Order> orderList;
    private LinearLayout lyNoOders;
    private ImageView imgBack;
    private TextView tvDelivered;
    private TextView tvPending;
    private TextView tvConfirmed;

    private Boolean havePagination = false;
    private LinearLayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_orders);

        viewModel = new ViewModelProvider(this).get(OrdersViewModel.class);

        initialViews();

        setupRecyclerViewOrders();

        getPendingOrders();

    }

    private void getPendingOrders() {

        viewModel.getMyOrders(this, 1, "pending");
        viewModel.mutableLivePagination.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                havePagination = aBoolean;
            }
        });

        viewModel.mutableLiveData.observe(this, new Observer<List<Order>>() {
            @Override
            public void onChanged(List<Order> orders) {
                if (orders != null || orders.size() != 0) {
                    orderList = orders;
                    ordersAdapter.setList(orderList);
                    rvPendingOrders.hideShimmerAdapter();
                    layoutManager = new LinearLayoutManager(MyOrdersActivity.this);
                    rvPendingOrders.setLayoutManager(layoutManager);
                    rvPendingOrders.setAdapter(ordersAdapter);

                    rvPendingOrders.addOnScrollListener(new EndlessRecyclerViewScrollListener(layoutManager) {
                        @Override
                        public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                            if (havePagination) {
                                loadMoreOrders(page + 1, "pending");
                            }
                        }
                    });

                } else {
                    rvPendingOrders.setVisibility(View.GONE);
                    lyNoOders.setVisibility(View.VISIBLE);
                }
            }
        });


    }

    private void getDeliveredOrders(String type) {
        orderList=new ArrayList<>();
        viewModel.getMyOrders(this, 1, type);
        viewModel.mutableLivePagination.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                havePagination = aBoolean;
            }
        });

        viewModel.mutableLiveData.observe(this, new Observer<List<Order>>() {
            @Override
            public void onChanged(List<Order> orders) {
                if (orders != null || orders.size() != 0) {
                    orderList = orders;
                    ordersAdapter.setList(orderList);
                    rvCompleteOrders.hideShimmerAdapter();
                    layoutManager = new LinearLayoutManager(MyOrdersActivity.this);
                    rvCompleteOrders.setLayoutManager(layoutManager);
                    rvCompleteOrders.setAdapter(ordersAdapter);

                    rvCompleteOrders.addOnScrollListener(new EndlessRecyclerViewScrollListener(layoutManager) {
                        @Override
                        public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                            if (havePagination) {
                                loadMoreOrders(page + 1, type);
                            }
                        }
                    });

                } else {
                    rvPendingOrders.setVisibility(View.GONE);
                    lyNoOders.setVisibility(View.VISIBLE);
                }
            }
        });


    }

    private void loadMoreOrders(int i, String flagOrder) {
        viewModel.getMyOrders(this, i, flagOrder);
        viewModel.mutableLiveData.observe(this, new Observer<List<Order>>() {
            @Override
            public void onChanged(List<Order> orders) {
                orderList.addAll(orders);
                ordersAdapter.setList(orderList);
            }
        });
    }

    private void setupRecyclerViewOrders() {

        ordersAdapter = new MyOrdersAdapter(new MyOrdersAdapter.onItemClick() {
            @Override
            public void onItemClick(Order order, int i, ImageView view) {
                showBottomSheetOrderStatus(order, i);
            }

            @Override
            public void onItemClick(Order order, LinearLayout view) {
                startActivity(new Intent(MyOrdersActivity.this, OrderDetailsActivity.class)
                        .putExtra("order", order));
            }
        });
        ordersAdapter.setLang(LocaleManager.getLocale(MyOrdersActivity.this.getResources()).getLanguage());
        rvPendingOrders.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        rvPendingOrders.setLayoutManager(layoutManager);
        rvCompleteOrders.setLayoutManager(new LinearLayoutManager(this));
        ordersAdapter.setPending(true);
        rvPendingOrders.showShimmerAdapter();

    }

    private void initialViews() {
        rvCompleteOrders = findViewById(R.id.rv_complete_orders);
        rvPendingOrders = findViewById(R.id.rv_pending_orders);
        lyNoOders = findViewById(R.id.ly_no_roders);
        imgBack = findViewById(R.id.img_back);
        tvDelivered = findViewById(R.id.tv_delivered);
        tvPending = findViewById(R.id.tv_pending);
        tvConfirmed = findViewById(R.id.tv_confirmed);

        tvConfirmed.setOnClickListener(this);
        tvDelivered.setOnClickListener(this);
        tvPending.setOnClickListener(this);
        imgBack.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.tv_pending:
                tvPending.setBackground(MyOrdersActivity.this.getResources().getDrawable(R.drawable.bk_button));
                tvDelivered.setBackground(MyOrdersActivity.this.getResources().getDrawable(R.drawable.bk_button_unselected));
                tvConfirmed.setBackground(MyOrdersActivity.this.getResources().getDrawable(R.drawable.bk_button_unselected));
                rvCompleteOrders.setVisibility(View.GONE);
                lyNoOders.setVisibility(View.GONE);
                rvPendingOrders.setVisibility(View.VISIBLE);
                ordersAdapter.setPending(true);
                rvPendingOrders.setAdapter(ordersAdapter);
                rvPendingOrders.showShimmerAdapter();
                getPendingOrders();
                break;
            case R.id.tv_delivered:
                tvPending.setBackground(MyOrdersActivity.this.getResources().getDrawable(R.drawable.bk_button_unselected));
                tvConfirmed.setBackground(MyOrdersActivity.this.getResources().getDrawable(R.drawable.bk_button_unselected));
                tvDelivered.setBackground(MyOrdersActivity.this.getResources().getDrawable(R.drawable.bk_button));
                rvCompleteOrders.setVisibility(View.VISIBLE);
                lyNoOders.setVisibility(View.GONE);
                rvPendingOrders.setVisibility(View.GONE);
                ordersAdapter.setPending(false);
                rvCompleteOrders.showShimmerAdapter();
                getDeliveredOrders("delivered");
                break;

            case R.id.tv_confirmed:
                tvPending.setBackground(MyOrdersActivity.this.getResources().getDrawable(R.drawable.bk_button_unselected));
                tvDelivered.setBackground(MyOrdersActivity.this.getResources().getDrawable(R.drawable.bk_button_unselected));
                tvConfirmed.setBackground(MyOrdersActivity.this.getResources().getDrawable(R.drawable.bk_button));
                rvCompleteOrders.setVisibility(View.VISIBLE);
                lyNoOders.setVisibility(View.GONE);
                rvPendingOrders.setVisibility(View.GONE);
                ordersAdapter.setPending(false);
                rvCompleteOrders.showShimmerAdapter();
                getDeliveredOrders("confirmed");
                break;
            case R.id.img_back:
                finish();
                break;

        }
    }

    private void showBottomSheetOrderStatus(Order order, int position) {
        final BottomSheetDialog mBottomSheetDialog = new BottomSheetDialog(MyOrdersActivity.this);
        View sheetView = getLayoutInflater().inflate(R.layout.layout_orders_types, null);
        mBottomSheetDialog.setContentView(sheetView);
        ((View) sheetView.getParent()).setBackgroundColor(Color.TRANSPARENT);


        tvOrderTittle = sheetView.findViewById(R.id.tv_order_tittle);
        tvCancelOrder = sheetView.findViewById(R.id.tv_cancel_order);
        tvCancel = sheetView.findViewById(R.id.tv_cancel);


        tvOrderTittle.append(String.valueOf(order.getOrderCode()));

        tvCancelOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelOrder(order.getId(), position);
                mBottomSheetDialog.dismiss();
            }
        });

        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBottomSheetDialog.dismiss();
            }
        });
        mBottomSheetDialog.show();

    }

    private void cancelOrder(Integer id, int position) {
        viewModel.cancelOrder(MyOrdersActivity.this, id);
        viewModel.mutableLiveStatus.observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                if (s != null && s.equals("done")) {
                    getPendingOrders();
                }
            }
        });

    }


    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleManager.setLocale(base));
    }
}
