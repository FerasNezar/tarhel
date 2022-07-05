package com.almusand.aaber.ui.myOffer;

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
import com.almusand.aaber.model.Offer;
import com.almusand.aaber.ui.base.BaseActivity;
import com.almusand.aaber.ui.chat.ChatActivity;
import com.almusand.aaber.ui.chat.MessagesActivity;
import com.almusand.aaber.utils.EndlessRecyclerViewScrollListener;
import com.almusand.aaber.utils.LocaleManager;
import com.cooltechworks.views.shimmer.ShimmerRecyclerView;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;
import java.util.List;

public class MyOffersActivity extends BaseActivity implements View.OnClickListener {

    private ShimmerRecyclerView rvOffers;
    private MyOffersAdapter orderOffersAdapter;
    private LinearLayoutManager layoutManager;

    private LinearLayout lyNoOffers;
    private ImageView imgBack;
    private TextView tvOfferTittle;
    private TextView tvCancelOffer;
    private TextView tvCancel;

    private MyOffersViewModel viewModel;
    private List<Offer> offerList;
    private boolean havePagination = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_offers);

        viewModel = new ViewModelProvider(this).get(MyOffersViewModel.class);

        initialViews();

        setUpRecyclerViewOffers();

    }

    private void setUpRecyclerViewOffers() {

        orderOffersAdapter = new MyOffersAdapter(new MyOffersAdapter.onItemClick() {
            @Override
            public void onItemClick(Offer offer, LinearLayout layout) {
                if (offer.getConversation() != null) {
                    if (offer.getOrderDetails().getStatus().equals("cancelled") ||
                            offer.getOrderDetails().getStatus().equals("delivered") ||
                            offer.getStatus().equals("cancelled")) {
                        startActivity(new Intent(MyOffersActivity.this, MessagesActivity.class)
                                .putExtra("offer", offer).putExtra("order_id", offer.getOrderDetails().getId()));

                    } else {
                        startActivity(new Intent(MyOffersActivity.this, ChatActivity.class)
                                .putExtra("offer", offer).putExtra("order_id", offer.getOrderDetails().getId()));

                    }
                }
            }

            @Override
            public void onItemCancel(Offer offer, int position, ImageView imageView) {
                showBottomSheetOrderStatus(offer, position);
            }
        });

        rvOffers.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(MyOffersActivity.this);
        rvOffers.setLayoutManager(layoutManager);
        rvOffers.setAdapter(orderOffersAdapter);
        rvOffers.showShimmerAdapter();
    }

    private void initialViews() {
        imgBack = findViewById(R.id.img_back);
        rvOffers = findViewById(R.id.rv_offers);
        lyNoOffers = findViewById(R.id.ly_no_offers);
        imgBack.setOnClickListener(this);
    }

    private void getOffers() {
        offerList = new ArrayList<>();

        viewModel.mutableLivePagination.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                havePagination = aBoolean;
            }
        });

        viewModel.getMyOffers(MyOffersActivity.this, 1);
        viewModel.mutableLiveData.observe(this, new Observer<List<Offer>>() {
            @Override
            public void onChanged(List<Offer> offers) {

                if (offers != null && offers.size() > 0) {
                    offerList = offers;
                    lyNoOffers.setVisibility(View.GONE);
                    rvOffers.setVisibility(View.VISIBLE);
                    orderOffersAdapter.setList(offers);
                    rvOffers.hideShimmerAdapter();

                    rvOffers.addOnScrollListener(new EndlessRecyclerViewScrollListener(layoutManager) {
                        @Override
                        public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                            if (havePagination) {
                                loadMoreOffers(page + 1);
                            }
                        }
                    });
                } else {
                    rvOffers.setVisibility(View.GONE);
                    lyNoOffers.setVisibility(View.VISIBLE);
                }

            }
        });



    }

    private void loadMoreOffers(int i) {
        viewModel.getMyOffers(MyOffersActivity.this, i);
        viewModel.mutableLiveData.observe(this, new Observer<List<Offer>>() {
            @Override
            public void onChanged(List<Offer> offers) {
                offerList.addAll(offers);
                orderOffersAdapter.setList(offerList);
            }
        });
    }

    private void showBottomSheetOrderStatus(Offer offer, int position) {
        final BottomSheetDialog mBottomSheetDialog = new BottomSheetDialog(MyOffersActivity.this);
        View sheetView = getLayoutInflater().inflate(R.layout.layout_offers_types, null);
        mBottomSheetDialog.setContentView(sheetView);
        ((View) sheetView.getParent()).setBackgroundColor(Color.TRANSPARENT);

        tvOfferTittle = sheetView.findViewById(R.id.tv_offer_tittle);
        tvCancelOffer = sheetView.findViewById(R.id.tv_cancel_offer);
        tvCancel = sheetView.findViewById(R.id.tv_cancel);

        tvOfferTittle.append(String.valueOf(offer.getId()));
        tvCancelOffer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelOfferFun(offer);
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

    private void cancelOfferFun(Offer offer) {
        viewModel.changeOfferStatus(this, String.valueOf(offer.getId()));
        viewModel.mutableLiveStatus.observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                if (s != null && s.equals("done")) {
                    getOffers();
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
    protected void onResume() {
        getOffers();
        super.onResume();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleManager.setLocale(base));
    }

}
