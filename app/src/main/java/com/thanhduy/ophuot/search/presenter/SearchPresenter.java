package com.thanhduy.ophuot.search.presenter;

import android.app.Activity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.thanhduy.ophuot.utils.Constants;

/**
 * Created by buivu on 04/04/2017.
 */

public class SearchPresenter {
    private Activity view;
    private DatabaseReference mDatabase;

    public SearchPresenter(Activity view) {
        this.view = view;
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    public Query searchHomestayNearby(int provinceId) {
        return mDatabase.child(Constants.HOMESTAY).child(String.valueOf(provinceId));
    }

    public Query searchHomestayByProvinceAndDistrict(int provinceId, int districtId){
        return mDatabase.child(Constants.HOMESTAY).child(String.valueOf(provinceId)).child(String.valueOf(districtId));
    }
}
