package com.thanhduy.ophuot.profile.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.thanhduy.ophuot.R;
import com.thanhduy.ophuot.base.BaseActivity;
import com.thanhduy.ophuot.base.ImageLoader;
import com.thanhduy.ophuot.model.User;
import com.thanhduy.ophuot.utils.Constants;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by buivu on 27/02/2017.
 */

public class ProfileUserFragment extends Fragment {

    private DatabaseReference mDatabase;

    @BindView(R.id.img_profile_avatar)
    ImageView imgAvatar;
    @BindView(R.id.txt_profile_name)
    TextView txtName;
    @BindView(R.id.txt_profile_address)
    TextView txtAddress;
    @BindView(R.id.txt_profile_male)
    TextView txtMale;
    @BindView(R.id.txt_profile_female)
    TextView txtFemale;
    @BindView(R.id.txt_profile_description)
    TextView txtDescription;
    @BindView(R.id.txt_profile_camera)
    TextView txtCamera;
    @BindView(R.id.txt_profile_edit)
    TextView txtEditProfile;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.activity_profile, container, false);
        ButterKnife.bind(this, rootView);
        //init
        mDatabase = FirebaseDatabase.getInstance().getReference();
        initInfo();
        return rootView;
    }

    private void initInfo() {
        mDatabase.child(Constants.USERS).child(BaseActivity.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    User user = dataSnapshot.getValue(User.class);
                    if (user != null) {
                        txtName.setText(user.getName());
                        txtAddress.setText(user.getAddress().get(Constants.ADDRESS).toString());
                        txtDescription.setText(user.getDescription());
                        checkGender(user.getGender());
                        ImageLoader.getInstance().loadImageAvatar(getActivity(), user.getAvatar(), imgAvatar);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("ERROR", databaseError.getMessage());
            }
        });
    }

    private void checkGender(int gender) {
        if (gender == 1) {
            txtMale.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.border_solid_textview_layout));
            txtMale.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorWhite));
            txtFemale.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.border_stroke_textview_layout));
            txtFemale.setTextColor(ContextCompat.getColor(getActivity(), android.R.color.tab_indicator_text));
        } else {

        }
    }
}
