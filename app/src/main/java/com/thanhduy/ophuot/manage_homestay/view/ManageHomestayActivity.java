package com.thanhduy.ophuot.manage_homestay.view;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.thanhduy.ophuot.R;
import com.thanhduy.ophuot.base.BaseActivity;
import com.thanhduy.ophuot.base.InternetConnection;
import com.thanhduy.ophuot.comment.view.CommentActivity;
import com.thanhduy.ophuot.manage_homestay.AdapterViewPager;
import com.thanhduy.ophuot.manage_homestay.presenter.ManageHomestayPresenter;
import com.thanhduy.ophuot.model.Homestay;
import com.thanhduy.ophuot.utils.Constants;
import com.thanhduy.ophuot.utils.ShowSnackbar;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by buivu on 13/03/2017.
 */

public class ManageHomestayActivity extends BaseActivity implements OnMapReadyCallback, View.OnClickListener {

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
    @BindView(R.id.btn_comment)
    Button btnComment;
    @BindView(R.id.btn_see_rating)
    Button btnSeeRating;

    private Homestay homestay;
    private AdapterViewPager mViewPagerAdapter;
    private GoogleMap googleMap;
    private ManageHomestayPresenter presenter;
    private DatabaseReference mDatabase;

    private MenuItem menuEdit, menuDelete;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_homestay_detail);
        presenter = new ManageHomestayPresenter(this);
        mDatabase = FirebaseDatabase.getInstance().getReference();

        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //get intent
        homestay = (Homestay) getIntent().getSerializableExtra(Constants.HOMESTAY);
        initInfo();
        //event click button
        btnComment.setOnClickListener(this);
        btnSeeRating.setOnClickListener(this);
    }

    private void initInfo() {
        if (InternetConnection.getInstance().isOnline(ManageHomestayActivity.this)) {
            //show menu
            if (menuEdit != null && menuDelete != null) {
                menuDelete.setVisible(true);
                menuEdit.setVisible(true);
            }
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
            loadDataAfterEdited();
            setUpMapIfNeeded();
            changeTextCommentIfYes();
        } else {
            //hide menu
            if (menuEdit != null && menuDelete != null) {
                menuDelete.setVisible(false);
                menuEdit.setVisible(false);
            }
            Snackbar snackbar = Snackbar.make(findViewById(R.id.activity), getResources().getString(R.string.noInternet), Snackbar.LENGTH_INDEFINITE)
                    .setAction(getResources().getString(R.string.retry), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            initInfo();
                        }
                    });
            snackbar.show();
        }
    }

    private void changeTextCommentIfYes() {
        if (homestay.getComments() != null) {
            mDatabase.child(Constants.HOMESTAY).child(String.valueOf(homestay.getProvinceId())).child(String.valueOf(homestay.getDistrictId()))
                    .child(homestay.getId()).child(Constants.COMMENTS).child(Constants.COMMENT_COUNT).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        Long commentCount = dataSnapshot.getValue(Long.class);
                        btnComment.setText(String.format("%s (%d)", getResources().getString(R.string.comment), commentCount));
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }
    }

    private void loadDataAfterEdited() {
        mDatabase.child(Constants.HOMESTAY).child(String.valueOf(homestay.getProvinceId())).child(String.valueOf(homestay.getDistrictId())).child(homestay.getId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    Homestay homestay = dataSnapshot.getValue(Homestay.class);
                    if (homestay != null) {
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
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
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
        menuEdit = menu.findItem(R.id.action_edit);
        menuDelete = menu.findItem(R.id.action_delete);
        if (!InternetConnection.getInstance().isOnline(ManageHomestayActivity.this)) {
            menuEdit.setVisible(false);
            menuDelete.setVisible(false);
        } else {
            menuEdit.setVisible(true);
            menuDelete.setVisible(true);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        } else if (item.getItemId() == R.id.action_delete) {
            alertDeleteHomestay();
        } else if (item.getItemId() == R.id.action_edit) {
            moveToActivityEditHomestay();
        }
        return super.onOptionsItemSelected(item);
    }

    private void moveToActivityEditHomestay() {
        Intent intent = new Intent(ManageHomestayActivity.this, EditHomestayActivity.class);
        intent.putExtra(Constants.HOMESTAY, homestay);
        startActivity(intent);
    }

    private void alertDeleteHomestay() {
        //show alert xác nhận
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.confirmDeleteHomestay)
                .setCancelable(false)
                .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (InternetConnection.getInstance().isOnline(ManageHomestayActivity.this)) {
                            presenter.deleteHomestay(String.valueOf(homestay.getProvinceId()), String.valueOf(homestay.getDistrictId()), homestay.getId(), getUid());
                            finish();
                        } else {
                            ShowSnackbar.showSnack(ManageHomestayActivity.this, getResources().getString(R.string.noInternet));
                        }
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
        builder.create().show();
    }


    @Override
    public void onClick(View v) {
        if (v == btnComment) {
            Intent intent = new Intent(ManageHomestayActivity.this, CommentActivity.class);
            intent.putExtra(Constants.HOMESTAY, homestay);
            startActivity(intent);
        } else if (v == btnSeeRating) {
            Intent intent = new Intent(ManageHomestayActivity.this, ListRatingActivity.class);
            intent.putExtra(Constants.HOMESTAY, homestay);
            startActivity(intent);
        }
    }
}
