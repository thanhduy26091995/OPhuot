package com.thanhduy.ophuot.list_homestay;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.thanhduy.ophuot.R;
import com.thanhduy.ophuot.base.ImageLoader;
import com.thanhduy.ophuot.homestay_detail.view.ActivityHomestayDetail;
import com.thanhduy.ophuot.list_homestay.model.ListHomestayViewHolder;
import com.thanhduy.ophuot.model.Homestay;
import com.thanhduy.ophuot.model.User;
import com.thanhduy.ophuot.utils.Constants;

import java.util.List;

/**
 * Created by buivu on 19/03/2017.
 */

public class ListHomestayAdapter extends RecyclerView.Adapter<ListHomestayViewHolder> implements GetUserInfoCallback {

    private Activity activity;
    private List<Homestay> homestayList;
    private DatabaseReference mDatabase;
    private User user;
    private boolean isLoadSuccess = false;

    public ListHomestayAdapter(Activity activity, List<Homestay> homestayList) {
        this.activity = activity;
        this.homestayList = homestayList;
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    public ListHomestayViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rootView = View.inflate(activity, R.layout.item_homestay, null);
        return new ListHomestayViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(ListHomestayViewHolder holder, int position) {
        final Homestay homestay = homestayList.get(position);
        holder.txtName.setText(homestay.getName());
        holder.txtAddress.setText(homestay.getAddress().get(Constants.ADDRESS).toString());
        holder.txtType.setText(homestay.getType());
        holder.txtPrice.setText(homestay.getPrice());
        ImageLoader.getInstance().loadImageOther(activity, homestay.getImages().get(0), holder.imgPoster);
        mDatabase.child(Constants.USERS).child(homestay.getPostBy()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    User user = dataSnapshot.getValue(User.class);
                    if (user != null) {
                        getUser(user);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, ActivityHomestayDetail.class);
                intent.putExtra(Constants.HOMESTAY, homestay);
                if (user != null) {
                    intent.putExtra(Constants.IS_LOAD_SUCCESS, isLoadSuccess);
                    intent.putExtra(Constants.USERS, user);
                }
                activity.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return homestayList.size();
    }

    @Override
    public void getUser(User user) {
        this.user = user;
        isLoadSuccess = true;
    }
}
