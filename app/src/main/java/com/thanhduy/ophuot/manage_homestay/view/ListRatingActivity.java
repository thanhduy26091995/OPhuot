package com.thanhduy.ophuot.manage_homestay.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.thanhduy.ophuot.R;
import com.thanhduy.ophuot.base.BaseActivity;
import com.thanhduy.ophuot.manage_homestay.ListRatingAdapter;
import com.thanhduy.ophuot.model.Homestay;
import com.thanhduy.ophuot.model.Rating;
import com.thanhduy.ophuot.utils.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by buivu on 30/05/2017.
 */

public class ListRatingActivity extends BaseActivity {
    private RecyclerView mRecycler;
    private ListRatingAdapter listRatingAdapter;
    private List<Rating> ratingList = new ArrayList<>();
    private Toolbar toolbar;
    private Homestay homestay;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_rating);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(getResources().getString(R.string.listRating));
        //get intent
        homestay = (Homestay) getIntent().getSerializableExtra(Constants.HOMESTAY);
        mRecycler = (RecyclerView) findViewById(R.id.recycler_rating);
        loadData();
        mRecycler.setLayoutManager(new LinearLayoutManager(this));
        mRecycler.setAdapter(listRatingAdapter);
        listRatingAdapter.notifyDataSetChanged();
    }

    private void loadData() {
        if (homestay.getRatingBy() != null) {
            for (Map.Entry<String, Object> entry : homestay.getRatingBy().entrySet()) {
                Rating rating = new Rating();
                String id = entry.getKey();
                Long rate = (long) entry.getValue();
                //save rating
                rating.setId(id);
                rating.setRating(rate);
                ratingList.add(rating);
            }
            listRatingAdapter = new ListRatingAdapter(ListRatingActivity.this, ratingList);
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
