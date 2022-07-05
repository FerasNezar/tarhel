package com.almusand.aaber.ui.balance;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.almusand.aaber.R;
import com.almusand.aaber.model.User;
import com.almusand.aaber.service.ApiRequest;
import com.almusand.aaber.utils.AppPreferences;
import com.almusand.aaber.utils.Constants;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;

import es.dmoral.toasty.Toasty;
import timber.log.Timber;

/**
 * Developed by Anas Elshwaf
 * anaselshawaf357@gmail.com
 */
public class BalanceViewModel extends ViewModel {

    public MutableLiveData<String> mutableLiveData = new MutableLiveData<>();
    public MutableLiveData<String> mutableLiveError = new MutableLiveData<>();


    public void getBalances(Context context) {
        mutableLiveData=new MutableLiveData<>();
        ApiRequest apiRequest = ApiRequest.getInstance(context);
        apiRequest.createGetRequest(Constants.BalancesUrl,  context, new ApiRequest.ServiceCallback<String>() {
            @Override
            public void onSuccess(String response) throws JSONException {
                Log.e("response",response);
                JSONObject object = new JSONObject(response);
                try {
                    if (object.get("success").equals(true)) {
                        mutableLiveData.setValue(response);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFail(ANError error) throws JSONException {
                Timber.e(error.getErrorBody());
                JSONObject object = new JSONObject(error.getErrorBody());
                Toasty.error(context, object.getString("message"), Toasty.LENGTH_SHORT).show();
            }
        });
    }


}
