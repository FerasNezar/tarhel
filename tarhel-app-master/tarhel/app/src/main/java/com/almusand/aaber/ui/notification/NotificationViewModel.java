package com.almusand.aaber.ui.notification;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.almusand.aaber.model.Notification;
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

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import es.dmoral.toasty.Toasty;

public class NotificationViewModel extends ViewModel {

    public MutableLiveData<List<Notification>> mutableLiveData = new MutableLiveData<>();
    public MutableLiveData<String> mutableLiveStatus = new MutableLiveData<>();
    public MutableLiveData<String> mutableLiveError = new MutableLiveData<>();
    public MutableLiveData<Boolean> mutableLivePagination = new MutableLiveData<>();


    ApiRequest apiRequest;
    List<Notification> notificationList;
    private URL url;

    public void getMyNotifications(Context context, int page) {

        mutableLivePagination = new MutableLiveData<>();
        mutableLiveData = new MutableLiveData<>();
        notificationList = new ArrayList<>();
        apiRequest = ApiRequest.getInstance(context);
        apiRequest.createGetRequest(Constants.userNotificationsUrl, context, new ApiRequest.ServiceCallback<String>() {

            @Override
            public void onSuccess(String response) throws JSONException {
                JSONObject object = new JSONObject(response);
                JSONObject data = object.getJSONObject("data");
                try {
                    if (data.optJSONObject("notifications") != null) {
                        Gson gson = new GsonBuilder().create();
                        JSONObject jsonObject = data.getJSONObject("notifications");

                        if (page + 1 <= jsonObject.getInt("last_page")) {
                            mutableLivePagination.setValue(true);
                        }

                        JSONArray dataArray = jsonObject.getJSONArray("data");
                        for (int i = 0; i < dataArray.length(); i++) {
                            Notification notification = gson.fromJson(dataArray.getJSONObject(i).toString(), Notification.class);
                            notificationList.add(notification);
                        }
                        mutableLiveData.setValue(notificationList);

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

    public void markNotificationRead(Context context, String id) {

        HashMap<String,String>map=new HashMap<>();
        map.put("id",id);
        Uri builtUri = Uri.parse(Constants.userNotificationsUrl)
                .buildUpon()
                .appendPath("readed")
                .build();
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        mutableLiveStatus = new MutableLiveData<>();
        apiRequest = ApiRequest.getInstance(context);
        apiRequest.createPostRequest(url.toString(), context, map, new ApiRequest.ServiceCallback<String>() {

            @Override
            public void onSuccess(String response) throws JSONException {
                JSONObject object = new JSONObject(response);
                try {
                    if (object.getBoolean("success")) {
                        mutableLiveStatus.setValue("done");

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


}
