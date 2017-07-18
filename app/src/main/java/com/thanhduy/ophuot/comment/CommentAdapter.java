package com.thanhduy.ophuot.comment;

import android.app.Activity;
import android.content.Intent;
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
import com.thanhduy.ophuot.profile.guess_profile.GuessProfileActivitiy;
import com.thanhduy.ophuot.profile.view.ProfileUserActivity;
import com.thanhduy.ophuot.utils.Constants;
import com.thanhduy.ophuot.utils.DateFormatter;
import com.thanhduy.ophuot.utils.ShowSnackbar;

import java.util.HashMap;
import java.util.List;

import static com.thanhduy.ophuot.base.BaseActivity.getUid;

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
        // holder.ratingBar.setRating((int) comment.getRating());
        //load name and avatar
        if (listUserCommented.get(comment.getCommentBy()) != null) {
            final User user = listUserCommented.get(comment.getCommentBy());
            //load info
            holder.txtName.setText(user.getName());
            ImageLoader.getInstance().loadImageAvatar(activity, user.getAvatar(), holder.imgAvatar);
            //event click
            holder.imgAvatar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    eventClickAvatar(activity, user);
                }
            });

        } else {
            mDatabase.child(Constants.USERS).child(comment.getCommentBy()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    try {
                        if (dataSnapshot != null) {
                            final User user = dataSnapshot.getValue(User.class);
                            if (user != null) {
                                //save to hashmap
                                listUserCommented.put(comment.getCommentBy(), user);
                                //load data
                                holder.txtName.setText(user.getName());
                                ImageLoader.getInstance().loadImageAvatar(activity, user.getAvatar(), holder.imgAvatar);
                                //event click
                                holder.imgAvatar.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        eventClickAvatar(activity, user);
                                    }
                                });
                            }
                        }
                    } catch (Exception e) {
                        ShowSnackbar.showSnack(activity, activity.getResources().getString(R.string.error));
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    private void eventClickAvatar(Activity activity, User user) {
        if (user.getUid().equals(getUid())) {
            Intent intent = new Intent(activity, ProfileUserActivity.class);
            intent.putExtra(Constants.USERS, user);
            activity.startActivity(intent);
        } else {
            Intent intent = new Intent(activity, GuessProfileActivitiy.class);
            intent.putExtra(Constants.USERS, user);
            activity.startActivity(intent);
        }
    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }
}
