package com.thanhduy.ophuot.support.view;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.thanhduy.ophuot.R;
import com.thanhduy.ophuot.database.SqlLiteDbHelper;
import com.thanhduy.ophuot.database.model.Province;
import com.thanhduy.ophuot.model.Supporter;
import com.thanhduy.ophuot.support.SupportAdapter;
import com.thanhduy.ophuot.support.presenter.SupportPresenter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by buivu on 21/04/2017.
 */

public class SupportFragment extends Fragment {

    private View rootView;
    private RecyclerView mRecycler;
    private List<Supporter> listSupporter = new ArrayList<>();
    private SupportPresenter presenter;
    private SupportAdapter supportAdapter;
    private Spinner spnProvince;
    private ProgressBar progressBar;
    private ImageView imgNoResult;

    private static final int REQUEST_CODE_PHONE_CALL = 1001;
    private static final String[] PERMISSIONS_PHONE_CALL = {
            Manifest.permission.CALL_PHONE
    };

    private ArrayAdapter<String> arrayAdapter;
    private List<String> listProvinceName;
    private List<Province> listProvince;
    private SqlLiteDbHelper databaseAdapter;
    private int provinceId = 0;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.activity_support, container, false);
        //init components
        mRecycler = (RecyclerView) rootView.findViewById(R.id.recycler_support);
        spnProvince = (Spinner) rootView.findViewById(R.id.spn_province);
        progressBar = (ProgressBar) rootView.findViewById(R.id.progress_bar);
        imgNoResult = (ImageView) rootView.findViewById(R.id.img_no_result);
        //init
        presenter = new SupportPresenter(this);
        databaseAdapter = new SqlLiteDbHelper(getActivity());
        databaseAdapter.openDataBase();
        verifyPhoneCallPermission();
        supportAdapter = new SupportAdapter(getActivity(), listSupporter);
        mRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecycler.setAdapter(supportAdapter);
        initSpinner();
        return rootView;
    }

    private void initSpinner() {
        listProvince = new ArrayList<>();
        listProvinceName = new ArrayList<>();
        listProvince = databaseAdapter.getAllProvince();
        fetchDataProvinceName(listProvince);
        handleSpinner();
    }

    private void handleSpinner() {
        arrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, listProvinceName);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //set adapter
        spnProvince.setAdapter(arrayAdapter);
        //event click for spinner country
        spnProvince.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String item = parent.getItemAtPosition(position).toString();
                provinceId = databaseAdapter.getProvinceIdByProvinceName(item);
                listSupporter.clear();
                supportAdapter.notifyDataSetChanged();
                Log.d("PROVINCE", "" + provinceId);
                if (provinceId == 0) {
                    loadData(false, 0);
                } else {
                    loadData(true, provinceId);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void fetchDataProvinceName(List<Province> listProvince) {
        listProvinceName.add(getActivity().getResources().getString(R.string.all));
        for (Province province : listProvince) {
            listProvinceName.add(province.getProvinceName());
        }
    }

    private boolean verifyPhoneCallPermission() {
        // Check if we have read or write permission
        int readPermission = ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.CALL_PHONE);
        if (readPermission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    getActivity(),
                    PERMISSIONS_PHONE_CALL,
                    REQUEST_CODE_PHONE_CALL
            );

            return false;
        }
        return true;
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

    private void loadData(boolean isLoadAll, final int provinceId) {
        if (!isLoadAll) {
            presenter.getAllSupporter().addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        hideItemData();
                        presenter.getAllSupporter().addChildEventListener(new ChildEventListener() {
                            @Override
                            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                if (dataSnapshot != null) {
                                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                        Supporter supporter = snapshot.getValue(Supporter.class);
                                        if (!listSupporter.contains(supporter)) {
                                            listSupporter.add(supporter);
                                            supportAdapter.notifyDataSetChanged();
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
                                showItemData();
                            }
                        });
                    } else {
                        mRecycler.setVisibility(View.GONE);
                        imgNoResult.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        } else {
            presenter.getSupporterByProvinceId(provinceId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        hideItemData();
                        presenter.getSupporterByProvinceId(provinceId).addChildEventListener(new ChildEventListener() {
                            @Override
                            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                if (dataSnapshot != null) {
                                    Supporter supporter = dataSnapshot.getValue(Supporter.class);
                                    if (!listSupporter.contains(supporter)) {
                                        listSupporter.add(supporter);
                                        supportAdapter.notifyDataSetChanged();
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
                                showItemData();
                            }
                        });
                    } else {
                        mRecycler.setVisibility(View.GONE);
                        imgNoResult.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }
    }
}
