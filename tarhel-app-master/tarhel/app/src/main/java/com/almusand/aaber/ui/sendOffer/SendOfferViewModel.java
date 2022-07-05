package com.almusand.aaber.ui.sendOffer;

import android.content.Context;
import android.net.Uri;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.almusand.aaber.model.Offer;
import com.almusand.aaber.service.ApiRequest;
import com.almusand.aaber.utils.Constants;
import com.androidnetworking.error.ANError;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import es.dmoral.toasty.Toasty;

public class SendOfferViewModel extends ViewModel {

    public MutableLiveData<String> mutableLiveData = new MutableLiveData<String>();
    public MutableLiveData<Offer> mutableLiveDataUpdate = new MutableLiveData<Offer>();

    ApiRequest apiRequest;
    private URL url;

    public void SendOffer(Context context, HashMap<String, String> hashMap) {

        mutableLiveData = new MutableLiveData<>();
        apiRequest = ApiRequest.getInstance(context);
        apiRequest.createPostRequest(Constants.sendOfferUrl, context, hashMap, new ApiRequest.ServiceCallback<String>() {
            @Override
            public void onSuccess(String response) throws JSONException {
                JSONObject object = new JSONObject(response);
                try {
                    if (object.getBoolean("success")) {
                        mutableLiveData.setValue("done");

                    } else {
                        Toasty.error(context, object.getString("message"), Toasty.LENGTH_SHORT).show();
                        mutableLiveData.setValue(null);
                    }
                } catch (JSONException e) {
                    mutableLiveData.setValue(e.getMessage());
                    e.printStackTrace();
                }
            }

            @Override
            public void onFail(ANError error) throws JSONException {
                JSONObject object = new JSONObject(error.getErrorBody());
                Toasty.info(context, object.getString("message"), Toasty.LENGTH_SHORT).show();
                mutableLiveData.setValue(null);
            }
        });
    }

    public void UpdateOffer(Context context, String offerId, String price) {

        Uri builtUri = Uri.parse(Constants.sendOfferUrl)
                .buildUpon()
                .appendPath(offerId)
                .appendQueryParameter("price", price)
                .build();
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        mutableLiveDataUpdate = new MutableLiveData<Offer>();
        apiRequest = ApiRequest.getInstance(context);
        apiRequest.createPutRequest(url.toString(), context, new ApiRequest.ServiceCallback<String>() {
            @Override
            public void onSuccess(String response) throws JSONException {
                JSONObject object = new JSONObject(response);
                try {
                    if (object.getBoolean("success")) {
                        mutableLiveDataUpdate.setValue(new Gson().fromJson(object.getJSONObject("data").toString(), Offer.class));
                    } else {
                        Toasty.error(context, object.getString("message"), Toasty.LENGTH_SHORT).show();
                        mutableLiveDataUpdate.setValue(null);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFail(ANError error) throws JSONException {
                JSONObject object = new JSONObject(error.getErrorBody());
                Toasty.info(context, object.getString("message"), Toasty.LENGTH_SHORT).show();
                mutableLiveDataUpdate.setValue(null);
            }
        });
    }


}