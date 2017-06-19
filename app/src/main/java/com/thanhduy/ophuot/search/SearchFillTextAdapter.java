package com.thanhduy.ophuot.search;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.thanhduy.ophuot.R;
import com.thanhduy.ophuot.database.model.SearchResult;
import com.thanhduy.ophuot.search.view.SearchByProvinceAndDistrictActivity;
import com.thanhduy.ophuot.utils.Constants;

import java.util.List;

/**
 * Created by buivu on 21/05/2017.
 */

public class SearchFillTextAdapter extends RecyclerView.Adapter<SearchFillTextAdapter.SearchFillTextViewHolder> {

    public Activity activity;
    public List<SearchResult> searchResults;

    public SearchFillTextAdapter(Activity activity, List<SearchResult> searchResults) {
        this.activity = activity;
        this.searchResults = searchResults;
    }

    @Override
    public SearchFillTextViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = View.inflate(activity, R.layout.item_search_fill_text, null);
        return new SearchFillTextViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(SearchFillTextViewHolder holder, int position) {
        final SearchResult searchResult = searchResults.get(position);
        holder.txtName.setText(searchResult.getName());
        //event click item
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, SearchByProvinceAndDistrictActivity.class);
                intent.putExtra(Constants.ID_PROVINCE, searchResult.getProvinceId());
                intent.putExtra(Constants.ID_DISTRICT, searchResult.getDistrictId());
                intent.putExtra(Constants.NAME, searchResult.getName());
                activity.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return searchResults.size();
    }

    public class SearchFillTextViewHolder extends RecyclerView.ViewHolder {

        public TextView txtName;

        public SearchFillTextViewHolder(View itemView) {
            super(itemView);

            txtName = (TextView) itemView.findViewById(R.id.txt_search_name);
        }
    }
}
