package com.almusand.aaber.ui.myOffer;

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
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpCookie;
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
public class MyOffersViewModel extends ViewModel {

    public MutableLiveData<List<Offer>> mutableLiveData = new MutableLiveData<>();
    public MutableLiveData<Offer> mutableLiveDataOffer = new MutableLiveData<>();
    public MutableLiveData<String> mutableLiveStatus = new MutableLiveData<>();
    public MutableLiveData<Boolean> mutableLivePagination = new MutableLiveData<>();

    ApiRequest apiRequest;
    List<Offer> offerList;
    private URL url;

    public void getMyOffers(Context context, int page) {
        Uri builtUri = Uri.parse(Constants.sendOfferUrl)
                .buildUpon()
                .appendPath("history")
                .appendQueryParameter("page",String.valueOf(page))
                .build();
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        offerList = new ArrayList<>();
        mutableLivePagination = new MutableLiveData<>();
        mutableLiveData = new MutableLiveData<>();
        apiRequest = ApiRequest.getInstance(context);
        apiRequest.createPostRequest(url.toString(), context, new ApiRequest.ServiceCallback<String>() {

            @Override
            public void onSuccess(String response) throws JSONException {
                JSONObject object = new JSONObject(response);
                try {
                    if (object.optJSONArray("data")!=null) {
                        Gson gson = new GsonBuilder().create();
                        JSONObject jsonObject = object.getJSONObject("meta");

                        if (page + 1 <= jsonObject.getInt("last_page")) {
                            mutableLivePagination.setValue(true);
                        }

                        JSONArray data = object.getJSONArray("data");
                        for (int i = 0; i < data.length(); i++) {
                            Offer offer = gson.fromJson(data.getJSONObject(i).toString(), Offer.class);
                            offerList.add(offer);
                        }

                        mutableLiveData.setValue(offerList);

                    } else {
                        mutableLiveData.setValue(null);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFail(ANError error) throws JSONException {
                JSONObject object = new JSONObject(error.getErrorBody());
                Toasty.error(context, object.getString("message"), Toast.LENGTH_SHORT).show();
                mutableLiveData.setValue(null);

            }
        });
    }

    public void getOfferDetails(Context context, int offerId) {
        Uri builtUri = Uri.parse(Constants.sendOfferUrl)
                .buildUpon()
                .appendPath(String.valueOf(offerId))
                .build();
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        mutableLiveData = new MutableLiveData<>();
        apiRequest = ApiRequest.getInstance(context);
        apiRequest.createGetRequest(url.toString(), context, new ApiRequest.ServiceCallback<String>() {

            @Override
            public void onSuccess(String response) throws JSONException {
                JSONObject object = new JSONObject(response);
                try {
                    if (object.getBoolean("success")) {
                        Gson gson = new GsonBuilder().create();
                        JSONObject jsonObject = object.getJSONObject("data");
                        Offer offer=new Gson().fromJson(jsonObject.toString(),Offer.class);
                        mutableLiveDataOffer.setValue(offer);

                    } else {
                        mutableLiveDataOffer.setValue(null);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFail(ANError error) throws JSONException {
                JSONObject object = new JSONObject(error.getErrorBody());
                Toasty.error(context, object.getString("message"), Toast.LENGTH_SHORT).show();
                mutableLiveDataOffer.setValue(null);

            }
        });
    }


    public void changeOfferStatus(Context context, String offerId) {

        Uri builtUri = Uri.parse(Constants.sendOfferUrl)
                .buildUpon()
                .appendPath("canceled")
                .appendPath(offerId)
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
                Timber.e(response);
                JSONObject object = new JSONObject(response);
                try {
                    if (object.getBoolean("success")) {
                        Gson gson = new GsonBuilder().create();
                        JSONObject jsonObject = object.getJSONObject("data");
                        Offer offer = gson.fromJson(jsonObject.toString(), Offer.class);
                        mutableLiveStatus.setValue("done");

                    } else {
                        Toasty.error(context, object.getString("message"), Toast.LENGTH_SHORT).show();
                        mutableLiveStatus.setValue(null);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFail(ANError error) throws JSONException {
                Toasty.error(context, error.getErrorBody(), Toast.LENGTH_SHORT).show();
                mutableLiveStatus.setValue(null);
            }
        });
    }

}
