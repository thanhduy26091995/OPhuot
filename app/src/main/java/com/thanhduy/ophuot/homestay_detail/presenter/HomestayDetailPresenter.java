package com.thanhduy.ophuot.homestay_detail.presenter;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.thanhduy.ophuot.homestay_detail.view.ActivityHomestayDetail;
import com.thanhduy.ophuot.model.Homestay;
import com.thanhduy.ophuot.utils.Constants;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by buivu on 29/05/2017.
 */

public class HomestayDetailPresenter {
    private DatabaseReference mDatabase;
    private ActivityHomestayDetail view;

    public HomestayDetailPresenter(ActivityHomestayDetail view) {
        this.view = view;
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    public void addToRatingBy(String ratingBy, int rate, final Homestay homestay) {
        Map<String, Object> myData = new HashMap<>();
        myData.put(ratingBy, rate);

        //update data
        mDatabase.child(Constants.HOMESTAY).child(String.valueOf(homestay.getProvinceId())).child(String.valueOf(homestay.getDistrictId()))
                .child(homestay.getId()).child(Constants.RATING_BY).updateChildren(myData);

        //update rating
        mDatabase.child(Constants.HOMESTAY).child(String.valueOf(homestay.getProvinceId())).child(String.valueOf(homestay.getDistrictId()))
                .child(homestay.getId()).child(Constants.RATING_BY).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    Map<String, Object> data = (Map<String, Object>) dataSnapshot.getValue();
                    int sum = 0;
                    for (Map.Entry<String, Object> entry : data.entrySet()) {
                        long value = (long) entry.getValue();
                        sum += value;
                    }
                    //caculate rating
                    float rating = sum / dataSnapshot.getChildrenCount();
                    //update
                    updateRating(rating, homestay);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void updateRating(float rating, Homestay homestay) {
        Map<String, Object> dataRating = new HashMap<>();
        dataRating.put(Constants.RATING, rating);
        mDatabase.child(Constants.HOMESTAY).child(String.valueOf(homestay.getProvinceId())).child(String.valueOf(homestay.getDistrictId()))
                .child(homestay.getId()).updateChildren(dataRating);
    }
}
