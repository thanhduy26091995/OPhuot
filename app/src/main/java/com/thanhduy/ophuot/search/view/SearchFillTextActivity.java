package com.thanhduy.ophuot.search.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;

import com.thanhduy.ophuot.R;
import com.thanhduy.ophuot.base.BaseActivity;

/**
 * Created by buivu on 20/05/2017.
 */

public class SearchFillTextActivity extends BaseActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_fill_text);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search_view_item, menu);
        MenuItem menuItemSearch = menu.findItem(R.id.action_search);
        final SearchView searchViewActionBar = (SearchView) MenuItemCompat.getActionView(menuItemSearch);
        searchViewActionBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchViewActionBar.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }
}
