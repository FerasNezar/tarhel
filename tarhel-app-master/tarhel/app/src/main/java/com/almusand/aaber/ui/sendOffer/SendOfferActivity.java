package com.almusand.aaber.ui.sendOffer;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.almusand.aaber.R;
import com.almusand.aaber.model.Offer;
import com.almusand.aaber.model.Order;
import com.almusand.aaber.model.User;
import com.almusand.aaber.ui.base.BaseActivity;
import com.almusand.aaber.ui.main.MainActivityProvider;
import com.almusand.aaber.utils.AppPreferences;
import com.almusand.aaber.utils.Constants;
import com.almusand.aaber.utils.LocaleManager;
import com.almusand.aaber.utils.ProgressButton;
import com.directions.route.AbstractRouting;
import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import es.dmoral.toasty.Toasty;
import timber.log.Timber;

import static com.almusand.aaber.utils.Constants.MAPVIEW_BUNDLE_KEY;


/**
 * Developed by Anas Elshwaf
 * anaselshawaf357@gmail.com
 */
public class SendOfferActivity extends BaseActivity implements OnMapReadyCallback, View.OnClickListener, RoutingListener {

    private MapView mapView;
    private ImageView imgBack;
    private TextView tvDistance;
    private TextView tvTitle;
    private EditText etPrice;
    private EditText etNotes;

    private String modifyPrice;

    private Order order;
    private User user;

    private GoogleMap mMap;
    private List<Polyline> polylines;
    private static final int[] COLORS = new int[]{R.color.colorAccent};

    View btConfirm;
    private ProgressButton progressButton;
    private SendOfferViewModel viewModel;
    private Offer offer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_offer);

        viewModel = new ViewModelProvider(this).get(SendOfferViewModel.class);
        order = (Order) getIntent().getExtras().getSerializable("order");
        offer = (Offer) getIntent().getExtras().getSerializable("offer");
        modifyPrice = getIntent().getExtras().getString("modify");

        user = new Gson().fromJson(AppPreferences.getUser(this), User.class);

        initialViews();

        initGoogleMap(savedInstanceState);

    }


    private void initialViews() {
        tvTitle = findViewById(R.id.tvTitle);
        mapView = findViewById(R.id.map_view);
        btConfirm = findViewById(R.id.bt_confirm);
        imgBack = findViewById(R.id.img_back);
        tvDistance = findViewById(R.id.tv_distance);
        etPrice = findViewById(R.id.et_price);
        etNotes = findViewById(R.id.et_notes);

        if (modifyPrice != null && modifyPrice.equals("yes")) {
            tvTitle.setText(getString(R.string.modify_price));
        }

        String distance = getDistance(Double.parseDouble(order.getPickupLat()), Double.parseDouble(order.getPickupLng()),
                Float.parseFloat(user.getLat()), Float.parseFloat(user.getLng()));

        tvDistance.append(new StringBuilder().append(" ").append(distance).append(getString(R.string.km)).toString());

        progressButton = new ProgressButton(this, btConfirm);

        if (offer != null) {
            etPrice.setText(offer.getPrice());
            etNotes.setText(offer.getNote());
            progressButton.setButtonTittle(SendOfferActivity.this.getResources().getString(R.string.update));
        } else {
            progressButton.setButtonTittle(SendOfferActivity.this.getResources().getString(R.string.confirm));
        }


        btConfirm.setOnClickListener(this);
        imgBack.setOnClickListener(this);
        polylines = new ArrayList<>();

    }

    private void initGoogleMap(Bundle savedInstanceState) {
        // *** IMPORTANT ***
        // MapView requires that the Bundle you pass contain _ONLY_ MapView SDK
        // objects or sub-Bundles.
        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
        }

        mapView.onCreate(mapViewBundle);

        mapView.getMapAsync(this);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Bundle mapViewBundle = outState.getBundle(MAPVIEW_BUNDLE_KEY);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MAPVIEW_BUNDLE_KEY, mapViewBundle);
        }

        mapView.onSaveInstanceState(mapViewBundle);
    }

    @Override
    public void onMapReady(GoogleMap map) {

        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            boolean success = map.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this, R.raw.google_style));

            if (!success) {
                Timber.e("Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Timber.e("Can't find style. Error: ");
        }

        mMap = map;

        getRouteToMarker();

    }

    private void getRouteToMarker() {

        LatLng providerLatLng = new LatLng(Double.parseDouble(user.getLat()), Double.parseDouble(user.getLng()));
        LatLng storeLatLng = new LatLng(Double.parseDouble(order.getPickupLat()), Double.parseDouble(order.getPickupLng()));

        Marker markerProvider = mMap.addMarker(new MarkerOptions().position(providerLatLng).title("Me"));
        markerProvider.setIcon(BitmapDescriptorFactory.fromBitmap(getDrawableMarker(R.drawable.marker_me)));

        Marker markerStore = mMap.addMarker(new MarkerOptions().position(storeLatLng).title(order.getLocation()));
        markerStore.setIcon(BitmapDescriptorFactory.fromBitmap(getDrawableMarker(R.drawable.marker_store)));

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(providerLatLng, 11));

        Routing routing = new Routing.Builder()
                .travelMode(AbstractRouting.TravelMode.DRIVING)
                .withListener(this)
                .alternativeRoutes(false)
                .waypoints(storeLatLng, providerLatLng)
                .key(Constants.Routing_KEY)
                .build();
        routing.execute();


    }

    private Bitmap getDrawableMarker(int icon) {
        int height = 80;
        int width = 80;
        @SuppressLint("UseCompatLoadingForDrawables")
        BitmapDrawable bitmapdraw = (BitmapDrawable) getResources().getDrawable(icon);
        Bitmap b = bitmapdraw.getBitmap();
        Bitmap marker = Bitmap.createScaledBitmap(b, width, height, false);
        return marker;
    }

    @Override
    public void onRoutingFailure(RouteException e) {
        if (e != null) {
            Toast.makeText(SendOfferActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(SendOfferActivity.this, "Something went wrong, Try again", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRoutingStart() {
    }

    @Override
    public void onRoutingSuccess(ArrayList<Route> route, int shortestRouteIndex) {
        if (polylines.size() > 0) {
            for (Polyline poly : polylines) {
                poly.remove();
            }
        }

        polylines = new ArrayList<>();
        //add route(s) to the map.
        for (int i = 0; i < route.size(); i++) {

            //In case of more than 5 alternative routes
//            int colorIndex = i % COLORS.length;

            PolylineOptions polyOptions = new PolylineOptions();
            polyOptions.color(getResources().getColor(COLORS[0]));
            polyOptions.width(10 + i * 3);
            polyOptions.addAll(route.get(i).getPoints());
            Polyline polyline = mMap.addPolyline(polyOptions);
            polylines.add(polyline);

//            Toast.makeText(getContext(), "Route " + (i + 1) + ": distance - " + route.get(i).getDistanceValue() + ": duration - " + route.get(i).getDurationValue(), Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onRoutingCancelled() {
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onPause() {
        mapView.onPause();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        mapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.bt_confirm:
                if (validInput()) {
                    btConfirm.setEnabled(false);
                    progressButton.buttonActivated();
                    if (offer == null) {
                        sendOffer();
                    } else {
                        updateOffer();
                    }
                }
                break;

            case R.id.img_back:
                finish();
                break;
        }
    }

    private void updateOffer() {

        viewModel.UpdateOffer(SendOfferActivity.this, String.valueOf(offer.getId()), etPrice.getText().toString());
        viewModel.mutableLiveDataUpdate.observe(this, new Observer<Offer>() {
            @Override
            public void onChanged(Offer offer) {
                if (offer != null) {
                    Toasty.success(SendOfferActivity.this, SendOfferActivity.this.getResources().getString(R.string.offer_updated), Toasty.LENGTH_SHORT).show();
                    Intent intent = new Intent();
                    intent.putExtra("new_price", etPrice.getText().toString());
                    intent.putExtra("offer", offer);
                    setResult(12, intent);
                    finish();
                } else {
                    btConfirm.setEnabled(true);
                    progressButton.buttonDactivated();
                }
            }
        });

    }

    private boolean validInput() {

        if (etPrice.getText() == null || etPrice.getText().toString().isEmpty()) {
            Toast.makeText(SendOfferActivity.this, SendOfferActivity.this.getResources().getText(R.string.msg_price), Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }


    private void sendOffer() {
        HashMap<String, String> map = new HashMap<>();
        map.put("order_id", String.valueOf(order.getId()));
        map.put("price", etPrice.getText().toString());
        map.put("note", etNotes.getText().toString());
        viewModel.SendOffer(SendOfferActivity.this, map);

        viewModel.mutableLiveData.observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                if (s != null && s.equals("done")) {
                    progressButton.buttonFinished();
                    Toasty.success(SendOfferActivity.this, SendOfferActivity.this.getResources().getString(R.string.offer_saved_successfully), Toasty.LENGTH_SHORT).show();
                    startActivity(new Intent(SendOfferActivity.this, MainActivityProvider.class)
                            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                    finish();
                } else {
                    btConfirm.setEnabled(true);
                    progressButton.buttonDactivated();
                }
            }
        });
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
