package com.thanhduy.ophuot.fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.CoordinatorLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.thanhduy.ophuot.R;
import com.thanhduy.ophuot.base.BaseActivity;
import com.thanhduy.ophuot.model.FavoriteInfo;
import com.thanhduy.ophuot.model.PostInfo;
import com.thanhduy.ophuot.utils.Constants;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by buivu on 24/03/2017.
 */

public class BottomDialogFragment extends BottomSheetDialogFragment implements View.OnClickListener {

    private PostInfo postInfo;
    private LinearLayout linearMyFavorite;
    private DatabaseReference mDatabase;
    private int position;
    private PositionCallback positionCallback;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        postInfo = (PostInfo) getArguments().getSerializable(Constants.POST_INFO);
        position = getArguments().getInt(Constants.POSITION);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    public void setPositionCallback(PositionCallback positionCallback) {
        this.positionCallback = positionCallback;
    }

    private BottomSheetBehavior.BottomSheetCallback bottomSheetCallback = new BottomSheetBehavior.BottomSheetCallback() {
        @Override
        public void onStateChanged(@NonNull View bottomSheet, int newState) {
            if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                dismiss();
            }
        }

        @Override
        public void onSlide(@NonNull View bottomSheet, float slideOffset) {

        }
    };

    @Override
    public void setupDialog(Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        View contentView = View.inflate(getContext(), R.layout.activity_bottom_sheet_featured, null);
        linearMyFavorite = (LinearLayout) contentView.findViewById(R.id.linear_my_favorite);
        linearMyFavorite.setOnClickListener(this);
        dialog.setContentView(contentView);

        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) ((View) contentView.getParent()).getLayoutParams();
        CoordinatorLayout.Behavior behavior = params.getBehavior();

        if (behavior != null && behavior instanceof BottomSheetBehavior) {
            ((BottomSheetBehavior) behavior).setBottomSheetCallback(bottomSheetCallback);
        }
    }

    @Override
    public void onClick(View v) {
        if (v == linearMyFavorite) {
            if (postInfo != null) {
                addDataToFavoriteHomestay(postInfo, BaseActivity.getUid());
                positionCallback.positionCallBack(position);
            }
        }
    }

    private void addDataToMyFavorite() {

    }

    private void addDataToFavoriteHomestay(PostInfo postInfo, final String uid) {
        /*
        save user node
         */
        //init data favorite
        Map<String, Object> data = new HashMap<>();
        data.put(Constants.ID_DISTRICT, postInfo.getDistrictId());
        data.put(Constants.ID_PROVINCE, postInfo.getProvinceId());
        data.put(Constants.HOMESTAY_ID, postInfo.getHomestayId());
        //init data info favorite
        final Map<String, Object> dataInfo = new HashMap<>();
        dataInfo.put(Constants.FAVORITE_NAME, getActivity().getResources().getString(R.string.myFavorite));

        mDatabase.child(Constants.USERS).child(BaseActivity.getUid()).child(Constants.FAVORITE).child(BaseActivity.getUid()).child(Constants.INFO).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    FavoriteInfo favoriteInfo = dataSnapshot.getValue(FavoriteInfo.class);
                    if (favoriteInfo != null) {
                        long favoriteCount = favoriteInfo.getFavoriteCount();
                        dataInfo.put(Constants.FAVORITE_COUNT, favoriteCount + 1);
                        //save data info
                        mDatabase.child(Constants.USERS).child(uid).child(Constants.FAVORITE).child(BaseActivity.getUid()).child(Constants.INFO).updateChildren(dataInfo);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        //save homestay to list homestay favorite (yêu thích cảu tôi)
        mDatabase.child(Constants.USERS).child(uid).child(Constants.FAVORITE).child(BaseActivity.getUid()).child(Constants.LIST_HOMESTAY).child(postInfo.getHomestayId()).updateChildren(data);
        /*
            save homestay node
         */
        //add data into homestay favorite (save homestay node)
        Map<String, Object> dataFavoriteHomestay = new HashMap<>();
        dataFavoriteHomestay.put(uid, true);
        mDatabase.child(Constants.HOMESTAY).child(String.valueOf(postInfo.getProvinceId())).child(String.valueOf(postInfo.getDistrictId()))
                .child(postInfo.getHomestayId()).child(Constants.FAVORITE).updateChildren(dataFavoriteHomestay);
        //close dialog fragment
        dismiss();
    }
}
