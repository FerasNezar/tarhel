package com.almusand.aaber.ui.balance;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.almusand.aaber.R;
import com.almusand.aaber.model.Payment;
import com.almusand.aaber.model.User;
import com.almusand.aaber.ui.base.BaseActivity;
import com.almusand.aaber.utils.AppPreferences;
import com.almusand.aaber.utils.LocaleManager;
import com.cooltechworks.views.shimmer.ShimmerRecyclerView;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class BalanceActivity extends BaseActivity implements View.OnClickListener {

    private ImageView imgBack;
    private ShimmerRecyclerView rvPayments;
    private LinearLayout lyNoPayments;
    private TextView tvPaymentTotal;

    private LinearLayoutManager layoutManager;
    private BalanceViewModel viewModel;
    private List<Payment> paymentList;
    private MyPaymentsAdapter myPaymentsAdapter;

    private User user;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_balance);
        setUpToolbar();

        viewModel = new ViewModelProvider(this).get(BalanceViewModel.class);
        user = new Gson().fromJson(AppPreferences.getUser(this), User.class);

        initialViews();

        setUpRecyclerPayments();

        getBalances();

    }

    private void setUpRecyclerPayments() {
        myPaymentsAdapter = new MyPaymentsAdapter();
        rvPayments.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        rvPayments.setLayoutManager(layoutManager);
        rvPayments.setAdapter(myPaymentsAdapter);
        rvPayments.showShimmerAdapter();
    }

    private void getBalances() {
        viewModel.getBalances(this);
        viewModel.mutableLiveData.observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {

                try {
                    JSONObject object = new JSONObject(s);
                    JSONObject data = object.getJSONObject("data");
                    tvPaymentTotal.setText(String.valueOf(data.get("total")));
                    JSONArray list = data.getJSONArray("balances");
                    for (int i = 0; i < list.length(); i++) {
                        Payment payment = new Gson().fromJson(list.getJSONObject(i).toString(), Payment.class);
                        paymentList.add(payment);
                    }
                    if (paymentList.size() > 0) {
                        rvPayments.setVisibility(View.VISIBLE);
                        lyNoPayments.setVisibility(View.GONE);
                        rvPayments.hideShimmerAdapter();
                        myPaymentsAdapter.setList(paymentList);

                    } else {
                        rvPayments.setVisibility(View.GONE);
                        lyNoPayments.setVisibility(View.VISIBLE);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        });

    }

    private void initialViews() {
        rvPayments = findViewById(R.id.rv_payments);
        lyNoPayments = findViewById(R.id.ly_no_payments);
        tvPaymentTotal = findViewById(R.id.tv_payment_total);
        imgBack = findViewById(R.id.img_back);

        paymentList = new ArrayList<>();

        tvPaymentTotal.setText(String.valueOf(user.getTotalBalance()));

        imgBack.setOnClickListener(this);
    }

    private void setUpToolbar() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

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