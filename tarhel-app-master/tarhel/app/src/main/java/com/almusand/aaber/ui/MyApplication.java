package com.almusand.aaber.ui;


import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;

import com.almusand.aaber.BuildConfig;
import com.almusand.aaber.utils.LocaleManager;
import com.almusand.aaber.utils.ReleaseTree;

import timber.log.Timber;

public class MyApplication extends Application {


    @Override
    public void onCreate() {
        super.onCreate();

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        } else {
            Timber.plant(new ReleaseTree());
        }
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleManager.setLocale(base));
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        LocaleManager.setLocale(this);
    }

}