package com.thanhduy.ophuot.favorite.presenter;

import android.app.Activity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.thanhduy.ophuot.favorite.model.FavoriteSubmitter;
import com.thanhduy.ophuot.model.PostInfo;

/**
 * Created by buivu on 23/03/2017.
 */

public class FavoritePresenter {
    private Activity activity;
    private FavoriteSubmitter submitter;
    private DatabaseReference mDatabase;

    public FavoritePresenter(Activity activity) {
        this.activity = activity;
        mDatabase = FirebaseDatabase.getInstance().getReference();
        submitter = new FavoriteSubmitter(mDatabase);
    }



    //add member's favorite
    public void addDataToFavoriteHomestay(PostInfo postInfo, String uid) {
        submitter.addDataToFavoriteHomestay(postInfo, uid);
    }

    public Query getAllFavoriteHomestay(String uid) {
        return submitter.getAllFavoriteHomestay(uid);
    }

}
