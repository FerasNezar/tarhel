package com.almusand.aaber.ui.verifaction;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.almusand.aaber.R;
import com.almusand.aaber.ui.base.BaseActivity;
import com.almusand.aaber.utils.AppPreferences;
import com.almusand.aaber.utils.LocaleManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.raycoarana.codeinputview.CodeInputView;
import com.raycoarana.codeinputview.OnCodeCompleteListener;

import java.util.concurrent.TimeUnit;

import es.dmoral.toasty.Toasty;
import ticker.views.com.ticker.widgets.circular.timer.callbacks.CircularViewCallback;
import ticker.views.com.ticker.widgets.circular.timer.view.CircularView;
import timber.log.Timber;

public class VerifactionActivity extends BaseActivity implements View.OnClickListener {

    private CodeInputView otherCodeInput;
    private CircularView circularViewWithTimer;
    private TextView tvResendCode;
    private CoordinatorLayout layout;
    private TextView tvEnterCode;
    private ImageView imgBack;
    private String mVerificationId;

    //firebase auth object
    private FirebaseAuth mAuth;

    private String phone;
    private String code;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verifaction);
        setUpToolbar();

        //initializing objects
        mAuth = FirebaseAuth.getInstance();

        initialViews();

        phone = getIntent().getStringExtra("phone");
        code = getIntent().getStringExtra("code");

        tvEnterCode.append(" " + phone);

        tvResendCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendVerificationCode(phone);
                setUpCircleCountDown(60);

            }
        });

        otherCodeInput.addOnCompleteListener(new OnCodeCompleteListener() {
            @Override
            public void onCompleted(String code) {
                verifyVerificationCode(code);
            }
        });

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                //Getting the code sent by SMS
                try {
                    Timber.e("onVerificationCompleted");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                String code = phoneAuthCredential.getSmsCode();

                if (code != null) {
                    otherCodeInput.setCode(code);
                    verifyVerificationCode(code);

                } else {
                    signInWithPhoneAuthCredential(phoneAuthCredential);
                }

            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                Toasty.error(VerifactionActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                mVerificationId = s;
            }
        };

        sendVerificationCode(code + phone);

    }

    private void setUpCircleCountDown(int i) {
        tvResendCode.setEnabled(false);
        tvResendCode.setTextColor(VerifactionActivity.this.getResources().getColor(R.color.text_color_unused));
        CircularView.OptionsBuilder builderWithTimer =
                new CircularView.OptionsBuilder()
                        .shouldDisplayText(true)
                        .setCounterInSeconds(i)
                        .setCircularViewCallback(new CircularViewCallback() {
                            @Override
                            public void onTimerFinish() {
                                tvResendCode.setEnabled(true);
                                tvResendCode.setTextColor(VerifactionActivity.this.getResources().getColor(R.color.text_color));
                                circularViewWithTimer.setVisibility(View.GONE);
                            }

                            @Override
                            public void onTimerCancelled() {
                                // Will be called if stopTimer is called
                            }
                        });

        circularViewWithTimer.setOptions(builderWithTimer);
        circularViewWithTimer.startTimer();
        circularViewWithTimer.setVisibility(View.VISIBLE);


    }

    private void initialViews() {
        tvResendCode = findViewById(R.id.tv_resend_code);
        otherCodeInput = findViewById(R.id.code_input_view);
        circularViewWithTimer = findViewById(R.id.circular_view);
        layout = findViewById(R.id.layout);
        tvEnterCode = findViewById(R.id.tv_enter_code);
        imgBack = findViewById(R.id.img_back);

        imgBack.setOnClickListener(this);

    }

    private void sendVerificationCode(String mobile) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                mobile,
                60,
                TimeUnit.SECONDS,
                TaskExecutors.MAIN_THREAD,
                mCallbacks);
    }


    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            //Getting the code sent by SMS
            String code = phoneAuthCredential.getSmsCode();

            if (code != null) {
                otherCodeInput.setCode(code);
                verifyVerificationCode(code);
            }
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            Toasty.error(VerifactionActivity.this, VerifactionActivity.this.getResources().getString(R.string.msg_phone_not_valid), Toast.LENGTH_LONG).show();
        }

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            mVerificationId = s;
        }
    };

    private void verifyVerificationCode(String otp) {
        try {
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, otp);
            signInWithPhoneAuthCredential(credential);
        } catch (Exception e) {
            Log.e("verifyVerificationCode", e.toString());
            otherCodeInput.setEditable(true);
            otherCodeInput.setError(VerifactionActivity.this.getResources().getString(R.string.msg_code_incorrect));

        }
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(VerifactionActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Intent returnIntent = new Intent();
                            setResult(Activity.RESULT_OK, returnIntent.putExtra("phone", phone));
                            AppPreferences.saveUserPhone(VerifactionActivity.this, phone);
                            finish();
                        } else {
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                otherCodeInput.setEditable(true);
                                otherCodeInput.setError(VerifactionActivity.this.getResources().getString(R.string.msg_code_incorrect));
                            }
                        }
                    }
                });
    }

    private void setUpToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }


    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleManager.setLocale(base));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.img_back:
                finish();
                break;
        }
    }
}
