package com.almusand.aaber.ui.mapOrders;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.almusand.aaber.R;
import com.almusand.aaber.model.CurrentLocation;
import com.almusand.aaber.model.Order;
import com.almusand.aaber.model.User;
import com.almusand.aaber.ui.myOrders.OrdersViewModel;
import com.almusand.aaber.ui.orderDetails.OrderDetailsActivity;
import com.almusand.aaber.utils.AppPreferences;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;

import java.util.List;

import es.dmoral.toasty.Toasty;
import timber.log.Timber;

import static com.almusand.aaber.utils.Constants.MAPVIEW_BUNDLE_KEY;

/**
 * Developed by Anas Elshwaf
 * anaselshawaf357@gmail.com
 */
public class OrdersFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener, View.OnClickListener {

    private Button btRefresh;
    private MapView mMapView;
    private TextView tvMoreOrders;
    private static GoogleMap mMap;

    private User user;
    private Context mContext;
    private NearbyOrdersViewModel nearbyOrdersViewModel;
    private OrdersViewModel ordersViewModel;
    private List<Order> ordersList;
    private static Bitmap smallMarker;
    private static String currentLat;
    private static String currentLng;

    public OrdersFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_orders, container, false);

        nearbyOrdersViewModel = new ViewModelProvider(getActivity()).get(NearbyOrdersViewModel.class);
        ordersViewModel = new ViewModelProvider(this).get(OrdersViewModel.class);

        user = new Gson().fromJson(AppPreferences.getUser(mContext), User.class);

        initialViews(view);

        initGoogleMap(savedInstanceState);

        nearbyOrdersViewModel.mutableLiveLocation.observe(getViewLifecycleOwner(), new Observer<CurrentLocation>() {
            @Override
            public void onChanged(CurrentLocation currentLocation) {
                if (currentLocation != null) {
                    currentLat = currentLocation.getLat();
                    currentLng = currentLocation.getLng();
                    getNearbyOrders(currentLocation.getLat(), currentLocation.getLng());
                }
            }
        });

        return view;
    }

    private void getNearbyOrders(String lat, String lng) {
        nearbyOrdersViewModel.getOrders(getContext(), 1, lat, lng);
        nearbyOrdersViewModel.mutableLiveData.observe(getViewLifecycleOwner(), new Observer<List<Order>>() {
            @Override
            public void onChanged(List<Order> orders) {
                if (orders.size() > 0) {
                    for (Order order : orders) {
                        LatLng location = new LatLng(Double.parseDouble(order.getPickupLat()), Double.parseDouble(order.getPickupLng()));
                        Marker marker = mMap.addMarker(new MarkerOptions().position(location).title(order.getLocation()));
                        marker.setIcon(BitmapDescriptorFactory.fromBitmap(smallMarker));
                        marker.setTag(order);
                        marker.showInfoWindow();
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 10));

                    }
                } else {
                    Toasty.info(mContext, R.string.msg_no_nearby_orders, Toasty.LENGTH_SHORT).show();

//                    BitmapDrawable bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.drawable.ic_pin);
//                    Bitmap b = bitmapdraw.getBitmap();
//                    Bitmap userMarker = Bitmap.createScaledBitmap(b, 70, 70, false);
                    LatLng location = new LatLng(Double.parseDouble(lat), Double.parseDouble(lng));
//                    Marker marker = mMap.addMarker(new MarkerOptions().position(location).title(getString(R.string.current_location)));
//                    marker.setIcon(BitmapDescriptorFactory.fromBitmap(userMarker));
//                    marker.showInfoWindow();
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 10));

                }
            }
        });
    }

    private void initialViews(View view) {
        mMapView = view.findViewById(R.id.orders_map);
        btRefresh = view.findViewById(R.id.bt_refresh);
        tvMoreOrders = view.findViewById(R.id.tv_more_orders);

        btRefresh.setOnClickListener(this);
        tvMoreOrders.setOnClickListener(this);
    }


    private void initGoogleMap(Bundle savedInstanceState) {

        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
        }

        mMapView.onCreate(mapViewBundle);

        mMapView.getMapAsync(this);
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Bundle mapViewBundle = outState.getBundle(MAPVIEW_BUNDLE_KEY);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MAPVIEW_BUNDLE_KEY, mapViewBundle);
        }

        mMapView.onSaveInstanceState(mapViewBundle);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {

        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            boolean success = googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            mContext, R.raw.google_style));

            if (!success) {
                Timber.e("Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Timber.e("Can't find style. Error: ");
        }

        mMap = googleMap;
        googleMap.setOnMarkerClickListener(this);

        int height = 70;
        int width = 70;
        BitmapDrawable bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.drawable.ic_pin);
        Bitmap b = bitmapdraw.getBitmap();
        smallMarker = Bitmap.createScaledBitmap(b, width, height, false);

        if (getView() != null)
            if (currentLat == null) {
                getNearbyOrders(user.getLat(), user.getLng());
            } else {
                getNearbyOrders(currentLat, currentLng);
            }
    }


    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
        mMapView.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        mMapView.onStop();
    }


    @Override
    public void onPause() {
        mMapView.onPause();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        mMapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }


    @Override
    public boolean onMarkerClick(Marker marker) {
        Order selectedOrder = (Order) marker.getTag();

        ordersViewModel.getSingleOrder(mContext, selectedOrder.getId());
        ordersViewModel.mutableLiveDataSingleOrder.observe(getViewLifecycleOwner(), new Observer<Order>() {
            @Override
            public void onChanged(Order order) {
                startActivity(new Intent(mContext, OrderDetailsActivity.class)
                        .putExtra("order", order).putExtra("type", "provider"));

            }
        });

        return false;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.bt_refresh:
                if (currentLat == null) {
                    getNearbyOrders(user.getLat(), user.getLng());
                } else {
                    getNearbyOrders(currentLat, currentLng);
                }
                break;

            case R.id.tv_more_orders:
                startActivity(new Intent(mContext, AllOrdersActivity.class)
                        .putExtra("currentLat", currentLat).putExtra("currentLng", currentLng));
                break;
        }
    }

}
