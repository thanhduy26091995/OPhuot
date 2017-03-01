package com.thanhduy.ophuot.profile.edit_profile.model;

import com.google.firebase.database.DatabaseReference;
import com.thanhduy.ophuot.utils.Constants;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by buivu on 01/03/2017.
 */

public class EditProfileSubmitter {
    private DatabaseReference mDatabase;

    public EditProfileSubmitter(DatabaseReference mDatabase) {
        this.mDatabase = mDatabase;
    }

    public void updateUserInfo(String uid, Map<String, Object> address, String description,
                               String name, String phone, int gender) {
        Map<String, Object> dataUser = new HashMap<>();
        dataUser.put(Constants.ADDRESS, address);
        dataUser.put(Constants.DESCRIPTION, description);
        dataUser.put(Constants.NAME, name);
        dataUser.put(Constants.PHONE, phone);
        dataUser.put(Constants.GENDER, gender);
        //update data
        mDatabase.child(Constants.USERS).child(uid).updateChildren(dataUser);
    }
}
