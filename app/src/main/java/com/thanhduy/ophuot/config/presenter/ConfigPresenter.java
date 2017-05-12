package com.thanhduy.ophuot.config.presenter;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.thanhduy.ophuot.config.view.ConfigFragment;
import com.thanhduy.ophuot.utils.Constants;

/**
 * Created by buivu on 12/05/2017.
 */

public class ConfigPresenter {
    private DatabaseReference mDatabase;
    private ConfigFragment view;

    public ConfigPresenter(ConfigFragment view) {
        this.view = view;
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    public void updateNotiComment(String uid, String homestayId, boolean state) {
        mDatabase.child(Constants.USERS).child(uid).child(Constants.NOTI_COMMENT).child(homestayId).setValue(state);
    }

    public Query getAllNoti(String uid){
        return mDatabase.child(Constants.USERS).child(uid).child(Constants.NOTI_COMMENT);
    }
}
