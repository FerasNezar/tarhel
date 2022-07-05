package com.almusand.aaber.ui.main;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.almusand.aaber.model.Categories;
import com.almusand.aaber.service.ApiRequest;
import com.almusand.aaber.utils.Constants;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

/**
 * Developed by Anas Elshwaf
 * anaselshawaf357@gmail.com
 */
public class MainViewModel extends ViewModel {

    public MutableLiveData<List<Categories>> mutableLiveCategories = new MutableLiveData<>();
    ApiRequest apiRequest;

    public void getCategories(Context context) {
        List<Categories> categoriesList = new ArrayList<>();
        apiRequest = ApiRequest.getInstance(context);
        apiRequest.createGetRequest(Constants.CategoriesUrl, context,Priority.HIGH, new ApiRequest.ServiceCallback<String>() {
            @Override
            public void onSuccess(String response) throws JSONException {
                Timber.e(response);
                JSONObject object = new JSONObject(response);
                try {
                    if (object.optBoolean("success")) {
                        JSONArray data = object.getJSONArray("data");
                        for (int i = 0; i < data.length(); i++) {
                            Categories categories = new Gson().fromJson(data.get(i).toString(), Categories.class);
                            categoriesList.add(categories);
                        }
                        mutableLiveCategories.setValue(categoriesList);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFail(ANError error) throws JSONException {
                mutableLiveCategories.setValue(null);
            }
        });
    }

}
