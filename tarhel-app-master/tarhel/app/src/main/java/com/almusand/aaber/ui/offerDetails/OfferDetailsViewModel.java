package com.almusand.aaber.ui.offerDetails;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.almusand.aaber.model.Offer;
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
public class OfferDetailsViewModel extends ViewModel {

    public MutableLiveData<Offer> mutableLiveData = new MutableLiveData<Offer>();
    ApiRequest apiRequest;
    URL url;

    public void acceptOffer(Context context, String offerId) {

        Uri builtUri = Uri.parse(Constants.AcceptOfferrUrl)
                .buildUpon()
                .appendQueryParameter("offer_id",offerId)
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
                        JSONObject jsonObject = object.getJSONObject("data");
                        Offer offer = new Gson().fromJson(jsonObject.toString(), Offer.class);
                        mutableLiveData.setValue(offer);

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

    public void getOffer(Context context, String offerId) {

        Uri builtUri = Uri.parse(Constants.sendOfferUrl)
                .buildUpon()
                .appendQueryParameter("offer_id",offerId)
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
                        Offer offer = new Gson().fromJson(jsonObject.toString(), Offer.class);
                        mutableLiveData.setValue(offer);

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
