package com.thanhduy.ophuot.my_homestay.model;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.thanhduy.ophuot.R;

/**
 * Created by buivu on 09/03/2017.
 */

public class MyHomestayViewHolder extends RecyclerView.ViewHolder {

    public TextView txtName, txtAddress, txtType, txtPrice;
    public RatingBar rating;
    public ImageView imgPoster;
    public LinearLayout linearMore;

    public MyHomestayViewHolder(View itemView) {
        super(itemView);

        txtName = (TextView) itemView.findViewById(R.id.txt_homestay_name);
        txtAddress = (TextView) itemView.findViewById(R.id.txt_homestay_address);
        txtType = (TextView) itemView.findViewById(R.id.txt_homestay_type);
        txtPrice = (TextView) itemView.findViewById(R.id.txt_homestay_price);
        rating = (RatingBar) itemView.findViewById(R.id.homestay_rating);
        imgPoster = (ImageView) itemView.findViewById(R.id.img_homestay_poster);
       // linearMore = (LinearLayout) itemView.findViewById(R.id.linear_more);
    }
}
