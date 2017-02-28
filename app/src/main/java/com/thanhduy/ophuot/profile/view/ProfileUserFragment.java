package com.thanhduy.ophuot.profile.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.thanhduy.ophuot.R;
import com.thanhduy.ophuot.base.BaseActivity;
import com.thanhduy.ophuot.utils.Constants;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by buivu on 27/02/2017.
 */

public class ProfileUserFragment extends Fragment {

   private DatabaseReference mDatabase;

    @BindView(R.id.img_profile_avatar)
    private ImageView imgAvatar;
    @BindView(R.id.txt_profile_name)
    private TextView txtName;
    @BindView(R.id.txt_profile_address)
    private TextView txtAddress;
    @BindView(R.id.txt_profile_male)
    private TextView txtMale;
    @BindView(R.id.txt_profile_female)
    private TextView txtFemale;
    @BindView(R.id.txt_profile_description)
    private TextView txtDescription;
    @BindView(R.id.txt_profile_camera)
    private TextView txtCamera;
    @BindView(R.id.txt_profile_edit)
    private TextView txtEditProfile;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.activity_profile, container, false);
        ButterKnife.bind(rootView);
        //init
        mDatabase = FirebaseDatabase.getInstance().getReference();
        return rootView;
    }

    private void initInfo(){
        mDatabase.child(Constants.USERS).child(BaseActivity.getUid()).
    }
}
