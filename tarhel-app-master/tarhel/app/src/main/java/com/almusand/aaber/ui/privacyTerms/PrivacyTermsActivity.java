package com.almusand.aaber.ui.privacyTerms;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.almusand.aaber.R;
import com.almusand.aaber.model.Setting;
import com.almusand.aaber.ui.base.BaseActivity;
import com.almusand.aaber.utils.AppPreferences;
import com.almusand.aaber.utils.LocaleManager;
import com.google.gson.Gson;
import com.mikhaellopez.circularimageview.CircularImageView;

public class PrivacyTermsActivity extends BaseActivity implements View.OnClickListener {

    private ImageView imgBack;
    private CircularImageView imgLogo;
    private TextView tvPrivacyTerms;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy_terms);

        initViews();

    }

    private void initViews() {
        imgBack = findViewById(R.id.img_back);
        imgLogo = findViewById(R.id.img_logo);
        tvPrivacyTerms = findViewById(R.id.tv_privacy_terms);

        Setting setting = new Gson().fromJson(AppPreferences.getSetting(this), Setting.class);

        if (LocaleManager.getLocale(this.getResources()).getLanguage().equals("ar")) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                tvPrivacyTerms.setText(Html.fromHtml(setting.getPrivacyAr(), Html.FROM_HTML_MODE_COMPACT));
            } else {
                tvPrivacyTerms.setText(Html.fromHtml(setting.getPrivacyAr()));
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                tvPrivacyTerms.setText(Html.fromHtml(setting.getPrivacyEn(), Html.FROM_HTML_MODE_COMPACT));
            } else {
                tvPrivacyTerms.setText(Html.fromHtml(setting.getPrivacyEn()));
            }
        }


        imgBack.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.img_back:
                finish();
                break;

        }
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleManager.setLocale(base));
    }

}
