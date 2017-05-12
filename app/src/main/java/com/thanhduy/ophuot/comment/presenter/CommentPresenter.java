package com.thanhduy.ophuot.comment.presenter;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.thanhduy.ophuot.comment.model.CommentSubmitter;
import com.thanhduy.ophuot.comment.view.CommentActivity;
import com.thanhduy.ophuot.model.Comment;
import com.thanhduy.ophuot.model.Homestay;
import com.thanhduy.ophuot.utils.Constants;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by buivu on 22/03/2017.
 */

public class CommentPresenter {
    private DatabaseReference mDatabase;
    private CommentSubmitter submitter;
    private CommentActivity view;

    public CommentPresenter(CommentActivity view) {
        this.view = view;
        mDatabase = FirebaseDatabase.getInstance().getReference();
        submitter = new CommentSubmitter(mDatabase);
    }

    public void addComment(final Homestay homestay, String commentBy, String content, int rating, long commentTime) {
        submitter.addComment(homestay, commentBy, content, rating, commentTime);
        //update số lượng comment
        mDatabase.child(Constants.HOMESTAY).child(String.valueOf(homestay.getProvinceId())).child(String.valueOf(homestay.getDistrictId()))
                .child(homestay.getId()).child(Constants.COMMENTS).child(Constants.CONTENTS).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    //tính tổng lượng ratung
                    long sumRating = 0;
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Comment comment = snapshot.getValue(Comment.class);
                        if (comment != null) {
                            sumRating += comment.getRating();
                        }
                    }
                    //tính số lượng comment
                    long sumCommented = dataSnapshot.getChildrenCount();
                    //tính toán trung bình comment
                    Float rating = (float) sumRating / sumCommented;
                    //cập nhật số lượng rating
                    submitter.updateRating(Math.round(rating), homestay);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    //get all comment
    public Query getAllComments(Homestay homestay) {
        return submitter.getAllComments(homestay);
    }

    public void addNotiComment(String uid, String homestayId) {
        Map<String, Object> data = new HashMap<>();
        data.put(homestayId, true);
        mDatabase.child(Constants.USERS).child(uid).child(Constants.NOTI_COMMENT).updateChildren(data);
    }
}
