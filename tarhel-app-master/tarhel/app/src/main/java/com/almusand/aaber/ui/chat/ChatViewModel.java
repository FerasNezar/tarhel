package com.almusand.aaber.ui.chat;

import android.content.Context;
import android.net.Uri;
import android.widget.Toast;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.almusand.aaber.model.MessageModel;
import com.almusand.aaber.model.Order;
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
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import es.dmoral.toasty.Toasty;

import static android.widget.Toast.LENGTH_SHORT;

/**
 * Developed by Anas Elshwaf
 * anaselshawaf357@gmail.com
 */
public class ChatViewModel extends ViewModel {

    public MutableLiveData<List<MessageModel>> mutableLiveData = new MutableLiveData<>();
    public MutableLiveData<Order> mutableLiveDataOrder = new MutableLiveData<>();
    public MutableLiveData<String> mutableLiveMessage = new MutableLiveData<>();
    public MutableLiveData<Boolean> mutableLivePagination = new MutableLiveData<>();

    private List<MessageModel> chatList;

    ApiRequest apiRequest;
    URL url;

    public void getConversation(Context context, String conversatonID, int page) {

        mutableLivePagination = new MutableLiveData<>();
        mutableLiveData = new MutableLiveData<>();
        chatList = new ArrayList<>();

        Uri builtUri = Uri.parse(Constants.conversationsUrl)
                .buildUpon()
                .appendPath(conversatonID)
                .appendQueryParameter("page", String.valueOf(page))
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
                        JSONObject data = object.getJSONObject("data");
                        if ((data.optJSONObject("messages")) != null) {
                            JSONObject messages = data.getJSONObject("messages");

                            if (page + 1 <= messages.getInt("last_page")) {
                                mutableLivePagination.setValue(true);
                            }
                            JSONArray jsonArray = messages.getJSONArray("data");

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                MessageModel messageModel = new Gson().fromJson(jsonObject.toString(), MessageModel.class);
                                chatList.add(messageModel);
                            }
                            mutableLiveData.setValue(chatList);
                        } else {
                            mutableLiveData.setValue(new ArrayList<MessageModel>());

                        }
                    } else {
                        Toasty.error(context, object.getString("message"), Toasty.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFail(ANError error) throws JSONException {
                mutableLiveData.setValue(null);
            }
        });
    }

    public void sendMessage(Context context, HashMap<String, String> stringHashMap, HashMap<String, File> fileHashMap) {
        mutableLiveMessage = new MutableLiveData<>();
        apiRequest = ApiRequest.getInstance(context);
        apiRequest.createUploadRequest(Constants.SendMessageUrl, context, fileHashMap, stringHashMap, Priority.MEDIUM, new ApiRequest.ServiceCallback<String>() {
            @Override
            public void onSuccess(String response) throws JSONException {
                JSONObject object = new JSONObject(response);
                try {
                    if (object.get("success").equals(true)) {
                        mutableLiveMessage.setValue("done");
                    } else {
                        Toast.makeText(context, object.getString("message"), LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFail(ANError error) throws JSONException {
                mutableLiveMessage.setValue(null);
            }
        });
    }

    public void makeOrderDelivered(Context context, int orderId, HashMap<String, String> map) {

        Uri builtUri = Uri.parse(Constants.deliverorderUrl)
                .buildUpon()
                .appendPath(String.valueOf(orderId))
                .build();
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        apiRequest = ApiRequest.getInstance(context);
        apiRequest.createPostRequest(url.toString(), context, map, new ApiRequest.ServiceCallback<String>() {
            @Override
            public void onSuccess(String response) throws JSONException {
                JSONObject object = new JSONObject(response);
                try {
                    if (object.getBoolean("success")) {
                        Gson gson = new GsonBuilder().create();
                        JSONObject jsonObject = object.getJSONObject("data");
                        Order order = gson.fromJson(jsonObject.toString(), Order.class);
                        Toasty.success(context, object.getString("message"), Toasty.LENGTH_SHORT).show();
                        mutableLiveDataOrder.setValue(order);

                    } else {
                        Toasty.error(context, object.getString("message"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFail(ANError error) throws JSONException {
                Toasty.error(context, error.getErrorBody(), Toasty.LENGTH_SHORT).show();
                mutableLiveDataOrder.setValue(null);
            }
        });
    }


}
