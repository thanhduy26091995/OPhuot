package com.thanhduy.ophuot.my_homestay.presenter;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.thanhduy.ophuot.my_homestay.model.MyHomestaySubmitter;
import com.thanhduy.ophuot.my_homestay.view.MyHomestayFragment;

/**
 * Created by buivu on 09/03/2017.
 */

public class MyHomestayPresenter {
    private MyHomestaySubmitter submitter;
    private MyHomestayFragment view;
    private DatabaseReference mDatabase;

    public MyHomestayPresenter(MyHomestayFragment view) {
        this.view = view;
        mDatabase = FirebaseDatabase.getInstance().getReference();
        submitter = new MyHomestaySubmitter(mDatabase);
    }

    public Query getAllPost(String uid) {
        return submitter.getAllPost(uid);
    }
}
