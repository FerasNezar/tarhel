package com.almusand.aaber.ui.profile;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
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
import com.almusand.aaber.ui.main.MainViewModel;
import com.almusand.aaber.ui.phone.PhoneActivity;
import com.almusand.aaber.ui.profile.ProfileActivity;
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

public class ProfileActivity extends BaseActivity implements View.OnClickListener, CategoriesDialog.OnCategorysaved {

    private static final int REQUEST_LOCATION = 100;
    private CoordinatorLayout layout;
    private CircularImageView imgPofile;
    private ImageButton imgbtProfile;
    private ImageView imgBack;
    private EditText etUsername;
    private EditText etEmail;
    private EditText etPhone;
    private EditText etPassword;
    private LinearLayout lyProviderData;
    private Button btPersonalId;
    private EditText etCategory;
    private View btSave;
    private Button btLocation;
    private ProgressButton progressButton;

    private Uri profileUri, persnalIDUri;
    private String profilePath, personalIDPath;
    private HashMap<String, File> mapFiles;
    private HashMap<String, String> map;

    private List<Categories> categoriesList;

    private String txtUserName, txtPassword, txtPhone, txtEmail;
    private ProfileViewModel viewModel;
    private MainViewModel viewModelCategores;

    private User user;
    private double pickupLat, pickupLng;
    private String location;
    private File profileFile;
    private File personalIdFile;

    private double currentLat, currentLng;
    private double defaultLat = 21.543333, defaultLng = 39.172779;
    LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        user = new Gson().fromJson(AppPreferences.getUser(this), User.class);
        viewModel = new ViewModelProvider(this).get(ProfileViewModel.class);
        viewModelCategores = new ViewModelProvider(this).get(MainViewModel.class);

        locationManager = (LocationManager) ProfileActivity.this.getSystemService(Context.LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            OnGPS();
        } else {
            getLocation();
        }

        intialViews();

        getCategories();

        prepareData();

    }

    private void prepareData() {
        etUsername.setText(user.getUserName());
        etEmail.setText(user.getEmail());
        etPhone.setText(user.getPhone());

        if (user.getAvatar() != null)
            Glide.with(this)
                    .load(user.getAvatar())
                    .error(R.drawable.aaber_logo)
                    .into(imgPofile);

        if (user.getType().equals("provider")) {
            btLocation.setText(user.getAddress());
            if (!user.getCategories().isEmpty()) {
                if (LocaleManager.getLocale(ProfileActivity.this.getResources())
                        .getLanguage().equals(user.getCategories().get(0).getName().getEn())) {
                    etCategory.setText(user.getCategories().get(0).getName().getEn());
                } else {
                    etCategory.setText(user.getCategories().get(0).getName().getAr());
                }
            }
        }
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
        imgPofile = findViewById(R.id.img_pofile);
        imgBack = findViewById(R.id.img_back);
        imgbtProfile = findViewById(R.id.imgbt_profile);
        etUsername = findViewById(R.id.et_username);
        etPhone = findViewById(R.id.et_phone);
        etPassword = findViewById(R.id.et_password);
        lyProviderData = findViewById(R.id.ly_provider_data);
        etEmail = findViewById(R.id.et_email);
        btPersonalId = findViewById(R.id.et_personal_id);
        etCategory = findViewById(R.id.et_category);
        btSave = findViewById(R.id.bt_register);
        btLocation = findViewById(R.id.et_location);

        progressButton = new ProgressButton(ProfileActivity.this, btSave);
        progressButton.setButtonTittle(ProfileActivity.this.getResources().getString(R.string.save));

        map = new HashMap<>();
        mapFiles = new HashMap<>();
        categoriesList = new ArrayList<>();

        Glide.with(this)
                .load(this.getResources().getDrawable(R.drawable.aaber_logo))
                .into(imgPofile);

        if (user.getType().equals("provider")) {
            lyProviderData.setVisibility(View.VISIBLE);
        }
        imgbtProfile.setOnClickListener(this);
        btSave.setOnClickListener(this);
        etCategory.setOnClickListener(this);
        etPhone.setOnClickListener(this);
        btPersonalId.setOnClickListener(this);
        imgBack.setOnClickListener(this);
        btLocation.setOnClickListener(this);

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
                    if (!txtPassword.isEmpty()) map.put("password", txtPassword);
                    map.put("type", user.getType());

                    if (AppPreferences.getFCMToken(ProfileActivity.this) != null) {
                        map.put("device_id", AppPreferences.getFCMToken(ProfileActivity.this));
                    }
                    functionSignUp();
                }
                break;

            case R.id.imgbt_profile:
                checkStoragePermission(SELECT_IMAGE);
                break;

            case R.id.tv_have_account:
                startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
                finish();
                break;
            case R.id.et_personal_id:
                checkStoragePermission(SELECT_PERSONALID);
                break;
            case R.id.et_category:
                new CategoriesDialog(ProfileActivity.this, categoriesList, ProfileActivity.this).show();
                break;

            case R.id.et_phone:
                startActivityForResult(new Intent(ProfileActivity.this, PhoneActivity.class), 11);
                break;
            case R.id.img_back:
                finish();
                break;

            case R.id.et_location:
                checkLocationPermission(PICKUP_LOCATION);
                break;
        }
    }

    private void functionSignUp() {
        viewModel.updateUser(ProfileActivity.this, mapFiles, map);
        viewModel.mutableLiveData.observe(this, new Observer<User>() {
            @Override
            public void onChanged(User user) {
                if (user != null) {
                    AppPreferences.setUser(ProfileActivity.this, new Gson().toJson(user));
                    btSave.setEnabled(true);
                    progressButton.buttonFinished();
                    Toasty.success(ProfileActivity.this, R.string.profile_updated, Toasty.LENGTH_SHORT).show();
                    finish();
                } else {
                    btSave.setEnabled(true);
                    progressButton.buttonDactivated();

                }
            }
        });
    }

    private void showPlacePicker(int s) {
        if (currentLat != 0.0) {
            defaultLat = currentLat;
            defaultLng = currentLng;
        }
        Intent locationPickerIntent = new LocationPickerActivity.Builder()
                .withLocation(defaultLat, defaultLng)
                .withGeolocApiKey(ProfileActivity.this.getResources().getString(R.string.google_maps_key))
                .shouldReturnOkOnBackPressed()
                .withGoogleTimeZoneEnabled()
                .withVoiceSearchHidden()
                .withUnnamedRoadHidden()
                .build(ProfileActivity.this);

        startActivityForResult(locationPickerIntent, s);
    }

    private boolean checkInfo() {

        txtPhone = etPhone.getText().toString();
        txtEmail = etEmail.getText().toString();
        txtUserName = etUsername.getText().toString();
        txtPassword = etPassword.getText().toString();

        if (txtUserName.isEmpty()) {
            Utilities.showSnackbar(this.getResources().getString(R.string.msg_wrong_username), ProfileActivity.this, layout);
            return false;
        }
        if (txtPhone.isEmpty()) {
            Utilities.showSnackbar(this.getResources().getString(R.string.msg_wrong_phone), ProfileActivity.this, layout);
            return false;
        }

        if (txtEmail.isEmpty()) {
            Utilities.showSnackbar(this.getResources().getString(R.string.msg_valid_mail), ProfileActivity.this, layout);
            return false;
        }

        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECT_IMAGE && resultCode == RESULT_OK) {
            profileUri = data.getData();
            if (profileUri != null) {
                Glide.with(this).load(profileUri).into(imgPofile);
                profilePath = getActualPath(ProfileActivity.this, profileUri);

                Luban.compress(this, new File(profilePath))
                        .putGear(Luban.THIRD_GEAR)      // set the compress mode, default is : THIRD_GEAR
                        .launch(new OnCompressListener() {

                            @Override
                            public void onStart() {

                            }

                            @Override
                            public void onSuccess(File file) {
                                profileFile = file;
                                mapFiles.put("avatar", profileFile);
                            }

                            @Override
                            public void onError(Throwable e) {

                            }
                        });

            }
        } else if (requestCode == SELECT_PERSONALID && resultCode == RESULT_OK) {
            persnalIDUri = data.getData();
            if (persnalIDUri != null) {
                personalIDPath = getActualPath(ProfileActivity.this, persnalIDUri);

                Luban.compress(this, new File(personalIDPath))
                        .putGear(Luban.THIRD_GEAR)      // set the compress mode, default is : THIRD_GEAR
                        .launch(new OnCompressListener() {

                            @Override
                            public void onStart() {
                            }

                            @Override
                            public void onSuccess(File file) {
                                personalIdFile = file;
                                mapFiles.put("personal_id", personalIdFile);
                            }

                            @Override
                            public void onError(Throwable e) {

                            }
                        });

                btPersonalId.setText(personalIDPath);
            }
        } else if (requestCode == 11) {
            if (AppPreferences.getUserPhone(ProfileActivity.this) != null) {
                etPhone.setText(AppPreferences.getUserPhone(ProfileActivity.this));
            }
        } else if (requestCode == PICKUP_LOCATION && resultCode == RESULT_OK) {
            if (data != null) {
                pickupLat = data.getDoubleExtra(LATITUDE, 0.0);
                pickupLng = data.getDoubleExtra(LONGITUDE, 0.0);
                location = data.getStringExtra(LOCATION_ADDRESS);
                if (pickupLat != 0.0) {
                    map.put("lat", String.valueOf(pickupLat));
                    map.put("lng", String.valueOf(pickupLng));
                    map.put("address", location);
                }
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
        startActivityForResult(Intent.createChooser(intent, ProfileActivity.this.getResources().getString(R.string.select_picture_profile)), i);
    }

    @Override
    public void onCategorySaved(List<Categories> list) {
        String selectCategory = null;
        for (int i = 0; i < list.size(); i++) {
            map.put("categories[" + i + "]", String.valueOf(list.get(i).getId()));
            if (selectCategory != null) {
                if (LocaleManager.getLocale(ProfileActivity.this.getResources()).getLanguage().equals("ar")) {
                    selectCategory = new StringBuilder().append(selectCategory).append(" - ").append(list.get(i).getName().getAr()).toString();
                } else {
                    selectCategory = new StringBuilder().append(selectCategory).append(" - ").append(list.get(i).getName().getEn()).toString();
                }
            } else {
                if (LocaleManager.getLocale(ProfileActivity.this.getResources()).getLanguage().equals("ar")) {
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
                Toasty.info(ProfileActivity.this, "Permission Denied\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
            }
        };

        TedPermission.with(this)
                .setPermissionListener(permissionlistener)
                .setDeniedMessage(ProfileActivity.this.getResources().getString(R.string.msg_add_permission))
                .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
                .check();
    }

    private void checkLocationPermission(int i) {
        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                showPlacePicker(i);
            }

            @Override
            public void onPermissionDenied(List<String> deniedPermissions) {
                Toasty.info(ProfileActivity.this, "Permission Denied\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
            }
        };

        TedPermission.with(this)
                .setPermissionListener(permissionlistener)
                .setDeniedMessage(ProfileActivity.this.getResources().getString(R.string.msg_add_permission))
                .setPermissions(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
                .check();
    }

    private void OnGPS() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
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

        if (ActivityCompat.checkSelfPermission(ProfileActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                ProfileActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(ProfileActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
        } else {
            LocationRequest locationRequest = new LocationRequest();
            locationRequest.setInterval(10000);
            locationRequest.setFastestInterval(3000);
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

            LocationServices.getFusedLocationProviderClient(ProfileActivity.this)
                    .requestLocationUpdates(locationRequest, new LocationCallback() {

                        @Override
                        public void onLocationResult(LocationResult locationResult) {
                            super.onLocationResult(locationResult);
                            LocationServices.getFusedLocationProviderClient(ProfileActivity.this)
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

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleManager.setLocale(base));
    }

}