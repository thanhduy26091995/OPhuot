package com.thanhduy.ophuot.manage_homestay;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.thanhduy.ophuot.R;
import com.thanhduy.ophuot.base.ImageLoader;
import com.thanhduy.ophuot.model.Rating;
import com.thanhduy.ophuot.model.User;
import com.thanhduy.ophuot.utils.Constants;

import java.util.List;

/**
 * Created by buivu on 30/05/2017.
 */

public class ListRatingAdapter extends RecyclerView.Adapter<ListRatingAdapter.ListRatingViewHolder> {

    private Activity activity;
    private List<Rating> ratingList;
    private DatabaseReference mDatabase;

    public ListRatingAdapter(Activity activity, List<Rating> ratingList) {
        this.activity = activity;
        this.ratingList = ratingList;
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    public ListRatingViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = View.inflate(activity, R.layout.item_see_rating, null);
        return new ListRatingViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ListRatingViewHolder holder, int position) {
        final Rating rating = ratingList.get(position);
        mDatabase.child(Constants.USERS).child(rating.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    User user = dataSnapshot.getValue(User.class);
                    if (user != null) {
                        holder.txtName.setText(user.getName());
                        ImageLoader.getInstance().loadImageAvatar(activity, user.getAvatar(), holder.imgAvatar);
                        holder.ratingBar.setRating((float) rating.getRating());
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return ratingList.size();
    }


    public class ListRatingViewHolder extends RecyclerView.ViewHolder {
        public ImageView imgAvatar;
        public TextView txtName;
        public RatingBar ratingBar;

        public ListRatingViewHolder(View itemView) {
            super(itemView);

            imgAvatar = (ImageView) itemView.findViewById(R.id.img_avatar);
            txtName = (TextView) itemView.findViewById(R.id.txt_rating_name);
            ratingBar = (RatingBar) itemView.findViewById(R.id.rating);
        }
    }
}
