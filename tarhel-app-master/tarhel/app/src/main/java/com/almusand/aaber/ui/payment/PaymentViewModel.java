package com.almusand.aaber.ui.payment;

import android.content.Context;
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

import es.dmoral.toasty.Toasty;
import timber.log.Timber;

public class PaymentViewModel extends ViewModel {

    public MutableLiveData<String> mutableLiveData = new MutableLiveData<>();

    public void payOnlineOrCash(Context context, HashMap<String,String> map) {
        mutableLiveData=new MutableLiveData<>();
        ApiRequest apiRequest = ApiRequest.getInstance(context);
        apiRequest.createPostRequest(Constants.BalancesUrl, context, map, new ApiRequest.ServiceCallback<String>() {
            @Override
            public void onSuccess(String response) throws JSONException {
                Timber.i(response);
                JSONObject object = new JSONObject(response);
                try {
                    if (object.get("success").equals(true)) {
                        Toasty.success(context, object.getString("message"), Toasty.LENGTH_SHORT).show();
                        mutableLiveData.setValue("success");

                    } else {
                        Toast.makeText(context, object.getString("message"), Toast.LENGTH_SHORT).show();
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
                mutableLiveData.setValue("failed");
            }
        });
    }


}
