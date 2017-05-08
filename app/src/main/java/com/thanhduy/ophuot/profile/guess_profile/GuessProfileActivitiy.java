package com.thanhduy.ophuot.profile.guess_profile;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.thanhduy.ophuot.R;
import com.thanhduy.ophuot.base.BaseActivity;
import com.thanhduy.ophuot.base.ImageLoader;
import com.thanhduy.ophuot.model.User;
import com.thanhduy.ophuot.report.view.ReportActivity;
import com.thanhduy.ophuot.utils.Constants;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by buivu on 30/03/2017.
 */

public class GuessProfileActivitiy extends BaseActivity {

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

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    private User user;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guess_user);
        ButterKnife.bind(this);
        user = (User) getIntent().getSerializableExtra(Constants.USERS);
        setSupportActionBar(toolbar);
        //add back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(getResources().getString(R.string.profile));
        initInfo();
    }

    private void initInfo() {
        if (user != null) {
            txtName.setText(user.getName());
            txtAddress.setText(user.getAddress().get(Constants.ADDRESS).toString());
            txtDescription.setText(user.getDescription());
            txtPhone.setText(user.getPhone());
            if (user.getGender() == 1) {
                txtGender.setText(getResources().getString(R.string.male));
            } else {
                txtGender.setText(getResources().getString(R.string.female));
            }
            //checkGender(user.getGender());
            ImageLoader.getInstance().loadImageAvatar(GuessProfileActivitiy.this, user.getAvatar(), imgAvatar);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_report, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        } else if (item.getItemId() == R.id.action_report) {
            Intent intent = new Intent(GuessProfileActivitiy.this, ReportActivity.class);
            intent.putExtra(Constants.USERS, user);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
}
