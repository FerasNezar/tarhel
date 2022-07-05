package com.almusand.aaber.ui.sendOrder;

import android.Manifest;
import android.app.Activity;
import android.app.TimePickerDialog;
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
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.almusand.aaber.R;
import com.almusand.aaber.model.Categories;
import com.almusand.aaber.model.Order;
import com.almusand.aaber.model.User;
import com.almusand.aaber.ui.base.BaseActivity;
import com.almusand.aaber.utils.AppPreferences;
import com.almusand.aaber.utils.LocaleManager;
import com.almusand.aaber.utils.ProgressButton;
import com.almusand.aaber.utils.Utilities;
import com.bumptech.glide.Glide;
import com.github.florent37.singledateandtimepicker.dialog.SingleDateAndTimePickerDialog;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.gson.Gson;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.makeramen.roundedimageview.RoundedImageView;
import com.schibstedspain.leku.LocationPickerActivity;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import es.dmoral.toasty.Toasty;
import me.shaohui.advancedluban.Luban;
import me.shaohui.advancedluban.OnCompressListener;

import static com.almusand.aaber.utils.Constants.PICKOFF_LOCATION;
import static com.almusand.aaber.utils.Constants.PICKUP_LOCATION;
import static com.almusand.aaber.utils.Constants.SELECT_IMAGE;
import static com.schibstedspain.leku.LocationPickerActivityKt.LATITUDE;
import static com.schibstedspain.leku.LocationPickerActivityKt.LOCATION_ADDRESS;
import static com.schibstedspain.leku.LocationPickerActivityKt.LONGITUDE;

public class SendOrderActivity extends BaseActivity implements View.OnClickListener {

    private static final int REQUEST_LOCATION = 100;
    private LinearLayout layout;
    private RoundedImageView imgOrder;
    private EditText etCategory;
    private EditText etWeight;
    private EditText etNotes;
    private EditText etTime;
    private EditText etArrivalDate;
    private Button etPickupLocation;
    private Button etDropoffLocation;

    private ProgressButton progressButton;
    private View btRequest;

    private TimePickerDialog mDialogHourMinute;
    private SimpleDateFormat simpleTimeFormat, simpleDateFormat;
    private String selectedTime, selectedData;
    private Double pickupLat, pickupLng, dropoffLat, dropoffLng;
    private String txtWeight, txtNote, txtCategory;
    private double lat = 0, lng;
    private Categories categories;

    private Uri profileUri;
    private HashMap<String, File> mapFiles;
    private String profilePath;

    private SendOrderViewModel viewModel;
    private String location;
    private String locationDropOff;
    private User user;
    private File fileProfile;
    private double currentLat, currentLng;
    private double defaultLat = 21.543333, defaultLng = 39.172779;
    LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_request);
        setUpToolbar();

        viewModel = new ViewModelProvider(this).get(SendOrderViewModel.class);
        user = new Gson().fromJson(AppPreferences.getUser(this), User.class);

        categories = (Categories) getIntent().getExtras().getSerializable("categories");
        simpleTimeFormat = new SimpleDateFormat("hh:mm", Locale.getDefault());
        simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.US);

        checkLocationPermission();

        locationManager = (LocationManager) SendOrderActivity.this.getSystemService(Context.LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            OnGPS();
        } else {
            getLocation();
        }

        initViews();

    }

    private void initViews() {
        layout = findViewById(R.id.layout);
        imgOrder = findViewById(R.id.img_order);
        etCategory = findViewById(R.id.et_category);
        etWeight = findViewById(R.id.et_weight);
        etNotes = findViewById(R.id.et_notes);
        etTime = findViewById(R.id.et_time);
        etArrivalDate = findViewById(R.id.et_arrival_date);
        etPickupLocation = findViewById(R.id.et_pickup_location);
        etDropoffLocation = findViewById(R.id.et_dropoff_location);
        btRequest = findViewById(R.id.bt_request);

        if (LocaleManager.getLocale(SendOrderActivity.this.getResources()).getLanguage().equals("en")) {
            etCategory.setText(categories.getName().getEn());
        } else {
            etCategory.setText(categories.getName().getAr());
        }

        progressButton = new ProgressButton(SendOrderActivity.this, btRequest);
        progressButton.setButtonTittle(SendOrderActivity.this.getResources().getString(R.string.request));

        btRequest.setOnClickListener(this);
        imgOrder.setOnClickListener(this);
        etTime.setOnClickListener(this);
        etArrivalDate.setOnClickListener(this);
        etPickupLocation.setOnClickListener(this);
        etDropoffLocation.setOnClickListener(this);
        etArrivalDate.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.bt_request:
                if (checkInfo()) {
                    btRequest.setEnabled(false);
                    progressButton.buttonActivated();
                    sendOrder();
                }
                break;

            case R.id.et_time:
                funPickTime();
                break;

            case R.id.et_arrival_date:
                funPickDate();
                break;

            case R.id.et_pickup_location:
                if (ActivityCompat.checkSelfPermission(SendOrderActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        SendOrderActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(SendOrderActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 200);
                } else {
                    pickerLocation(PICKUP_LOCATION);
                }
                break;

            case R.id.et_dropoff_location:
                if (ActivityCompat.checkSelfPermission(SendOrderActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        SendOrderActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(SendOrderActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 200);
                } else {
                    pickerLocation(PICKOFF_LOCATION);
                }
                break;

            case R.id.et_category:
//                new CategoriesDialog(SendRequestActivity.this, categoriesList, SendRequestActivity.this).show();
                break;

            case R.id.img_order:
                checkStoragePermission(SELECT_IMAGE);
                break;

        }
    }

    private void sendOrder() {
        HashMap<String, String> map = new HashMap<>();
        map.put("note", txtNote);
        map.put("weight", txtWeight);
        map.put("pickup_lat", pickupLat.toString());
        map.put("pickup_lng", pickupLng.toString());
        map.put("time", etTime.getText().toString());
        map.put("arrival_date", etArrivalDate.getText().toString());
        map.put("category_id", categories.getId().toString());
        map.put("dropoff_lng", dropoffLng.toString());
        map.put("dropoff_lat", dropoffLat.toString());
        map.put("location", location);
        map.put("location_dropoff", locationDropOff);

        Log.e("order_", map.toString());
        viewModel.sendOrder(this, map, mapFiles);
        viewModel.mutableLiveData.observe(this, new Observer<Order>() {
            @Override
            public void onChanged(Order order) {
                if (order != null) {
                    progressButton.buttonFinished();
                    Toasty.success(SendOrderActivity.this, SendOrderActivity.this.getResources().getString(R.string.order_has_sent), Toasty.LENGTH_SHORT).show();
                    finish();
                } else {
                    btRequest.setEnabled(true);
                    progressButton.buttonDactivated();
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICKUP_LOCATION && resultCode == RESULT_OK) {
            if (data != null) {
                pickupLat = data.getDoubleExtra(LATITUDE, 0.0);
                pickupLng = data.getDoubleExtra(LONGITUDE, 0.0);
                location = data.getStringExtra(LOCATION_ADDRESS);
                etPickupLocation.setText(location);
            }
        } else if (requestCode == PICKOFF_LOCATION && resultCode == RESULT_OK) {
            if (data != null) {
                dropoffLat = data.getDoubleExtra(LATITUDE, 0.0);
                dropoffLng = data.getDoubleExtra(LONGITUDE, 0.0);
                locationDropOff = data.getStringExtra(LOCATION_ADDRESS);
                etDropoffLocation.setText(locationDropOff);

            }

        } else if (requestCode == REQUEST_LOCATION) {
            getLocation();

        } else if (requestCode == SELECT_IMAGE && resultCode == RESULT_OK) {
            profileUri = data.getData();
            if (profileUri != null) {
                Glide.with(this).load(profileUri).centerCrop().into(imgOrder);
                mapFiles = new HashMap<String, File>();
                profilePath = getActualPath(SendOrderActivity.this, profileUri);

                Luban.compress(this, new File(profilePath))
                        .putGear(Luban.THIRD_GEAR)      // set the compress mode, default is : THIRD_GEAR
                        .launch(new OnCompressListener() {

                            @Override
                            public void onStart() {

                            }

                            @Override
                            public void onSuccess(File file) {
                                fileProfile = file;
                            }

                            @Override
                            public void onError(Throwable e) {

                            }
                        });

                mapFiles.put("image", fileProfile);

            }
        }
    }

    private void funPickTime() {
        new SingleDateAndTimePickerDialog.Builder(this)
                .bottomSheet()
                .curved()
                .displayMinutes(true)
                .displayHours(true)
                .displayDays(false)
                .displayMonth(false)
                .displayYears(false)
                .displayDaysOfMonth(false)
                .title(SendOrderActivity.this.getResources().getString(R.string.pick_time))
                .listener(new SingleDateAndTimePickerDialog.Listener() {
                    @Override
                    public void onDateSelected(Date date) {
                        selectedTime = simpleTimeFormat.format(date);
                        etTime.setText(arabicToDecimal(selectedTime));
                    }
                })
                .display();
    }

    private void funPickDate() {
        new SingleDateAndTimePickerDialog.Builder(this)
                .bottomSheet()
                .curved()
                .mustBeOnFuture()
                .displayMinutes(false)
                .displayHours(false)
                .displayDays(false)
                .displayMonth(true)
                .displayYears(true)
                .displayDaysOfMonth(true)
                .title(SendOrderActivity.this.getResources().getString(R.string.pick_date))
                .listener(new SingleDateAndTimePickerDialog.Listener() {
                    @Override
                    public void onDateSelected(Date date) {
                        selectedData = simpleDateFormat.format(date);
                        etArrivalDate.setText(selectedData);
                    }
                })
                .display();
    }

    private void setUpToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private boolean checkInfo() {

        txtNote = etNotes.getText().toString();
        txtWeight = etWeight.getText().toString();

        if (TextUtils.isEmpty(txtNote)) {
            Utilities.showSnackbar(this.getResources().getString(R.string.msg_write_note), SendOrderActivity.this, layout);
            return false;
        }
        if (TextUtils.isEmpty(txtWeight)) {
            Utilities.showSnackbar(this.getResources().getString(R.string.msg_write_weight), SendOrderActivity.this, layout);
            return false;
        }

        if (etTime.getText().toString().isEmpty()) {
            Utilities.showSnackbar(this.getResources().getString(R.string.msg_enter_time), SendOrderActivity.this, layout);
            return false;
        }

        if (etArrivalDate.getText().toString().isEmpty()) {
            Utilities.showSnackbar(this.getResources().getString(R.string.msg_enter_date), SendOrderActivity.this, layout);
            return false;
        }

        if (location.isEmpty()) {
            Utilities.showSnackbar(this.getResources().getString(R.string.msg_enter_location), SendOrderActivity.this, layout);
            return false;
        }

        if (locationDropOff.isEmpty()) {
            Utilities.showSnackbar(this.getResources().getString(R.string.msg_enter_location), SendOrderActivity.this, layout);
            return false;
        }

        return true;
    }

    public void OpenGallery(int i) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, SendOrderActivity.this.getResources().getString(R.string.select_picture_profile)), i);
    }

    private void checkStoragePermission(int i) {
        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                OpenGallery(i);
            }

            @Override
            public void onPermissionDenied(List<String> deniedPermissions) {
                Toasty.info(SendOrderActivity.this, "Permission Denied\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
            }
        };

        TedPermission.with(this)
                .setPermissionListener(permissionlistener)
                .setDeniedMessage(SendOrderActivity.this.getResources().getString(R.string.msg_add_permission))
                .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
                .check();
    }

    private void checkLocationPermission() {
        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                getLocation();
            }

            @Override
            public void onPermissionDenied(List<String> deniedPermissions) {
                Toasty.info(SendOrderActivity.this, "Permission Denied\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
            }
        };

        TedPermission.with(this)
                .setPermissionListener(permissionlistener)
                .setDeniedMessage(SendOrderActivity.this.getResources().getString(R.string.msg_add_permission))
                .setPermissions(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
                .check();
    }

    private static String arabicToDecimal(String number) {
        char[] chars = new char[number.length()];
        for (int i = 0; i < number.length(); i++) {
            char ch = number.charAt(i);
            if (ch >= 0x0660 && ch <= 0x0669)
                ch -= 0x0660 - '0';
            else if (ch >= 0x06f0 && ch <= 0x06F9)
                ch -= 0x06f0 - '0';
            chars[i] = ch;
        }
        return new String(chars);
    }

    private void pickerLocation(int s) {
        if (currentLat != 0.0) {
            defaultLat = currentLat;
            defaultLng = currentLng;
        }
        Intent locationPickerIntent = new LocationPickerActivity.Builder()
                .withLocation(defaultLat, defaultLng)
                .withGeolocApiKey(SendOrderActivity.this.getResources().getString(R.string.google_maps_key))
                .shouldReturnOkOnBackPressed()
                .withGoogleTimeZoneEnabled()
                .withVoiceSearchHidden()
                .withUnnamedRoadHidden()
                .build(SendOrderActivity.this);

        startActivityForResult(locationPickerIntent, s);
    }

    private void OnGPS() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(SendOrderActivity.this);
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

        if (ActivityCompat.checkSelfPermission(SendOrderActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                SendOrderActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(SendOrderActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
        } else {
            LocationRequest locationRequest = new LocationRequest();
            locationRequest.setInterval(10000);
            locationRequest.setFastestInterval(3000);
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

            LocationServices.getFusedLocationProviderClient(SendOrderActivity.this)
                    .requestLocationUpdates(locationRequest, new LocationCallback() {

                        @Override
                        public void onLocationResult(LocationResult locationResult) {
                            super.onLocationResult(locationResult);
                            LocationServices.getFusedLocationProviderClient(SendOrderActivity.this)
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


    public void hideSoftKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}