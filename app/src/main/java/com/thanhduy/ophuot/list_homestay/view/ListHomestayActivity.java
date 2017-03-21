package com.thanhduy.ophuot.list_homestay.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.thanhduy.ophuot.R;
import com.thanhduy.ophuot.base.BaseActivity;
import com.thanhduy.ophuot.list_homestay.ListHomestayAdapter;
import com.thanhduy.ophuot.list_homestay.presenter.ListHomestayPresenter;
import com.thanhduy.ophuot.model.Homestay;
import com.thanhduy.ophuot.utils.Constants;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by buivu on 19/03/2017.
 */

public class ListHomestayActivity extends BaseActivity {

    @BindView(R.id.recycler_homestay)
    RecyclerView mRecycler;
    @BindView(R.id.toolbar)
    Toolbar toolbar;


    private String provinceId, districtId, title;
    private boolean isHasDistrict;
    private ListHomestayPresenter presenter;
    private List<Homestay> homestayList = new ArrayList<>();
    private ListHomestayAdapter listHomestayAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_homestay);
        ButterKnife.bind(this);
        title = getIntent().getStringExtra(Constants.ADDRESS);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(title);


        presenter = new ListHomestayPresenter(this);
        listHomestayAdapter = new ListHomestayAdapter(this, homestayList);

        provinceId = getIntent().getStringExtra(Constants.ID_PROVINCE);
        districtId = getIntent().getStringExtra(Constants.ID_DISTRICT);
        isHasDistrict = getIntent().getBooleanExtra(Constants.IS_HAS_DISTRICT, false);
        if (isHasDistrict) {
            loadDataHomestayByProvinceAndDistrict();
        } else {
            loadDataHomestayByProvince();
        }

        mRecycler.setLayoutManager(new LinearLayoutManager(this));
        mRecycler.setAdapter(listHomestayAdapter);
    }

    private void loadDataHomestayByProvince() {
        presenter.getListHomestayByProcince(provinceId).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot != null) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Homestay homestay = snapshot.getValue(Homestay.class);
                        if (homestay != null) {
                            if (!homestayList.contains(homestay)) {
                                homestayList.add(homestay);
                                listHomestayAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void loadDataHomestayByProvinceAndDistrict() {
        presenter.getListHomestayWithBothProvinceAndDistrict(provinceId, districtId).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot != null) {
                    Homestay homestay = dataSnapshot.getValue(Homestay.class);
                    if (homestay != null) {
                        if (!homestayList.contains(homestay)) {
                            homestayList.add(homestay);
                            listHomestayAdapter.notifyDataSetChanged();
                        }
                    }
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
