package com.thanhduy.ophuot.profile.view;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.thanhduy.ophuot.R;
import com.thanhduy.ophuot.base.BaseActivity;
import com.thanhduy.ophuot.base.ImageLoader;
import com.thanhduy.ophuot.model.User;
import com.thanhduy.ophuot.utils.Constants;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by buivu on 28/02/2017.
 */

public class ProfileUserActivity extends BaseActivity implements View.OnClickListener{
    private DatabaseReference mDatabase;


    @BindView(R.id.img_profile_avatar)
    ImageView imgAvatar;
    @BindView(R.id.txt_profile_name)
    TextView txtName;
    @BindView(R.id.txt_profile_address)
    TextView txtAddress;
    @BindView(R.id.txt_profile_male)
    TextView txtMale;
    @BindView(R.id.txt_profile_female)
    TextView txtFemale;
    @BindView(R.id.txt_profile_description)
    TextView txtDescription;
    @BindView(R.id.txt_profile_camera)
    TextView txtCamera;
    @BindView(R.id.txt_profile_edit)
    TextView txtEditProfile;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        //add back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(getResources().getString(R.string.profile));
        //init
        mDatabase = FirebaseDatabase.getInstance().getReference();
        initInfo();
        setEventClick();
    }

    private void setEventClick(){
        txtCamera.setOnClickListener(this);
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
                        checkGender(user.getGender());
                        ImageLoader.getInstance().loadImageAvatar(ProfileUserActivity.this, user.getAvatar(), imgAvatar);
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

    private void checkGender(int gender) {
        if (gender == 1) {
            txtMale.setBackground(ContextCompat.getDrawable(ProfileUserActivity.this, R.drawable.border_solid_textview_layout));
            txtMale.setTextColor(ContextCompat.getColor(ProfileUserActivity.this, R.color.colorWhite));
            txtFemale.setBackground(ContextCompat.getDrawable(ProfileUserActivity.this, R.drawable.border_stroke_textview_layout));
            txtFemale.setTextColor(ContextCompat.getColor(ProfileUserActivity.this, android.R.color.tab_indicator_text));
        } else {
            txtFemale.setBackground(ContextCompat.getDrawable(ProfileUserActivity.this, R.drawable.border_solid_textview_layout));
            txtFemale.setTextColor(ContextCompat.getColor(ProfileUserActivity.this, R.color.colorWhite));
            txtMale.setBackground(ContextCompat.getDrawable(ProfileUserActivity.this, R.drawable.border_stroke_textview_layout));
            txtMale.setTextColor(ContextCompat.getColor(ProfileUserActivity.this, android.R.color.tab_indicator_text));
        }
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
        if (v == txtCamera){
            showAlertForCamera();
        }
    }
    private void showAlertForCamera(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater layoutInflater = getLayoutInflater();
        View v = layoutInflater.inflate(R.layout.custom_dialog_up_image, null);
        builder.setView(v);
        //show dialog
        builder.create().show();
    }
}
