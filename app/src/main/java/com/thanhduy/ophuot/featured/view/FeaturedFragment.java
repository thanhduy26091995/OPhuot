package com.thanhduy.ophuot.featured.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.thanhduy.ophuot.R;
import com.thanhduy.ophuot.featured.FeaturedAdapter;
import com.thanhduy.ophuot.featured.model.Featured;
import com.thanhduy.ophuot.utils.Constants;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Created by buivu on 18/03/2017.
 */

public class FeaturedFragment extends Fragment {


    @BindView(R.id.recycler_featured)
    RecyclerView mRecycler;

    private FeaturedAdapter featuredAdapter;
    private List<Featured> featuredList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_featured, container, false);
        ButterKnife.bind(this, rootView);
        prepareData();
        featuredAdapter = new FeaturedAdapter(getActivity(), featuredList);
        mRecycler.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        mRecycler.setAdapter(featuredAdapter);
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
}
