package com.almusand.aaber.custom.categories;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.NonNull;

import com.almusand.aaber.R;
import com.almusand.aaber.service.ApiRequest;
import com.almusand.aaber.utils.Constants;
import com.almusand.aaber.utils.ProgressButton;
import com.androidnetworking.error.ANError;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import es.dmoral.toasty.Toasty;

/**
 * Developed by Anas Elshwaf
 * anaselshawaf357@gmail.com
 */
public class ForgetPasswordDialog extends Dialog {

    private final Context context;
    View view;
    ApiRequest apiRequest;
    private TextInputEditText etEmail;
    private ProgressButton progressButton;


    public ForgetPasswordDialog(@NonNull Context context) {
        super(context);
        this.context=context;

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        this.setCancelable(true);
        this.setContentView(R.layout.layout_forget_password_dialog);
        getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

        initialViews();

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!etEmail.getText().toString().isEmpty()) {
                    forgetPassword(etEmail.getText().toString());
                }
            }
        });

    }

    private void initialViews() {
        etEmail = findViewById(R.id.et_email);

        view = findViewById(R.id.bt_save);
        progressButton = new ProgressButton(getContext(), view);
        progressButton.setButtonTittle(getContext().getResources().getString(R.string.send));

    }

    @Override
    public void onStart() {
        super.onStart();

    }

    private void forgetPassword(String txtEmail) {
        view.setEnabled(false);
        progressButton.buttonActivated();

        HashMap<String, String> map = new HashMap<>();
        map.put("email", txtEmail);

        apiRequest = ApiRequest.getInstance(getContext());
        apiRequest.createPostRequest(Constants.ForgetPassUrl, getContext(), map, new ApiRequest.ServiceCallback<String>() {

            @Override
            public void onSuccess(String response) throws JSONException {
                progressButton.buttonFinished();
                JSONObject object = new JSONObject(response);
                try {
                    if (object.getBoolean("success")) {
                        Toasty.success(getContext(),object.getString("message"), Toasty.LENGTH_SHORT).show();
                        dismiss();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFail(ANError error) throws JSONException {
                JSONObject object = new JSONObject(error.getErrorBody());
                JSONObject errors=object.getJSONObject("errors");
                if (errors.has("email")){
                    Toasty.error(getContext(), errors.getJSONArray("email").get(0).toString(), Toasty.LENGTH_SHORT).show();
                }else {
                    Toasty.error(getContext(), object.getString("message"), Toasty.LENGTH_SHORT).show();
                }
                view.setEnabled(true);
                progressButton.buttonDactivated();
            }
        });
    }
}
