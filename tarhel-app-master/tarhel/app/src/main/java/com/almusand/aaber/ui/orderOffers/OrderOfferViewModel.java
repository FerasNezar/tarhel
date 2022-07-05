package com.almusand.aaber.ui.orderOffers;

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

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;

import es.dmoral.toasty.Toasty;

/**
 * Developed by Anas Elshwaf
 * anaselshawaf357@gmail.com
 */

public class OrderOfferViewModel extends ViewModel {

    public MutableLiveData<Order> mutableLiveData = new MutableLiveData<Order>();
    ApiRequest apiRequest;
    URL url;

    public void getSingleOrder(Context context, String orderId) {
        mutableLiveData = new MutableLiveData<Order>();
        Uri builtUri = Uri.parse(Constants.OrderrUrl)
                .buildUpon()
                .appendPath(orderId)
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
                        mutableLiveData.setValue(order);
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

}
