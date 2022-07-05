package com.almusand.aaber.ui.contactUs;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.almusand.aaber.R;
import com.almusand.aaber.model.Setting;
import com.almusand.aaber.ui.base.BaseActivity;
import com.almusand.aaber.utils.AppPreferences;
import com.almusand.aaber.utils.LocaleManager;
import com.almusand.aaber.utils.ProgressButton;
import com.almusand.aaber.utils.Utilities;
import com.google.gson.Gson;

import java.util.HashMap;

public class ContactUsActivity extends BaseActivity implements View.OnClickListener {

    private ImageView imgBack;
    private TextView tvMobile;
    private TextView tvAddress;
    private TextView tvEmail;
    private EditText etMessage;
    private LinearLayout layout;

    private Setting setting;
    private ProgressButton progressButton;
    private View btSend;
    private ContactUsViewModel viewModel;
    private String flag;

    @Override
    protected void onStart() {
        super.onStart();

        if (getIntent().getExtras() != null)
            flag = getIntent().getExtras().getString("flag");

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_us);

        setting = new Gson().fromJson(AppPreferences.getSetting(this), Setting.class);
        viewModel = new ViewModelProvider(this).get(ContactUsViewModel.class);

        initViews();

    }

    private void initViews() {
        imgBack = findViewById(R.id.img_back);
        tvMobile = findViewById(R.id.tv_mobile);
        tvAddress = findViewById(R.id.tv_address);
        tvEmail = findViewById(R.id.tv_email);
        etMessage = findViewById(R.id.et_message);
        btSend = findViewById(R.id.bt_send);
        layout = findViewById(R.id.layout);

        tvAddress.setText(setting.getContactAddress());
        tvEmail.setText(setting.getContactEmail());
        tvMobile.setText(setting.getContactPhone());

        progressButton = new ProgressButton(ContactUsActivity.this, btSend);
        progressButton.setButtonTittle(ContactUsActivity.this.getResources().getString(R.string.send));

        btSend.setOnClickListener(this);
        imgBack.setOnClickListener(this);

        tvAddress.setOnClickListener(this);
        tvEmail.setOnClickListener(this);
        tvMobile.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.bt_send:
                if (checkInfo()) {
                    sendMessage();
                }
                break;

            case R.id.img_back:
                finish();
                break;

            case R.id.tv_mobile:
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", tvMobile.getText().toString(), null));
                startActivity(intent);
                break;

            case R.id.tv_address:
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(tvAddress.getText().toString()));
                if (browserIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(browserIntent);
                }
                 break;

            case R.id.tv_email:
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:" + tvEmail.getText().toString()));
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "");
                emailIntent.putExtra(Intent.EXTRA_TEXT, " ");
                //emailIntent.putExtra(Intent.EXTRA_HTML_TEXT, body); //If you are using HTML in your body text

                startActivity(Intent.createChooser(emailIntent, "Contact Morad support"));

                break;

        }
    }

    private void sendMessage() {
        progressButton.buttonActivated();
        btSend.setEnabled(false);
        HashMap<String, String> map = new HashMap<>();
        map.put("message", etMessage.getText().toString());
        viewModel.sendMessage(ContactUsActivity.this, map);
        viewModel.mutableLiveData.observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                if (s != null) {
                    progressButton.buttonFinished();
                    if (flag != null && flag.equals("chat")) {
                        Intent returnIntent = new Intent();
                        setResult(Activity.RESULT_OK, returnIntent);
                    }
                    finish();
                } else {
                    btSend.setEnabled(true);
                }
            }
        });
    }

    private boolean checkInfo() {


        if (etMessage.getText().toString().isEmpty()) {
            Utilities.showSnackbar(this.getResources().getString(R.string.msg_notes), ContactUsActivity.this, layout);
            return false;
        }

        return true;
    }


    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleManager.setLocale(base));
    }
}
