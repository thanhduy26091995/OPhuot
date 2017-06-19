package com.thanhduy.ophuot.profile.edit_profile.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.thanhduy.ophuot.R;
import com.thanhduy.ophuot.base.BaseActivity;
import com.thanhduy.ophuot.base.InternetConnection;
import com.thanhduy.ophuot.profile.edit_profile.presenter.EditProfilePresenter;
import com.thanhduy.ophuot.utils.Constants;
import com.thanhduy.ophuot.utils.ShowAlertDialog;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by buivu on 01/03/2017.
 */

public class EditProfileActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.edt_edit_profile_name)
    EditText edtName;
    @BindView(R.id.edt_edit_profile_phone)
    EditText edtPhone;
    @BindView(R.id.edt_edit_profile_description)
    EditText edtDescription;
    @BindView(R.id.txt_edit_profile_address)
    TextView txtAddress;
    @BindView(R.id.txt_edit_profile_male)
    TextView txtMale;
    @BindView(R.id.txt_edit_profile_female)
    TextView txtFemale;
    @BindView(R.id.linear_address)
    LinearLayout linearAddress;

    private String name, address, phone, description;
    private int gender;
    private double lat, lng;
    private EditProfilePresenter presenter;
    private MenuItem menuConfirm;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        ButterKnife.bind(this);
        presenter = new EditProfilePresenter(this);
        //show button back, button confirm
        setSupportActionBar(toolbar);
        //show back button
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getResources().getString(R.string.editProfile));
        //get intent
        name = getIntent().getStringExtra(Constants.NAME);
        address = getIntent().getStringExtra(Constants.ADDRESS);
        phone = getIntent().getStringExtra(Constants.PHONE);
        description = getIntent().getStringExtra(Constants.DESCRIPTION);
        gender = getIntent().getIntExtra(Constants.GENDER, 1);
        lat = getIntent().getDoubleExtra(Constants.LAT, 0);
        lng = getIntent().getDoubleExtra(Constants.LNG, 0);
        initData();
        //event click
        txtMale.setOnClickListener(this);
        txtFemale.setOnClickListener(this);
        linearAddress.setOnClickListener(this);

    }

    private void initData() {
        if (InternetConnection.getInstance().isOnline(EditProfileActivity.this)) {
            if (menuConfirm != null) {
                menuConfirm.setVisible(true);
            }
            //show data
            edtName.setText(name);
            edtPhone.setText(phone);
            edtDescription.setText(description);
            txtAddress.setText(address);
            checkGender(gender);
        } else {
            //hide menu
            if (menuConfirm != null) {
                menuConfirm.setVisible(false);
            }
            Snackbar snackbar = Snackbar.make(findViewById(R.id.activity), getResources().getString(R.string.noInternet), Snackbar.LENGTH_INDEFINITE)
                    .setAction(getResources().getString(R.string.retry), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            initData();
                        }
                    });
            snackbar.show();
        }

    }

    private void checkGender(int gender) {
        if (gender == 1) {
            txtMale.setBackground(ContextCompat.getDrawable(EditProfileActivity.this, R.drawable.border_solid_textview_layout));
            txtMale.setTextColor(ContextCompat.getColor(EditProfileActivity.this, R.color.colorWhite));
            txtFemale.setBackground(ContextCompat.getDrawable(EditProfileActivity.this, R.drawable.border_stroke_textview_layout));
            txtFemale.setTextColor(ContextCompat.getColor(EditProfileActivity.this, android.R.color.tab_indicator_text));
        } else {
            txtFemale.setBackground(ContextCompat.getDrawable(EditProfileActivity.this, R.drawable.border_solid_textview_layout));
            txtFemale.setTextColor(ContextCompat.getColor(EditProfileActivity.this, R.color.colorWhite));
            txtMale.setBackground(ContextCompat.getDrawable(EditProfileActivity.this, R.drawable.border_stroke_textview_layout));
            txtMale.setTextColor(ContextCompat.getColor(EditProfileActivity.this, android.R.color.tab_indicator_text));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_confirm_edit_profile, menu);
        menuConfirm = menu.findItem(R.id.action_confirm);
        if (!InternetConnection.getInstance().isOnline(EditProfileActivity.this)) {
            menuConfirm.setVisible(false);
        } else {
            menuConfirm.setVisible(true);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            finish();
        } else if (item.getItemId() == R.id.action_confirm) {
            updateDataUser();

        }
        return super.onOptionsItemSelected(item);
    }

    private void updateDataUser() {
        String name = edtName.getText().toString().trim();
        String phone = edtPhone.getText().toString().trim();
        String description = edtDescription.getText().toString().trim();
        Map<String, Object> address = new HashMap<>();
        address.put(Constants.ADDRESS, txtAddress.getText().toString());
        address.put(Constants.LAT, lat);
        address.put(Constants.LNG, lng);
        if (TextUtils.isEmpty(name)) {
            ShowAlertDialog.showAlert(getResources().getString(R.string.nameRequired), EditProfileActivity.this);
        } else {
            //update data
            presenter.updateUserInfo(getUid(), address, description, name, phone, gender);
            finish();
        }

    }

    private void showGooglePlaces() {

        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
        Intent myIntent;
        try {
            myIntent = builder.build(EditProfileActivity.this);
            startActivityForResult(myIntent, Constants.PLACE_PICKER_REQUEST);
        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onClick(View v) {
        if (v == txtMale) {
            checkGender(1);
            gender = 1;
        } else if (v == txtFemale) {
            checkGender(0);
            gender = 0;
        } else if (v == linearAddress) {
            //block textview
            linearAddress.setEnabled(false);
            showGooglePlaces();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Constants.PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(data, this);
                String address = String.format("%s", place.getAddress());
                txtAddress.setText(address);
                lat = place.getLatLng().latitude;
                lng = place.getLatLng().longitude;
            }
            //un-block textview
            linearAddress.setEnabled(true);
        }

    }
}