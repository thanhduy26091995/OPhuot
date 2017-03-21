package com.thanhduy.ophuot.list_homestay.model;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.thanhduy.ophuot.utils.Constants;

/**
 * Created by buivu on 19/03/2017.
 */

public class ListHomestaySubmitter {
    private DatabaseReference mDatabase;

    public ListHomestaySubmitter(DatabaseReference mDatabase) {
        this.mDatabase = mDatabase;
    }

    public Query getListHomestayWithBothProvinceAndDistrict(String provinceId, String districtId) {
        return mDatabase.child(Constants.HOMESTAY).child(provinceId).child(districtId);
    }

    public Query getListHomestayByProcince(String provinceId){
        return mDatabase.child(Constants.HOMESTAY).child(provinceId);
    }
}
