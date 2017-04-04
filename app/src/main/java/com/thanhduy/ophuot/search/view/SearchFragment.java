package com.thanhduy.ophuot.search.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.thanhduy.ophuot.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by buivu on 03/04/2017.
 */

public class SearchFragment extends Fragment implements View.OnClickListener{

    private View rootView;

    @BindView(R.id.linear_search_near_by)
    LinearLayout linearSearchNearBy;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.activity_search, container, false);
        ButterKnife.bind(this, rootView);
        //event click
        linearSearchNearBy.setOnClickListener(this);
        return rootView;
    }

    @Override
    public void onClick(View v) {
        if (v == linearSearchNearBy){
            Intent intent = new Intent(getActivity(), SearchNearByResultActivity.class);
            startActivity(intent);
        }
    }
}
