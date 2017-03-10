package com.thanhduy.ophuot.create_homestay.model;

import com.google.firebase.database.DatabaseReference;
import com.thanhduy.ophuot.model.Homestay;
import com.thanhduy.ophuot.utils.Constants;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by buivu on 08/03/2017.
 */

public class CreateHomestaySubmitter {
    private DatabaseReference mDatabase;

    public CreateHomestaySubmitter(DatabaseReference mDatabase) {
        this.mDatabase = mDatabase;
    }

    public void createHomestay(String provinceId, String districtId, Homestay homestay) {
        String pushId = mDatabase.child(Constants.HOMESTAY).child(provinceId).child(districtId).push().getKey();
        //add homestayid
        homestay.setId(pushId);
        Map<String, Object> data = new HashMap<>();
        data.put(Constants.ADDRESS, homestay.getAddress());
        data.put(Constants.DESCRIPTION, homestay.getDescription());
        data.put(Constants.DETAILS, homestay.getDetails());
        data.put(Constants.ID, pushId);
        data.put(Constants.ID_PROVINCE, provinceId);
        data.put(Constants.ID_DISTRICT, districtId);
        data.put(Constants.NAME, homestay.getName());
        data.put(Constants.POST_BY, homestay.getPostBy());
        data.put(Constants.PRICE, homestay.getPrice());
        data.put(Constants.TYPE, homestay.getType());
        data.put(Constants.CREATE_AT, homestay.getCreateAt());
        data.put(Constants.IMAGES, homestay.getImages());
        //add to database
        mDatabase.child(Constants.HOMESTAY).child(provinceId).child(districtId).child(pushId).setValue(data);
    }

    //add homestay - owner user
    public void addOwnerHomestay(String homeStayId, int districtId, int provinceId, String uid) {
        Map<String, Object> myMap = new HashMap<>();
        myMap.put(Constants.HOMESTAY_ID, homeStayId);
        myMap.put(Constants.ID_DISTRICT, districtId);
        myMap.put(Constants.ID_PROVINCE, provinceId);
        //add database
        mDatabase.child(Constants.USERS).child(uid).child(Constants.POST).child(homeStayId).setValue(myMap);
    }
}
