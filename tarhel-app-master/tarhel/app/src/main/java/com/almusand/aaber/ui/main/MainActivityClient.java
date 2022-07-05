package com.almusand.aaber.ui.main;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;

import com.almusand.aaber.R;
import com.almusand.aaber.model.Categories;
import com.almusand.aaber.model.Setting;
import com.almusand.aaber.model.User;
import com.almusand.aaber.ui.balance.BalanceActivity;
import com.almusand.aaber.ui.base.BaseActivity;
import com.almusand.aaber.ui.contactUs.ContactUsActivity;
import com.almusand.aaber.ui.myOrders.MyOrdersActivity;
import com.almusand.aaber.ui.notification.NotificationActivity;
import com.almusand.aaber.ui.privacyTerms.PrivacyTermsActivity;
import com.almusand.aaber.ui.profile.ProfileActivity;
import com.almusand.aaber.ui.profile.ProfileViewModel;
import com.almusand.aaber.ui.sendOrder.SendOrderActivity;
import com.almusand.aaber.ui.setting.SettingActivity;
import com.almusand.aaber.ui.splash.SettingViewModel;
import com.almusand.aaber.utils.AppPreferences;
import com.almusand.aaber.utils.CustomTypefaceSpan;
import com.almusand.aaber.utils.LocaleManager;
import com.cooltechworks.views.shimmer.ShimmerRecyclerView;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivityClient extends BaseActivity implements View.OnClickListener {


    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    private ImageView imgNotification;
    private TextView tvNotificationCount;
    private ShimmerRecyclerView rvCategories;
    private List<Categories> categoriesList;
    private MainCategoriesAdapter mainCategoriesAdapter;
    private MainViewModel viewModelCategores;
    private NavigationView navView;

    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_client);
        setUpToolbar();

        viewModelCategores = new ViewModelProvider(this).get(MainViewModel.class);
        user = new Gson().fromJson(AppPreferences.getUser(this), User.class);

        initViews();

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                displaySelectedScreen(menuItem.getItemId());
                return true;
            }
        });

        setupRecyclerViewCategories();

        getMainCategories();
    }

    private void getMainCategories() {
        viewModelCategores.getCategories(this);
        viewModelCategores.mutableLiveCategories.observe(this, new Observer<List<Categories>>() {
            @Override
            public void onChanged(List<Categories> categories) {
                categoriesList = categories;
                mainCategoriesAdapter.setList(categoriesList);
                rvCategories.hideShimmerAdapter();
            }
        });
    }

    private void setupRecyclerViewCategories() {
        mainCategoriesAdapter = new MainCategoriesAdapter(new MainCategoriesAdapter.onItemClick() {
            @Override
            public void onItemClick(Categories categories, LinearLayout lyContainer) {
                startActivity(new Intent(MainActivityClient.this, SendOrderActivity.class)
                        .putExtra("categories", categories));
            }
        });
        mainCategoriesAdapter.setLang(LocaleManager.getLocale(MainActivityClient.this.getResources()).getLanguage());
        rvCategories.setHasFixedSize(true);
        rvCategories.setLayoutManager(new GridLayoutManager(this, 2));
        rvCategories.setAdapter(mainCategoriesAdapter);
        rvCategories.showShimmerAdapter();
    }

    private void initViews() {
        rvCategories = findViewById(R.id.rv_categories);
        drawerLayout = findViewById(R.id.drawer_layout);
        navView = findViewById(R.id.nav_view);
        imgNotification = findViewById(R.id.img_notification);
        tvNotificationCount = findViewById(R.id.tv_notification_count);

        categoriesList = new ArrayList<>();

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

        //creating fragment object
        Fragment fragment = null;

        switch (itemId) {

            case R.id.nav_profile:
                startActivity(new Intent(MainActivityClient.this, ProfileActivity.class));
                break;
            case R.id.nav_my_payment:
                startActivity(new Intent(MainActivityClient.this, BalanceActivity.class));
                break;
            case R.id.nav_history:
                startActivity(new Intent(MainActivityClient.this, MyOrdersActivity.class));
                break;
            case R.id.nav_contact:
                startActivity(new Intent(MainActivityClient.this, ContactUsActivity.class));
                break;
            case R.id.nav_privacy_policy:
                startActivity(new Intent(MainActivityClient.this, PrivacyTermsActivity.class));

                break;
            case R.id.nav_setting:
                startActivity(new Intent(MainActivityClient.this, SettingActivity.class));

                break;
            case R.id.nav_logout:
                showLogoutAlert(MainActivityClient.this);

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
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.img_notification:
                startActivity(new Intent(MainActivityClient.this, NotificationActivity.class));
                break;

        }
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START))
            drawerLayout.closeDrawer(GravityCompat.START);
        else
            super.onBackPressed();
    }

    private void applyFontToMenuItem(MenuItem mi) {
//        Typeface font = Typeface.createFromAsset(getAssets(), "tajawal_medium.TTF");
        Typeface font = ResourcesCompat.getFont(MainActivityClient.this, R.font.tajawal_medium);
        SpannableString mNewTitle = new SpannableString(mi.getTitle());
        mNewTitle.setSpan(new CustomTypefaceSpan("", font), 0, mNewTitle.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        mi.setTitle(mNewTitle);

    }

    @Override
    protected void onResume() {
        super.onResume();
        updateNotificationCount();
    }

    private void updateNotificationCount() {
        SettingViewModel viewModel = new ViewModelProvider(this).get(SettingViewModel.class);
        viewModel.getSettings(this, user.getId());
        viewModel.mutableLiveData.observe(this, new Observer<Setting>() {
            @Override
            public void onChanged(Setting setting) {
                if (setting != null) {
                    AppPreferences.setSetting(MainActivityClient.this, new Gson().toJson(setting));
                    if (setting.getUndreadnotifications() > 0) {
                        tvNotificationCount.setVisibility(View.VISIBLE);
                        tvNotificationCount.setText(String.valueOf(setting.getUndreadnotifications()));
                    } else {
                        tvNotificationCount.setVisibility(View.GONE);
                    }
                }
            }
        });

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
        viewModel.updateUser(MainActivityClient.this, null, map);
        viewModel.mutableLiveData.observe(this, new Observer<User>() {
            @Override
            public void onChanged(User user) {
                if (user != null) {
                    AppPreferences.logout(MainActivityClient.this);
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