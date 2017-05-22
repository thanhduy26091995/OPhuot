package com.thanhduy.ophuot.search;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.thanhduy.ophuot.R;
import com.thanhduy.ophuot.database.model.Province;

import java.util.List;

/**
 * Created by buivu on 21/05/2017.
 */

public class SearchFillTextAdapter extends RecyclerView.Adapter<SearchFillTextAdapter.SearchFillTextViewHolder> {

    public Activity activity;
    public List<Province> provinceList;

    public SearchFillTextAdapter(Activity activity, List<Province> provinceList) {
        this.activity = activity;
        this.provinceList = provinceList;
    }

    @Override
    public SearchFillTextViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = View.inflate(activity, R.layout.item_search_fill_text, null);
        return new SearchFillTextViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(SearchFillTextViewHolder holder, int position) {
        Province province = provinceList.get(position);
        holder.txtName.setText(province.getProvinceName());
    }

    @Override
    public int getItemCount() {
        return provinceList.size();
    }

    public class SearchFillTextViewHolder extends RecyclerView.ViewHolder {

        public TextView txtName;

        public SearchFillTextViewHolder(View itemView) {
            super(itemView);

            txtName = (TextView) itemView.findViewById(R.id.txt_search_name);
        }
    }
}
