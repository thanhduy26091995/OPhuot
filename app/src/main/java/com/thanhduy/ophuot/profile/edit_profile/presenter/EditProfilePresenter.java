package com.thanhduy.ophuot.profile.edit_profile.presenter;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.thanhduy.ophuot.profile.edit_profile.model.EditProfileSubmitter;
import com.thanhduy.ophuot.profile.edit_profile.view.EditProfileActivity;

import java.util.Map;

/**
 * Created by buivu on 01/03/2017.
 */

public class EditProfilePresenter {
    private EditProfileSubmitter submitter;
    private EditProfileActivity view;
    private DatabaseReference mDatabase;

    public EditProfilePresenter(EditProfileActivity view) {
        this.view = view;
        mDatabase = FirebaseDatabase.getInstance().getReference();
        submitter = new EditProfileSubmitter(mDatabase);
    }

    public void updateUserInfo(String uid, Map<String, Object> address, String description,
                               String name, String phone, int gender) {
        submitter.updateUserInfo(uid, address, description, name, phone, gender);
    }
}
