package com.thanhduy.ophuot.homestay_detail.view;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
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
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;
import com.thanhduy.ophuot.R;
import com.thanhduy.ophuot.base.BaseActivity;
import com.thanhduy.ophuot.base.InternetConnection;
import com.thanhduy.ophuot.chat.view.ChatActivity;
import com.thanhduy.ophuot.comment.view.CommentActivity;
import com.thanhduy.ophuot.fragment.BottomDialogFragment;
import com.thanhduy.ophuot.fragment.PositionCallback;
import com.thanhduy.ophuot.homestay_detail.presenter.HomestayDetailPresenter;
import com.thanhduy.ophuot.list_homestay.GetUserInfoCallback;
import com.thanhduy.ophuot.manage_homestay.AdapterViewPager;
import com.thanhduy.ophuot.manage_homestay.view.FullScreenGoogleMapActivity;
import com.thanhduy.ophuot.model.Homestay;
import com.thanhduy.ophuot.model.PostInfo;
import com.thanhduy.ophuot.model.User;
import com.thanhduy.ophuot.profile.guess_profile.GuessProfileActivitiy;
import com.thanhduy.ophuot.profile.view.ProfileUserActivity;
import com.thanhduy.ophuot.utils.Constants;
import com.thanhduy.ophuot.utils.DateFormatter;
import com.thanhduy.ophuot.utils.OldRoundedBitmapDisplayer;
import com.thanhduy.ophuot.utils.ShowAlertDialog;
import com.thanhduy.ophuot.utils.ShowSnackbar;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.thanhduy.ophuot.R.id.activity;

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
    @BindView(R.id.txt_my_rating)
    TextView txtMyRating;


    private Homestay homestay;
    private AdapterViewPager mViewPagerAdapter;
    private GoogleMap googleMap;
    private DatabaseReference mDatabase;
    private User user;
    private boolean isLoadSuccess = false;
    private GetUserInfoCallback getUserInfoCallback;
    private int position = 0;
    private HomestayDetailPresenter presenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homestay_detail);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        presenter = new HomestayDetailPresenter(this);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        //get intent
        homestay = (Homestay) getIntent().getSerializableExtra(Constants.HOMESTAY);
        user = (User) getIntent().getSerializableExtra(Constants.USERS);
        position = getIntent().getIntExtra(Constants.POSITION, 0);
        initInfo();
        //event click
        btnComment.setOnClickListener(this);
        imgFavorite.setOnClickListener(this);
        imgAvatar.setOnClickListener(this);
        btnContact.setOnClickListener(this);
        txtMyRating.setOnClickListener(this);
        changeTextCommentIfYes();
    }

    private void initInfo() {
        if (InternetConnection.getInstance().isOnline(ActivityHomestayDetail.this)) {
            //set view pager for image slide
            if (homestay.getImages() != null) {
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
            }

            //show data
            loadData();
            setUpMapIfNeeded();
        } else {
            Snackbar snackbar = Snackbar.make(findViewById(activity), getResources().getString(R.string.noInternet), Snackbar.LENGTH_INDEFINITE)
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
//                            ImageLoader.getInstance().loadImageAvatar(ActivityHomestayDetail.this,
//                                    user.getAvatar(), imgAvatar);

                            final ImageViewAware imageViewAware = new ImageViewAware(imgAvatar);
                            Glide.with(ActivityHomestayDetail.this)
                                    .load(user.getAvatar())
                                    .asBitmap()
                                    .centerCrop()
                                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                                    .into(new SimpleTarget<Bitmap>() {
                                        @Override
                                        public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                                            OldRoundedBitmapDisplayer oldRoundedBitmapDisplayer = new OldRoundedBitmapDisplayer(20);
                                            oldRoundedBitmapDisplayer.display(resource, imageViewAware, null);
                                        }
                                    });

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
//            ImageLoader.getInstance().loadImageAvatar(ActivityHomestayDetail.this,
//                    user.getAvatar(), imgAvatar);

            final ImageViewAware imageViewAware = new ImageViewAware(imgAvatar);
            Glide.with(ActivityHomestayDetail.this)
                    .load(user.getAvatar())
                    .asBitmap()
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                            OldRoundedBitmapDisplayer oldRoundedBitmapDisplayer = new OldRoundedBitmapDisplayer(20);
                            oldRoundedBitmapDisplayer.display(resource, imageViewAware, null);
                        }
                    });
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

    public Bitmap getRoundedCornerBitmap(Bitmap bitmap, int pixels) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPx = pixels;

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
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
            Intent returnIntent = new Intent();
            returnIntent.putExtra(Constants.POSITION, position);
            returnIntent.putExtra(Constants.HOMESTAY, homestay);
            setResult(RESULT_OK, returnIntent);
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
        } else if (v == btnContact) {
            if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                Intent intent = new Intent(ActivityHomestayDetail.this, ChatActivity.class);
                intent.putExtra(Constants.USERS, user);
                startActivity(intent);
            } else {
                ShowAlertDialog.showAlert(getResources().getString(R.string.loginFirst), this);
            }
        } else if (v == imgFavorite) {
            if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                PostInfo postInfo = new PostInfo(homestay.getDistrictId(), homestay.getProvinceId(), homestay.getId());
                if (homestay.getFavorite() != null) {
                    if (homestay.getFavorite().containsKey(BaseActivity.getUid())) {
                        deleteFavoriteHomstay(postInfo, BaseActivity.getUid());
                        homestay.getFavorite().remove(BaseActivity.getUid());
                        Log.d("SIZE", "" + homestay.getFavorite().size());
                        //set icon
                        imgFavorite.setImageResource(R.drawable.ic_favorite_border_black_24dp);
                        //show snackbar
                        ShowSnackbar.showSnack(ActivityHomestayDetail.this, String.format("%s %s %s", getResources().getString(R.string.deleteFavorite),
                                homestay.getName(), getResources().getString(R.string.outOfFavorite)));
                        Constants.IS_CHANGE_LIST_FAVORITE = true;
                    } else {
                        BottomDialogFragment bottomDialogFragment = new BottomDialogFragment();
                        bottomDialogFragment.setPositionCallback(new PositionCallback() {
                            @Override
                            public void positionCallBack(int position) {
                                homestay.getFavorite().put(BaseActivity.getUid(), true);
                                Log.d("SIZE", "" + homestay.getFavorite().size());
                                //set icon
                                imgFavorite.setImageResource(R.drawable.ic_favorite_black_24dp);
                                //show snackbar
                                ShowSnackbar.showSnack(ActivityHomestayDetail.this, String.format("%s %s %s", getResources().getString(R.string.addFavorite),
                                        homestay.getName(), getResources().getString(R.string.into)));
                                Constants.IS_CHANGE_LIST_FAVORITE = true;
                            }
                        });
//                        //change icon heart from FavoriteListAdapterBottom
//                        favoriteListAdapterForBottomDialog.setPositionCallback(new PositionCallback() {
//                            @Override
//                            public void positionCallBack(int position) {
//                                homestay.getFavorite().put(BaseActivity.getUid(), true);
//                            }
//                        });
                        Bundle dataMove = new Bundle();
                        dataMove.putSerializable(Constants.POST_INFO, postInfo);
                        // dataMove.putSerializable(Constants.POSITION, position);
                        bottomDialogFragment.setArguments(dataMove);
                        bottomDialogFragment.show(getSupportFragmentManager(), bottomDialogFragment.getTag());

                    }
                } else {
                    BottomDialogFragment bottomDialogFragment = new BottomDialogFragment();
                    bottomDialogFragment.setPositionCallback(new PositionCallback() {
                        @Override
                        public void positionCallBack(int position) {
                            Map<String, Boolean> data = new HashMap<>();
                            data.put(BaseActivity.getUid(), true);
                            homestay.setFavorite(data);
                            homestay.getFavorite().put(BaseActivity.getUid(), true);
                            //set icon
                            imgFavorite.setImageResource(R.drawable.ic_favorite_black_24dp);
                            //show snackbar
                            ShowSnackbar.showSnack(ActivityHomestayDetail.this, String.format("%s %s %s", getResources().getString(R.string.addFavorite),
                                    homestay.getName(), getResources().getString(R.string.into)));
                            Constants.IS_CHANGE_LIST_FAVORITE = true;
                        }
                    });
                    Bundle dataMove = new Bundle();
                    dataMove.putSerializable(Constants.POST_INFO, postInfo);
                    //dataMove.putSerializable(Constants.POSITION, position);
                    bottomDialogFragment.setArguments(dataMove);
                    bottomDialogFragment.show(getSupportFragmentManager(), bottomDialogFragment.getTag());
                }
            } else {
                ShowAlertDialog.showAlert(getResources().getString(R.string.loginFirst), ActivityHomestayDetail.this);
            }
        } else if (v == txtMyRating) {
            showDialogRating();
        }
    }

    private void showDialogRating() {
        final Dialog dialog = new Dialog(ActivityHomestayDetail.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_dialog_rating);
       /*
       Init components
        */
        final RatingBar ratingBar = (RatingBar) dialog.findViewById(R.id.rating_bar);
        final TextView txtRatingContent = (TextView) dialog.findViewById(R.id.txt_content_rating);
        Button btnCancel = (Button) dialog.findViewById(R.id.btn_cancel);
        Button btnSend = (Button) dialog.findViewById(R.id.btn_send);
        //load data
        if (homestay.getRatingBy() != null) {
            if (homestay.getRatingBy().get(getUid()) != null) {
                long rate = (long) homestay.getRatingBy().get(getUid());
                ratingBar.setRating((float) rate);
                switch ((int) rate) {
                    case 5: {
                        txtRatingContent.setText(getResources().getString(R.string.excellent));
                        break;
                    }
                    case 4: {
                        txtRatingContent.setText(getResources().getString(R.string.veryGood));
                        break;
                    }
                    case 3: {
                        txtRatingContent.setText(getResources().getString(R.string.good));
                        break;
                    }
                    case 2: {
                        txtRatingContent.setText(getResources().getString(R.string.notBad));
                        break;
                    }
                    case 1: {
                        txtRatingContent.setText(getResources().getString(R.string.veryBad));
                        break;
                    }
                }
                btnSend.setText(getResources().getString(R.string.edit));
            }
        }
        //event rating bar
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                switch ((int) rating) {
                    case 5: {
                        txtRatingContent.setText(getResources().getString(R.string.excellent));
                        break;
                    }
                    case 4: {
                        txtRatingContent.setText(getResources().getString(R.string.veryGood));
                        break;
                    }
                    case 3: {
                        txtRatingContent.setText(getResources().getString(R.string.good));
                        break;
                    }
                    case 2: {
                        txtRatingContent.setText(getResources().getString(R.string.notBad));
                        break;
                    }
                    case 1: {
                        txtRatingContent.setText(getResources().getString(R.string.veryBad));
                        break;
                    }
                    default: {
                        ratingBar.setRating(1);
                        txtRatingContent.setText("Bad");
                        break;
                    }
                }
            }
        });

        //format position dialog
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setGravity(Gravity.CENTER);
        WindowManager.LayoutParams layoutParams = dialog.getWindow().getAttributes();

        //layoutParams.x = convertDpToPixel((float) 130 / 1, ResortsActivity.this);
        dialog.getWindow().setAttributes(layoutParams);
        //event click
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                    presenter.addToRatingBy(getUid(), (int) ratingBar.getRating(), homestay);
                    //update homestay
                    if (homestay.getRatingBy() == null) {
                        Map<String, Object> data = new HashMap<>();
                        data.put(BaseActivity.getUid(), (long) ratingBar.getRating());
                        homestay.setRatingBy(data);
                        homestay.getRatingBy().put(BaseActivity.getUid(), (long) ratingBar.getRating());
                    } else {
                        homestay.getRatingBy().put(getUid(), (long) ratingBar.getRating());
                    }
                    //dismiss
                    dialog.dismiss();
                    Snackbar snackbar = Snackbar.make(findViewById(activity), getResources().getString(R.string.ratingSave), Snackbar.LENGTH_LONG);
                    snackbar.show();
                } else {
                    ShowAlertDialog.showAlert(getResources().getString(R.string.loginFirst), ActivityHomestayDetail.this);
                }

            }
        });
        dialog.show();
    }

    private void deleteFavoriteHomstay(final PostInfo postInfo, final String uid) {
        // mDatabase.child(Constants.USERS).child(uid).child(Constants.FAVORITE).child(postInfo.getHomestayId()).removeValue();
        mDatabase.child(Constants.HOMESTAY).child(String.valueOf(postInfo.getProvinceId())).child(String.valueOf(postInfo.getDistrictId()))
                .child(postInfo.getHomestayId()).child(Constants.FAVORITE).child(uid).removeValue();
        mDatabase.child(Constants.USERS).child(uid).child(Constants.FAVORITE).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    for (DataSnapshot snapshotListFavorite : dataSnapshot.getChildren()) {
                        for (DataSnapshot snapshotListHomestay : snapshotListFavorite.child(Constants.LIST_HOMESTAY).getChildren()) {
                            if (snapshotListHomestay.getKey().equals(postInfo.getHomestayId())) {
                                mDatabase.child(Constants.USERS).child(uid).child(Constants.FAVORITE).child(snapshotListFavorite.getKey())
                                        .child(Constants.LIST_HOMESTAY).child(snapshotListHomestay.getKey()).removeValue();
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent returnIntent = new Intent();
        returnIntent.putExtra(Constants.POSITION, position);
        returnIntent.putExtra(Constants.HOMESTAY, homestay);
        setResult(RESULT_OK, returnIntent);
        super.onBackPressed();
    }
}
