package com.almusand.aaber.ui.splash;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.almusand.aaber.model.Setting;
import com.almusand.aaber.service.ApiRequest;
import com.almusand.aaber.utils.Constants;
import com.androidnetworking.common.Priority;
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
public class SettingViewModel extends ViewModel {

    public MutableLiveData<Setting> mutableLiveData = new MutableLiveData<>();

    ApiRequest apiRequest;
    private URL url;

    public void getSettings(Context context,int userId) {
        Uri builtUri = Uri.parse(Constants.SettingUrl)
                .buildUpon()
                .appendQueryParameter("user_id", String.valueOf(userId))
                .build();
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        apiRequest = ApiRequest.getInstance(context);
        apiRequest.createGetRequest(url.toString(),context ,new ApiRequest.ServiceCallback<String>() {
            @Override
            public void onSuccess(String response) throws JSONException {
                JSONObject object = new JSONObject(response);
                try {
                    if (object.getBoolean("success")) {
                        Setting setting = new Gson().fromJson(object.getJSONObject("data").toString(), Setting.class);
                        mutableLiveData.setValue(setting);
                    } else {
                        Toasty.error(context, object.getString("message"), Toasty.LENGTH_SHORT).show();
                        mutableLiveData.setValue(null);
                    }
                } catch (JSONException e) {
                    Toasty.error(context, e.getMessage(), Toasty.LENGTH_SHORT).show();
                    mutableLiveData.setValue(null);
                    e.printStackTrace();
                }
            }

            @Override
            public void onFail(ANError error) throws JSONException {
                Toasty.error(context, error.getMessage(), Toasty.LENGTH_SHORT).show();
                mutableLiveData.setValue(null);
            }
        });
    }

}
