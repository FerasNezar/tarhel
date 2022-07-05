package com.almusand.aaber.ui.register;

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
public class RegisterViewModel extends ViewModel {

    public MutableLiveData<User> mutableLiveData = new MutableLiveData<>();

    ApiRequest apiRequest;

    public void registerUser(Context context, HashMap<String, File> fileHashMap, HashMap<String, String> stringHashMap) {
        apiRequest = ApiRequest.getInstance(context);
        apiRequest.createUploadRequest(Constants.RegisterUrl, context,fileHashMap, stringHashMap, Priority.HIGH, new ApiRequest.ServiceCallback<String>() {
            @Override
            public void onSuccess(String response) throws JSONException {
                JSONObject object = new JSONObject(response);
                try {
                    if (object.optBoolean("success") ) {
                        Gson gson = new GsonBuilder().create();
                        User user = gson.fromJson(object.getJSONObject("data").optJSONObject("user").toString(), User.class);
                        AppPreferences.saveUserToken(context, object.getJSONObject("data").getString("token"));
                        mutableLiveData.setValue(user);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFail(ANError error) throws JSONException {
                JSONObject object = new JSONObject(error.getErrorBody());
                JSONObject objectMessage = object.getJSONObject("message");
                JSONArray email=objectMessage.optJSONArray("email");
                JSONArray phone=objectMessage.optJSONArray("phone");
                if (email!=null){
                    Toasty.error(context,email.get(0).toString(), Toasty.LENGTH_SHORT).show();
                }else if (phone!=null){
                    Toasty.error(context,phone.get(0).toString(), Toasty.LENGTH_SHORT).show();
                }else {
                    Toasty.info(context, R.string.please_check_your_data, Toasty.LENGTH_SHORT).show();
                }
                mutableLiveData.setValue(null);
            }
        });
    }


}
