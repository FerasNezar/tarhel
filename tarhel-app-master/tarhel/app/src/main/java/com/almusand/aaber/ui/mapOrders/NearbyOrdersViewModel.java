package com.almusand.aaber.ui.mapOrders;

import android.content.Context;
import android.net.Uri;
import android.widget.Toast;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.almusand.aaber.model.CurrentLocation;
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
import java.util.List;

import es.dmoral.toasty.Toasty;
import timber.log.Timber;

/**
 * Developed by Anas Elshwaf
 * anaselshawaf357@gmail.com
 */
public class NearbyOrdersViewModel extends ViewModel {

    public MutableLiveData<List<Order>> mutableLiveData = new MutableLiveData<>();
    public MutableLiveData<Boolean> mutableLivePagination = new MutableLiveData<>();
    public MutableLiveData<CurrentLocation> mutableLiveLocation = new MutableLiveData<>();

    ApiRequest apiRequest;
    List<Order> orderList;
    private String url;

    public void getOrders(Context context, int page, String lat, String lng) {

        Uri builtUri = Uri.parse(Constants.OrdersProviderrUrl)
                .buildUpon()
                .appendQueryParameter("provider_lat", lat)
                .appendQueryParameter("provider_lng", lng)
                .appendQueryParameter("page", String.valueOf(page))
                .build();
        try {
            url = new URL(builtUri.toString()).toString();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        mutableLivePagination = new MutableLiveData<>();
        mutableLiveData = new MutableLiveData<>();
        orderList = new ArrayList<>();
        apiRequest = ApiRequest.getInstance(context);
        apiRequest.createGetRequest(url, context, new ApiRequest.ServiceCallback<String>() {

            @Override
            public void onSuccess(String response) throws JSONException {
                Timber.e(response);
                JSONObject object = new JSONObject(response);
                try {
                    if (object.get("success").equals(true)) {
                        Gson gson = new GsonBuilder().create();
                        JSONArray results = object.optJSONObject("data").getJSONArray("data");

                        if (page + 1 <= object.optJSONObject("data").getInt("last_page")) {
                            mutableLivePagination.setValue(true);
                        }
                        for (int i = 0; i < results.length(); i++) {
                            Order order = gson.fromJson(results.getJSONObject(i).toString(), Order.class);
                            orderList.add(order);
                        }
                        mutableLiveData.setValue(orderList);

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
                mutableLiveData.setValue(null);
            }
        });
    }

    public void setCurrentLatLng(CurrentLocation currentLocation) {
        mutableLiveLocation.setValue(currentLocation);
    }
}
