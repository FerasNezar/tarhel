package com.almusand.aaber.ui.language;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.almusand.aaber.R;
import com.almusand.aaber.model.Setting;
import com.almusand.aaber.model.User;
import com.almusand.aaber.ui.base.BaseActivity;
import com.almusand.aaber.ui.intro.WelcomeActivity;
import com.almusand.aaber.ui.login.LoginActivity;
import com.almusand.aaber.ui.main.MainActivityClient;
import com.almusand.aaber.ui.main.MainActivityProvider;
import com.almusand.aaber.utils.AppPreferences;
import com.almusand.aaber.utils.LocaleManager;
import com.almusand.aaber.utils.PrefManager;
import com.google.gson.Gson;

/**
 * Developed by Anas Elshwaf
 * anaselshawaf357@gmail.com
 */
public class LanguageActivity extends BaseActivity implements View.OnClickListener {

    private LinearLayout lyLangAr;
    private LinearLayout lyLangEn;
    private LinearLayout lyNext;
    private PrefManager prefManager;
    private User user;
    private Setting setting;

    private String lang;
    private Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        prefManager = new PrefManager(this);

        user = new Gson().fromJson(AppPreferences.getUser(this), User.class);

        lang = LocaleManager.getLocale(this.getResources()).getLanguage();

        if (!prefManager.isFirstTimeLaunch()) {
            launchHomeScreen();
            finish();
        }

        // Making notification bar transparent
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }

        setContentView(R.layout.activity_language);

        initialViews();


    }

    private void initialViews() {
        lyLangAr = findViewById(R.id.ly_lang_ar);
        lyLangEn = findViewById(R.id.ly_lang_en);
        lyNext = findViewById(R.id.ly_next);

        switch (lang) {

            case "ar":
                lyLangEn.setBackground(this.getResources().getDrawable(R.drawable.background_lang_border));
                lyLangAr.setBackground(this.getResources().getDrawable(R.drawable.background_gray_with_border));

                break;

            case "en":
                lyLangAr.setBackground(this.getResources().getDrawable(R.drawable.background_lang_border));
                lyLangEn.setBackground(this.getResources().getDrawable(R.drawable.background_gray_with_border));

                break;

        }

        lyLangAr.setOnClickListener(this);
        lyLangEn.setOnClickListener(this);
        lyNext.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.ly_lang_ar:
                lyLangEn.setBackground(this.getResources().getDrawable(R.drawable.background_lang_border));
                lyLangAr.setBackground(this.getResources().getDrawable(R.drawable.background_gray_with_border));
                lang = "ar";
                setNewLocale(LocaleManager.HINDI);
                break;

            case R.id.ly_lang_en:
                lyLangAr.setBackground(this.getResources().getDrawable(R.drawable.background_lang_border));
                lyLangEn.setBackground(this.getResources().getDrawable(R.drawable.background_gray_with_border));
                lang = "en";
                setNewLocale(LocaleManager.ENGLISH);

                break;

            case R.id.ly_next:

                prefManager.setFirstTimeLaunch(false);
                startActivity(new Intent(LanguageActivity.this, WelcomeActivity.class));
                finish();

                break;

        }

    }

    private void setNewLocale(@LocaleManager.LocaleDef String language) {
        LocaleManager.setNewLocale(this, language);
    }

    private void launchHomeScreen() {
        prefManager.setFirstTimeLaunch(false);
        if (user != null) {
            if (user.getType().equals("user")) {
                startActivity(new Intent(LanguageActivity.this, MainActivityClient.class));
            } else {
                startActivity(new Intent(LanguageActivity.this, MainActivityProvider.class));
            }
        } else {
            startActivity(new Intent(LanguageActivity.this, LoginActivity.class));
        }

        finish();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleManager.setLocale(base));
    }
}
