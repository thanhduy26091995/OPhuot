package com.thanhduy.ophuot.profile.edit_profile.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.thanhduy.ophuot.R;
import com.thanhduy.ophuot.base.BaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by buivu on 01/03/2017.
 */

public class EditProfileActivity extends BaseActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        ButterKnife.bind(this);
        //show button back, button confirm
        setSupportActionBar(toolbar);
        //show back button
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_confirm, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            finish();
        } else if (item.getItemId() == R.id.action_confirm) {

        }
        return super.onOptionsItemSelected(item);
    }
}
