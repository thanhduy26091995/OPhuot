package com.thanhduy.ophuot.comment.model;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.thanhduy.ophuot.R;

/**
 * Created by buivu on 22/03/2017.
 */

public class CommentViewHolder extends RecyclerView.ViewHolder {

    public ImageView imgAvatar;
    public TextView txtName, txtTime, txtContent;
    public RatingBar ratingBar;

    public CommentViewHolder(View itemView) {
        super(itemView);

        imgAvatar = (ImageView) itemView.findViewById(R.id.img_comment_avatar);
        txtName = (TextView) itemView.findViewById(R.id.txt_comment_name);
        txtTime = (TextView) itemView.findViewById(R.id.txt_comment_time);
        txtContent = (TextView) itemView.findViewById(R.id.txt_comment_content);
        ratingBar = (RatingBar) itemView.findViewById(R.id.rating_comment);
    }
}
