package com.thanhduy.ophuot.my_homestay.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.thanhduy.ophuot.R;
import com.thanhduy.ophuot.create_homestay.view.CreateHomeStayActivityOne;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by buivu on 02/03/2017.
 */

public class MyHomestayFragment extends Fragment implements View.OnClickListener {

    private View rootView;

    @BindView(R.id.fab_create_homestay)
    FloatingActionButton fabCreateHomestay;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.activity_my_homestay, container, false);
        ButterKnife.bind(this, rootView);
        //event click
        fabCreateHomestay.setOnClickListener(this);

        return rootView;
    }

    @Override
    public void onClick(View v) {
        if (v == fabCreateHomestay) {
            Intent intent = new Intent(getActivity(), CreateHomeStayActivityOne.class);
            startActivity(intent);
        }
    }
}
