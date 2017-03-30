package com.thanhduy.ophuot.favorite;

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
import com.thanhduy.ophuot.base.BaseActivity;
import com.thanhduy.ophuot.base.ImageLoader;
import com.thanhduy.ophuot.favorite.model.FavoriteViewHolder;
import com.thanhduy.ophuot.favorite.view.ListFavoriteActivity;
import com.thanhduy.ophuot.model.FavoriteInfo;
import com.thanhduy.ophuot.model.Homestay;
import com.thanhduy.ophuot.model.PostInfo;
import com.thanhduy.ophuot.utils.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by buivu on 27/03/2017.
 */

public class FavoriteAdapter extends RecyclerView.Adapter<FavoriteViewHolder> {

    private Activity activity;
    private List<String> listFavoriteId;
    private DatabaseReference mDatabase;
    private boolean isLoadFirst = false;
    private ArrayList<PostInfo> postInfos;
    private String favoriteName;

    public FavoriteAdapter(Activity activity, List<String> listFavoriteId) {
        this.activity = activity;
        this.listFavoriteId = listFavoriteId;
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    public FavoriteViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rootView = View.inflate(activity, R.layout.item_favorite, null);
        return new FavoriteViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(final FavoriteViewHolder holder, final int position) {
        final String favoriteId = listFavoriteId.get(position);
        /*
        trỏ tới danh sách tới favorite dựa vào favoriteId
        Sau đó sẽ có dc 2 node đó là Info và Listhomestay
        Với node info thì ta có thể lấy được thông tin về favorite đó
        Với node listhomestay, thì sẽ cho 1 vòng lặp chạy, và lấy dc PostInfo đầu tiên, sau đó truy vấn ngc lại
        node Homestay để lấy thông tin poster đầu tiên trong danh sách yêu thích
         */
        mDatabase.child(Constants.USERS).child(BaseActivity.getUid()).child(Constants.FAVORITE).child(favoriteId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(final DataSnapshot dataSnapshot) {
                        if (dataSnapshot != null) {
                            FavoriteInfo favoriteInfo = dataSnapshot.child(Constants.INFO).getValue(FavoriteInfo.class);
                            if (favoriteInfo != null) {
                                favoriteName = favoriteInfo.getFavoriteName();
                                holder.txtFavoriteName.setText(favoriteInfo.getFavoriteName());
                                holder.txtFavoriteNumber.setText(String.format("%d %s", dataSnapshot.child(Constants.LIST_HOMESTAY).getChildrenCount(), activity.getResources().getString(R.string.post)));
                            }
                            if (dataSnapshot.child(Constants.LIST_HOMESTAY).exists()) {
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
                                                        ImageLoader.getInstance().loadImageOther(activity, homestay.getImages().get(0), holder.imgPoster);
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
                            } else {
                                ImageLoader.getInstance().loadImageOther(activity, "None", holder.imgPoster);
                            }


                            //event click
                            holder.itemView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    postInfos = new ArrayList<>();
                                    //get list post info
                                    for (DataSnapshot listHomestay : dataSnapshot.child(Constants.LIST_HOMESTAY).getChildren()) {
                                        PostInfo postInfo = listHomestay.getValue(PostInfo.class);
                                        // Log.d("POST_ADAPTER", "" + postInfo.getProvinceId() + "/" + postInfo.getDistrictId() + "/" + postInfo.getHomestayId());
                                        postInfos.add(listHomestay.getValue(PostInfo.class));
                                    }
                                    Intent intent = new Intent(activity, ListFavoriteActivity.class);
                                    intent.putExtra(Constants.LIST_POST_INFO, postInfos);
                                    intent.putExtra(Constants.FAVORITE_NAME, favoriteName);
                                    intent.putExtra(Constants.FAVORITE_ID, favoriteId);
                                    activity.startActivity(intent);
                                }
                            });
//
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


}
