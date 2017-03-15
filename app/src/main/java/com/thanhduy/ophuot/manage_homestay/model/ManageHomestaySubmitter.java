package com.thanhduy.ophuot.manage_homestay.model;

import com.google.firebase.database.DatabaseReference;

/**
 * Created by buivu on 15/03/2017.
 */

public class ManageHomestaySubmitter {
    private DatabaseReference mDatabase;

    public ManageHomestaySubmitter(DatabaseReference mDatabase) {
        this.mDatabase = mDatabase;
    }
}
