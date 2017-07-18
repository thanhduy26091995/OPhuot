package com.thanhduy.ophuot.create_homestay.view;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.thanhduy.ophuot.R;
import com.thanhduy.ophuot.base.BaseActivity;
import com.thanhduy.ophuot.base.ImageLoader;
import com.thanhduy.ophuot.base.InternetConnection;
import com.thanhduy.ophuot.create_homestay.presenter.CreateHomestayPresenter;
import com.thanhduy.ophuot.database.SqlLiteDbHelper;
import com.thanhduy.ophuot.model.Homestay;
import com.thanhduy.ophuot.utils.Constants;
import com.thanhduy.ophuot.utils.ShowSnackbar;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by buivu on 08/03/2017.
 */

public class CreateHomeStayActivityFour extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.txt_create_last_province_name)
    TextView txtProvince;
    @BindView(R.id.txt_create_last_district_name)
    TextView txtDistrict;
    @BindView(R.id.txt_create_last_address)
    TextView txtAddress;
    @BindView(R.id.txt_create_last_homestay_name)
    TextView txtName;
    @BindView(R.id.txt_create_last_homestay_des)
    TextView txtDescription;
    @BindView(R.id.txt_create_last_homestay_type)
    TextView txtType;
    @BindView(R.id.txt_create_last_homestay_price)
    TextView txtPrice;
    @BindView(R.id.txt_create_last_homestay_max_passenger)
    TextView txtMaxPassenger;
    @BindView(R.id.txt_create_last_homestay_bedroom)
    TextView txtNumberBedroom;
    @BindView(R.id.txt_create_last_homestay_bed)
    TextView txtNumberBed;
    @BindView(R.id.txt_create_last_homestay_bathroom)
    TextView txtNumberBathroom;
    @BindView(R.id.txt_create_last_homestay_timeOpen)
    TextView txtOpen;
    @BindView(R.id.txt_create_last_homestay_timeClose)
    TextView txtClose;
    @BindView(R.id.txt_create_last_homestay_convenient)
    TextView txtConvenient;
    @BindView(R.id.txt_create_last_homestay_animal)
    TextView txtAnimal;
    @BindView(R.id.photoContainer)
    LinearLayout photoContainer;
    @BindView(R.id.btn_complete)
    Button btnComplete;


    private String strAddress, strHomestayName, strDescription, strType, strPrice;
    private double lat, lng;
    private Map<String, Object> mapDataDetails = new HashMap<>();
    private int provinceId, districtId;
    private ArrayList<Uri> listImage = new ArrayList<>();
    //private DatabaseAdapter databaseAdapter;
    private CreateHomestayPresenter presenter;
    public ProgressDialog mProgressDialog;
    private SqlLiteDbHelper databaseAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_homestay_4);
        ButterKnife.bind(this);
        databaseAdapter = new SqlLiteDbHelper(this);
        presenter = new CreateHomestayPresenter(this);
        //set toolbar
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(getResources().getString(R.string.confirmInformation));
        //get data
        getDataFromIntent();
        showData();
        //event click
        btnComplete.setOnClickListener(this);
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
        listImage = getIntent().getParcelableArrayListExtra(Constants.LIST_URI);
    }

    private void showData() {
        String provinceName = databaseAdapter.getProvinceNameByProvinceId(String.valueOf(provinceId));
        String districtName = databaseAdapter.getDistrictNameByDistrictId(String.valueOf(districtId));
        txtProvince.setText(provinceName);
        txtDistrict.setText(districtName);
        txtAddress.setText(strAddress);
        txtName.setText(strHomestayName);
        txtDescription.setText(strDescription);
        txtType.setText(strType);
        txtPrice.setText(strPrice);
        txtMaxPassenger.setText(mapDataDetails.get(Constants.MAX).toString());
        txtNumberBedroom.setText(mapDataDetails.get(Constants.BED_ROOM).toString());
        txtNumberBed.setText(mapDataDetails.get(Constants.BED).toString());
        txtNumberBathroom.setText(mapDataDetails.get(Constants.BATH_ROOM).toString());
        txtOpen.setText(mapDataDetails.get(Constants.TIME_OPEN).toString());
        txtClose.setText(mapDataDetails.get(Constants.TIME_CLOSE).toString());
        txtAnimal.setText(Boolean.valueOf(mapDataDetails.get(Constants.PET).toString()) ? "Có" : "Không");
        txtConvenient.setText(mapDataDetails.get(Constants.CONVENIENT).toString());
        onPickImageSuccess();
    }

    private void onPickImageSuccess() {
        int previewImageSize = getPixelValue(CreateHomeStayActivityFour.this, 150);
        photoContainer.removeAllViews();

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(previewImageSize, previewImageSize);
        params.setMargins(5, 0, 5, 0);

        for (Uri uri : listImage) {
            ImageView photo = new ImageView(this);
            photo.setScaleType(ImageView.ScaleType.CENTER_CROP);
            photo.setLayoutParams(params);
            //using Glide to load image
            ImageLoader.getInstance().loadImageOther(CreateHomeStayActivityFour.this, uri.toString(), photo);

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
    public boolean onCreateOptionsMenu(Menu menu) {

        return true;
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
        if (v == btnComplete) {
            if (InternetConnection.getInstance().isOnline(CreateHomeStayActivityFour.this)) {
                showProgessDialog();
                createHomestay();
            } else {
                ShowSnackbar.showSnack(CreateHomeStayActivityFour.this, getResources().getString(R.string.noInternet));
            }
        }
    }

    private void createHomestay() {
        try {
            //create map address
            Map<String, Object> address = new HashMap<>();
            address.put(Constants.ADDRESS, strAddress);
            address.put(Constants.LAT, lat);
            address.put(Constants.LNG, lng);
            //create instance homestay
            long creatAt = new Date().getTime();
            Homestay homestay = new Homestay(address, strDescription, mapDataDetails, strHomestayName, getUid(), strPrice,
                    0, strType, creatAt);
            presenter.createHomestay(String.valueOf(provinceId), String.valueOf(districtId), homestay, listImage);
        } catch (Exception e) {
            ShowSnackbar.showSnack(this, getResources().getString(R.string.error));
        }
    }

    public void showProgessDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(getResources().getString(R.string.loading));
            mProgressDialog.setCancelable(false);

            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    mProgressDialog.dismiss();
                }
            };
            Handler handlerCancel = new Handler();
            handlerCancel.postDelayed(runnable, 10000);
        }
        try {
            mProgressDialog.show();
        } catch (Exception e) {
        }

    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            try {
                mProgressDialog.dismiss();
            } catch (Exception e) {
            }

        }
    }
}
