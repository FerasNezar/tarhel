package com.almusand.aaber.ui.splash;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.almusand.aaber.R;
import com.almusand.aaber.model.Setting;
import com.almusand.aaber.ui.language.LanguageActivity;
import com.almusand.aaber.utils.AppPreferences;
import com.almusand.aaber.utils.PrefManager;
import com.google.gson.Gson;
import com.jaredrummler.android.widget.AnimatedSvgView;

/**
 * Developed by Anas Elshwaf
 * anaselshawaf357@gmail.com
 */
public class SplashActivity extends AppCompatActivity {

    private static final long SPLASH_SCREEN_TIME_OUT = 1000;
    private SettingViewModel viewModel;

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        //AnimatedSvgView svgView = findViewById(R.id.animated_svg_view);
        //svgView.start();

        viewModel = new ViewModelProvider(this).get(SettingViewModel.class);
        viewModel.getSettings(this,0);
        viewModel.mutableLiveData.observe(this, new Observer<Setting>() {
            @Override
            public void onChanged(Setting setting) {
                if (setting != null) {
                    AppPreferences.setSetting(SplashActivity.this, new Gson().toJson(setting));
                    GoToLanguageActivity();

                }
            }
        });

    }

    private void GoToLanguageActivity() {

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SplashActivity.this, LanguageActivity.class));
                finish();
            }
        }, SPLASH_SCREEN_TIME_OUT);

    }
}
