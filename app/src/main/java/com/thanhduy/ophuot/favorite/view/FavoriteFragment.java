package com.thanhduy.ophuot.favorite.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.thanhduy.ophuot.R;
import com.thanhduy.ophuot.base.BaseActivity;
import com.thanhduy.ophuot.favorite.FavoriteAdapter;
import com.thanhduy.ophuot.favorite.presenter.FavoritePresenter;
import com.thanhduy.ophuot.utils.ShowAlertDialog;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by buivu on 27/03/2017.
 */

public class FavoriteFragment extends android.support.v4.app.Fragment {
    private List<String> listFavoriteId;
    private FavoriteAdapter favoriteAdapter;
    private FavoritePresenter presenter;

    @BindView(R.id.recycler_favorite)
    RecyclerView mRecycler;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_favorite, container, false);
        ButterKnife.bind(this, rootView);
        presenter = new FavoritePresenter(getActivity());
        listFavoriteId = new ArrayList<>();
        favoriteAdapter = new FavoriteAdapter(getActivity(), listFavoriteId);
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            loadDataFavorite();
        } else {
            ShowAlertDialog.showAlert(getActivity().getResources().getString(R.string.loginFirst), getActivity());
        }

        mRecycler.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        mRecycler.setAdapter(favoriteAdapter);
        return rootView;
    }

    private void loadDataFavorite() {
        presenter.getAllFavoriteHomestay(BaseActivity.getUid()).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot != null) {
                    if (!listFavoriteId.contains(dataSnapshot.getKey())) {
                        listFavoriteId.add(dataSnapshot.getKey());
                        favoriteAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    if (listFavoriteId.contains(dataSnapshot.getKey())) {
                        listFavoriteId.remove(dataSnapshot.getKey());
                        favoriteAdapter.notifyDataSetChanged();
                    }
                }
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
