package com.thanhduy.ophuot.create_homestay.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.codetroopers.betterpickers.radialtimepicker.RadialTimePickerDialogFragment;
import com.thanhduy.ophuot.R;
import com.thanhduy.ophuot.base.BaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by buivu on 02/03/2017.
 */

public class CreateHomeStayActivityTwo extends BaseActivity implements View.OnClickListener,
        RadialTimePickerDialogFragment.OnTimeSetListener {


    private static final String FRAG_TAG_TIME_PICKER_OPEN = "timePickerDialogFragmentOpen";
    private static final String FRAG_TAG_TIME_PICKER_CLOSE = "timePickerDialogFragmentClose";
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.btn_next)
    Button btnNext;
    @BindView(R.id.linear_time_open)
    LinearLayout linearTimeOpen;
    @BindView(R.id.linear_time_close)
    LinearLayout linearTimeClose;
    @BindView(R.id.txt_create_2_time_open)
    TextView txtTimeOpen;
    @BindView(R.id.txt_create_2_time_close)
    TextView txtTimeClose;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_homestay_2);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getResources().getString(R.string.createHomestay));
        //event click
        btnNext.setOnClickListener(this);
        linearTimeOpen.setOnClickListener(this);
        linearTimeClose.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == btnNext) {
            Intent intent = new Intent(CreateHomeStayActivityTwo.this, CreateHomeStayActivityThree.class);
            startActivity(intent);
        } else if (v == linearTimeOpen) {
            RadialTimePickerDialogFragment rtpd = new RadialTimePickerDialogFragment()
                    .setOnTimeSetListener(CreateHomeStayActivityTwo.this)
                    .setStartTime(12, 00)
                    .setDoneText(getResources().getString(R.string.ok))
                    .setCancelText(getResources().getString(R.string.cancel));
            rtpd.show(getSupportFragmentManager(), FRAG_TAG_TIME_PICKER_OPEN);
        } else if (v == linearTimeClose) {
            RadialTimePickerDialogFragment rtpd = new RadialTimePickerDialogFragment()
                    .setOnTimeSetListener(CreateHomeStayActivityTwo.this)
                    .setStartTime(12, 00)
                    .setDoneText(getResources().getString(R.string.ok))
                    .setCancelText(getResources().getString(R.string.cancel));
            rtpd.show(getSupportFragmentManager(), FRAG_TAG_TIME_PICKER_CLOSE);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTimeSet(RadialTimePickerDialogFragment dialog, int hourOfDay, int minute) {
        if (dialog.getTag().equals(FRAG_TAG_TIME_PICKER_OPEN)) {
            txtTimeOpen.setText(String.format("%d : %d", hourOfDay, minute));
        }
        else {
            txtTimeClose.setText(String.format("%d : %d", hourOfDay, minute));
        }
    }

    @Override
    public void onResume() {
        // Example of reattaching to the fragment
        super.onResume();
        RadialTimePickerDialogFragment rtpdOpen = (RadialTimePickerDialogFragment) getSupportFragmentManager().findFragmentByTag(FRAG_TAG_TIME_PICKER_OPEN);
        RadialTimePickerDialogFragment rtpdClose = (RadialTimePickerDialogFragment) getSupportFragmentManager().findFragmentByTag(FRAG_TAG_TIME_PICKER_CLOSE);
        if (rtpdOpen != null) {
            rtpdOpen.setOnTimeSetListener(this);
        }
        if (rtpdClose != null){
            rtpdClose.setOnTimeSetListener(this);
        }
    }

}
