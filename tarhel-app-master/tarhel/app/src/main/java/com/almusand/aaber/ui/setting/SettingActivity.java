package com.almusand.aaber.ui.setting;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.widget.SwitchCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.almusand.aaber.R;
import com.almusand.aaber.model.Setting;
import com.almusand.aaber.model.User;
import com.almusand.aaber.ui.base.BaseActivity;
import com.almusand.aaber.ui.main.MainActivityClient;
import com.almusand.aaber.ui.main.MainActivityProvider;
import com.almusand.aaber.ui.profile.ProfileViewModel;
import com.almusand.aaber.utils.AppPreferences;
import com.almusand.aaber.utils.LocaleManager;
import com.apkfuns.xprogressdialog.XProgressDialog;
import com.google.gson.Gson;

import java.util.HashMap;

public class SettingActivity extends BaseActivity implements CompoundButton.OnCheckedChangeListener, View.OnClickListener {

    private Spinner spLanguage;
    private TextView tvRateApp;
    private TextView tvShareApp;
    private SwitchCompat switchNotification;
    private ImageView imgInstagram;
    private ImageView imgTwitter;
    private ImageView imgFacebook;
    private ImageView imgBack;

    String[] list_of_items;
    private String selectedLang;
    private String currentLang;

    ArrayAdapter<String> genderAdapter;

    private Setting setting;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        setting = new Gson().fromJson(AppPreferences.getSetting(this), Setting.class);
        user = new Gson().fromJson(AppPreferences.getUser(this), User.class);

        initViews();

        list_of_items = new String[]{this.getResources().getString(R.string.language)
                , this.getResources().getString(R.string.english),
                this.getResources().getString(R.string.arabic)};

        currentLang = LocaleManager.getLocale(SettingActivity.this.getResources()).getLanguage();

        setUpSpinnerLanguage();


    }

    private void initViews() {
        tvRateApp = findViewById(R.id.tv_rate_app);
        tvShareApp = findViewById(R.id.tv_share_app);
        switchNotification = findViewById(R.id.switch_notification);
        imgInstagram = findViewById(R.id.img_instagram);
        imgTwitter = findViewById(R.id.img_twitter);
        imgFacebook = findViewById(R.id.img_facebook);
        spLanguage = findViewById(R.id.sp_language);
        imgBack = findViewById(R.id.img_back);

        switchNotification.setOnCheckedChangeListener(this);
        imgInstagram.setOnClickListener(this);
        imgTwitter.setOnClickListener(this);
        imgFacebook.setOnClickListener(this);
        imgBack.setOnClickListener(this);

    }

    private void setUpSpinnerLanguage() {
        genderAdapter = new ArrayAdapter<String>(SettingActivity.this, R.layout.layout_custom_spinner, list_of_items) {
            @Override
            public boolean isEnabled(int position) {
                if (position == 0) {
                    // Disable the first item from Spinner
                    // First item will be use for hint
                    return false;
                } else {
                    return true;
                }
            }

            @Override
            public View getDropDownView(int position, View convertView,
                                        ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if (position == 0) {
                    // Set the hint text color gray
                    tv.setTextColor(Color.GRAY);
                } else {
                    tv.setTextColor(Color.BLACK);
                }
                return view;
            }
        };
        genderAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        spLanguage.setAdapter(genderAdapter);

        spLanguage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            private Context context;

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                switch (position) {

                    case 1:
                        selectedLang = "en";
                        if (!currentLang.equals(selectedLang)) {
                            setNewLocale(LocaleManager.ENGLISH);
                            goToMainActivity();
                        }
                        break;

                    case 2:
                        selectedLang = "ar";
                        if (!currentLang.equals(selectedLang)) {
                            setNewLocale(LocaleManager.HINDI);
                            goToMainActivity();
                        }
                        break;
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        if (currentLang.equals("ar")) {
            spLanguage.setSelection(2);
        } else if (currentLang.equals("en")) {
            spLanguage.setSelection(1);
        }
    }

    private void setNewLocale(@LocaleManager.LocaleDef String language) {
        LocaleManager.setNewLocale(this, language);
    }


    private void goToMainActivity() {
        if (user.getType().equals("user")) {
            startActivity(new Intent(SettingActivity.this, MainActivityClient.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        } else {
            startActivity(new Intent(SettingActivity.this, MainActivityProvider.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));

        }
    }

    public void showProgressDialog() {
        progressDialog = new XProgressDialog(SettingActivity.this);
        progressDialog.setMessage(SettingActivity.this.getResources().getString(R.string.loading));
        progressDialog.show();
    }

    public static void hideProgressDialog() {
        progressDialog.hide();
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            updateUserLangAndNotify(null, "1");
        } else {
            updateUserLangAndNotify(null, "0");
        }
    }

    private void updateUserLangAndNotify(String lang, String enableNotify) {
        showProgressDialog();
        HashMap<String, String> map = new HashMap<>();
        if (lang != null) {
            map.put("lang", lang);
        }
        if (enableNotify != null) {
            map.put("enable_notify", enableNotify);

        }
        map.put("email", user.getEmail());
        map.put("phone", user.getPhone());
        ProfileViewModel viewModel = new ViewModelProvider(this).get(ProfileViewModel.class);
        viewModel.updateUser(SettingActivity.this, null, map);
        viewModel.mutableLiveData.observe(this, new Observer<User>() {
            @Override
            public void onChanged(User user) {
                hideProgressDialog();
                AppPreferences.setUser(SettingActivity.this, new Gson().toJson(user));
                if (lang != null) {
                    if (user.getType().equals("user")) {
                        startActivity(new Intent(SettingActivity.this, MainActivityClient.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                    } else {
                        startActivity(new Intent(SettingActivity.this, MainActivityProvider.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));

                    }
                }
            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.img_twitter:
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(setting.getTwitter())));
                break;

            case R.id.img_facebook:
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(setting.getFacebook())));
                break;

            case R.id.img_instagram:
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(setting.getInstagram())));
                break;
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
