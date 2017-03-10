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

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.thanhduy.ophuot.R;
import com.thanhduy.ophuot.base.BaseActivity;
import com.thanhduy.ophuot.create_homestay.view.CreateHomeStayActivityOne;
import com.thanhduy.ophuot.model.MyPost;
import com.thanhduy.ophuot.my_homestay.MyHomestayAdapter;
import com.thanhduy.ophuot.my_homestay.presenter.MyHomestayPresenter;
import com.thanhduy.ophuot.utils.MyLinearLayoutManager;

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

    private MyHomestayPresenter presenter;
    private List<MyPost> myPosts = new ArrayList<>();
    private MyHomestayAdapter myHomestayAdapter;
    private MyLinearLayoutManager customLinearLayoutManager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.activity_my_homestay, container, false);
        ButterKnife.bind(this, rootView);
        presenter = new MyHomestayPresenter(this);
        myHomestayAdapter = new MyHomestayAdapter(myPosts, getActivity());
        customLinearLayoutManager = new MyLinearLayoutManager(getActivity());
        mRecycler.setLayoutManager(customLinearLayoutManager);
        mRecycler.setAdapter(myHomestayAdapter);
        //event click
        fabCreateHomestay.setOnClickListener(this);
        loadData();
        return rootView;
    }

    private void loadData() {
        presenter.getAllPost(BaseActivity.getUid()).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot != null) {
                    MyPost myPost = dataSnapshot.getValue(MyPost.class);
                    if (!myPosts.contains(myPost)) {
                        myPosts.add(myPost);
                        myHomestayAdapter.notifyDataSetChanged();
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
    public void onClick(View v) {
        if (v == fabCreateHomestay) {
            Intent intent = new Intent(getActivity(), CreateHomeStayActivityOne.class);
            startActivity(intent);
        }
    }
}
