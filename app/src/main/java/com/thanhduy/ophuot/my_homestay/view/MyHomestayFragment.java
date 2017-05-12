package com.thanhduy.ophuot.my_homestay.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.thanhduy.ophuot.R;
import com.thanhduy.ophuot.base.BaseActivity;
import com.thanhduy.ophuot.create_homestay.view.CreateHomeStayActivityOne;
import com.thanhduy.ophuot.model.PostInfo;
import com.thanhduy.ophuot.my_homestay.MyHomestayAdapter;
import com.thanhduy.ophuot.my_homestay.presenter.MyHomestayPresenter;
import com.thanhduy.ophuot.utils.MyLinearLayoutManager;
import com.thanhduy.ophuot.utils.ShowAlertDialog;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by buivu on 02/03/2017.
 */

public class MyHomestayFragment extends Fragment implements View.OnClickListener {

    private View rootView;

    @BindView(R.id.fab_create_homestay)
    FloatingActionButton fabCreateHomestay;
    @BindView(R.id.recycler_my_homestay)
    RecyclerView mRecycler;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;

    private MyHomestayPresenter presenter;
    private List<PostInfo> postInfos = new ArrayList<>();
    private MyHomestayAdapter myHomestayAdapter;
    private MyLinearLayoutManager customLinearLayoutManager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.activity_my_homestay, container, false);
        ButterKnife.bind(this, rootView);
        presenter = new MyHomestayPresenter(this);
        myHomestayAdapter = new MyHomestayAdapter(postInfos, getActivity());
        customLinearLayoutManager = new MyLinearLayoutManager(getActivity());
        mRecycler.setLayoutManager(customLinearLayoutManager);
        mRecycler.setAdapter(myHomestayAdapter);
        //event click
        fabCreateHomestay.setOnClickListener(this);
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            fabCreateHomestay.setVisibility(View.VISIBLE);
            loadData();
        } else {
            fabCreateHomestay.setVisibility(View.GONE);
            ShowAlertDialog.showAlert(getResources().getString(R.string.loginFirst), getActivity());
        }
        return rootView;
    }

    private void showItemData() {
        progressBar.setVisibility(View.GONE);
        mRecycler.setVisibility(View.VISIBLE);
    }

    private void hideItemData() {
        progressBar.setVisibility(View.VISIBLE);
        mRecycler.setVisibility(View.GONE);
    }

    private void loadData() {
        presenter.getAllPost(BaseActivity.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    hideItemData();
                    presenter.getAllPost(BaseActivity.getUid()).addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                            if (dataSnapshot != null) {
                                PostInfo postInfo = dataSnapshot.getValue(PostInfo.class);
                                if (!postInfos.contains(postInfo)) {
                                    postInfos.add(postInfo);
                                    myHomestayAdapter.notifyDataSetChanged();
                                }
                            }
                            showItemData();
                        }

                        @Override
                        public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                        }

                        @Override
                        public void onChildRemoved(DataSnapshot dataSnapshot) {
                            PostInfo postInfo = dataSnapshot.getValue(PostInfo.class);
                            int indexMyPostInList = findIndexMyPost(postInfo);
                            postInfos.remove(indexMyPostInList);
                            myHomestayAdapter.notifyDataSetChanged();
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
                    progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private int findIndexMyPost(PostInfo postInfo) {
        int index = 0;
        for (int i = 0; i < postInfos.size(); i++) {
            if (postInfos.get(i).getHomestayId().equals(postInfo.getHomestayId())) {
                index = i;
                break;
            }
        }
        return index;
    }

    @Override
    public void onClick(View v) {
        if (v == fabCreateHomestay) {
            Intent intent = new Intent(getActivity(), CreateHomeStayActivityOne.class);
            startActivity(intent);
        }
    }
}
