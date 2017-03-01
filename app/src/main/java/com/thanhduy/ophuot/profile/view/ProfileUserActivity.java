package com.thanhduy.ophuot.profile.view;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.UploadTask;
import com.thanhduy.ophuot.R;
import com.thanhduy.ophuot.base.BaseActivity;
import com.thanhduy.ophuot.base.ImageLoader;
import com.thanhduy.ophuot.model.User;
import com.thanhduy.ophuot.profile.edit_profile.view.EditProfileActivity;
import com.thanhduy.ophuot.profile.presenter.ProfileUserPresenter;
import com.thanhduy.ophuot.utils.Constants;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by buivu on 28/02/2017.
 */

public class ProfileUserActivity extends BaseActivity implements View.OnClickListener {
    private DatabaseReference mDatabase;


    @BindView(R.id.img_profile_avatar)
    ImageView imgAvatar;
    @BindView(R.id.txt_profile_name)
    TextView txtName;
    @BindView(R.id.txt_profile_address)
    TextView txtAddress;
    @BindView(R.id.txt_profile_gender)
    TextView txtGender;
    @BindView(R.id.txt_profile_phone)
    TextView txtPhone;
    @BindView(R.id.txt_profile_description)
    TextView txtDescription;
    @BindView(R.id.txt_profile_camera)
    TextView txtCamera;
    @BindView(R.id.txt_profile_edit)
    TextView txtEditProfile;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    private static final int REQUEST_CODE_READ_STORAGE = 1001;
    private static final String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE
    };
    private static final int REQUEST_CODE_CAMERA = 1002;
    private static final String[] PERMISSIONS_CAMERA = {
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private ProfileUserPresenter presenter;
    private String name, address, phone, description;
    private int gender;
    private double lat, lng;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        presenter = new ProfileUserPresenter(this);
        //add back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(getResources().getString(R.string.profile));
        //init
        mDatabase = FirebaseDatabase.getInstance().getReference();
        initInfo();
        setEventClick();
    }

    private void setEventClick() {
        txtCamera.setOnClickListener(this);
        txtEditProfile.setOnClickListener(this);
    }

    private void initInfo() {
        showProgessDialog();
        mDatabase.child(Constants.USERS).child(BaseActivity.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    hideProgressDialog();
                    User user = dataSnapshot.getValue(User.class);
                    if (user != null) {
                        txtName.setText(user.getName());
                        txtAddress.setText(user.getAddress().get(Constants.ADDRESS).toString());
                        txtDescription.setText(user.getDescription());
                        txtPhone.setText(user.getPhone());
                        if (user.getGender() == 1) {
                            txtGender.setText("Nam");
                        } else {
                            txtGender.setText("Nữ");
                        }
                        //checkGender(user.getGender());
                        ImageLoader.getInstance().loadImageAvatar(ProfileUserActivity.this, user.getAvatar(), imgAvatar);
                        //save to instance
                        name = user.getName();
                        address = user.getAddress().get(Constants.ADDRESS).toString();
                        lat = Double.parseDouble(user.getAddress().get(Constants.LAT).toString());
                        lng = Double.parseDouble(user.getAddress().get(Constants.LNG).toString());
                        gender = user.getGender();
                        phone = user.getPhone();
                        description = user.getDescription();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("ERROR", databaseError.getMessage());
                hideProgressDialog();
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
        if (v == txtCamera) {
            showAlertForCamera();
        } else if (v == txtEditProfile) {
            Intent intent = new Intent(ProfileUserActivity.this, EditProfileActivity.class);
            intent.putExtra(Constants.NAME, name);
            intent.putExtra(Constants.ADDRESS, address);
            intent.putExtra(Constants.LAT, lat);
            intent.putExtra(Constants.LNG, lng);
            intent.putExtra(Constants.PHONE, phone);
            intent.putExtra(Constants.GENDER, gender);
            intent.putExtra(Constants.DESCRIPTION, description);
            startActivity(intent);
        }
    }

    private void showAlertForCamera() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater layoutInflater = getLayoutInflater();
        View v = layoutInflater.inflate(R.layout.custom_dialog_up_image, null);
        builder.setView(v);
        //components in custom view
        TextView txtGallery = (TextView) v.findViewById(R.id.txt_open_gallery);
        TextView txtCamera = (TextView) v.findViewById(R.id.txt_open_camera);
        //show dialog
        final AlertDialog alertDialog = builder.show();
        //event click
        txtGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (verifyStoragePermissions()) {
                    showGallery();
                }
                alertDialog.dismiss();
            }
        });
        txtCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (verifyOpenCamera()) {
                    openCamera();
                }
                alertDialog.dismiss();
            }
        });

    }

    //open gallery to choosing image
    private void showGallery() {
        Intent intent = new Intent(
                Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, Constants.GALLERY_INTENT);
    }

    //open gallery to taking a picture
    private void openCamera() {
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, Constants.CAMERA_INTENT);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_CODE_READ_STORAGE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    showGallery();
                }
                break;
            case REQUEST_CODE_CAMERA:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openCamera();
                }
                break;
        }
    }

    //confirm request persmission
    private boolean verifyOpenCamera() {
        int camera = ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        if (camera != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    PERMISSIONS_CAMERA, REQUEST_CODE_CAMERA
            );

            return false;
        }
        return true;
    }

    //confirm request persmission
    private boolean verifyStoragePermissions() {
        // Check if we have read or write permission
        int readPermission = ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        if (readPermission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    PERMISSIONS_STORAGE, REQUEST_CODE_READ_STORAGE
            );

            return false;
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.GALLERY_INTENT && resultCode == RESULT_OK) {
            //load image into imageview
            ImageLoader.getInstance().loadImageAvatar(ProfileUserActivity.this, data.getData().toString(), imgAvatar);
            Constants.USER_FILE_PATH = getRealPathFromURI(data.getData());
            presenter.addImageUser(getUid(), new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    presenter.editUserPhotoURL(getUid(), taskSnapshot.getDownloadUrl().toString());
                }
            });
        } else if (requestCode == Constants.CAMERA_INTENT && resultCode == RESULT_OK) {
            //load image into imageview
            ImageLoader.getInstance().loadImageAvatar(ProfileUserActivity.this, data.getData().toString(), imgAvatar);
            Constants.USER_FILE_PATH = getRealPathFromURI(data.getData());
            presenter.addImageUser(getUid(), new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    presenter.editUserPhotoURL(getUid(), taskSnapshot.getDownloadUrl().toString());
                }
            });
        }
    }

    private String getRealPathFromURI(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
    }
}
