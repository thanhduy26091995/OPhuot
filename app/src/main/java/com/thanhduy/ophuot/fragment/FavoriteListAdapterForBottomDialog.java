package com.thanhduy.ophuot.fragment;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.thanhduy.ophuot.R;
import com.thanhduy.ophuot.base.BaseActivity;
import com.thanhduy.ophuot.base.ImageLoader;
import com.thanhduy.ophuot.model.FavoriteInfo;
import com.thanhduy.ophuot.model.Homestay;
import com.thanhduy.ophuot.model.PostInfo;
import com.thanhduy.ophuot.utils.Constants;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by buivu on 28/03/2017.
 * truyền data giữa 2 adapter
 */

public class FavoriteListAdapterForBottomDialog extends RecyclerView.Adapter<FavoriteListAdapterForBottomDialog.FavoriteViewHolderForBottomDialog> {

    private Activity activity;
    private List<String> listFavoriteId;
    private DatabaseReference mDatabase;
    private PostInfo postInfoFromFragment;
    private int positionItem;
    private PositionCallback positionCallback;

    public FavoriteListAdapterForBottomDialog(PositionCallback positionCallback) {
        this.positionCallback = positionCallback;
    }

    public FavoriteListAdapterForBottomDialog(Activity activity, List<String> listFavoriteId, PostInfo postInfoFromFragment, int positionItem, PositionCallback positionCallback) {
        this.activity = activity;
        this.listFavoriteId = listFavoriteId;
        this.postInfoFromFragment = postInfoFromFragment;
        this.positionItem = positionItem;
        this.positionCallback = positionCallback;
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    public void setPositionCallback(PositionCallback positionCallback) {
        this.positionCallback = positionCallback;
    }

    @Override
    public FavoriteViewHolderForBottomDialog onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = View.inflate(activity, R.layout.item_save_into_favorite, null);
        return new FavoriteViewHolderForBottomDialog(view);
    }

    @Override
    public void onBindViewHolder(final FavoriteViewHolderForBottomDialog holder, final int position) {
        final String favoriteId = listFavoriteId.get(position);
        mDatabase.child(Constants.USERS).child(BaseActivity.getUid()).child(Constants.FAVORITE).child(favoriteId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    if (dataSnapshot != null) {
                        final FavoriteInfo favoriteInfo = dataSnapshot.child(Constants.INFO).getValue(FavoriteInfo.class);
                        if (favoriteInfo != null) {
                            holder.txtFavoriteName.setText(favoriteInfo.getFavoriteName());
                            holder.txtFavoriteNumber.setText(String.format("%d %s", dataSnapshot.child(Constants.LIST_HOMESTAY).getChildrenCount(), activity.getResources().getString(R.string.post)));
                        }
                        for (DataSnapshot listHomestay : dataSnapshot.child(Constants.LIST_HOMESTAY).getChildren()) {
                            PostInfo postInfo = listHomestay.getValue(PostInfo.class);
                            if (postInfo != null) {
                                mDatabase.child(Constants.HOMESTAY).child(String.valueOf(postInfo.getProvinceId())).child(String.valueOf(postInfo.getDistrictId()))
                                        .child(postInfo.getHomestayId()).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if (dataSnapshot != null) {
                                            Homestay homestay = dataSnapshot.getValue(Homestay.class);
                                            if (homestay != null) {
                                                ImageLoader.getInstance().loadImageOther(activity, homestay.getImages().get(0), holder.imgFavoriteImage);
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                            }
                            break;
                        }
                        //event click item
                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                addDataToFavoriteHomestay(postInfoFromFragment, BaseActivity.getUid(), favoriteId);
                                positionCallback.positionCallBack(positionItem);
                            }
                        });
                    }
                } catch (Exception e) {

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }


    @Override
    public int getItemCount() {
        return listFavoriteId.size();
    }

    public class FavoriteViewHolderForBottomDialog extends RecyclerView.ViewHolder {
        public TextView txtFavoriteName, txtFavoriteNumber;
        public ImageView imgFavoriteImage;

        public FavoriteViewHolderForBottomDialog(View itemView) {
            super(itemView);

            txtFavoriteName = (TextView) itemView.findViewById(R.id.txt_favorite_name);
            txtFavoriteNumber = (TextView) itemView.findViewById(R.id.txt_favorite_number);
            imgFavoriteImage = (ImageView) itemView.findViewById(R.id.img_favorite_image);
        }
    }

    private void addDataToFavoriteHomestay(PostInfo postInfo, String userId, final String favoriteId) {
        try {
            /*
        save user node
         */
            //init data favorite
            Map<String, Object> data = new HashMap<>();
            data.put(Constants.ID_DISTRICT, postInfo.getDistrictId());
            data.put(Constants.ID_PROVINCE, postInfo.getProvinceId());
            data.put(Constants.HOMESTAY_ID, postInfo.getHomestayId());
            //save homestay to list homestay favorite (yêu thích cảu tôi)
            mDatabase.child(Constants.USERS).child(userId).child(Constants.FAVORITE).child(favoriteId).child(Constants.LIST_HOMESTAY).child(postInfo.getHomestayId()).updateChildren(data);
        /*
            save homestay node
         */
            //add data into homestay favorite (save homestay node)
            Map<String, Object> dataFavoriteHomestay = new HashMap<>();
            dataFavoriteHomestay.put(userId, true);
            mDatabase.child(Constants.HOMESTAY).child(String.valueOf(postInfo.getProvinceId())).child(String.valueOf(postInfo.getDistrictId()))
                    .child(postInfo.getHomestayId()).child(Constants.FAVORITE).updateChildren(dataFavoriteHomestay);
            //close dialog fragment
            BottomDialogFragment.instance.dismiss();
        } catch (Exception e) {

        }
    }
}
