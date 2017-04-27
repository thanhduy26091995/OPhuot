package com.thanhduy.ophuot.search.view;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.thanhduy.ophuot.R;
import com.thanhduy.ophuot.database.SqlLiteDbHelper;
import com.thanhduy.ophuot.database.model.District;
import com.thanhduy.ophuot.database.model.Province;
import com.thanhduy.ophuot.featured.model.Featured;
import com.thanhduy.ophuot.search.PlacesFeaturedAdapter;
import com.thanhduy.ophuot.utils.Constants;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by buivu on 03/04/2017.
 */

public class SearchFragment extends Fragment implements View.OnClickListener {

    private View rootView;
    private Spinner spnProvince, spnDistrict;
    private List<String> listProvinceName, listDistrictName;
    private ArrayAdapter<String> spinnerProvinceAdapter, spinnerDistrictAdapter, spinnerTypeAdapter;
    private List<Province> listProvince;
    private List<District> listDistrict;
    private SqlLiteDbHelper sqlLiteDbHelper;
    private int provinceId, districtId;
    private List<Featured> featuredList = new ArrayList<>();
    private PlacesFeaturedAdapter placesFeaturedAdapter;

    @BindView(R.id.linear_search_near_by)
    LinearLayout linearSearchNearBy;
    @BindView(R.id.linear_search_by_province)
    LinearLayout linearSearchByProvince;
    @BindView(R.id.recycler_places_featured)
    RecyclerView recyclerView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.activity_search, container, false);
        ButterKnife.bind(this, rootView);
        //init
        sqlLiteDbHelper = new SqlLiteDbHelper(getActivity());
        sqlLiteDbHelper.openDataBase();
        listProvinceName = new ArrayList<>();
        listProvince = new ArrayList<>();
        listDistrict = new ArrayList<>();
        listDistrictName = new ArrayList<>();
        listProvince = sqlLiteDbHelper.getAllProvince();
        for (Province province : listProvince) {
            listProvinceName.add(province.getProvinceName());
        }
        //prepare data
        prepareData();
        placesFeaturedAdapter = new PlacesFeaturedAdapter(featuredList, getActivity());
        // placesFeaturedAdapter.notifyDataSetChanged();
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(placesFeaturedAdapter);
        recyclerView.smoothScrollToPosition(0);
        //event click
        linearSearchNearBy.setOnClickListener(this);
        linearSearchByProvince.setOnClickListener(this);
        return rootView;
    }

    private void prepareData() {

        Featured featuredDaLat = new Featured("41", "475", R.drawable.da_lat, Constants.DA_LAT);
        Featured featuredNhaTrang = new Featured("40", "466", R.drawable.nha_trang, Constants.NHA_TRANG);
        Featured featuredDaNang = new Featured("4", "1", R.drawable.da_nang, Constants.DA_NANG);
        Featured featuredSaiGon = new Featured("2", "1", R.drawable.sai_gon, Constants.SAI_GON);
        Featured featuredHaNoi = new Featured("1", "1", R.drawable.ha_noi, Constants.HA_NOI);
        Featured featuredHoiAn = new Featured("33", "376", R.drawable.hoi_an, Constants.HOI_AN);
        Featured featuredHue = new Featured("32", "367", R.drawable.hue, Constants.HUE);
        Featured featuredVinhHaLong = new Featured("17", "1", R.drawable.vinh_ha_long, Constants.VINH_HA_LONG);
        Featured featuredSapa = new Featured("8", "112", R.drawable.sapa, Constants.SAPA);

        featuredList.add(featuredDaLat);
        featuredList.add(featuredNhaTrang);
        featuredList.add(featuredDaNang);
        featuredList.add(featuredSaiGon);
        featuredList.add(featuredHaNoi);
        featuredList.add(featuredHoiAn);
        featuredList.add(featuredHue);
        featuredList.add(featuredVinhHaLong);
        featuredList.add(featuredSapa);
    }

    @Override
    public void onClick(View v) {
        if (v == linearSearchNearBy) {
            Intent intent = new Intent(getActivity(), SearchNearByResultActivity.class);
            startActivity(intent);
        } else if (v == linearSearchByProvince) {
            showDialogFilter();
        }
    }

    private void showDialogFilter() {
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_dialog_filter);
       /*
       Init components
        */

        spnProvince = (Spinner) dialog.findViewById(R.id.spinner_province);
        spnDistrict = (Spinner) dialog.findViewById(R.id.spinner_district);
        //format position dialog
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setGravity(Gravity.CENTER);
        WindowManager.LayoutParams layoutParams = dialog.getWindow().getAttributes();

        //layoutParams.x = convertDpToPixel((float) 130 / 1, ResortsActivity.this);
        dialog.getWindow().setAttributes(layoutParams);

        //handle spinner
        handleSpinner();
        //event click dialog
        TextView txtCancel = (TextView) dialog.findViewById(R.id.txt_filter_cancel);
        TextView txtOk = (TextView) dialog.findViewById(R.id.txt_filter_ok);
        txtCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                // hashCountry.clear();
            }
        });

        txtOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //chuyển dữ liệu qua trang mới
                Intent intent = new Intent(getActivity(), SearchByProvinceAndDistrictActivity.class);
                intent.putExtra(Constants.ID_PROVINCE, provinceId);
                intent.putExtra(Constants.ID_DISTRICT, districtId);
                startActivity(intent);
                //dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void handleSpinner() {
        /*
        SPinner province and district
         */
        spinnerProvinceAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, listProvinceName);
        spinnerProvinceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //set adapter
        spnProvince.setAdapter(spinnerProvinceAdapter);
        //event click for spinner country
        spnProvince.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String item = parent.getItemAtPosition(position).toString();
                provinceId = sqlLiteDbHelper.getProvinceIdByProvinceName(item);
                //clear list
                listDistrictName.clear();
                //call sqlite
                listDistrict = sqlLiteDbHelper.getDistrictsByProvinceId(provinceId);
                //fill data to list
                for (District district : listDistrict) {
                    listDistrictName.add(district.getDistrictName());
                }
                spinnerDistrictAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, listDistrictName);
                spinnerDistrictAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                //set adapter
                spnDistrict.setAdapter(spinnerDistrictAdapter);
                //event click
                spnDistrict.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        String item = parent.getItemAtPosition(position).toString();
                        districtId = sqlLiteDbHelper.getDistrictIdByDistrictName(item);
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
    }
}
