package com.thanhduy.ophuot.comment;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.thanhduy.ophuot.R;
import com.thanhduy.ophuot.base.ImageLoader;
import com.thanhduy.ophuot.comment.model.CommentViewHolder;
import com.thanhduy.ophuot.model.Comment;
import com.thanhduy.ophuot.model.User;
import com.thanhduy.ophuot.utils.Constants;
import com.thanhduy.ophuot.utils.DateFormatter;

import java.util.HashMap;
import java.util.List;

/**
 * Created by buivu on 22/03/2017.
 */

public class CommentAdapter extends RecyclerView.Adapter<CommentViewHolder> {

    public Activity activity;
    public List<Comment> commentList;
    private HashMap<String, User> listUserCommented;
    private DatabaseReference mDatabase;

    public CommentAdapter(Activity activity, List<Comment> commentList) {
        this.activity = activity;
        this.commentList = commentList;
        listUserCommented = new HashMap<>();
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    public CommentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rootView = View.inflate(activity, R.layout.item_comment, null);
        return new CommentViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(final CommentViewHolder holder, int position) {
        final Comment comment = commentList.get(position);
        //load data
        holder.txtContent.setText(comment.getContent());
        holder.txtTime.setText(DateFormatter.formatDateByYMD(comment.getCommentTime()));
        holder.ratingBar.setRating((int) comment.getRating());
        //load name and avatar
        if (listUserCommented.get(comment.getCommentBy()) != null) {
            User user = listUserCommented.get(comment.getCommentBy());
            //load info
            holder.txtName.setText(user.getName());
            ImageLoader.getInstance().loadImageAvatar(activity, user.getAvatar(), holder.imgAvatar);
        } else {
            mDatabase.child(Constants.USERS).child(comment.getCommentBy()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot != null) {
                        User user = dataSnapshot.getValue(User.class);
                        if (user != null) {
                            //save to hashmap
                            listUserCommented.put(comment.getCommentBy(), user);
                            //load data
                            holder.txtName.setText(user.getName());
                            ImageLoader.getInstance().loadImageAvatar(activity, user.getAvatar(), holder.imgAvatar);
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }
}
