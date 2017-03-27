package com.thanhduy.ophuot.list_homestay;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.thanhduy.ophuot.R;
import com.thanhduy.ophuot.base.BaseActivity;
import com.thanhduy.ophuot.base.ImageLoader;
import com.thanhduy.ophuot.fragment.BottomDialogFragment;
import com.thanhduy.ophuot.fragment.PositionCallback;
import com.thanhduy.ophuot.homestay_detail.view.ActivityHomestayDetail;
import com.thanhduy.ophuot.list_homestay.model.ListHomestayViewHolder;
import com.thanhduy.ophuot.model.Homestay;
import com.thanhduy.ophuot.model.PostInfo;
import com.thanhduy.ophuot.model.User;
import com.thanhduy.ophuot.utils.Constants;
import com.thanhduy.ophuot.utils.ShowSnackbar;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by buivu on 19/03/2017.
 */

public class ListHomestayAdapter extends RecyclerView.Adapter<ListHomestayViewHolder> implements GetUserInfoCallback, PositionCallback {

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
    public void onBindViewHolder(final ListHomestayViewHolder holder, final int position) {
        final Homestay homestay = homestayList.get(position);
        holder.txtName.setText(homestay.getName());
        holder.txtAddress.setText(homestay.getAddress().get(Constants.ADDRESS).toString());
        holder.txtType.setText(homestay.getType());
        holder.txtPrice.setText(homestay.getPrice());
        ImageLoader.getInstance().loadImageOther(activity, homestay.getImages().get(0), holder.imgPoster);
        //check if current user favorite this homestay, if it has, use heart with red color
        if (homestay.getFavorite() != null) {
            if (homestay.getFavorite().containsKey(BaseActivity.getUid())) {
                holder.imgFavorite.setImageResource(R.drawable.ic_favorite_black_24dp);
            } else {
                holder.imgFavorite.setImageResource(R.drawable.ic_favorite_border_black_24dp);
            }
        } else {
            holder.imgFavorite.setImageResource(R.drawable.ic_favorite_border_black_24dp);
        }
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

        //event click icon favorite
        holder.imgFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PostInfo postInfo = new PostInfo(homestay.getDistrictId(), homestay.getProvinceId(), homestay.getId());
                if (homestay.getFavorite() != null) {
                    if (homestay.getFavorite().containsKey(BaseActivity.getUid())) {
                        deleteFavoriteHomstay(postInfo, BaseActivity.getUid());
                        homestay.getFavorite().remove(BaseActivity.getUid());
                        //set icon
                        holder.imgFavorite.setImageResource(R.drawable.ic_favorite_border_black_24dp);
                        //show snackbar
                        ShowSnackbar.showSnack(activity, String.format("%s %s %s", activity.getResources().getString(R.string.deleteFavorite),
                                homestay.getName(), activity.getResources().getString(R.string.outOfFavorite)));
                    } else {
                        BottomDialogFragment bottomDialogFragment = new BottomDialogFragment();
                        bottomDialogFragment.setPositionCallback(new PositionCallback() {
                            @Override
                            public void positionCallBack(int position) {
                                Homestay homestay = homestayList.get(position);
                                homestay.getFavorite().put(BaseActivity.getUid(), true);
                                notifyDataSetChanged();
                            }
                        });
                        Bundle dataMove = new Bundle();
                        dataMove.putSerializable(Constants.POST_INFO, postInfo);
                        dataMove.putSerializable(Constants.POSITION, position);
                        bottomDialogFragment.setArguments(dataMove);
                        bottomDialogFragment.show(((AppCompatActivity) activity).getSupportFragmentManager(), bottomDialogFragment.getTag());
                    }
                } else {
                    BottomDialogFragment bottomDialogFragment = new BottomDialogFragment();
                    bottomDialogFragment.setPositionCallback(new PositionCallback() {
                        @Override
                        public void positionCallBack(int position) {
                            Homestay homestay = homestayList.get(position);
                            Map<String, Boolean> data = new HashMap<>();
                            data.put(BaseActivity.getUid(), true);
                            homestay.setFavorite(data);
                            homestay.getFavorite().put(BaseActivity.getUid(), true);
                            notifyDataSetChanged();
                        }
                    });
                    Bundle dataMove = new Bundle();
                    dataMove.putSerializable(Constants.POST_INFO, postInfo);
                    dataMove.putSerializable(Constants.POSITION, position);
                    bottomDialogFragment.setArguments(dataMove);
                    bottomDialogFragment.show(((AppCompatActivity) activity).getSupportFragmentManager(), bottomDialogFragment.getTag());
                }
                //refresh data
                notifyDataSetChanged();


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

    private void addDataToFavoriteHomestay(PostInfo postInfo, String uid) {
        Map<String, Object> data = new HashMap<>();
        data.put(Constants.ID_DISTRICT, postInfo.getDistrictId());
        data.put(Constants.ID_PROVINCE, postInfo.getProvinceId());
        data.put(Constants.HOMESTAY_ID, postInfo.getHomestayId());
        //update data
        mDatabase.child(Constants.USERS).child(uid).child(Constants.FAVORITE).child(postInfo.getHomestayId()).updateChildren(data);
        //add data into homestay favorite
        Map<String, Object> dataFavoriteHomestay = new HashMap<>();
        dataFavoriteHomestay.put(uid, true);
        mDatabase.child(Constants.HOMESTAY).child(String.valueOf(postInfo.getProvinceId())).child(String.valueOf(postInfo.getDistrictId()))
                .child(postInfo.getHomestayId()).child(Constants.FAVORITE).updateChildren(dataFavoriteHomestay);
    }

    private void deleteFavoriteHomstay(final PostInfo postInfo, final String uid) {
        // mDatabase.child(Constants.USERS).child(uid).child(Constants.FAVORITE).child(postInfo.getHomestayId()).removeValue();
        mDatabase.child(Constants.HOMESTAY).child(String.valueOf(postInfo.getProvinceId())).child(String.valueOf(postInfo.getDistrictId()))
                .child(postInfo.getHomestayId()).child(Constants.FAVORITE).child(uid).removeValue();
        mDatabase.child(Constants.USERS).child(uid).child(Constants.FAVORITE).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    for (DataSnapshot snapshotListFavorite : dataSnapshot.getChildren()) {
                        for (DataSnapshot snapshotListHomestay : snapshotListFavorite.child(Constants.LIST_HOMESTAY).getChildren()) {
                            if (snapshotListHomestay.getKey().equals(postInfo.getHomestayId())) {
                                mDatabase.child(Constants.USERS).child(uid).child(Constants.FAVORITE).child(snapshotListFavorite.getKey())
                                        .child(Constants.LIST_HOMESTAY).child(snapshotListHomestay.getKey()).removeValue();
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void positionCallBack(int position) {
        Homestay homestay = homestayList.get(position);
        homestay.getFavorite().put(BaseActivity.getUid(), true);
        notifyDataSetChanged();
    }
}
