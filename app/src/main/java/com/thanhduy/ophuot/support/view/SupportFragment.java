package com.thanhduy.ophuot.support.view;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.thanhduy.ophuot.R;
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

    private static final int REQUEST_CODE_PHONE_CALL = 1001;
    private static final String[] PERMISSIONS_PHONE_CALL = {
            Manifest.permission.CALL_PHONE
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.activity_support, container, false);
        mRecycler = (RecyclerView) rootView.findViewById(R.id.recycler_support);
        presenter = new SupportPresenter(this);
        verifyPhoneCallPermission();
        supportAdapter = new SupportAdapter(getActivity(), listSupporter);
        mRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecycler.setAdapter(supportAdapter);
        loadData();
        return rootView;
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


    private void loadData() {
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
}
