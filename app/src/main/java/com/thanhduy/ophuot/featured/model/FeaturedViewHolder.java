package com.thanhduy.ophuot.featured.model;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.thanhduy.ophuot.R;

/**
 * Created by buivu on 18/03/2017.
 */

public class FeaturedViewHolder extends RecyclerView.ViewHolder {

    public ImageView imgPoster;
    public TextView txtPoster;

    public FeaturedViewHolder(View itemView) {
        super(itemView);

        imgPoster = (ImageView) itemView.findViewById(R.id.img_poster_featured);
        txtPoster = (TextView) itemView.findViewById(R.id.txt_poster_featured);
    }
}
