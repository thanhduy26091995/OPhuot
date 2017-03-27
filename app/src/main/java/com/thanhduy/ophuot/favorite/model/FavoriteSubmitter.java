package com.thanhduy.ophuot.favorite.model;

import com.google.firebase.database.DatabaseReference;
import com.thanhduy.ophuot.model.PostInfo;
import com.thanhduy.ophuot.utils.Constants;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by buivu on 23/03/2017.
 */

public class FavoriteSubmitter {
    private DatabaseReference mDatabase;

    public FavoriteSubmitter(DatabaseReference mDatabase) {
        this.mDatabase = mDatabase;
    }

    //add member's favorite
    public void addDataToFavoriteHomestay(PostInfo postInfo, String uid) {
        Map<String, Object> data = new HashMap<>();
        data.put(Constants.DISTRICT_ID, postInfo.getDistrictId());
        data.put(Constants.PROVINCE_ID, postInfo.getProvinceId());
        data.put(Constants.HOMESTAY_ID, postInfo.getHomestayId());
        //update data
        mDatabase.child(Constants.USERS).child(uid).child(Constants.FAVORITE).child(postInfo.getHomestayId()).updateChildren(data);
        //add data into homestay favorite
        Map<String, Object> dataFavoriteHomestay = new HashMap<>();
        dataFavoriteHomestay.put(uid, true);
        mDatabase.child(Constants.HOMESTAY).child(String.valueOf(postInfo.getProvinceId())).child(String.valueOf(postInfo.getDistrictId()))
                .child(postInfo.getHomestayId()).child(Constants.FAVORITE).updateChildren(dataFavoriteHomestay);
    }

}
