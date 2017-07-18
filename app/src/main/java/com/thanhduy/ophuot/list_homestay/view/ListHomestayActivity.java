package com.thanhduy.ophuot.list_homestay.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.thanhduy.ophuot.R;
import com.thanhduy.ophuot.base.BaseActivity;
import com.thanhduy.ophuot.base.InternetConnection;
import com.thanhduy.ophuot.list_homestay.ListHomestayAdapter;
import com.thanhduy.ophuot.list_homestay.presenter.ListHomestayPresenter;
import com.thanhduy.ophuot.model.Homestay;
import com.thanhduy.ophuot.utils.Constants;
import com.thanhduy.ophuot.utils.ShowAlertDialog;
import com.thanhduy.ophuot.utils.ShowSnackbar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.img_no_result)
    ImageView imgNoResult;


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

        initData();

        mRecycler.setLayoutManager(new LinearLayoutManager(this));
        mRecycler.setAdapter(listHomestayAdapter);


    }

    private void initData() {
        if (InternetConnection.getInstance().isOnline(ListHomestayActivity.this)) {
            if (isHasDistrict) {
                loadDataHomestayByProvinceAndDistrict();
            } else {
                loadDataHomestayByProvince();
            }
        } else {
            //hide proress
            progressBar.setVisibility(View.GONE);
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

    private void showItemData() {
        progressBar.setVisibility(View.GONE);
        mRecycler.setVisibility(View.VISIBLE);
        imgNoResult.setVisibility(View.GONE);
    }

    private void hideItemData() {
        progressBar.setVisibility(View.VISIBLE);
        mRecycler.setVisibility(View.GONE);
        imgNoResult.setVisibility(View.GONE);
    }

    private void loadDataHomestayByProvince() {
        try {
            presenter.getListHomestayByProcince(provinceId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        hideItemData();
                        presenter.getListHomestayByProcince(provinceId).addChildEventListener(new ChildEventListener() {
                            @Override
                            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                try {
                                    if (dataSnapshot != null) {
                                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                            Homestay homestay = snapshot.getValue(Homestay.class);
                                            if (homestay != null) {
                                                if (!homestayList.contains(homestay)) {
                                                    homestayList.add(homestay);
                                                    listHomestayAdapter.notifyDataSetChanged();
                                                    Log.d("OWNER", homestay.getPostBy());
                                                }
                                            }
                                        }
                                    }
                                    showItemData();
                                } catch (Exception e) {
                                    ShowSnackbar.showSnack(ListHomestayActivity.this, getResources().getString(R.string.error));
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
                                try {
                                    if (databaseError.getCode() == -3) {
                                        ShowAlertDialog.showAlert(getResources().getString(R.string.accountBlocked), ListHomestayActivity.this);
                                    }
                                    showItemData();
                                } catch (Exception e) {
                                    ShowSnackbar.showSnack(ListHomestayActivity.this, getResources().getString(R.string.error));
                                }
                            }
                        });
                    } else {
                        //nếu không có node này
                        imgNoResult.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.GONE);
                        mRecycler.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        } catch (Exception e) {
            ShowSnackbar.showSnack(this, getResources().getString(R.string.error));
        }
    }

    private void loadDataHomestayByProvinceAndDistrict() {
        try {
            presenter.getListHomestayWithBothProvinceAndDistrict(provinceId, districtId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    try {
                        if (dataSnapshot.exists()) {
                            hideItemData();
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
                                        ShowAlertDialog.showAlert(getResources().getString(R.string.accountBlocked), ListHomestayActivity.this);
                                    }
                                    showItemData();
                                }
                            });
                        } else {
                            //nếu không có node này
                            imgNoResult.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.GONE);
                            progressBar.setVisibility(View.GONE);
                        }
                    } catch (Exception e) {
                        ShowSnackbar.showSnack(ListHomestayActivity.this, getResources().getString(R.string.error));
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        } catch (Exception e) {
            ShowSnackbar.showSnack(this, getResources().getString(R.string.error));
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 100 && resultCode == RESULT_OK) {
            try {
                int position = data.getIntExtra(Constants.POSITION, 0);
                Homestay homestayIntent = (Homestay) data.getSerializableExtra(Constants.HOMESTAY);
                if (Constants.IS_CHANGE_LIST_FAVORITE) {
                    Homestay homestay = homestayList.get(position);
                    if (homestay.getFavorite() != null) {
                        homestay.getFavorite().clear();
                    }
                    homestay.setFavorite(homestayIntent.getFavorite());
                    if (homestay.getFavorite() != null) {
                        listHomestayAdapter.notifyDataSetChanged();
                    } else {
                        Map<String, Boolean> dataFavorite = new HashMap<>();
                        dataFavorite.put(BaseActivity.getUid(), true);
                        homestay.setFavorite(dataFavorite);
                        homestay.getFavorite().put(BaseActivity.getUid(), true);
                        listHomestayAdapter.notifyDataSetChanged();
                    }
                    Constants.IS_CHANGE_LIST_FAVORITE = false;
                }
            } catch (Exception e) {
                ShowSnackbar.showSnack(this, getResources().getString(R.string.error));
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
