package com.thanhduy.ophuot.list_homestay.presenter;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.thanhduy.ophuot.list_homestay.model.ListHomestaySubmitter;
import com.thanhduy.ophuot.list_homestay.view.ListHomestayActivity;

/**
 * Created by buivu on 19/03/2017.
 */

public class ListHomestayPresenter {
    private ListHomestaySubmitter submitter;
    private ListHomestayActivity view;
    private DatabaseReference mDatabase;

    public ListHomestayPresenter(ListHomestayActivity view) {
        this.view = view;
        mDatabase = FirebaseDatabase.getInstance().getReference();
        submitter = new ListHomestaySubmitter(mDatabase);
    }

    public Query getListHomestayWithBothProvinceAndDistrict(String provinceId, String districtId) {
        return submitter.getListHomestayWithBothProvinceAndDistrict(provinceId, districtId);
    }

    public Query getListHomestayByProcince(String provinceId){
        return submitter.getListHomestayByProcince(provinceId);
    }
}
