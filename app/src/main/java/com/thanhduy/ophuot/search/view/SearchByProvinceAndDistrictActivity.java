package com.thanhduy.ophuot.search.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.thanhduy.ophuot.R;
import com.thanhduy.ophuot.base.BaseActivity;
import com.thanhduy.ophuot.base.InternetConnection;
import com.thanhduy.ophuot.list_homestay.ListHomestayAdapter;
import com.thanhduy.ophuot.model.Homestay;
import com.thanhduy.ophuot.search.presenter.SearchPresenter;
import com.thanhduy.ophuot.utils.Constants;
import com.thanhduy.ophuot.utils.ShowAlertDialog;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by buivu on 05/04/2017.
 */

public class SearchByProvinceAndDistrictActivity extends BaseActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.fab_map)
    FloatingActionButton fabMap;
    @BindView(R.id.recycler_homestay)
    RecyclerView recyclerView;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.img_no_result)
    ImageView imgNoResult;

    private int provinceId, districtId;
    private DatabaseReference mDatabase;
    private SearchPresenter presenter;
    private List<Homestay> homestayList = new ArrayList<>();
    private ListHomestayAdapter listHomestayAdapter;
    private String name;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_by_province_district_result);
        ButterKnife.bind(this);
        //init
        mDatabase = FirebaseDatabase.getInstance().getReference();
        presenter = new SearchPresenter(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        name = getIntent().getStringExtra(Constants.NAME);
        getSupportActionBar().setTitle(name);
        //get intent
        provinceId = getIntent().getIntExtra(Constants.ID_PROVINCE, 0);
        districtId = getIntent().getIntExtra(Constants.ID_DISTRICT, 0);

        initInfo();
    }

    private void initInfo() {
        if (InternetConnection.getInstance().isOnline(SearchByProvinceAndDistrictActivity.this)) {
            handleData();
            listHomestayAdapter = new ListHomestayAdapter(this, homestayList);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setAdapter(listHomestayAdapter);
        } else {
            Snackbar snackbar = Snackbar.make(findViewById(R.id.activity), getResources().getString(R.string.noInternet), Snackbar.LENGTH_INDEFINITE)
                    .setAction(getResources().getString(R.string.retry), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            initInfo();
                        }
                    });
            snackbar.show();
        }
    }

    private void handleData() {
        hideItemData();
        mDatabase.child(Constants.HOMESTAY).child(String.valueOf(provinceId)).child(String.valueOf(districtId)).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    //recyclerView.setVisibility(View.VISIBLE);
                    //fabMap.setVisibility(View.VISIBLE);
                    // linearNoResult.setVisibility(View.GONE);
                    //show data
                    loadDataHomestayByProvinceAndDistrict();
                } else {
                    recyclerView.setVisibility(View.GONE);
                    fabMap.setVisibility(View.GONE);
                    imgNoResult.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void showItemData() {
        progressBar.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
        imgNoResult.setVisibility(View.GONE);
    }

    private void hideItemData() {
        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        imgNoResult.setVisibility(View.GONE);
    }

    private void loadDataHomestayByProvinceAndDistrict() {
        hideItemData();
        presenter.searchHomestayByProvinceAndDistrict(provinceId, districtId).addChildEventListener(new ChildEventListener() {
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
                showItemData();
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
                if (databaseError.getCode() == -3) {
                    ShowAlertDialog.showAlert(getResources().getString(R.string.accountBlocked), SearchByProvinceAndDistrictActivity.this);
                }
                showItemData();
            }
        });
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
}
