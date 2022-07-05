package com.almusand.aaber.ui.notification;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.almusand.aaber.R;
import com.almusand.aaber.model.Notification;
import com.almusand.aaber.model.Offer;
import com.almusand.aaber.model.Order;
import com.almusand.aaber.ui.chat.ChatActivity;
import com.almusand.aaber.ui.orderDetails.OrderDetailsActivity;
import com.almusand.aaber.ui.orderOffers.OrderOfferViewModel;
import com.almusand.aaber.ui.orderOffers.OrderOffersActivity;
import com.almusand.aaber.utils.EndlessRecyclerViewScrollListener;
import com.almusand.aaber.utils.LocaleManager;
import com.almusand.aaber.utils.Utilities;
import com.apkfuns.xprogressdialog.XProgressDialog;
import com.cooltechworks.views.shimmer.ShimmerRecyclerView;

import java.util.List;

public class NotificationActivity extends AppCompatActivity implements View.OnClickListener {

    private ShimmerRecyclerView rvNotifications;
    private LinearLayout lyNoNotifications;
    private ImageView imgBack;

    private NotificationsAdapter notificationsAdapter;
    private List<Notification> notificationList;
    private NotificationViewModel viewModel;
    private LinearLayoutManager layoutManager;
    private boolean havePagination = false;
    private Order mOrder;
    private Offer mOffer;

    private XProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        viewModel = new ViewModelProvider(this).get(NotificationViewModel.class);

        initialViews();

        setupRecyclerView();

        getNotifications();

    }

    private void getNotifications() {

        viewModel.getMyNotifications(NotificationActivity.this, 1);
        viewModel.mutableLiveData.observe(this, new Observer<List<Notification>>() {
            @Override
            public void onChanged(List<Notification> notifications) {
                notificationList = notifications;
                if (notificationList.size() > 0) {
                    rvNotifications.setVisibility(View.VISIBLE);
                    lyNoNotifications.setVisibility(View.GONE);
                    notificationsAdapter.setList(notificationList);
                    rvNotifications.hideShimmerAdapter();

                    rvNotifications.addOnScrollListener(new EndlessRecyclerViewScrollListener(layoutManager) {
                        @Override
                        public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                            if (havePagination) {
                                loadMoreNotifications(page + 1);
                            }
                        }
                    });

                } else {
                    rvNotifications.setVisibility(View.GONE);
                    lyNoNotifications.setVisibility(View.VISIBLE);
                }
            }
        });

        viewModel.mutableLivePagination.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                havePagination = aBoolean;
            }
        });

        viewModel.mutableLiveError.observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                rvNotifications.hideShimmerAdapter();
                Toast.makeText(NotificationActivity.this, s, Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void loadMoreNotifications(int i) {
        viewModel.getMyNotifications(NotificationActivity.this, i);
        viewModel.mutableLiveData.observe(this, new Observer<List<Notification>>() {
            @Override
            public void onChanged(List<Notification> notifications) {
                notificationList.addAll(notifications);
                notificationsAdapter.setList(notificationList);
            }
        });
    }

    private void setupRecyclerView() {

        notificationsAdapter = new NotificationsAdapter(new NotificationsAdapter.onItemClick() {
            @Override
            public void onItemClick(Notification notification, TextView textView) {

                showProgressDialog(NotificationActivity.this.getResources().getString(R.string.loading));

                if (notification.getReadAt() == null) {
                    makeNotificationRead(notification);
                } else {
                    handleNotificationAction(notification);
                }

            }
        });
        notificationsAdapter.setLang(LocaleManager.getLocale(NotificationActivity.this.getResources()).getLanguage());
        rvNotifications.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(NotificationActivity.this);
        rvNotifications.setLayoutManager(layoutManager);
        rvNotifications.setAdapter(notificationsAdapter);
        rvNotifications.showShimmerAdapter();
    }

    private void makeNotificationRead(Notification notification) {
        viewModel.markNotificationRead(NotificationActivity.this, notification.getId());
        viewModel.mutableLiveStatus.observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                if (s != null && s.equals("done")) {
                    notification.setReadAt(Utilities.getCurrentDateTime());
                    handleNotificationAction(notification);
                }
            }
        });
    }

    private void handleNotificationAction(Notification notification) {
        switch (notification.getType()) {
            case "App\\Notifications\\NewOffer":
                getSingleOrder(notification.getData().getOrderId(), 0, "newOffer");
                break;

            case "App\\Notifications\\NewMessage":

            case "App\\Notifications\\ClientAcceptOffer":
                getSingleOrder(notification.getData().getOrderId(), notification.getData().getOfferId(), "chat");
                break;

            case "App\\Notifications\\NewOrder":
                getSingleOrder(notification.getData().getOrderId(), 0, "orderDetails");
                break;

        }

    }

    private void initialViews() {

        rvNotifications = findViewById(R.id.rv_notifications);
        lyNoNotifications = findViewById(R.id.ly_no_notifications);
        imgBack = findViewById(R.id.img_back);

        imgBack.setOnClickListener(this);

    }

    private void getSingleOrder(int orderID, int offerId, String type) {
        OrderOfferViewModel orderOfferViewModel = new ViewModelProvider(this).get(OrderOfferViewModel.class);
        orderOfferViewModel.getSingleOrder(NotificationActivity.this, String.valueOf(orderID));
        orderOfferViewModel.mutableLiveData.observe(this, new Observer<Order>() {
            @Override
            public void onChanged(Order order) {

                if (progressDialog != null) hideProgressDialog();

                if (order != null) {
                    mOrder = order;
                    for (Offer offer : order.getOffers()) {
                        if (offer.getId() == offerId) {
                            mOffer = offer;
                            break;
                        }
                    }

                    if (type.equals("chat")) {

                        startActivity(new Intent(NotificationActivity.this, ChatActivity.class)
                                .putExtra("offer", mOffer)
                                .putExtra("order", mOrder));
                        finish();

                    } else if (type.equals("orderDetails")) {
                        startActivity(new Intent(NotificationActivity.this, OrderDetailsActivity.class)
                                .putExtra("order", mOrder).putExtra("type", "provider"));
                        finish();

                    } else if (type.equals("newOffer")) {
                        startActivity(new Intent(NotificationActivity.this, OrderOffersActivity.class)
                                .putExtra("order", mOrder));
                        finish();
                    }
                }
            }
        });
    }

    public void showProgressDialog(String s) {
        progressDialog = new XProgressDialog(NotificationActivity.this);
        progressDialog.setMessage(s);
        progressDialog.show();
    }

    public void hideProgressDialog() {
        progressDialog.hide();
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
