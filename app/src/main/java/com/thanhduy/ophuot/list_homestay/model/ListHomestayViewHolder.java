package com.thanhduy.ophuot.list_homestay.model;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.thanhduy.ophuot.R;

/**
 * Created by buivu on 19/03/2017.
 */

public class ListHomestayViewHolder extends RecyclerView.ViewHolder {

    public TextView txtName, txtAddress, txtType, txtPrice;
    public RatingBar rating;
    public ImageView imgPoster, imgFavorite;

    public ListHomestayViewHolder(View itemView) {
        super(itemView);

        txtName = (TextView) itemView.findViewById(R.id.txt_homestay_name);
        txtAddress = (TextView) itemView.findViewById(R.id.txt_homestay_address);
        txtType = (TextView) itemView.findViewById(R.id.txt_homestay_type);
        txtPrice = (TextView) itemView.findViewById(R.id.txt_homestay_price);
        rating = (RatingBar) itemView.findViewById(R.id.homestay_rating);
        imgPoster = (ImageView) itemView.findViewById(R.id.img_homestay_poster);
        imgFavorite = (ImageView) itemView.findViewById(R.id.img_favorite);
    }
}
