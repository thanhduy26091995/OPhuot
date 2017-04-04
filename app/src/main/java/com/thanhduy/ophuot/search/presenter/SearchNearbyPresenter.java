package com.thanhduy.ophuot.search.presenter;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.thanhduy.ophuot.search.view.SearchNearByResultActivity;
import com.thanhduy.ophuot.utils.Constants;

/**
 * Created by buivu on 04/04/2017.
 */

public class SearchNearbyPresenter {
    private SearchNearByResultActivity view;
    private DatabaseReference mDatabase;

    public SearchNearbyPresenter(SearchNearByResultActivity view) {
        this.view = view;
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    public Query searchHomestayNearby(int provinceId) {
        return mDatabase.child(Constants.HOMESTAY).child(String.valueOf(provinceId));
    }
}
