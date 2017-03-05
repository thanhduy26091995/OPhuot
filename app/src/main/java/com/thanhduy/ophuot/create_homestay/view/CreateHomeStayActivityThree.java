package com.thanhduy.ophuot.create_homestay.view;

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
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.gun0912.tedpicker.Config;
import com.gun0912.tedpicker.ImagePickerActivity;
import com.thanhduy.ophuot.R;
import com.thanhduy.ophuot.base.BaseActivity;
import com.thanhduy.ophuot.base.ImageLoader;
import com.thanhduy.ophuot.utils.Constants;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by buivu on 04/03/2017.
 */

public class CreateHomeStayActivityThree extends BaseActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.photoContainer)
    LinearLayout photoContainer;

    private static final int REQUEST_CODE_READ_EXTERNAL_STORAGE = 1001;
    private static final String[] PERMISSIONS_STORAGE = {
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.CAMERA,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private ArrayList<Uri> _eventImageUris = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_homestay_3);
        ButterKnife.bind(this);
        //set toolbar
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(getResources().getString(R.string.chooseImage));
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
}
