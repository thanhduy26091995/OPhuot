package com.thanhduy.ophuot.create_homestay.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.thanhduy.ophuot.R;
import com.thanhduy.ophuot.base.BaseActivity;
import com.thanhduy.ophuot.database.DatabaseAdapter;
import com.thanhduy.ophuot.database.model.District;
import com.thanhduy.ophuot.database.model.Province;
import com.thanhduy.ophuot.utils.Constants;
import com.thanhduy.ophuot.utils.ShowAlertDialog;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by buivu on 02/03/2017.
 */

public class CreateHomeStayActivityOne extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.btn_next)
    Button btnNext;
    @BindView(R.id.spnProvince)
    Spinner spnProvince;
    @BindView(R.id.spnDistrict)
    Spinner spnDistrict;
    @BindView(R.id.spn_type)
    Spinner spnType;
    @BindView(R.id.linear_address)
    LinearLayout linearAddress;
    @BindView(R.id.txt_create_1_address)
    TextView txtAddress;
    @BindView(R.id.edt_create_1_name)
    EditText edtName;
    @BindView(R.id.edt_create_1_des)
    EditText edtDescription;
    @BindView(R.id.edt_create_1_price)
    EditText edtPrice;

    private DatabaseAdapter databaseAdapter;
    private List<String> listProvinceName, listDistrictName;
    private ArrayAdapter<String> spinnerProvinceAdapter, spinnerDistrictAdapter, spinnerTypeAdapter;
    private List<Province> listProvince;
    private List<District> listDistrict;
    private Double lat, lng;
    private int provinceId, districtId;
    private String strAddress, strHomestayName, strHomestayDes, strHomestayType, strHomestayPrice;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_homestay_1);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getResources().getString(R.string.createHomestay));
        //event click
        btnNext.setOnClickListener(this);
        linearAddress.setOnClickListener(this);
        //init
        databaseAdapter = new DatabaseAdapter(this);
        listProvinceName = new ArrayList<>();
        listProvince = new ArrayList<>();
        listDistrict = new ArrayList<>();
        listDistrictName = new ArrayList<>();
        //fetch data
        listProvince = databaseAdapter.getAllProvince();
        fetchDataProvinceName(listProvince);
        handleSpinner();
    }

    private void handleSpinner() {
        /*
        SPinner province and district
         */
        spinnerProvinceAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, listProvinceName);
        spinnerProvinceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //set adapter
        spnProvince.setAdapter(spinnerProvinceAdapter);
        //event click for spinner country
        spnProvince.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String item = parent.getItemAtPosition(position).toString();
                provinceId = databaseAdapter.getProvinceIdByProvinceName(item);
                //clear list
                listDistrictName.clear();
                //call sqlite
                listDistrict = databaseAdapter.getDistrictsByProvinceId(provinceId);
                //fill data to list
                for (District district : listDistrict) {
                    listDistrictName.add(district.getDistrictName());
                }
                spinnerDistrictAdapter = new ArrayAdapter<String>(CreateHomeStayActivityOne.this, android.R.layout.simple_spinner_dropdown_item, listDistrictName);
                spinnerDistrictAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                //set adapter
                spnDistrict.setAdapter(spinnerDistrictAdapter);
                //event click
                spnDistrict.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        String item = parent.getItemAtPosition(position).toString();
                        districtId = databaseAdapter.getDistrictIdByDistrictName(item);
                        Log.d("DATA", "" + provinceId + "/" + districtId);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        /*
        spinner type
         */
        spinnerTypeAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item,
                getResources().getStringArray(R.array.arrType));
        spinnerTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //set adapter
        spnType.setAdapter(spinnerTypeAdapter);
        //set event click
        spnType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String itemType = parent.getItemAtPosition(position).toString();
                strHomestayType = itemType;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void fetchDataProvinceName(List<Province> listProvince) {
        for (Province province : listProvince) {
            listProvinceName.add(province.getProvinceName());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
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
        if (v == btnNext) {
            moveToActivityTwo();
        } else if (v == linearAddress) {
            showGooglePlaces();
        }
    }

    private void moveToActivityTwo() {
        strHomestayName = edtName.getText().toString();
        strHomestayDes = edtDescription.getText().toString();
        strHomestayPrice = edtPrice.getText().toString();
        if (strHomestayPrice.length() == 0 || strHomestayDes.length() == 0 || strHomestayName.length() == 0) {
            ShowAlertDialog.showAlert(getResources().getString(R.string.fillAllData), this);
        } else {
            Intent intent = new Intent(CreateHomeStayActivityOne.this, CreateHomeStayActivityTwo.class);
            intent.putExtra(Constants.ID_PROVINCE, provinceId);
            intent.putExtra(Constants.ID_DISTRICT, districtId);
            intent.putExtra(Constants.ADDRESS, strAddress);
            intent.putExtra(Constants.LAT, lat);
            intent.putExtra(Constants.LNG, lng);
            intent.putExtra(Constants.HOMESTAY_NAME, strHomestayName);
            intent.putExtra(Constants.HOMESTAY_DESCRIPTION, strHomestayDes);
            intent.putExtra(Constants.HOMESTAY_TYPE, strHomestayType);
            intent.putExtra(Constants.HOMESTAY_PRICE, strHomestayPrice);
            startActivity(intent);
        }
    }

    private void showGooglePlaces() {

        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
        Intent myIntent;
        try {
            myIntent = builder.build(CreateHomeStayActivityOne.this);
            startActivityForResult(myIntent, Constants.PLACE_PICKER_REQUEST);
        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Constants.PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(data, this);
                strAddress = String.format("%s", place.getAddress());
                txtAddress.setText(strAddress);
                lat = place.getLatLng().latitude;
                lng = place.getLatLng().longitude;
            }

        }

    }
}
