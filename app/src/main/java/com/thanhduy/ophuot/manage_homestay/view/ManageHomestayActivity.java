package com.thanhduy.ophuot.manage_homestay.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.thanhduy.ophuot.R;
import com.thanhduy.ophuot.base.BaseActivity;
import com.thanhduy.ophuot.manage_homestay.AdapterViewPager;
import com.thanhduy.ophuot.model.Homestay;
import com.thanhduy.ophuot.utils.Constants;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by buivu on 13/03/2017.
 */

public class ManageHomestayActivity extends BaseActivity implements OnMapReadyCallback {

    @BindView(R.id.txt_manage_type_title)
    TextView txtTitleType;
    @BindView(R.id.txt_manage_title_price)
    TextView txtTitlePrice;
    @BindView(R.id.txt_manage_title_number_bathroom)
    TextView txtTitleNumberBathroom;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.viewPager)
    ViewPager mViewPager;
    @BindView(R.id.imgPoster_detailResort)
    ImageView imgPoster;
    @BindView(R.id.txt_manage_description)
    TextView txtDescription;
    @BindView(R.id.txt_manage_type)
    TextView txtType;
    @BindView(R.id.txt_manage_number_passemger)
    TextView txtNumberPassenger;
    @BindView(R.id.txt_manage_number_bedroom)
    TextView txtNumberBedroom;
    @BindView(R.id.txt_manage_time_open)
    TextView txtTimeOpen;
    @BindView(R.id.txt_manage_time_close)
    TextView txtTimeClose;
    @BindView(R.id.txt_manage_number_bed)
    TextView txtNumberBed;
    @BindView(R.id.txt_manage_convenient)
    TextView txtConvenient;
    @BindView(R.id.txt_manage_animal)
    TextView txtAnimal;


    private Homestay homestay;
    private AdapterViewPager mViewPagerAdapter;
    private GoogleMap googleMap;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homestay_detail);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //get intent
        homestay = (Homestay) getIntent().getSerializableExtra(Constants.HOMESTAY);
        //set view pager for image slide
        if (homestay.getImages().size() > 0) {
            imgPoster.setVisibility(View.GONE);
            mViewPagerAdapter = new AdapterViewPager(this, homestay.getImages());
            mViewPager.setAdapter(mViewPagerAdapter);
        } else {
            mViewPager.setVisibility(View.GONE);
            imgPoster.setImageResource(R.drawable.no_image);
        }
        mViewPagerAdapter = new AdapterViewPager(this, homestay.getImages());
        mViewPager.setAdapter(mViewPagerAdapter);
        //show data
        loadData();
        setUpMapIfNeeded();
    }

    private void loadData() {
        //load title
        getSupportActionBar().setTitle(homestay.getName());
        txtTitleType.setText(homestay.getType());
        txtTitlePrice.setText(homestay.getPrice());
        txtTitleNumberBathroom.setText(homestay.getDetails().get(Constants.BATH_ROOM).toString());
        txtDescription.setText(homestay.getDescription());
        txtType.setText(homestay.getType());
        txtNumberPassenger.setText(homestay.getDetails().get(Constants.MAX).toString());
        txtNumberBedroom.setText(homestay.getDetails().get(Constants.BED_ROOM).toString());
        txtTimeClose.setText(homestay.getDetails().get(Constants.TIME_CLOSE).toString());
        txtTimeOpen.setText(homestay.getDetails().get(Constants.TIME_OPEN).toString());
        txtNumberBed.setText(homestay.getDetails().get(Constants.BED).toString());
        txtConvenient.setText(homestay.getDetails().get(Constants.CONVENIENT).toString());

        txtAnimal.setText(Boolean.parseBoolean(homestay.getDetails().get(Constants.PET).toString()) ? getResources().getString(R.string.yes) : getResources().getString(R.string.no));
    }

    private void setUpMapIfNeeded() {
        if (googleMap == null) {
            SupportMapFragment mapFrag
                    = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_map);
            mapFrag.getMapAsync(this);
            if (googleMap != null) {
                googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker) {
                        marker.showInfoWindow();
                        marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
                        return true;
                    }
                });
            }
        }

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        //move camera
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Double.parseDouble(homestay.getAddress().get(Constants.LAT).toString()),
                Double.parseDouble(homestay.getAddress().get(Constants.LNG).toString())), 15));
        //add marker
        MarkerOptions markerOptions = new MarkerOptions()
                .position(new LatLng(Double.parseDouble(homestay.getAddress().get(Constants.LAT).toString()),
                        Double.parseDouble(homestay.getAddress().get(Constants.LNG).toString())));
        googleMap.addMarker(markerOptions);
        //event click google map
        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                Intent intent = new Intent(ManageHomestayActivity.this, FullScreenGoogleMapActivity.class);
                intent.putExtra(Constants.LAT, Double.parseDouble(homestay.getAddress().get(Constants.LAT).toString()));
                intent.putExtra(Constants.LNG, Double.parseDouble(homestay.getAddress().get(Constants.LNG).toString()));
                intent.putExtra(Constants.HOMESTAY_NAME, homestay.getName());
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_manage_homestay, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
