package com.thanhduy.ophuot.homestay_detail.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.thanhduy.ophuot.R;
import com.thanhduy.ophuot.base.BaseActivity;
import com.thanhduy.ophuot.base.ImageLoader;
import com.thanhduy.ophuot.chat.view.ChatActivity;
import com.thanhduy.ophuot.comment.view.CommentActivity;
import com.thanhduy.ophuot.list_homestay.GetUserInfoCallback;
import com.thanhduy.ophuot.manage_homestay.AdapterViewPager;
import com.thanhduy.ophuot.manage_homestay.view.FullScreenGoogleMapActivity;
import com.thanhduy.ophuot.model.Homestay;
import com.thanhduy.ophuot.model.User;
import com.thanhduy.ophuot.profile.guess_profile.GuessProfileActivitiy;
import com.thanhduy.ophuot.profile.view.ProfileUserActivity;
import com.thanhduy.ophuot.utils.Constants;
import com.thanhduy.ophuot.utils.DateFormatter;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by buivu on 21/03/2017.
 */

public class ActivityHomestayDetail extends BaseActivity implements OnMapReadyCallback, View.OnClickListener {

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
    @BindView(R.id.img_favorite)
    ImageView imgFavorite;
    @BindView(R.id.txt_homestay_owner_name)
    TextView txtOwnerName;
    @BindView(R.id.txt_homestay_owner_address)
    TextView txtOwnerAddress;
    @BindView(R.id.txt_homestay_owner_time)
    TextView txtTime;
    @BindView(R.id.btn_contact)
    Button btnContact;
    @BindView(R.id.btn_comment)
    Button btnComment;
    @BindView(R.id.img_owner_avatar)
    ImageView imgAvatar;

    private Homestay homestay;
    private AdapterViewPager mViewPagerAdapter;
    private GoogleMap googleMap;
    private DatabaseReference mDatabase;
    private User user;
    private boolean isLoadSuccess = false;
    private GetUserInfoCallback getUserInfoCallback;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homestay_detail);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        //get intent
        homestay = (Homestay) getIntent().getSerializableExtra(Constants.HOMESTAY);
        user = (User) getIntent().getSerializableExtra(Constants.USERS);

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
        //event click
        btnComment.setOnClickListener(this);
        imgFavorite.setOnClickListener(this);
        imgAvatar.setOnClickListener(this);
        btnContact.setOnClickListener(this);
        changeTextCommentIfYes();
    }

    private void changeTextCommentIfYes() {
        if (homestay.getComments() != null) {
            btnComment.setText(String.format("%s (%d)", getResources().getString(R.string.comment), homestay.getComments().getCommentCount()));
        }
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


        //load info owner
        if (user == null) {
            mDatabase.child(Constants.USERS).child(homestay.getPostBy()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot != null) {
                        User user = dataSnapshot.getValue(User.class);
                        if (user != null) {
                            txtOwnerName.setText(user.getName());
                            txtOwnerAddress.setText(user.getAddress().get(Constants.ADDRESS).toString());
                            txtTime.setText(String.format("%s %s", getResources().getString(R.string.joinAt), DateFormatter.formatDate(user.getCreateAt())));
                            ImageLoader.getInstance().loadImageAvatar(ActivityHomestayDetail.this,
                                    user.getAvatar(), imgAvatar);
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        } else {
            txtOwnerName.setText(user.getName());
            txtOwnerAddress.setText(user.getAddress().get(Constants.ADDRESS).toString());
            txtTime.setText(String.format("%s %s", getResources().getString(R.string.joinAt), DateFormatter.formatDate(user.getCreateAt())));
            ImageLoader.getInstance().loadImageAvatar(ActivityHomestayDetail.this,
                    user.getAvatar(), imgAvatar);
        }
        //yêu thích hoặc không
        if (homestay.getFavorite() != null) {
            if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                if (homestay.getFavorite().containsKey(BaseActivity.getUid())) {
                    imgFavorite.setImageResource(R.drawable.ic_favorite_black_24dp);
                } else {
                    imgFavorite.setImageResource(R.drawable.ic_favorite_border_black_24dp);
                }
            } else {
                imgFavorite.setImageResource(R.drawable.ic_favorite_border_black_24dp);
            }
        } else {
            imgFavorite.setImageResource(R.drawable.ic_favorite_border_black_24dp);
        }
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
                Intent intent = new Intent(ActivityHomestayDetail.this, FullScreenGoogleMapActivity.class);
                intent.putExtra(Constants.LAT, Double.parseDouble(homestay.getAddress().get(Constants.LAT).toString()));
                intent.putExtra(Constants.LNG, Double.parseDouble(homestay.getAddress().get(Constants.LNG).toString()));
                intent.putExtra(Constants.HOMESTAY_NAME, homestay.getName());
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        if (v == btnComment) {
            Intent intent = new Intent(ActivityHomestayDetail.this, CommentActivity.class);
            intent.putExtra(Constants.HOMESTAY, homestay);
            startActivity(intent);
        } else if (v == imgAvatar) {
            if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                if (user.getUid().equals(getUid())) {
                    Intent intent = new Intent(ActivityHomestayDetail.this, ProfileUserActivity.class);
                    intent.putExtra(Constants.USERS, user);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(ActivityHomestayDetail.this, GuessProfileActivitiy.class);
                    intent.putExtra(Constants.USERS, user);
                    startActivity(intent);
                }
            }
        }
        else if (v == btnContact){
            Intent intent = new Intent(ActivityHomestayDetail.this, ChatActivity.class);
            intent.putExtra(Constants.USERS, user);
            startActivity(intent);
        }
    }
}
