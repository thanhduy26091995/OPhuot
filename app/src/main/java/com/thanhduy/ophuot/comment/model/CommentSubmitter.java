package com.thanhduy.ophuot.comment.model;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.thanhduy.ophuot.model.Homestay;
import com.thanhduy.ophuot.utils.Constants;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by buivu on 22/03/2017.
 */

public class CommentSubmitter {
    private DatabaseReference mDatabase;

    public CommentSubmitter(DatabaseReference mDatabase) {
        this.mDatabase = mDatabase;
    }


    //get all comment
    public Query getAllComments(Homestay homestay) {
        return mDatabase.child(Constants.HOMESTAY).child(String.valueOf(homestay.getProvinceId())).child(String.valueOf(homestay.getDistrictId()))
                .child(homestay.getId()).child(Constants.COMMENTS).child(Constants.CONTENTS);
    }

    //add comment to server
    public void addComment(final Homestay homestay, String commentBy, String content, int rating, long commentTime) {
        String key = mDatabase.child(Constants.HOMESTAY).child(String.valueOf(homestay.getProvinceId())).child(String.valueOf(homestay.getDistrictId()))
                .child(homestay.getId()).child(Constants.COMMENTS).child(Constants.CONTENTS).push().getKey();
        //add comment
        Map<String, Object> dataComment = new HashMap<>();
        dataComment.put(Constants.COMMENT_BY, commentBy);
        dataComment.put(Constants.CONTENT, content);
        dataComment.put(Constants.RATING, rating);
        dataComment.put(Constants.COMMENT_TIME, commentTime);
        //save data
        mDatabase.child(Constants.HOMESTAY).child(String.valueOf(homestay.getProvinceId())).child(String.valueOf(homestay.getDistrictId()))
                .child(homestay.getId()).child(Constants.COMMENTS).child(Constants.CONTENTS).child(key).setValue(dataComment);
        //update comment count
        mDatabase.child(Constants.HOMESTAY).child(String.valueOf(homestay.getProvinceId())).child(String.valueOf(homestay.getDistrictId()))
                .child(homestay.getId()).child(Constants.COMMENTS).child(Constants.COMMENT_COUNT).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    updateCommentCount(homestay, 1);
                } else {
                    if (dataSnapshot != null) {
                        Long commentCount = dataSnapshot.getValue(Long.class);
                        updateCommentCount(homestay, commentCount + 1);

                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void updateCommentCount(Homestay homestay, long commentCount) {
        Map<String, Object> dataUpdateCommentCount = new HashMap<>();
        dataUpdateCommentCount.put(Constants.COMMENT_COUNT, commentCount);
        mDatabase.child(Constants.HOMESTAY).child(String.valueOf(homestay.getProvinceId())).child(String.valueOf(homestay.getDistrictId()))
                .child(homestay.getId()).child(Constants.COMMENTS).updateChildren(dataUpdateCommentCount);
    }

    public void updateRating(float rating, Homestay homestay) {
        Map<String, Object> dataRating = new HashMap<>();
        dataRating.put(Constants.RATING, rating);
        mDatabase.child(Constants.HOMESTAY).child(String.valueOf(homestay.getProvinceId())).child(String.valueOf(homestay.getDistrictId()))
                .child(homestay.getId()).updateChildren(dataRating);
    }
}
