package com.almusand.aaber.ui.addComplaint;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.almusand.aaber.R;
import com.almusand.aaber.service.ApiRequest;
import com.almusand.aaber.utils.AppPreferences;
import com.almusand.aaber.utils.Constants;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;

import es.dmoral.toasty.Toasty;
import timber.log.Timber;

public class AddComplaintViewModel extends ViewModel {

    public MutableLiveData<String> mutableLiveData = new MutableLiveData<>();

    ApiRequest apiRequest;

    public void addComplaint(Context context, HashMap<String, String> map, HashMap<String, File> mapFile) {
        apiRequest = ApiRequest.getInstance(context);
        apiRequest.createUploadRequest(Constants.ComplaintsUrl,context, mapFile, map, Priority.MEDIUM, new ApiRequest.ServiceCallback<String>() {

            @Override
            public void onSuccess(String response) throws JSONException {
                Timber.e(response);
                JSONObject object = new JSONObject(response);
                try {
                    if (object.getBoolean("success")) {
                        Toasty.success(context, context.getResources().getString(R.string.msg_success_add_complaint), Toasty.LENGTH_SHORT).show();
                        mutableLiveData.postValue("done");
                    } else {
                        Toasty.error(context, object.getString("message"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    Timber.e(e.getMessage());
                    e.printStackTrace();
                }

            }

            @Override
            public void onFail(ANError error) throws JSONException {
                JSONObject object = new JSONObject(error.getErrorBody());
                Log.e("errrr", error.getErrorBody());
                Toasty.error(context, object.getString("message"), Toast.LENGTH_SHORT).show();
                mutableLiveData.setValue(null);
            }
        });
    }

}
