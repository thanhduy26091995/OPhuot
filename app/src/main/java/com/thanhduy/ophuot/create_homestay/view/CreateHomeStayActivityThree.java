package com.thanhduy.ophuot.create_homestay.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.gun0912.tedpicker.Config;
import com.gun0912.tedpicker.ImagePickerActivity;
import com.thanhduy.ophuot.R;
import com.thanhduy.ophuot.base.BaseActivity;
import com.thanhduy.ophuot.base.ImageLoader;
import com.thanhduy.ophuot.utils.Constants;
import com.thanhduy.ophuot.utils.ShowAlertDialog;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by buivu on 04/03/2017.
 */

public class CreateHomeStayActivityThree extends BaseActivity implements View.OnClickListener {

    private int provinceId, districtId;
    private String strAddress, strHomestayName, strDescription, strType, strPrice;
    private double lat, lng;
    private Map<String, Object> mapDataDetails = new HashMap<>();

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.photoContainer)
    LinearLayout photoContainer;
    @BindView(R.id.btn_next)
    Button btnNext;

    private static final int REQUEST_CODE_READ_EXTERNAL_STORAGE = 1001;
    private static final String[] PERMISSIONS_STORAGE = {
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.CAMERA,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private ArrayList<Uri> _eventImageUris = new ArrayList<>();
    public static Activity createHomestayActivity3;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_homestay_3);
        ButterKnife.bind(this);
        createHomestayActivity3 = this;
        //set toolbar
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(getResources().getString(R.string.chooseImage));
        //get data
        getDataFromIntent();
        //event click
        btnNext.setOnClickListener(this);
    }

    private void getDataFromIntent() {
        provinceId = getIntent().getIntExtra(Constants.ID_PROVINCE, 0);
        districtId = getIntent().getIntExtra(Constants.ID_DISTRICT, 0);
        strAddress = getIntent().getStringExtra(Constants.ADDRESS);
        strHomestayName = getIntent().getStringExtra(Constants.HOMESTAY_NAME);
        strDescription = getIntent().getStringExtra(Constants.HOMESTAY_DESCRIPTION);
        strType = getIntent().getStringExtra(Constants.HOMESTAY_TYPE);
        strPrice = getIntent().getStringExtra(Constants.HOMESTAY_PRICE);
        lat = getIntent().getDoubleExtra(Constants.LAT, 0);
        lng = getIntent().getDoubleExtra(Constants.LNG, 0);
        mapDataDetails = (Map<String, Object>) getIntent().getSerializableExtra(Constants.DETAILS);
    }


    private void moveDataToLastActivity() {
        Intent intent = new Intent(CreateHomeStayActivityThree.this, CreateHomeStayActivityFour.class);
        intent.putExtra(Constants.ID_PROVINCE, provinceId);
        intent.putExtra(Constants.ID_DISTRICT, districtId);
        intent.putExtra(Constants.ADDRESS, strAddress);
        intent.putExtra(Constants.LAT, lat);
        intent.putExtra(Constants.LNG, lng);
        intent.putExtra(Constants.HOMESTAY_NAME, strHomestayName);
        intent.putExtra(Constants.HOMESTAY_DESCRIPTION, strDescription);
        intent.putExtra(Constants.HOMESTAY_TYPE, strType);
        intent.putExtra(Constants.HOMESTAY_PRICE, strPrice);
        intent.putExtra(Constants.DETAILS, (Serializable) mapDataDetails);
        intent.putExtra(Constants.LIST_URI, _eventImageUris);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_image, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_add_image) {
            if (verifyStoragePermission()) {
                addPhoto();
            }
        } else if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean verifyStoragePermission() {
        int readPermission = ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE);
        if (readPermission != PackageManager.PERMISSION_GRANTED) {
            //request permission
            ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE, REQUEST_CODE_READ_EXTERNAL_STORAGE);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_CODE_READ_EXTERNAL_STORAGE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    addPhoto();
                }
                break;
        }
    }

    private void addPhoto() {
        Config config = new Config();
        config.setSelectionMin(1);
        config.setSelectionLimit(10);

        ImagePickerActivity.setConfig(config);

        Intent myIntent = new Intent(CreateHomeStayActivityThree.this, ImagePickerActivity.class);
        myIntent.putParcelableArrayListExtra(ImagePickerActivity.EXTRA_IMAGE_URIS, _eventImageUris);
        startActivityForResult(myIntent, Constants.INTENT_REQUEST_GET_IMAGES);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.INTENT_REQUEST_GET_IMAGES && resultCode == RESULT_OK) {
            ArrayList<Uri> image_uris = data.getParcelableArrayListExtra(ImagePickerActivity.EXTRA_IMAGE_URIS);
            _eventImageUris.clear();
            for (Uri uri : image_uris) {
                _eventImageUris.add(uri);
            }
            onPickImageSuccess();
        }
    }

    private void onPickImageSuccess() {
        int previewImageSize = getPixelValue(CreateHomeStayActivityThree.this, 150);
        photoContainer.removeAllViews();

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(previewImageSize, previewImageSize);
        params.setMargins(5, 0, 5, 0);

        for (Uri uri : _eventImageUris) {
            ImageView photo = new ImageView(this);
            photo.setScaleType(ImageView.ScaleType.CENTER_CROP);
            photo.setLayoutParams(params);
            //using Glide to load image
            ImageLoader.getInstance().loadImageOther(CreateHomeStayActivityThree.this, uri.toString(), photo);

            photoContainer.addView(photo);
        }
    }

    private int getPixelValue(Context context, int dimenId) {
        Resources resources = context.getResources();
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dimenId,
                resources.getDisplayMetrics()
        );
    }

    @Override
    public void onClick(View v) {
        if (v == btnNext) {
            if (isSuccess()) {
                moveDataToLastActivity();
            }
        }
    }

    private boolean isSuccess() {
        boolean isSuccess = true;
        if (_eventImageUris.size() < 1) {
            isSuccess = false;
            ShowAlertDialog.showAlert(getResources().getString(R.string.chooseAtLeast1Image), CreateHomeStayActivityThree.this);
        }
        return isSuccess;
    }
}
