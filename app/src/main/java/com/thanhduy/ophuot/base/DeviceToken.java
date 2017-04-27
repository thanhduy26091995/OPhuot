package com.thanhduy.ophuot.base;

import com.google.firebase.database.DatabaseReference;
import com.thanhduy.ophuot.utils.Constants;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by buivu on 28/10/2016.
 */
public class DeviceToken {
    private static final String TAG = "DeviceToken";
    private static DeviceToken instance;

    //constructor
    private DeviceToken() {

    }

    public static DeviceToken getInstance() {
        if (instance == null) {
            instance = new DeviceToken();
        }
        return instance;
    }

    public void addDeviceToken(DatabaseReference mDatabase, String uid, String token) {
        Map<String, Object> myMap = new HashMap<>();
        myMap.put(Constants.DEVICE_TOKEN, token);
        mDatabase.child(Constants.USERS).child(uid).updateChildren(myMap);
    }
}
