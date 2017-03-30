package com.thanhduy.ophuot.login_and_register.model;

import com.google.firebase.database.DatabaseReference;
import com.thanhduy.ophuot.utils.Constants;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by buivu on 07/02/2017.
 */

public class LoginRegisterSubmitter {
    private DatabaseReference mDatabase;

    public LoginRegisterSubmitter(DatabaseReference mDatabase) {
        this.mDatabase = mDatabase;
    }

    public void addUser(String currentId, String name, String phone, String email, String favoriteName) {
        //get createAt\
        long createAt = new Date().getTime();
        //hashmap address
        Map<String, Object> mapAddress = new HashMap<>();
        mapAddress.put(Constants.ADDRESS, "");
        mapAddress.put(Constants.LAT, 0);
        mapAddress.put(Constants.LNG, 0);

        Map<String, Object> myMap = new HashMap<>();
        myMap.put(Constants.NAME, name);
        myMap.put(Constants.PHONE, phone);
        myMap.put(Constants.EMAIL, email);
        myMap.put(Constants.DEVICE_TOKEN, "");
        myMap.put(Constants.ADDRESS, mapAddress);
        myMap.put(Constants.AVATAR, "");
        myMap.put(Constants.CREATE_AT, createAt);
        myMap.put(Constants.DESCRIPTION, "");
        myMap.put(Constants.GENDER, 1);
        //add firebase
        mDatabase.child(Constants.USERS).child(currentId).setValue(myMap);


        //hashmap favorite
        Map<String, Object> mapInfo = new HashMap<>();
        mapInfo.put(Constants.FAVORITE_NAME, favoriteName);
        mDatabase.child(Constants.USERS).child(currentId).child(Constants.FAVORITE).child(currentId).child(Constants.INFO).updateChildren(mapInfo);
    }
}
