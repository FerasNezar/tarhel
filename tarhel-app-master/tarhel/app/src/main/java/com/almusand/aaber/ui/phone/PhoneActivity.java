package com.almusand.aaber.ui.phone;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.widget.Toolbar;

import com.almusand.aaber.R;
import com.almusand.aaber.ui.base.BaseActivity;
import com.almusand.aaber.ui.verifaction.VerifactionActivity;
import com.almusand.aaber.utils.LocaleManager;
import com.rilixtech.widget.countrycodepicker.CountryCodePicker;

public class PhoneActivity extends BaseActivity {

    private EditText etPhone;
    private Button btVerify;
    private CountryCodePicker ccp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone);
        setUpToolbar();

        initialViews();

        btVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (etPhone.getText() != null && !etPhone.getText().toString().isEmpty()) {
                    Intent returnIntent = new Intent(PhoneActivity.this, VerifactionActivity.class);
                    setResult(Activity.RESULT_OK, returnIntent.putExtra("phone", etPhone.getText().toString()));
                    startActivity(returnIntent
                            .putExtra("phone", etPhone.getText().toString())
                            .putExtra("code", ccp.getSelectedCountryCodeWithPlus())
                    );
                    finish();
                }
            }
        });


    }

    private void setUpToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    private void initialViews() {
        ccp = findViewById(R.id.ccp);
        etPhone = findViewById(R.id.et_phone);
        btVerify = findViewById(R.id.bt_verify);

    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleManager.setLocale(base));
    }
}
