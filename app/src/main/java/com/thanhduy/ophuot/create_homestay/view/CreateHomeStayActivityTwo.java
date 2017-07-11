package com.thanhduy.ophuot.create_homestay.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.codetroopers.betterpickers.radialtimepicker.RadialTimePickerDialogFragment;
import com.thanhduy.ophuot.R;
import com.thanhduy.ophuot.base.BaseActivity;
import com.thanhduy.ophuot.utils.Constants;
import com.thanhduy.ophuot.utils.ShowAlertDialog;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by buivu on 02/03/2017.
 */

public class CreateHomeStayActivityTwo extends BaseActivity implements View.OnClickListener,
        RadialTimePickerDialogFragment.OnTimeSetListener {


    private static final String FRAG_TAG_TIME_PICKER_OPEN = "timePickerDialogFragmentOpen";
    private static final String FRAG_TAG_TIME_PICKER_CLOSE = "timePickerDialogFragmentClose";
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.btn_next)
    Button btnNext;
    @BindView(R.id.linear_time_open)
    LinearLayout linearTimeOpen;
    @BindView(R.id.linear_time_close)
    LinearLayout linearTimeClose;
    @BindView(R.id.txt_create_2_time_open)
    TextView txtTimeOpen;
    @BindView(R.id.txt_create_2_time_close)
    TextView txtTimeClose;
    @BindView(R.id.edit_create_2_max_passenger)
    EditText edtMaxPassenger;
    @BindView(R.id.edit_create_2_number_bedroom)
    EditText edtNumberBedroom;
    @BindView(R.id.edit_create_2_number_bed)
    EditText edtNumberBed;
    @BindView(R.id.edit_create_2_number_bathroom)
    EditText edtNumberBathroom;
    @BindView(R.id.chk_air_condition)
    CheckBox chkAir;
    @BindView(R.id.chk_allow_smoke)
    CheckBox chkAllowSmoke;
    @BindView(R.id.chk_tv)
    CheckBox chkTivi;
    @BindView(R.id.chk_wifi)
    CheckBox chkWifi;
    @BindView(R.id.chk_heater)
    CheckBox chkHeater;
    @BindView(R.id.chk_water_heater)
    CheckBox chkWaterHeater;
    @BindView(R.id.chk_animal)
    CheckBox chkAnimal;
    @BindView(R.id.txt_create_2_air)
    TextView txtAir;
    @BindView(R.id.txt_create_2_smoke)
    TextView txtSmoke;
    @BindView(R.id.txt_create_2_tivi)
    TextView txtTivi;
    @BindView(R.id.txt_create_2_heater)
    TextView txtHeater;
    @BindView(R.id.txt_create_2_water_heater)
    TextView txtWaterHeater;
    @BindView(R.id.txt_create_2_wifi)
    TextView txtWifi;

    private int provinceId, districtId;
    private String strAddress, strHomestayName, strDescription, strType, strPrice;
    private double lat, lng;
    private long timeOpenInMinute = 0, timeCloseInMinute = 0;
    public static Activity createHomestayActivity2;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_homestay_2);
        ButterKnife.bind(this);
        createHomestayActivity2 = this;

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getResources().getString(R.string.baseInformation));
        //event click
        btnNext.setOnClickListener(this);
        linearTimeOpen.setOnClickListener(this);
        linearTimeClose.setOnClickListener(this);
        //get data from previous activity
        provinceId = getIntent().getIntExtra(Constants.ID_PROVINCE, 0);
        districtId = getIntent().getIntExtra(Constants.ID_DISTRICT, 0);
        strAddress = getIntent().getStringExtra(Constants.ADDRESS);
        strHomestayName = getIntent().getStringExtra(Constants.HOMESTAY_NAME);
        strDescription = getIntent().getStringExtra(Constants.HOMESTAY_DESCRIPTION);
        strType = getIntent().getStringExtra(Constants.HOMESTAY_TYPE);
        strPrice = getIntent().getStringExtra(Constants.HOMESTAY_PRICE);
        lat = getIntent().getDoubleExtra(Constants.LAT, 0);
        lng = getIntent().getDoubleExtra(Constants.LNG, 0);
    }

    @Override
    public void onClick(View v) {
        if (v == btnNext) {
            if (checkData()) {
                moveToCreateHomestay3();
            }
        } else if (v == linearTimeOpen) {
            RadialTimePickerDialogFragment rtpd = new RadialTimePickerDialogFragment()
                    .setOnTimeSetListener(CreateHomeStayActivityTwo.this)
                    .setStartTime(00, 00)
                    .setDoneText(getResources().getString(R.string.ok))
                    .setCancelText(getResources().getString(R.string.cancel));
            rtpd.show(getSupportFragmentManager(), FRAG_TAG_TIME_PICKER_OPEN);
        } else if (v == linearTimeClose) {
            RadialTimePickerDialogFragment rtpd = new RadialTimePickerDialogFragment()
                    .setOnTimeSetListener(CreateHomeStayActivityTwo.this)
                    .setStartTime(12, 00)
                    .setDoneText(getResources().getString(R.string.ok))
                    .setCancelText(getResources().getString(R.string.cancel));
            rtpd.show(getSupportFragmentManager(), FRAG_TAG_TIME_PICKER_CLOSE);
        }
    }

    private boolean checkData() {
        boolean isSuccess = true;
        if (edtMaxPassenger.getText().length() == 0 || edtNumberBedroom.getText().length() == 0 || edtMaxPassenger.getText().length() == 0 ||
                edtNumberBathroom.getText().length() == 0 || txtTimeOpen.getText().length() == 0 || txtTimeClose.getText().length() == 0) {
            isSuccess = false;
            ShowAlertDialog.showAlert(getResources().getString(R.string.fillAllData), this);
        } else {
            if (timeOpenInMinute >= timeCloseInMinute) {
                isSuccess = false;
                ShowAlertDialog.showAlert(getResources().getString(R.string.wrongTime), this);
            }
        }

        return isSuccess;
    }

    private void moveToCreateHomestay3() {
        //get value
        int maxPassenger = Integer.parseInt(edtMaxPassenger.getText().toString());
        int numberBedroom = Integer.parseInt(edtNumberBedroom.getText().toString());
        int numberBed = Integer.parseInt(edtNumberBed.getText().toString());
        int numberBathroom = Integer.parseInt(edtNumberBathroom.getText().toString());
        String strTimeOpen = txtTimeOpen.getText().toString();
        String strTimeClose = txtTimeClose.getText().toString();
        boolean isAllowAnimal = false;
        if (chkAnimal.isChecked()) {
            isAllowAnimal = true;
        }
        String strConvenient;
        StringBuilder builderConvenient = new StringBuilder();

        if (chkAir.isChecked()) {
            builderConvenient.append(String.format("%s, ", txtAir.getText()));
        }
        if (chkAllowSmoke.isChecked()) {
            builderConvenient.append(String.format("%s, ", txtSmoke.getText()));
        }
        if (chkTivi.isChecked()) {
            builderConvenient.append(String.format("%s, ", txtTivi.getText()));
        }
        if (chkWifi.isChecked()) {
            builderConvenient.append(String.format("%s, ", txtWifi.getText()));
        }
        if (chkHeater.isChecked()) {
            builderConvenient.append(String.format("%s, ", txtHeater.getText()));
        }
        if (chkWaterHeater.isChecked()) {
            builderConvenient.append(String.format("%s, ", txtWaterHeater.getText()));
        }
        strConvenient = builderConvenient.toString();
        strConvenient = strConvenient.substring(0, strConvenient.length() - 2);
        //save data detail to Hashmap
        Map<String, Object> mapDetail = new HashMap<>();
        mapDetail.put(Constants.BATH_ROOM, numberBathroom);
        mapDetail.put(Constants.BED, numberBed);
        mapDetail.put(Constants.BED_ROOM, numberBedroom);
        mapDetail.put(Constants.CONVENIENT, strConvenient);
        mapDetail.put(Constants.MAX, maxPassenger);
        mapDetail.put(Constants.PET, isAllowAnimal);
        mapDetail.put(Constants.TIME_CLOSE, strTimeClose);
        mapDetail.put(Constants.TIME_OPEN, strTimeOpen);
        //end save
        Intent intent = new Intent(CreateHomeStayActivityTwo.this, CreateHomeStayActivityThree.class);
        intent.putExtra(Constants.ID_PROVINCE, provinceId);
        intent.putExtra(Constants.ID_DISTRICT, districtId);
        intent.putExtra(Constants.ADDRESS, strAddress);
        intent.putExtra(Constants.LAT, lat);
        intent.putExtra(Constants.LNG, lng);
        intent.putExtra(Constants.HOMESTAY_NAME, strHomestayName);
        intent.putExtra(Constants.HOMESTAY_DESCRIPTION, strDescription);
        intent.putExtra(Constants.HOMESTAY_TYPE, strType);
        intent.putExtra(Constants.HOMESTAY_PRICE, strPrice);
        intent.putExtra(Constants.DETAILS, (Serializable) mapDetail);
        startActivity(intent);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTimeSet(RadialTimePickerDialogFragment dialog, int hourOfDay, int minute) {
        if (dialog.getTag().equals(FRAG_TAG_TIME_PICKER_OPEN)) {
            txtTimeOpen.setText(String.format("%d : %d", hourOfDay, minute));
            timeOpenInMinute = hourOfDay * 60 + minute;
        } else {
            txtTimeClose.setText(String.format("%d : %d", hourOfDay, minute));
            timeCloseInMinute = hourOfDay * 60 + minute;
        }
    }

    @Override
    public void onResume() {
        // Example of reattaching to the fragment
        super.onResume();
        RadialTimePickerDialogFragment rtpdOpen = (RadialTimePickerDialogFragment) getSupportFragmentManager().findFragmentByTag(FRAG_TAG_TIME_PICKER_OPEN);
        RadialTimePickerDialogFragment rtpdClose = (RadialTimePickerDialogFragment) getSupportFragmentManager().findFragmentByTag(FRAG_TAG_TIME_PICKER_CLOSE);
        if (rtpdOpen != null) {
            rtpdOpen.setOnTimeSetListener(this);
        }
        if (rtpdClose != null) {
            rtpdClose.setOnTimeSetListener(this);
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent(event);
    }
}
