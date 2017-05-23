package com.thanhduy.ophuot.featured;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.thanhduy.ophuot.R;
import com.thanhduy.ophuot.featured.model.Featured;
import com.thanhduy.ophuot.featured.model.FeaturedViewHolder;
import com.thanhduy.ophuot.list_homestay.view.ListHomestayActivity;
import com.thanhduy.ophuot.utils.Constants;

import java.util.List;

/**
 * Created by buivu on 18/03/2017.
 */

public class FeaturedAdapter extends RecyclerView.Adapter<FeaturedViewHolder> {

    private List<Featured> featuredList;
    private Activity activity;

    public FeaturedAdapter(Activity activity, List<Featured> featuredList) {
        this.activity = activity;
        this.featuredList = featuredList;
    }

    @Override
    public FeaturedViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rootView = View.inflate(activity, R.layout.item_featured, null);
        return new FeaturedViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(FeaturedViewHolder holder, int position) {
        final Featured featured = featuredList.get(position);
        Glide.with(activity)
                .load(featured.getImageId())
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.no_image)
                .into(holder.imgPoster);
        holder.txtPoster.setText(featured.getTitle());
        //event click
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String provinceId = "";
                provinceId = featured.getProvinceId();
                Intent intent = new Intent(activity, ListHomestayActivity.class);
                intent.putExtra(Constants.ID_PROVINCE, featured.getProvinceId());
                intent.putExtra(Constants.IS_HAS_DISTRICT, true);
                if (provinceId.equals("4") || provinceId.equals("2") || provinceId.equals("1") || provinceId.equals("17")) {
                    intent.putExtra(Constants.IS_HAS_DISTRICT, false);
                }
                intent.putExtra(Constants.ID_DISTRICT, featured.getDistrictId());
                intent.putExtra(Constants.ADDRESS, featured.getTitle());
                //start activity
                activity.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return featuredList.size();
    }
}
