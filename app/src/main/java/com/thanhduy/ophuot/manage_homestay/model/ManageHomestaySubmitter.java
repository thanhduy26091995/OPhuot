package com.thanhduy.ophuot.manage_homestay.model;

import com.google.firebase.database.DatabaseReference;
import com.thanhduy.ophuot.utils.Constants;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by buivu on 15/03/2017.
 */

public class ManageHomestaySubmitter {
    private DatabaseReference mDatabase;

    public ManageHomestaySubmitter(DatabaseReference mDatabase) {
        this.mDatabase = mDatabase;
    }

    public void deleteHomestay(String provinceId, String districtId, String homestayId, String uid) {
        mDatabase.child(Constants.USERS).child(uid).child(Constants.POST).child(homestayId).removeValue();
        mDatabase.child(Constants.HOMESTAY).child(provinceId).child(districtId).child(homestayId).removeValue();
    }

    public void editHomestay(String provinceId, String districtId, String homestayId, String name, String description,
                             String price, String type, Map<String, Object> details) {
        Map<String, Object> updateDataHomestay = new HashMap<>();
        updateDataHomestay.put(Constants.NAME, name);
        updateDataHomestay.put(Constants.DESCRIPTION, description);
        updateDataHomestay.put(Constants.PRICE, price);
        updateDataHomestay.put(Constants.TYPE, type);
        updateDataHomestay.put(Constants.DETAILS, details);
        //update data
        mDatabase.child(Constants.HOMESTAY).child(provinceId).child(districtId).child(homestayId).updateChildren(updateDataHomestay);
    }
}
