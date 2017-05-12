package com.thanhduy.ophuot.config.view;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.thanhduy.ophuot.R;
import com.thanhduy.ophuot.base.BaseActivity;
import com.thanhduy.ophuot.base.DeviceToken;
import com.thanhduy.ophuot.config.presenter.ConfigPresenter;
import com.thanhduy.ophuot.model.User;
import com.thanhduy.ophuot.utils.Constants;
import com.thanhduy.ophuot.utils.SessionManagerForLanguage;

import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.thanhduy.ophuot.base.BaseActivity.getUid;

/**
 * Created by buivu on 26/04/2017.
 */

public class ConfigFragment extends Fragment {

    private View rootView;
    private DatabaseReference mDatabase;
    private SessionManagerForLanguage sessionManagerForLanguage;
    private ArrayAdapter<String> adapterForSpinner;
    private boolean isSpinnerTouched = false;
    private Locale locale;
    private ConfigPresenter presenter;


    @BindView(R.id.chk_language)
    CheckBox chkLanguage;
    @BindView(R.id.tvLanguage)
    TextView txtLanguage;
    @BindView(R.id.spnLanguage)
    Spinner spnLanguage;
    @BindView(R.id.chk_noti_comment)
    CheckBox chkComment;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.activity_config, container, false);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        sessionManagerForLanguage = new SessionManagerForLanguage(getActivity());
        ButterKnife.bind(this, rootView);
        txtLanguage.setText(sessionManagerForLanguage.getLanguage());
        presenter = new ConfigPresenter(this);
        prepareDataForSpinner();
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            initData();
            chkLanguage.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        String token = FirebaseInstanceId.getInstance().getToken();
                        DeviceToken.getInstance().addDeviceToken(mDatabase, getUid(), token);
                    } else {
                        DeviceToken.getInstance().addDeviceToken(mDatabase, getUid(), "");
                    }
                }
            });
            //event click chkComment
            chkComment.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        presenter.getAllNoti(BaseActivity.getUid()).addChildEventListener(new ChildEventListener() {
                            @Override
                            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                if (dataSnapshot != null) {
                                    String homestayId = dataSnapshot.getKey();
                                    //update state
                                    presenter.updateNotiComment(BaseActivity.getUid(), homestayId, true);
                                    //register topic
                                    FirebaseMessaging.getInstance().subscribeToTopic(homestayId);
                                }
                            }

                            @Override
                            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                            }

                            @Override
                            public void onChildRemoved(DataSnapshot dataSnapshot) {

                            }

                            @Override
                            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    } else {
                        presenter.getAllNoti(BaseActivity.getUid()).addChildEventListener(new ChildEventListener() {
                            @Override
                            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                if (dataSnapshot != null) {
                                    String homestayId = dataSnapshot.getKey();
                                    //update state
                                    presenter.updateNotiComment(BaseActivity.getUid(), homestayId, false);
                                    //register topic
                                    FirebaseMessaging.getInstance().unsubscribeFromTopic(homestayId);
                                }
                            }

                            @Override
                            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                            }

                            @Override
                            public void onChildRemoved(DataSnapshot dataSnapshot) {

                            }

                            @Override
                            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                }
            });
        } else {
            chkLanguage.setChecked(false);
        }
        return rootView;
    }

    private void prepareDataForSpinner() {
        adapterForSpinner = new ArrayAdapter<String>(getActivity(), R.layout.spinner, getActivity().getResources().getStringArray(R.array.arr_language));
        adapterForSpinner.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
        spnLanguage.setAdapter(adapterForSpinner);
        for (int i = 0; i < spnLanguage.getCount(); i++) {
            if (spnLanguage.getItemAtPosition(i).toString().trim().equals(sessionManagerForLanguage.getLanguage())) {
                spnLanguage.setSelection(i);
                break;
            }
        }
        spnLanguage.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                isSpinnerTouched = true;
                return false;
            }
        });
        spnLanguage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!isSpinnerTouched) {
                    ((TextView) view).setText(null);
                } else {
                    ((TextView) view).setText(null);
                    String language;
                    if (spnLanguage.getSelectedItem().toString().trim().equals("Vietnamese") || spnLanguage.getSelectedItem().toString().trim().equals("Tiếng Việt")) {
                        language = "Tiếng Việt";
                        setLocale("vi", language);
                    } else {
                        language = "English";
                        setLocale("en", language);
                    }
                    sessionManagerForLanguage.setIsClickedLanguage(true);
                    sessionManagerForLanguage.createLanguageSession(language);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void setLocale(String lang, String language) {
        locale = new Locale(lang);
        Resources res = this.getResources();
        DisplayMetrics displayMetrics = res.getDisplayMetrics();
        Configuration configuration = res.getConfiguration();
        configuration.locale = locale;
        res.updateConfiguration(configuration, displayMetrics);
        Intent refresh = new Intent(getActivity(), getActivity().getClass());
        refresh.putExtra("language", language);
        startActivity(refresh);
        getActivity().finish();
    }

    private void initData() {
        mDatabase.child(Constants.USERS).child(getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    User user = dataSnapshot.getValue(User.class);
                    if (user != null) {
                        String deviceToken = user.getDeviceToken();
                        if (deviceToken.length() > 3) {
                            chkLanguage.setChecked(true);
                        } else {
                            chkLanguage.setChecked(false);
                        }
                        int count = 0;
                        Map<String, Object> notiComment = user.getNotiComment();
                        for (Map.Entry<String, Object> entry : notiComment.entrySet()) {
                            if ((Boolean) entry.getValue()) {
                                count++;
                            }
                        }
                        if (count > 0) {
                            chkComment.setChecked(true);
                        } else {
                            chkComment.setChecked(false);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
