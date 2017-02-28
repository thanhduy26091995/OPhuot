package com.thanhduy.ophuot.profile.presenter;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.UploadTask;
import com.thanhduy.ophuot.profile.model.ProfileUserSubmitter;
import com.thanhduy.ophuot.profile.view.ProfileUserActivity;

/**
 * Created by buivu on 28/02/2017.
 */

public class ProfileUserPresenter {
    private ProfileUserSubmitter submitter;
    private ProfileUserActivity view;
    private DatabaseReference mDatabase;

    public ProfileUserPresenter(ProfileUserActivity view) {
        this.view = view;
        mDatabase = FirebaseDatabase.getInstance().getReference();
        submitter = new ProfileUserSubmitter(mDatabase);
    }

    public void addImageUser(String uid, OnSuccessListener<UploadTask.TaskSnapshot> listener) {
        submitter.addImageUser(uid, listener);
    }

    //chỉnh sửa user photoURL
    public void editUserPhotoURL(String uid, String photoURL) {
        submitter.editUserPhotoURL(uid, photoURL);
    }

}
