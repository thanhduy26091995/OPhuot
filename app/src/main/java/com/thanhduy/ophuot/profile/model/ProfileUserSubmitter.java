package com.thanhduy.ophuot.profile.model;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.thanhduy.ophuot.utils.Constants;
import com.thanhduy.ophuot.utils.EncodeImage;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by buivu on 28/02/2017.
 */

public class ProfileUserSubmitter {
    private DatabaseReference mDatabase;
    private StorageReference mStorage;
    private byte[] bitmapDataUser = null;

    public ProfileUserSubmitter(DatabaseReference mDatabase) {
        this.mDatabase = mDatabase;
        mStorage = FirebaseStorage.getInstance().getReference();
    }

    //chỉnh sửa user photoURL
    public void editUserPhotoURL(String uid, String photoURL) {
        Map<String, Object> myMap = new HashMap<>();
        myMap.put(Constants.AVATAR, photoURL);
        mDatabase.child(Constants.USERS).child(uid).updateChildren(myMap);
    }

    public void addImageUser(String uid, OnSuccessListener<UploadTask.TaskSnapshot> listener) {
        if (Constants.USER_FILE_PATH != null) {
            bitmapDataUser = EncodeImage.encodeImage(Constants.USER_FILE_PATH);
        }
        if (bitmapDataUser != null) {
            StorageReference filePathAvatar = mStorage.child(Constants.USER_AVATAR).child(uid).child(Constants.AVATAR);
            UploadTask uploadTask = filePathAvatar.putBytes(bitmapDataUser);
            uploadTask.addOnSuccessListener(listener);

            //restart bitmap
            Constants.USER_FILE_PATH = null;
        }
    }
}
