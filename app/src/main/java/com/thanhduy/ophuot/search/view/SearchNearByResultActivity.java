package com.thanhduy.ophuot.search.view;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.thanhduy.ophuot.R;
import com.thanhduy.ophuot.base.BaseActivity;
import com.thanhduy.ophuot.database.SqlLiteDbHelper;
import com.thanhduy.ophuot.list_homestay.ListHomestayAdapter;
import com.thanhduy.ophuot.model.Homestay;
import com.thanhduy.ophuot.search.presenter.SearchPresenter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by buivu on 04/04/2017.
 */

public class SearchNearByResultActivity extends BaseActivity implements LocationListener, View.OnClickListener {
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.fab_map)
    FloatingActionButton fabMap;
    @BindView(R.id.recycler_homestay)
    RecyclerView recyclerView;
    @BindView(R.id.linear_reload)
    LinearLayout linearReload;
    @BindView(R.id.btn_reload)
    Button btnReload;

    private LocationManager locationManager;
    public static final int REQUEST_ID_ACCESS_COARSE_FINE_LOCATION = 101;
    private static final String[] PERMISSIONS_LOCATION = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters

    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1; // 1 minute
    // flag for GPS status
    private boolean isGPSEnabled = false;

    // flag for network status
    private boolean isNetworkEnabled = false;
    // flag for GPS status
    private boolean canGetLocation = false;
    private Location location; // location
    private double latitude; // latitude
    private double longitude; // longitude
    private List<String> listDataResponse = new ArrayList<>();
    private SqlLiteDbHelper sqlLiteDbHelper;
    private SearchPresenter presenter;
    private List<Homestay> homestayList = new ArrayList<>();
    private ListHomestayAdapter listHomestayAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_nearby_result);
        ButterKnife.bind(this);
        //init toolbase
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(getResources().getString(R.string.placesNearByTitle));
        //init data
        sqlLiteDbHelper = new SqlLiteDbHelper(this);
        presenter = new SearchPresenter(this);
        listHomestayAdapter = new ListHomestayAdapter(this, homestayList);

        //get current location
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkLocationPermission()) {
                getLocation();
            }
        } else {
            getLocation();
        }

        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            showSettingLocationAlert();
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            linearReload.setVisibility(View.GONE);
        }
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(listHomestayAdapter);
        //event click
        btnReload.setOnClickListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                //TODO:
                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                //Prompt the user once explanation has been shown
                //(just doing it here for now, note that with this code, no explanation is shown)
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        REQUEST_ID_ACCESS_COARSE_FINE_LOCATION);


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        REQUEST_ID_ACCESS_COARSE_FINE_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    private void showSettingLocationAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //set title
        builder.setTitle(getResources().getString(R.string.GPSTitle));
        //set message
        builder.setMessage(getResources().getString(R.string.GPSContent));
        //on press
        builder.setPositiveButton(getResources().getString(R.string.setting), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent settingIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(settingIntent);
            }
        });
        //on cancel
        builder.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        builder.show();
    }

    public void getLocation() {
        try {

            LocationManager locationManager;
            locationManager = (LocationManager) this
                    .getSystemService(LOCATION_SERVICE);

            // getting GPS status
            isGPSEnabled = locationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);

            // getting network status
            isNetworkEnabled = locationManager
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isGPSEnabled && !isNetworkEnabled) {
                // no network provider is enabled
            } else {
                this.canGetLocation = true;
                // First get location from Network Provider
                if (isNetworkEnabled) {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        locationManager.requestLocationUpdates(
                                LocationManager.NETWORK_PROVIDER,
                                MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                    }

                    if (locationManager != null) {
                        location = locationManager
                                .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (location != null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                        }
                    }
                }
                // if GPS Enabled get lat/long using GPS Services
                if (isGPSEnabled) {
                    if (location == null) {
                        locationManager.requestLocationUpdates(
                                LocationManager.GPS_PROVIDER,
                                MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                        if (locationManager != null) {
                            location = locationManager
                                    .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            if (location != null) {
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                            }
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (location != null) {
            listDataResponse = getDataFromGeocoder(location.getLatitude(), location.getLongitude());
            int provinceId = 0;
            for (int i = 0; i < listDataResponse.size() - 1; i++) {
                provinceId = sqlLiteDbHelper.getProvinceIdByProvinceName(removeCharacter(listDataResponse.get(i)));
                if (provinceId != 0) {
                    break;
                }
            }
            //call data from firebase
            showDataSearchResult(provinceId);
        } else {
        }
    }

    private void showDataSearchResult(int provinceId) {
        presenter.searchHomestayNearby(provinceId).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot != null) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Homestay homestay = snapshot.getValue(Homestay.class);
                        if (homestay != null) {
                            if (!homestayList.contains(homestay)) {
                                homestayList.add(homestay);
                                listHomestayAdapter.notifyDataSetChanged();
                                Log.d("DATA", homestay.getName());
                            }
                        }
                    }
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private String removeCharacter(String text) {
        if (text != null) {
            if (text.contains("tỉnh")) {
                text = text.substring(5, text.length());
            }
            return text;
        }
        return "";
    }

    private List<String> getDataFromGeocoder(double lat, double lng) {
        List<String> listDataResponse = new ArrayList<>();
        try {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> addressList = geocoder.getFromLocation(lat, lng, 1);
            if (addressList.isEmpty()) {
                return listDataResponse;
            } else {
                if (addressList.size() > 0) {
                    listDataResponse.add(addressList.get(0).getAdminArea());
                    listDataResponse.add(addressList.get(0).getAddressLine(3));
                    listDataResponse.add(addressList.get(0).getAddressLine(2));
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return listDataResponse;
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onClick(View v) {
        if (v == btnReload) {
            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                showSettingLocationAlert();
            } else {

                showProgessDialog();
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        hideProgressDialog();
                        getLocation();
                        if (location != null) {
                            recyclerView.setVisibility(View.VISIBLE);
                            linearReload.setVisibility(View.GONE);
                        }
                    }
                }, 5000);
            }
        }
    }
}
