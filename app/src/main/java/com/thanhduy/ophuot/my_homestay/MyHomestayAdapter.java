package com.thanhduy.ophuot.my_homestay;

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
import com.thanhduy.ophuot.manage_homestay.view.ManageHomestayActivity;
import com.thanhduy.ophuot.model.Homestay;
import com.thanhduy.ophuot.model.MyPost;
import com.thanhduy.ophuot.my_homestay.model.MyHomestayViewHolder;
import com.thanhduy.ophuot.utils.Constants;

import java.util.List;

/**
 * Created by buivu on 09/03/2017.
 */

public class MyHomestayAdapter extends RecyclerView.Adapter<MyHomestayViewHolder> {
    private List<MyPost> myPosts;
    private Activity activity;
    private DatabaseReference mDatabase;

    public MyHomestayAdapter(List<MyPost> myPosts, Activity activity) {
        this.myPosts = myPosts;
        this.activity = activity;
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    public MyHomestayViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rootView = View.inflate(activity, R.layout.item_my_homestay, null);
        return new MyHomestayViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(final MyHomestayViewHolder holder, int position) {
        final MyPost myPost = myPosts.get(position);
        mDatabase.child(Constants.HOMESTAY).child(String.valueOf(myPost.getProvinceId())).child(String.valueOf(myPost.getDistrictId()))
                .child(myPost.getHomestayId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    final Homestay homestay = dataSnapshot.getValue(Homestay.class);
                    if (homestay != null) {
                        //load data
                        holder.txtName.setText(homestay.getName());
                        holder.txtAddress.setText(homestay.getAddress().get(Constants.ADDRESS).toString());
                        holder.txtType.setText(homestay.getType());
                        holder.txtPrice.setText(homestay.getPrice());
                        ImageLoader.getInstance().loadImageOther(activity, homestay.getImages().get(0), holder.imgPoster);
                        //event click
                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(activity, ManageHomestayActivity.class);
                                intent.putExtra(Constants.HOMESTAY, homestay);
                                activity.startActivity(intent);
                            }
                        });
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
        return myPosts.size();
    }

}
