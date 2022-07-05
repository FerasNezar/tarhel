package com.almusand.aaber.ui.login;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.almusand.aaber.service.ApiRequest;
import com.almusand.aaber.utils.AppPreferences;
import com.almusand.aaber.utils.Constants;
import com.androidnetworking.error.ANError;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import timber.log.Timber;

public class LoginViewModel extends ViewModel {

    public MutableLiveData<String> mutableLiveData = new MutableLiveData<String>();
    public MutableLiveData<String> mutableLiveUpdateToken = new MutableLiveData<>();

    ApiRequest apiRequest;

    public void Login(Context context, HashMap<String, String> map) {
        mutableLiveData = new MutableLiveData<>();
        apiRequest = ApiRequest.getInstance(context);
        apiRequest.createPostRequest(Constants.LoginUrl, context,map, new ApiRequest.ServiceCallback<String>() {
            @Override
            public void onSuccess(String response) throws JSONException {
              mutableLiveData.setValue(response);
            }

            @Override
            public void onFail(ANError error) throws JSONException {
                mutableLiveData.setValue("error");
            }
        });
    }

    public void updateToken(Context context, String s,String email,String phone) {
        ApiRequest apiRequest = ApiRequest.getInstance(context);
        HashMap<String, String> map = new HashMap<>();
        map.put("device_id", s);
        map.put("email", email);
        map.put("phone", phone);
        apiRequest.createPostRequest(Constants.UpdateProfileUrl, context, map, new ApiRequest.ServiceCallback<String>() {
            @Override
            public void onSuccess(String response) throws JSONException {
                JSONObject object = new JSONObject(response);
                try {
                    if (object.get("success").equals(true)) {
                        mutableLiveUpdateToken.setValue("true");
                    } else {
                        Toast.makeText(context, object.getString("message"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFail(ANError error) throws JSONException {

            }
        });
    }


}
