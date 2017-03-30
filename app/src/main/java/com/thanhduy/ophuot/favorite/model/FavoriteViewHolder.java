package com.thanhduy.ophuot.favorite.model;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.thanhduy.ophuot.R;

/**
 * Created by buivu on 27/03/2017.
 */

public class FavoriteViewHolder extends RecyclerView.ViewHolder {

    public ImageView imgPoster;
    public TextView txtFavoriteName, txtFavoriteNumber;

    public FavoriteViewHolder(View itemView) {
        super(itemView);
        imgPoster = (ImageView) itemView.findViewById(R.id.img_homestay_poster);
        txtFavoriteName = (TextView) itemView.findViewById(R.id.txt_favorite_name);
        txtFavoriteNumber = (TextView) itemView.findViewById(R.id.txt_number_favorite);
    }
}
