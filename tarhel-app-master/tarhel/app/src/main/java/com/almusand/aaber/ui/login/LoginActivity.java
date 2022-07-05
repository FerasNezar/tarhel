package com.almusand.aaber.ui.login;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.almusand.aaber.R;
import com.almusand.aaber.custom.categories.ForgetPasswordDialog;
import com.almusand.aaber.model.User;
import com.almusand.aaber.ui.base.BaseActivity;
import com.almusand.aaber.ui.main.MainActivityClient;
import com.almusand.aaber.ui.main.MainActivityProvider;
import com.almusand.aaber.ui.register.RegisterActivity;
import com.almusand.aaber.utils.AppPreferences;
import com.almusand.aaber.utils.LocaleManager;
import com.almusand.aaber.utils.ProgressButton;
import com.almusand.aaber.utils.Utilities;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mikhaellopez.circularimageview.CircularImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import es.dmoral.toasty.Toasty;

public class LoginActivity extends BaseActivity implements View.OnClickListener {

    private TextView tvNotHaveAccount;
    private CoordinatorLayout layout;
    private TextView tvForgetpassword;
    private EditText etPassword;
    private Toolbar toolbar;
    private CircularImageView imgPofile;
    private EditText etPhone;
    private View btLogin;
    private ProgressButton progressButton;
    private String txtPhone, txtPassword;
    private LoginViewModel viewModel;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setUpToolbar();

        viewModel = new ViewModelProvider(this).get(LoginViewModel.class);

        initialViews();


    }

    private void initialViews() {
        layout = findViewById(R.id.layout);
        toolbar = findViewById(R.id.toolbar);
        imgPofile = findViewById(R.id.img_pofile);
        etPhone = findViewById(R.id.et_phone);
        etPassword = findViewById(R.id.et_password);
        btLogin = findViewById(R.id.bt_login);
        tvNotHaveAccount = findViewById(R.id.tv_not_have_account);
        tvForgetpassword = findViewById(R.id.tv_forgetpassword);

        progressButton = new ProgressButton(LoginActivity.this, btLogin);
        progressButton.setButtonTittle(LoginActivity.this.getResources().getString(R.string.login));

        Glide.with(this)
                .load(this.getResources().getDrawable(R.drawable.aaber_logo))
                .into(imgPofile);

        btLogin.setOnClickListener(this);
        tvNotHaveAccount.setOnClickListener(this);
        tvForgetpassword.setOnClickListener(this);
    }

    private void setUpToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.bt_login:

                if (checkInfo()) {
                    progressButton.buttonActivated();
                    HashMap<String, String> map = new HashMap<>();
                    map.put("phone", txtPhone);
                    map.put("password", txtPassword);
                    viewModel.Login(LoginActivity.this, map);
                    viewModel.mutableLiveData.observe(this, new Observer<String>() {
                        @Override
                        public void onChanged(String response) {
                            if (response.equals("error")) {
                                btLogin.setEnabled(true);
                                progressButton.buttonDactivated();
                                progressButton.setButtonTittle(LoginActivity.this.getResources().getString(R.string.login));
                                Toasty.error(LoginActivity.this, LoginActivity.this.getResources().getText(R.string.msg_login_failed), Toast.LENGTH_SHORT).show();
                            } else {
                                handleLoginResponse(response);

                            }
                        }
                    });
                }

                break;

            case R.id.tv_not_have_account:
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
                break;

            case R.id.tv_forgetpassword:
                new ForgetPasswordDialog(LoginActivity.this).show();
                break;


        }
    }

    private void handleLoginResponse(String response) {
        try {
            JSONObject object = new JSONObject(response);
            if (object.optJSONObject("data").optJSONObject("user") != null) {
                Gson gson = new GsonBuilder().create();
                user = gson.fromJson(object.optJSONObject("data").getJSONObject("user").toString(), User.class);
                AppPreferences.saveUserToken(LoginActivity.this, object.optJSONObject("data").getString("token"));
            } else {
                Toasty.error(LoginActivity.this, object.getString("message"), Toasty.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        AppPreferences.setUser(LoginActivity.this, new Gson().toJson(user));

        checkFcmToken();

    }

    private void checkFcmToken() {

        if (AppPreferences.getFCMToken(this) != null) {
            viewModel.updateToken(this, AppPreferences.getFCMToken(this), user.getEmail(), user.getPhone());
            viewModel.mutableLiveUpdateToken.observe(this, new Observer<String>() {
                @Override
                public void onChanged(String s) {
                    if (s != null && s.equals("true")) {
                        btLogin.setEnabled(true);
                        progressButton.buttonFinished();
                        goToMainActivity();
                    }
                }
            });

        }
    }

    private void goToMainActivity() {
        switch (user.getType()) {
            case "user":
                startActivity(new Intent(LoginActivity.this, MainActivityClient.class)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                finish();
                break;
            case "provider":
                startActivity(new Intent(LoginActivity.this, MainActivityProvider.class)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                finish();
                break;
        }
    }

    private boolean checkInfo() {

        txtPassword = etPassword.getText().toString();
        txtPhone = etPhone.getText().toString();

        if (TextUtils.isEmpty(txtPhone)) {
            Utilities.showSnackbar(this.getResources().getString(R.string.msg_filed_cant_empty), LoginActivity.this, layout);
            return false;
        }

        if (TextUtils.isEmpty(txtPassword)) {
            Utilities.showSnackbar(this.getResources().getString(R.string.msg_wrong_password), LoginActivity.this, layout);
            return false;
        }

        return true;
    }


    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleManager.setLocale(base));
    }

}
