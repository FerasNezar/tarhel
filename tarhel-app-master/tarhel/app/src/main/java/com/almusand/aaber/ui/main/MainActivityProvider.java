package com.almusand.aaber.ui.main;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.almusand.aaber.R;
import com.almusand.aaber.model.CurrentLocation;
import com.almusand.aaber.model.User;
import com.almusand.aaber.ui.balance.BalanceActivity;
import com.almusand.aaber.ui.base.BaseActivity;
import com.almusand.aaber.ui.contactUs.ContactUsActivity;
import com.almusand.aaber.ui.mapOrders.NearbyOrdersViewModel;
import com.almusand.aaber.ui.mapOrders.OrdersFragment;
import com.almusand.aaber.ui.myOffer.MyOffersActivity;
import com.almusand.aaber.ui.notification.NotificationActivity;
import com.almusand.aaber.ui.privacyTerms.PrivacyTermsActivity;
import com.almusand.aaber.ui.profile.ProfileActivity;
import com.almusand.aaber.ui.profile.ProfileViewModel;
import com.almusand.aaber.ui.setting.SettingActivity;
import com.almusand.aaber.utils.AppPreferences;
import com.almusand.aaber.utils.CustomTypefaceSpan;
import com.almusand.aaber.utils.LocaleManager;
import com.github.juanlabrador.badgecounter.BadgeCounter;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;

import java.util.HashMap;

public class MainActivityProvider extends BaseActivity implements View.OnClickListener {


    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    private NavigationView navView;
    private ImageView imgNotification;
    private TextView tvNotificationCount;

    private User user;

    private int mNotificationCounter = 0;
    private BadgeCounter badgeCounter;

    private static final int REQUEST_LOCATION = 1;
    LocationManager locationManager;
    private NearbyOrdersViewModel nearbyOrdersViewModel;


    @Override
    protected void onStart() {
        super.onStart();
        BaseActivity.requestAllPermissions(this);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_provider);
        setUpToolbar();

        nearbyOrdersViewModel = new ViewModelProvider(this).get(NearbyOrdersViewModel.class);
        user=new Gson().fromJson(AppPreferences.getUser(this),User.class);

        initViews();

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        getSupportFragmentManager().beginTransaction().replace(R.id.content_frame,
                new OrdersFragment()).commit();

        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                displaySelectedScreen(menuItem.getItemId());
                return true;
            }
        });

        locationManager = (LocationManager) MainActivityProvider.this.getSystemService(Context.LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            OnGPS();
        } else {
            getLocation();
        }
    }

    private void initViews() {
        drawerLayout = findViewById(R.id.drawer_layout);
        navView = findViewById(R.id.nav_view);
        imgNotification = findViewById(R.id.img_notification);
        tvNotificationCount = findViewById(R.id.tv_notification_count);

        Menu m = navView.getMenu();
        for (int i = 0; i < m.size(); i++) {
            MenuItem mi = m.getItem(i);

            //for aapplying a font to subMenu ...
            SubMenu subMenu = mi.getSubMenu();
            if (subMenu != null && subMenu.size() > 0) {
                for (int j = 0; j < subMenu.size(); j++) {
                    MenuItem subMenuItem = subMenu.getItem(j);
                    applyFontToMenuItem(subMenuItem);
                }
            }

            //the method we have create in activity
            applyFontToMenuItem(mi);
        }

        imgNotification.setOnClickListener(this);
    }

    private void setUpToolbar() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }


    private void displaySelectedScreen(int itemId) {

        Fragment fragment = null;

        switch (itemId) {

            case R.id.nav_profile:
                startActivity(new Intent(MainActivityProvider.this, ProfileActivity.class));
                break;
            case R.id.nav_my_payment:
                startActivity(new Intent(MainActivityProvider.this, BalanceActivity.class));
                break;
            case R.id.nav_my_offers:
                startActivity(new Intent(MainActivityProvider.this, MyOffersActivity.class));
                break;
            case R.id.nav_contact:
                startActivity(new Intent(MainActivityProvider.this, ContactUsActivity.class));
                break;
            case R.id.nav_privacy_policy:
                startActivity(new Intent(MainActivityProvider.this, PrivacyTermsActivity.class));

                break;
            case R.id.nav_setting:
                startActivity(new Intent(MainActivityProvider.this, SettingActivity.class));

                break;
            case R.id.nav_logout:
                showLogoutAlert(MainActivityProvider.this);
                break;
        }

        //replacing the fragment
        if (fragment != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame, fragment);
            ft.commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START))
            drawerLayout.closeDrawer(GravityCompat.START);
        else
            super.onBackPressed();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.img_notification:
                startActivity(new Intent(MainActivityProvider.this, NotificationActivity.class));
                break;

        }
    }

    private void applyFontToMenuItem(MenuItem mi) {
//        Typeface font = Typeface.createFromAsset(getAssets(), "tajawal_medium.TTF");
        Typeface font = ResourcesCompat.getFont(MainActivityProvider.this, R.font.tajawal_medium);
        SpannableString mNewTitle = new SpannableString(mi.getTitle());
        mNewTitle.setSpan(new CustomTypefaceSpan("", font), 0, mNewTitle.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        mi.setTitle(mNewTitle);

    }

    private void OnGPS() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivityProvider.this);
        builder.setMessage(getString(R.string.msg_enable_gps_provider)).setCancelable(false).setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
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

        if (ActivityCompat.checkSelfPermission(MainActivityProvider.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                MainActivityProvider.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivityProvider.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
        } else {
            LocationRequest locationRequest = new LocationRequest();
            locationRequest.setInterval(10000);
            locationRequest.setFastestInterval(3000);
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

            LocationServices.getFusedLocationProviderClient(MainActivityProvider.this)
                    .requestLocationUpdates(locationRequest, new LocationCallback() {

                        @Override
                        public void onLocationResult(LocationResult locationResult) {
                            super.onLocationResult(locationResult);
                            LocationServices.getFusedLocationProviderClient(MainActivityProvider.this)
                                    .removeLocationUpdates(this);
                            if (locationResult != null && locationResult.getLocations().size() > 0) {
                                int lastLocationIndex = locationResult.getLocations().size() - 1;

                                double lat = locationResult.getLocations().get(lastLocationIndex).getLatitude();
                                double lng = locationResult.getLocations().get(lastLocationIndex).getLongitude();

                                nearbyOrdersViewModel.setCurrentLatLng(new CurrentLocation(String.valueOf(lat), String.valueOf(lng)));

                            }
                        }
                    }, Looper.getMainLooper());
        }

    }

    public void showLogoutAlert(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder
                .setTitle(context.getString((R.string.logout)))
                .setMessage(context.getResources().getText(R.string.msg_logout))
                .setPositiveButton(context.getResources().getText(R.string.cancel), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .setNegativeButton(context.getResources().getText(R.string.yes), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        functionLogout();
                    }
                });
        AlertDialog alert11 = builder.create();
        alert11.show();
    }

    private void functionLogout() {
        HashMap<String, String> map = new HashMap<>();
        map.put("device_id", "");
        map.put("email",user.getEmail() );
        map.put("phone", user.getPhone());

        ProfileViewModel viewModel = new ViewModelProvider(this).get(ProfileViewModel.class);
        viewModel.updateUser(MainActivityProvider.this, null, map);
        viewModel.mutableLiveData.observe(this, new Observer<User>() {
            @Override
            public void onChanged(User user) {
                if (user != null) {
                    AppPreferences.logout(MainActivityProvider.this);
                } else {

                }
            }
        });
    }


    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleManager.setLocale(base));
    }


}