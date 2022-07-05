package com.almusand.aaber.ui.sendOrder;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.almusand.aaber.model.Order;
import com.almusand.aaber.service.ApiRequest;
import com.almusand.aaber.utils.AppPreferences;
import com.almusand.aaber.utils.Constants;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;

import es.dmoral.toasty.Toasty;
import timber.log.Timber;

public class SendOrderViewModel extends ViewModel {

    public MutableLiveData<Order> mutableLiveData = new MutableLiveData<Order>();
    ApiRequest apiRequest;

    public void sendOrder(Context context, HashMap<String, String> map, HashMap<String, File> fileHashMap) {

        apiRequest = ApiRequest.getInstance(context);
        apiRequest.createUploadRequest(Constants.OrderrUrl, context,fileHashMap, map, Priority.HIGH
                , new ApiRequest.ServiceCallback<String>() {
            @Override
            public void onSuccess(String response) throws JSONException {
                Log.e("response",response);
                JSONObject object = new JSONObject(response);
                try {
                    if (object.getBoolean("success")) {
                        Gson gson = new GsonBuilder().create();
                        Order order = gson.fromJson(object.getJSONObject("data").toString(), Order.class);
                        mutableLiveData.setValue(order);

                    } else {
                        Toasty.error(context, object.getString("message"), Toast.LENGTH_SHORT).show();
                        mutableLiveData.setValue(null);
                    }
                } catch (JSONException e) {
                    Timber.e(e.toString());
                    mutableLiveData.setValue(null);
                    e.printStackTrace();
                }
            }

            @Override
            public void onFail(ANError error) throws JSONException {
                JSONObject object = new JSONObject(error.getErrorBody());
                Toasty.error(context, error.getErrorBody(), Toast.LENGTH_SHORT).show();
                mutableLiveData.setValue(null);

            }
        });
    }


}
