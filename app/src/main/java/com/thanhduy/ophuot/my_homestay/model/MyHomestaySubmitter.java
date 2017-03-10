package com.thanhduy.ophuot.my_homestay.model;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.thanhduy.ophuot.utils.Constants;

/**
 * Created by buivu on 09/03/2017.
 */

public class MyHomestaySubmitter {
    private DatabaseReference mDatabase;

    public MyHomestaySubmitter(DatabaseReference mDatabase) {
        this.mDatabase = mDatabase;
    }

    public Query getAllPost(String uid) {
        Query query = mDatabase.child(Constants.USERS).child(uid).child(Constants.POST);
        return query;
    }
}
