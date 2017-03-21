package com.thanhduy.ophuot.manage_homestay.presenter;

import android.app.Activity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.thanhduy.ophuot.manage_homestay.model.ManageHomestaySubmitter;

import java.util.Map;

/**
 * Created by buivu on 15/03/2017.
 */

public class ManageHomestayPresenter {
    private ManageHomestaySubmitter submitter;
    private Activity view;
    private DatabaseReference mDatabase;

    public ManageHomestayPresenter(Activity view) {
        this.view = view;
        mDatabase = FirebaseDatabase.getInstance().getReference();
        submitter = new ManageHomestaySubmitter(mDatabase);
    }

    public void deleteHomestay(String provinceId, String districtId, String homestayId, String uid) {
        submitter.deleteHomestay(provinceId, districtId, homestayId, uid);
    }

    public void editHomestay(String provinceId, String districtId, String homestayId, String name, String description,
                             String price, String type, Map<String, Object> details) {
        submitter.editHomestay(provinceId, districtId, homestayId, name, description, price, type, details);
    }
}
