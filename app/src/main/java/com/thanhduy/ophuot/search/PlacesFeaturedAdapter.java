package com.thanhduy.ophuot.search;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.thanhduy.ophuot.R;
import com.thanhduy.ophuot.featured.model.Featured;
import com.thanhduy.ophuot.list_homestay.view.ListHomestayActivity;
import com.thanhduy.ophuot.utils.Constants;

import java.util.List;

/**
 * Created by buivu on 06/04/2017.
 */

public class PlacesFeaturedAdapter extends RecyclerView.Adapter<PlacesFeaturedAdapter.PlacesFeaturedViewHolder> {

    private List<Featured> featuredList;
    private Activity activity;

    public PlacesFeaturedAdapter(List<Featured> featuredList, Activity activity) {
        this.featuredList = featuredList;
        this.activity = activity;
    }

    @Override
    public PlacesFeaturedViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rootView = View.inflate(activity, R.layout.item_featured_places_in_search, null);
        return new PlacesFeaturedViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(PlacesFeaturedViewHolder holder, int position) {
        final Featured featured = featuredList.get(position);
        holder.txtPlaces.setText(featured.getTitle());
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

    public class PlacesFeaturedViewHolder extends RecyclerView.ViewHolder {
        public TextView txtPlaces;

        public PlacesFeaturedViewHolder(View itemView) {
            super(itemView);
            txtPlaces = (TextView) itemView.findViewById(R.id.txt_places);
        }
    }
}
