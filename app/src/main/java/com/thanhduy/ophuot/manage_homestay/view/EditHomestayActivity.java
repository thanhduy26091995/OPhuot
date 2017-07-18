package com.thanhduy.ophuot.manage_homestay.view;

import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.codetroopers.betterpickers.radialtimepicker.RadialTimePickerDialogFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.thanhduy.ophuot.R;
import com.thanhduy.ophuot.base.BaseActivity;
import com.thanhduy.ophuot.base.InternetConnection;
import com.thanhduy.ophuot.manage_homestay.presenter.ManageHomestayPresenter;
import com.thanhduy.ophuot.model.Homestay;
import com.thanhduy.ophuot.utils.Constants;
import com.thanhduy.ophuot.utils.ShowAlertDialog;
import com.thanhduy.ophuot.utils.ShowSnackbar;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by buivu on 16/03/2017.
 */

public class EditHomestayActivity extends BaseActivity implements View.OnClickListener, RadialTimePickerDialogFragment.OnTimeSetListener {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.edt_edit_homestay_bathroom)
    EditText edtBathroom;
    @BindView(R.id.edt_edit_homestay_bed)
    EditText edtBed;
    @BindView(R.id.edt_edit_homestay_convenient)
    EditText edtConvenient;
    @BindView(R.id.edt_edit_homestay_des)
    EditText edtDescription;
    @BindView(R.id.edt_edit_homestay_max_passenger)
    EditText edtPassenger;
    @BindView(R.id.edt_edit_homestay_name)
    EditText edtName;
    @BindView(R.id.edt_edit_homestay_number_bedroom)
    EditText edtBedroom;
    @BindView(R.id.edt_edit_homestay_price)
    EditText edtPrice;
    @BindView(R.id.spn_type)
    Spinner spnType;
    @BindView(R.id.txt_edit_homestay_timeOpen)
    TextView txtTimeOpen;
    @BindView(R.id.txt_edit_homestay_timeClose)
    TextView txtTimeClose;
    @BindView(R.id.chk_edit_homestay_animal)
    CheckBox chkAnimal;
    @BindView(R.id.txt_edit_homestay_type)
    TextView txtType;
    @BindView(R.id.linear_edit_homestay_time_open)
    LinearLayout linearTimeOpen;
    @BindView(R.id.linear_edit_homestay_time_close)
    LinearLayout linearTimeClose;

    private Homestay homestay;
    private DatabaseReference mDatabase;
    private ArrayAdapter<String> spinnerTypeAdapter;
    private boolean isSpinnerTouched = false;
    private ManageHomestayPresenter presenter;
    private static final String FRAG_TAG_TIME_PICKER_OPEN = "timePickerDialogFragmentOpen";
    private static final String FRAG_TAG_TIME_PICKER_CLOSE = "timePickerDialogFragmentClose";
    private long timeOpenInMinute = 0, timeCloseInMinute = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_homestay);
        ButterKnife.bind(this);
        presenter = new ManageHomestayPresenter(this);
        mDatabase = FirebaseDatabase.getInstance().getReference();

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getResources().getString(R.string.editProfile));

        //get intent
        homestay = (Homestay) getIntent().getSerializableExtra(Constants.HOMESTAY);
        loadData();
        prepareDataForSpinnerType();
        //event click
        linearTimeOpen.setOnClickListener(this);
        linearTimeClose.setOnClickListener(this);
    }

    private void prepareDataForSpinnerType() {
        /*
        spinner type
         */
        spinnerTypeAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,
                getResources().getStringArray(R.array.arrType));
        spinnerTypeAdapter.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
        //set adapter
        spnType.setAdapter(spinnerTypeAdapter);
        spnType.setSelected(false);
        //thiết lập sự kiện chọn phần tử cho Spinner
        spnType.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                isSpinnerTouched = true;
                return false;
            }
        });
        spnType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!isSpinnerTouched) {
                    ((TextView) view).setText(null);
                } else {
                    ((TextView) view).setText(null);
                    txtType.setText(spnType.getSelectedItem().toString());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void loadData() {
        try{
            mDatabase.child(Constants.HOMESTAY).child(String.valueOf(homestay.getProvinceId())).child(String.valueOf(homestay.getDistrictId())).child(homestay.getId()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot != null) {
                        Homestay homestay = dataSnapshot.getValue(Homestay.class);
                        if (homestay != null) {
                            //load title
                            getSupportActionBar().setTitle(getResources().getString(R.string.editProfile));
                            txtType.setText(homestay.getType());
                            edtName.setText(homestay.getName());
                            edtPrice.setText(homestay.getPrice());
                            edtBathroom.setText(homestay.getDetails().get(Constants.BATH_ROOM).toString());
                            edtDescription.setText(homestay.getDescription());
                            // txtType.setText(homestay.getType());
                            edtPassenger.setText(homestay.getDetails().get(Constants.MAX).toString());
                            edtBedroom.setText(homestay.getDetails().get(Constants.BED_ROOM).toString());
                            txtTimeClose.setText(homestay.getDetails().get(Constants.TIME_CLOSE).toString());
                            txtTimeOpen.setText(homestay.getDetails().get(Constants.TIME_OPEN).toString());
                            edtBed.setText(homestay.getDetails().get(Constants.BED).toString());
                            edtConvenient.setText(homestay.getDetails().get(Constants.CONVENIENT).toString());
                            if (Boolean.parseBoolean(homestay.getDetails().get(Constants.PET).toString())) {
                                chkAnimal.setChecked(true);
                            } else {
                                chkAnimal.setChecked(false);
                            }
                            timeOpenInMinute = handleTime(txtTimeOpen.getText().toString());
                            timeCloseInMinute = handleTime(txtTimeClose.getText().toString());
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
        catch (Exception e){
            ShowSnackbar.showSnack(this, getResources().getString(R.string.error));
        }
    }

    private boolean isEditSuccessfully() {
        boolean isSuccess = true;
        if (edtName.getText().length() == 0 || edtDescription.getText().length() == 0 || edtPrice.getText().length() == 0 ||
                edtPassenger.getText().length() == 0 || edtBedroom.getText().length() == 0 || edtBed.getText().length() == 0 ||
                edtBathroom.getText().length() == 0 || edtConvenient.getText().length() == 0) {
            isSuccess = false;
            ShowAlertDialog.showAlert(getResources().getString(R.string.fillAllData), this);
        } else {
            if (timeOpenInMinute >= timeCloseInMinute) {
                isSuccess = false;
                ShowAlertDialog.showAlert(getResources().getString(R.string.wrongTime), this);
            }
        }
        return isSuccess;
    }

    private void updateDataHomestay() {
        try{
            String name = edtName.getText().toString().trim();
            String description = edtDescription.getText().toString().trim();
            String type = txtType.getText().toString();
            String price = edtPrice.getText().toString().trim();
            //map data details
            int numberBathroom = Integer.parseInt(edtBathroom.getText().toString());
            int numberBed = Integer.parseInt(edtBed.getText().toString());
            int numberBedroom = Integer.parseInt(edtBedroom.getText().toString());
            String convenient = edtConvenient.getText().toString().trim();
            int maxPass = Integer.parseInt(edtPassenger.getText().toString());
            boolean isAllowAnimal = chkAnimal.isChecked();
            String timeOpen = txtTimeOpen.getText().toString();
            String timeClose = txtTimeClose.getText().toString();
            Map<String, Object> details = new HashMap<>();
            details.put(Constants.BATH_ROOM, numberBathroom);
            details.put(Constants.BED, numberBed);
            details.put(Constants.BED_ROOM, numberBedroom);
            details.put(Constants.CONVENIENT, convenient);
            details.put(Constants.MAX, maxPass);
            details.put(Constants.PET, isAllowAnimal);
            details.put(Constants.TIME_OPEN, timeOpen);
            details.put(Constants.TIME_CLOSE, timeClose);
            //update data to firebaes
            presenter.editHomestay(String.valueOf(homestay.getProvinceId()), String.valueOf(homestay.getDistrictId()),
                    homestay.getId(), name, description, price, type, details);
        }
        catch (Exception e){
            ShowSnackbar.showSnack(this, getResources().getString(R.string.error));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_confirm_edit_profile, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        } else if (item.getItemId() == R.id.action_confirm) {
            if (InternetConnection.getInstance().isOnline(EditHomestayActivity.this)) {
                if (isEditSuccessfully()) {
                    updateDataHomestay();
                    finish();
                }
            } else {
                ShowSnackbar.showSnack(EditHomestayActivity.this, getResources().getString(R.string.noInternet));
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        if (v == linearTimeOpen) {
            RadialTimePickerDialogFragment rtpd = new RadialTimePickerDialogFragment()
                    .setOnTimeSetListener(EditHomestayActivity.this)
                    .setStartTime(12, 00)
                    .setDoneText(getResources().getString(R.string.ok))
                    .setCancelText(getResources().getString(R.string.cancel));
            rtpd.show(getSupportFragmentManager(), FRAG_TAG_TIME_PICKER_OPEN);
        } else if (v == linearTimeClose) {
            RadialTimePickerDialogFragment rtpd = new RadialTimePickerDialogFragment()
                    .setOnTimeSetListener(EditHomestayActivity.this)
                    .setStartTime(12, 00)
                    .setDoneText(getResources().getString(R.string.ok))
                    .setCancelText(getResources().getString(R.string.cancel));
            rtpd.show(getSupportFragmentManager(), FRAG_TAG_TIME_PICKER_CLOSE);
        }
    }

    @Override
    public void onTimeSet(RadialTimePickerDialogFragment dialog, int hourOfDay, int minute) {
        if (dialog.getTag().equals(FRAG_TAG_TIME_PICKER_OPEN)) {
            txtTimeOpen.setText(String.format("%d : %d", hourOfDay, minute));
            timeOpenInMinute = hourOfDay * 60 + minute;
        } else {
            txtTimeClose.setText(String.format("%d : %d", hourOfDay, minute));
            timeCloseInMinute = hourOfDay * 60 + minute;
        }
    }

    private long handleTime(String time) {
        String[] arrDigit = time.split(":");
        long result = Long.parseLong(arrDigit[0].trim()) * 60 + Long.parseLong(arrDigit[1].trim());
        return result;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent(event);
    }
}
