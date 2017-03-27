package com.thanhduy.ophuot.comment.presenter;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.thanhduy.ophuot.comment.model.CommentSubmitter;
import com.thanhduy.ophuot.comment.view.CommentActivity;
import com.thanhduy.ophuot.model.Homestay;

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
    }

    //get all comment
    public Query getAllComments(Homestay homestay) {
        return submitter.getAllComments(homestay);
    }
}
