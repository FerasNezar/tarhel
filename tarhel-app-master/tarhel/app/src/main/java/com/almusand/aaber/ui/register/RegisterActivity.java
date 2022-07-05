package com.almusand.aaber.ui.register;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.almusand.aaber.R;
import com.almusand.aaber.custom.categories.CategoriesDialog;
import com.almusand.aaber.model.Categories;
import com.almusand.aaber.model.User;
import com.almusand.aaber.ui.base.BaseActivity;
import com.almusand.aaber.ui.login.LoginActivity;
import com.almusand.aaber.ui.login.LoginViewModel;
import com.almusand.aaber.ui.main.MainActivityClient;
import com.almusand.aaber.ui.main.MainActivityProvider;
import com.almusand.aaber.ui.main.MainViewModel;
import com.almusand.aaber.ui.phone.PhoneActivity;
import com.almusand.aaber.utils.AppPreferences;
import com.almusand.aaber.utils.LocaleManager;
import com.almusand.aaber.utils.ProgressButton;
import com.almusand.aaber.utils.Utilities;
import com.bumptech.glide.Glide;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.gson.Gson;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.schibstedspain.leku.LocationPickerActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import es.dmoral.toasty.Toasty;
import me.shaohui.advancedluban.Luban;
import me.shaohui.advancedluban.OnCompressListener;

import static com.almusand.aaber.utils.Constants.PICKUP_LOCATION;
import static com.almusand.aaber.utils.Constants.SELECT_IMAGE;
import static com.almusand.aaber.utils.Constants.SELECT_PERSONALID;
import static com.schibstedspain.leku.LocationPickerActivityKt.LATITUDE;
import static com.schibstedspain.leku.LocationPickerActivityKt.LOCATION_ADDRESS;
import static com.schibstedspain.leku.LocationPickerActivityKt.LONGITUDE;

public class RegisterActivity extends BaseActivity implements View.OnClickListener, CategoriesDialog.OnCategorysaved {

    private static final int REQUEST_LOCATION = 100;
    private CoordinatorLayout layout;
    private Toolbar toolbar;
    private CircularImageView imgPofile;
    private ImageButton imgbtProfile;
    private EditText etUsername;
    private EditText etEmail;
    private EditText etPhone;
    private EditText etPassword;
    private LinearLayout lyProviderData;
    private Button btPersonalId;
    private EditText etCategory;
    private View btRegister;
    private TextView tvHaveAccount;
    private TextView tvLoginAsProvider;
    private TextView tvLoginAsClient;
    private Button btLocation;

    private ProgressButton progressButton;

    private Uri profileUri, persnalIDUri;
    private String profilePath, personalIDPath;
    private HashMap<String, File> mapFiles;
    private HashMap<String, String> map;

    private List<Categories> categoriesList;

    private String txtUserName, txtPassword, txtPhone, txtEmail;
    private String type = "user";
    private File filePath;
    private RegisterViewModel viewModel;
    private MainViewModel viewModelCategores;
    private LoginViewModel loginViewModel;

    private User user;
    private double pickupLat, pickupLng;
    private String location;
    private File fileProfile;
    private String selectCategory;

    private double currentLat, currentLng;
    private double defaultLat = 21.543333, defaultLng = 39.172779;
    LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        setUpToolbar();

        viewModel = new ViewModelProvider(this).get(RegisterViewModel.class);
        loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);
        viewModelCategores = new ViewModelProvider(this).get(MainViewModel.class);

        locationManager = (LocationManager) RegisterActivity.this.getSystemService(Context.LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            OnGPS();
        } else {
            getLocation();
        }

        intialViews();

        getCategories();

    }

    private void getCategories() {
        viewModelCategores.getCategories(this);
        viewModelCategores.mutableLiveCategories.observe(this, new Observer<List<Categories>>() {
            @Override
            public void onChanged(List<Categories> categories) {
                categoriesList = categories;
            }
        });
    }

    private void intialViews() {
        layout = findViewById(R.id.layout);
        toolbar = findViewById(R.id.toolbar);
        imgPofile = findViewById(R.id.img_pofile);
        imgbtProfile = findViewById(R.id.imgbt_profile);
        etUsername = findViewById(R.id.et_username);
        etPhone = findViewById(R.id.et_phone);
        etPassword = findViewById(R.id.et_password);
        lyProviderData = findViewById(R.id.ly_provider_data);
        etEmail = findViewById(R.id.et_email);
        btPersonalId = findViewById(R.id.et_personal_id);
        etCategory = findViewById(R.id.et_category);
        btRegister = findViewById(R.id.bt_register);
        tvHaveAccount = findViewById(R.id.tv_have_account);
        tvLoginAsProvider = findViewById(R.id.tv_login_as_provider);
        tvLoginAsClient = findViewById(R.id.tv_login_as_client);
        btLocation = findViewById(R.id.et_location);

        progressButton = new ProgressButton(RegisterActivity.this, btRegister);
        progressButton.setButtonTittle(RegisterActivity.this.getResources().getString(R.string.register));

        map = new HashMap<>();
        mapFiles = new HashMap<>();
        categoriesList = new ArrayList<>();

        Glide.with(this)
                .load(this.getResources().getDrawable(R.drawable.aaber_logo))
                .into(imgPofile);

        imgbtProfile.setOnClickListener(this);
        btRegister.setOnClickListener(this);
        tvHaveAccount.setOnClickListener(this);
        tvLoginAsProvider.setOnClickListener(this);
        tvLoginAsClient.setOnClickListener(this);
        etCategory.setOnClickListener(this);
        etPhone.setOnClickListener(this);
        btPersonalId.setOnClickListener(this);
        btLocation.setOnClickListener(this);
    }

    private void setUpToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.bt_register:
                if (checkInfo()) {
                    progressButton.buttonActivated();
                    map.put("user_name", txtUserName);
                    map.put("phone", txtPhone);
                    map.put("email", txtEmail);
                    map.put("password", txtPassword);
                    map.put("type", type);
                    map.put("lat", String.valueOf(pickupLat));
                    map.put("lng", String.valueOf(pickupLng));
                    map.put("address", location);
                    if (type.equals("provider")) {
                        mapFiles.put("personal_id", filePath);
                    }
                    if (AppPreferences.getFCMToken(RegisterActivity.this) != null) {
                        map.put("device_id", AppPreferences.getFCMToken(RegisterActivity.this));
                    }
                    functionSignUp();
                }
                break;

            case R.id.imgbt_profile:
                checkStoragePermission(SELECT_IMAGE);

                break;

            case R.id.tv_have_account:
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                finish();
                break;
            case R.id.et_personal_id:
                checkStoragePermission(SELECT_PERSONALID);
                break;

            case R.id.tv_login_as_provider:
                type = "provider";
                lyProviderData.setVisibility(View.VISIBLE);
                tvLoginAsProvider.setVisibility(View.GONE);
                tvLoginAsClient.setVisibility(View.VISIBLE);
                break;

            case R.id.tv_login_as_client:
                type = "user";
                lyProviderData.setVisibility(View.GONE);
                tvLoginAsProvider.setVisibility(View.VISIBLE);
                tvLoginAsClient.setVisibility(View.GONE);

                break;

            case R.id.et_category:
                new CategoriesDialog(RegisterActivity.this, categoriesList, RegisterActivity.this).show();
                break;

            case R.id.et_phone:
                startActivityForResult(new Intent(RegisterActivity.this, PhoneActivity.class), 11);
                break;

            case R.id.et_location:
                checkLocationPermission();
                break;
        }
    }

    private void functionSignUp() {
        viewModel.registerUser(RegisterActivity.this, mapFiles, map);
        viewModel.mutableLiveData.observe(this, new Observer<User>() {
            @Override
            public void onChanged(User mUser) {
                if (mUser != null) {
                    user = mUser;
                    AppPreferences.setUser(RegisterActivity.this, new Gson().toJson(user));
                    btRegister.setEnabled(true);
                    progressButton.buttonFinished();
                    Toasty.success(RegisterActivity.this, R.string.success_signup, Toasty.LENGTH_SHORT).show();
                    goToMainActivity();
                } else {
                    btRegister.setEnabled(true);
                    progressButton.buttonDactivated();

                }
            }
        });
    }

    private void checkFcmToken() {

        if (AppPreferences.getFCMToken(this) != null) {
            loginViewModel.updateToken(this, AppPreferences.getFCMToken(this), user.getEmail(), user.getPhone());
            loginViewModel.mutableLiveUpdateToken.observe(this, new Observer<String>() {
                @Override
                public void onChanged(String s) {
                    if (s != null && s.equals("true")) {
                        btRegister.setEnabled(true);
                        progressButton.buttonFinished();
                        Toasty.success(RegisterActivity.this, R.string.success_signup, Toasty.LENGTH_SHORT).show();
                        goToMainActivity();
                    }
                }
            });

        }
    }

    private void goToMainActivity() {
        if (type.equals("user")) {
            startActivity(new Intent(RegisterActivity.this, MainActivityClient.class));
        } else {
            startActivity(new Intent(RegisterActivity.this, MainActivityProvider.class));
        }
        finish();
    }

    private boolean checkInfo() {

        txtPassword = etPassword.getText().toString();
        txtPhone = etPhone.getText().toString();
        txtEmail = etEmail.getText().toString();
        txtUserName = etUsername.getText().toString();

        if (TextUtils.isEmpty(txtUserName)) {
            Utilities.showSnackbar(this.getResources().getString(R.string.msg_wrong_username), RegisterActivity.this, layout);
            return false;
        }
        if (TextUtils.isEmpty(txtPhone)) {
            Utilities.showSnackbar(this.getResources().getString(R.string.msg_wrong_phone), RegisterActivity.this, layout);
            return false;
        }

        if (TextUtils.isEmpty(txtEmail)) {
            Utilities.showSnackbar(this.getResources().getString(R.string.msg_valid_mail), RegisterActivity.this, layout);
            return false;
        }
        if (TextUtils.isEmpty(txtPassword)) {
            Utilities.showSnackbar(this.getResources().getString(R.string.msg_wrong_password), RegisterActivity.this, layout);
            return false;
        }

        if (type.equals("provider")) {
            if (personalIDPath == null) {
                Utilities.showSnackbar(this.getResources().getString(R.string.msg_wrong_personal_id), RegisterActivity.this, layout);
                return false;
            }

            if (etCategory.getText().toString().isEmpty()) {
                Utilities.showSnackbar(this.getResources().getString(R.string.msg_choose_category), RegisterActivity.this, layout);
                return false;
            }

        }

        if (pickupLat == 0.0) {
            Utilities.showSnackbar(this.getResources().getString(R.string.msg_enter_location), RegisterActivity.this, layout);
            return false;
        }


        return true;
    }

    private void showPlacePicker(int s) {
        if (currentLat != 0.0) {
            defaultLat = currentLat;
            defaultLng = currentLng;
        }
        Intent locationPickerIntent = new LocationPickerActivity.Builder()
                .withLocation(defaultLat, defaultLng)
                .withGeolocApiKey(RegisterActivity.this.getResources().getString(R.string.google_maps_key))
                .shouldReturnOkOnBackPressed()
                .withGoogleTimeZoneEnabled()
                .withVoiceSearchHidden()
                .withUnnamedRoadHidden()
                .build(RegisterActivity.this);

        startActivityForResult(locationPickerIntent, s);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECT_IMAGE && resultCode == RESULT_OK) {
            profileUri = data.getData();
            if (profileUri != null) {
                Glide.with(this).load(profileUri).into(imgPofile);
                profilePath = getActualPath(RegisterActivity.this, profileUri);
                mapFiles = new HashMap<String, File>();

                Luban.compress(this, new File(profilePath))
                        .putGear(Luban.THIRD_GEAR)      // set the compress mode, default is : THIRD_GEAR
                        .launch(new OnCompressListener() {

                            @Override
                            public void onStart() {

                            }

                            @Override
                            public void onSuccess(File file) {
                                fileProfile = file;
                                mapFiles.put("avatar", fileProfile);
                            }

                            @Override
                            public void onError(Throwable e) {

                            }
                        });
            }
        } else if (requestCode == SELECT_PERSONALID && resultCode == RESULT_OK) {
            persnalIDUri = data.getData();
            if (persnalIDUri != null) {
                mapFiles = new HashMap<String, File>();
                personalIDPath = getActualPath(RegisterActivity.this, persnalIDUri);

                Luban.compress(this, new File(personalIDPath))
                        .putGear(Luban.THIRD_GEAR)      // set the compress mode, default is : THIRD_GEAR
                        .launch(new OnCompressListener() {

                            @Override
                            public void onStart() {

                            }

                            @Override
                            public void onSuccess(File file) {
                                filePath = file;
                                mapFiles.put("personal_id", filePath);

                            }

                            @Override
                            public void onError(Throwable e) {

                            }
                        });


                btPersonalId.setText(personalIDPath);
            }
        } else if (requestCode == 11) {
                if (data != null && data.getExtras() != null)
                    etPhone.setText(data.getStringExtra("phone"));

        } else if (requestCode == PICKUP_LOCATION && resultCode == RESULT_OK) {
            if (data != null) {
                pickupLat = data.getDoubleExtra(LATITUDE, 0.0);
                pickupLng = data.getDoubleExtra(LONGITUDE, 0.0);
                location = data.getStringExtra(LOCATION_ADDRESS);
                btLocation.setText(location);
            }

        } else if (requestCode == REQUEST_LOCATION) {
            getLocation();
        }
    }

    public void OpenGallery(int i) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, RegisterActivity.this.getResources().getString(R.string.select_picture_profile)), i);
    }

    @Override
    public void onCategorySaved(List<Categories> list) {
        for (int i = 0; i < list.size(); i++) {
            map.put("categories[" + i + "]", String.valueOf(list.get(i).getId()));
            if (selectCategory != null) {
                if (LocaleManager.getLocale(RegisterActivity.this.getResources()).getLanguage().equals("ar")) {
                    selectCategory = new StringBuilder().append(selectCategory).append(" - ").append(list.get(i).getName().getAr()).toString();
                } else {
                    selectCategory = new StringBuilder().append(selectCategory).append(" - ").append(list.get(i).getName().getEn()).toString();
                }
            } else {
                if (LocaleManager.getLocale(RegisterActivity.this.getResources()).getLanguage().equals("ar")) {
                    selectCategory = (list.get(i).getName().getAr());
                } else {
                    selectCategory = (list.get(i).getName().getEn());
                }
            }

        }
        etCategory.setText(selectCategory);
    }

    private void checkStoragePermission(int i) {
        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                OpenGallery(i);
            }

            @Override
            public void onPermissionDenied(List<String> deniedPermissions) {
                Toasty.info(RegisterActivity.this, "Permission Denied\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
            }
        };

        TedPermission.with(this)
                .setPermissionListener(permissionlistener)
                .setDeniedMessage(RegisterActivity.this.getResources().getString(R.string.msg_add_permission))
                .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
                .check();
    }

    private void checkLocationPermission() {
        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                showPlacePicker(PICKUP_LOCATION);
            }

            @Override
            public void onPermissionDenied(List<String> deniedPermissions) {
                Toasty.info(RegisterActivity.this, "Permission Denied\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
            }
        };

        TedPermission.with(this)
                .setPermissionListener(permissionlistener)
                .setDeniedMessage(RegisterActivity.this.getResources().getString(R.string.msg_add_permission))
                .setPermissions(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
                .check();
    }

    private void OnGPS() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
        builder.setMessage(getString(R.string.msg_enable_gps_provider)).setCancelable(false).setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), REQUEST_LOCATION);
                dialog.dismiss();
            }
        }).setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void getLocation() {

        if (ActivityCompat.checkSelfPermission(RegisterActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                RegisterActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(RegisterActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
        } else {
            LocationRequest locationRequest = new LocationRequest();
            locationRequest.setInterval(10000);
            locationRequest.setFastestInterval(3000);
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

            LocationServices.getFusedLocationProviderClient(RegisterActivity.this)
                    .requestLocationUpdates(locationRequest, new LocationCallback() {

                        @Override
                        public void onLocationResult(LocationResult locationResult) {
                            super.onLocationResult(locationResult);
                            LocationServices.getFusedLocationProviderClient(RegisterActivity.this)
                                    .removeLocationUpdates(this);
                            if (locationResult != null && locationResult.getLocations().size() > 0) {
                                int lastLocationIndex = locationResult.getLocations().size() - 1;
                                currentLat = locationResult.getLocations().get(lastLocationIndex).getLatitude();
                                currentLng = locationResult.getLocations().get(lastLocationIndex).getLongitude();

                            }
                        }
                    }, Looper.getMainLooper());
        }

    }

    public void hideSoftKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleManager.setLocale(base));
    }
}
