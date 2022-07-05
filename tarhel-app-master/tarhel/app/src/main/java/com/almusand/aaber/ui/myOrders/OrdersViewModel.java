package com.almusand.aaber.ui.myOrders;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.almusand.aaber.model.Order;
import com.almusand.aaber.service.ApiRequest;
import com.almusand.aaber.utils.AppPreferences;
import com.almusand.aaber.utils.Constants;
import com.androidnetworking.error.ANError;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import es.dmoral.toasty.Toasty;

/**
 * Developed by Anas Elshwaf
 * anaselshawaf357@gmail.com
 */
public class OrdersViewModel extends ViewModel {

    public MutableLiveData<List<Order>> mutableLiveData = new MutableLiveData<List<Order>>();
    public MutableLiveData<Order> mutableLiveDataSingleOrder = new MutableLiveData<>();
    public MutableLiveData<String> mutableLiveStatus = new MutableLiveData<>();
    public MutableLiveData<Boolean> mutableLivePagination = new MutableLiveData<>();

    ApiRequest apiRequest;
    List<Order> orderList;
    URL url;

    public void getMyOrders(Context context, int page, String status) {

        Uri builtUri = Uri.parse(Constants.myOrdersrUrl)
                .buildUpon()
                .appendQueryParameter("status", status)
                .appendQueryParameter("page", String.valueOf(page))
                .build();
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        mutableLivePagination = new MutableLiveData<>();
        mutableLiveData = new MutableLiveData<>();
        orderList = new ArrayList<>();

        apiRequest = ApiRequest.getInstance(context);
        apiRequest.createGetRequest(url.toString(), context, new ApiRequest.ServiceCallback<String>() {

            @Override
            public void onSuccess(String response) throws JSONException {
                JSONObject object = new JSONObject(response);
                try {
                    if (object.getBoolean("success")) {
                        Gson gson = new GsonBuilder().create();
                        JSONObject jsonObject = object.getJSONObject("data");

                        if (page + 1 <= jsonObject.getInt("last_page")) {
                            mutableLivePagination.setValue(true);
                        }

                        JSONArray data = jsonObject.getJSONArray("data");
                        for (int i = 0; i < data.length(); i++) {
                            Order order = gson.fromJson(data.getJSONObject(i).toString(), Order.class);
                            orderList.add(order);
                        }
                        mutableLiveData.setValue(orderList);

                    } else {
                        Toasty.error(context, object.getString("message"), Toast.LENGTH_SHORT).show();
                        mutableLiveData.setValue(null);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFail(ANError error) throws JSONException {
                Toasty.error(context, error.getErrorBody(), Toast.LENGTH_SHORT).show();
                mutableLiveData.setValue(null);
            }
        });
    }

    public void cancelOrder(Context context, int orderId) {

        Uri builtUri = Uri.parse(Constants.cancelorderUrl)
                .buildUpon()
                .appendPath(String.valueOf(orderId))
                .build();
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        apiRequest = ApiRequest.getInstance(context);
        apiRequest.createPostRequest(url.toString(), context, new ApiRequest.ServiceCallback<String>() {

            @Override
            public void onSuccess(String response) throws JSONException {

                JSONObject object = new JSONObject(response);
                try {
                    if (object.getBoolean("success")) {
                        Gson gson = new GsonBuilder().create();
                        JSONObject jsonObject = object.getJSONObject("data");

                        Order order = gson.fromJson(jsonObject.toString(), Order.class);

                        mutableLiveStatus.setValue("done");

                    } else {
                        Toasty.error(context, object.getString("message"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFail(ANError error) throws JSONException {
                Toasty.error(context, error.getErrorBody(), Toasty.LENGTH_SHORT).show();
                mutableLiveStatus.setValue(null);
            }
        });
    }

    public void getSingleOrder(Context context, int orderId) {

        mutableLiveDataSingleOrder = new MutableLiveData<>();
        Uri builtUri = Uri.parse(Constants.OrderrUrl)
                .buildUpon()
                .appendPath(String.valueOf(orderId))
                .build();
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        apiRequest = ApiRequest.getInstance(context);
        apiRequest.createGetRequest(url.toString(), context, new ApiRequest.ServiceCallback<String>() {

            @Override
            public void onSuccess(String response) throws JSONException {
                JSONObject object = new JSONObject(response);
                try {
                    if (object.getBoolean("success")) {
                        JSONObject jsonObject = object.getJSONObject("data");
                        Order order = new Gson().fromJson(jsonObject.toString(), Order.class);
                        mutableLiveDataSingleOrder.setValue(order);

                    } else {
                        Toasty.error(context, object.getString("message"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFail(ANError error) throws JSONException {
                Toasty.error(context, error.getErrorBody(), Toast.LENGTH_SHORT).show();
                mutableLiveDataSingleOrder.setValue(null);
            }
        });
    }

}
