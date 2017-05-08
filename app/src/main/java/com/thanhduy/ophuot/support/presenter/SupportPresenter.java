package com.thanhduy.ophuot.support.presenter;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.thanhduy.ophuot.support.view.SupportFragment;
import com.thanhduy.ophuot.utils.Constants;

/**
 * Created by buivu on 21/04/2017.
 */

public class SupportPresenter {
    private SupportFragment view;
    private DatabaseReference mDatabase;

    public SupportPresenter(SupportFragment view) {
        this.view = view;
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    public Query getAllSupporter(){
        return mDatabase.child(Constants.SUPPORTERS);
    }

    public Query getSupporterByProvinceId(int provinceId){
        return mDatabase.child(Constants.SUPPORTERS).child(String.valueOf(provinceId));
    }
}
