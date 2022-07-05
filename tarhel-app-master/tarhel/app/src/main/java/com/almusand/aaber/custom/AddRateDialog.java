package com.almusand.aaber.custom;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.almusand.aaber.R;
import com.almusand.aaber.model.User;
import com.almusand.aaber.service.ApiRequest;
import com.almusand.aaber.utils.AppPreferences;
import com.almusand.aaber.utils.Constants;
import com.almusand.aaber.utils.ProgressButton;
import com.androidnetworking.error.ANError;
import com.willy.ratingbar.BaseRatingBar;
import com.willy.ratingbar.ScaleRatingBar;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import es.dmoral.toasty.Toasty;
import timber.log.Timber;

/**
 * Developed by Anas Elshwaf
 * anaselshawaf357@gmail.com
 */
public class AddRateDialog extends Dialog {

    View view;
    ApiRequest apiRequest;

    private TextView tvRate;
    private ScaleRatingBar rtReview;
    private ProgressButton progressButton;

    private User user;
    private String rate;

    private OnAddRate onAddRate;

    public interface OnAddRate {
        void OnAddRate(String rate);
    }


    public AddRateDialog(@NonNull Context context, User user, OnAddRate  onAddRate) {
        super(context);
        this.user = user;
        this.onAddRate = onAddRate;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        this.setCancelable(true);
        this.setContentView(R.layout.layout_rate_dialog);
        getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

        initialViews();

        rtReview.setOnRatingChangeListener(new BaseRatingBar.OnRatingChangeListener() {
            @Override
            public void onRatingChange(BaseRatingBar ratingBar, float rating, boolean fromUser) {
                rate = String.valueOf(rating);
            }
        });

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (rate != null) {
                    addRate(rate);
                }
            }
        });

    }

    private void initialViews() {
        tvRate = findViewById(R.id.tv_rate);
        rtReview = findViewById(R.id.rt_review);

        if (user.getType().equals("user")) {
            tvRate.append(" " + getContext().getResources().getString(R.string.provider));
        } else {
            tvRate.append(" " + getContext().getResources().getString(R.string.client));
        }

        view = findViewById(R.id.bt_rate);
        progressButton = new ProgressButton(getContext(), view);
        progressButton.setButtonTittle(getContext().getResources().getString(R.string.submit));

    }


    private void addRate(String rate) {
        view.setEnabled(false);
        progressButton.buttonActivated();
        HashMap<String, String> map = new HashMap<>();
        map.put("rate", rate);
        map.put("user_id", String.valueOf(user.getId()));

        apiRequest = ApiRequest.getInstance(getContext());
        apiRequest.createPostRequest(Constants.RateUserUrl, getContext(), map, new ApiRequest.ServiceCallback<String>() {

            @Override
            public void onSuccess(String response) throws JSONException {
                progressButton.buttonFinished();
                JSONObject object = new JSONObject(response);
                try {
                    if (object.getBoolean("success")) {
                        Toasty.success(getContext(), getContext().getResources().getString(R.string.msg_success_add_rate), Toasty.LENGTH_SHORT).show();
                        onAddRate.OnAddRate(rate);
                        dismiss();

                    } else {
                        progressButton.buttonDactivated();
                        Toasty.error(getContext(), object.getString("message"), Toast.LENGTH_SHORT).show();
                        dismiss();
                    }
                } catch (JSONException e) {
                    Timber.e(e.getMessage());
                    e.printStackTrace();
                }

            }

            @Override
            public void onFail(ANError error) throws JSONException {
                JSONObject object = new JSONObject(error.getErrorBody());
                progressButton.buttonDactivated();
                Toasty.info(getContext(), object.getString("message"), Toast.LENGTH_SHORT).show();
                dismiss();
            }
        });
    }
}
