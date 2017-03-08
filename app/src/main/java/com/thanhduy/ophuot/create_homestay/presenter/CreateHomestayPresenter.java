package com.thanhduy.ophuot.create_homestay.presenter;

import android.net.Uri;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.thanhduy.ophuot.base.BaseActivity;
import com.thanhduy.ophuot.create_homestay.model.CreateHomestaySubmitter;
import com.thanhduy.ophuot.create_homestay.service.UploadPhotoThread;
import com.thanhduy.ophuot.create_homestay.service.UploadPhotoThreadListener;
import com.thanhduy.ophuot.create_homestay.view.CreateHomeStayActivityFour;
import com.thanhduy.ophuot.create_homestay.view.CreateHomeStayActivityOne;
import com.thanhduy.ophuot.create_homestay.view.CreateHomeStayActivityThree;
import com.thanhduy.ophuot.create_homestay.view.CreateHomeStayActivityTwo;
import com.thanhduy.ophuot.model.Homestay;
import com.thanhduy.ophuot.utils.Constants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by buivu on 08/03/2017.
 */

public class CreateHomestayPresenter {
    private CreateHomestaySubmitter submitter;
    private DatabaseReference mDatabase;
    private CreateHomeStayActivityFour view;

    public CreateHomestayPresenter(CreateHomeStayActivityFour view) {
        this.view = view;
        mDatabase = FirebaseDatabase.getInstance().getReference();
        submitter = new CreateHomestaySubmitter(mDatabase);
    }

    public void createHomestay(final String provinceId, final String districtId, final Homestay homestay, ArrayList<Uri> _eventImageUris) {
        // final String pushId = mDatabase.child(Constants.HOMESTAY).child(provinceId).child(districtId).push().getKey();
        submitter.createHomestay(provinceId, districtId, homestay);
        UploadPhotoThreadListener uploadPhotoThreadListener = new UploadPhotoThreadListener() {
            @Override
            public void onUploadPhotoSuccess(ArrayList<String> photoUrls) {
                // homestay.setImages(photoUrls);
                //   homestay.setId(pushId);
                //update images url
                Map<String, Object> updateImages = new HashMap<>();
                updateImages.put(Constants.IMAGES, photoUrls);
                //update database
                mDatabase.child(Constants.HOMESTAY).child(provinceId).child(districtId).child(homestay.getId()).updateChildren(updateImages);
                //
                //add homestay to owner user
                submitter.addOwnerHomestay(homestay.getId(), Integer.parseInt(districtId), Integer.parseInt(provinceId), BaseActivity.getUid());
                view.hideProgressDialog();
                view.finish();
                //close all activity
                if (CreateHomeStayActivityOne.createHomestayActivity1 != null) {
                    CreateHomeStayActivityOne.createHomestayActivity1.finish();
                }
                if (CreateHomeStayActivityTwo.createHomestayActivity2 != null) {
                    CreateHomeStayActivityTwo.createHomestayActivity2.finish();
                }
                if (CreateHomeStayActivityThree.createHomestayActivity3 != null) {
                    CreateHomeStayActivityThree.createHomestayActivity3.finish();
                }
            }
        };
        new UploadPhotoThread(homestay.getId(), _eventImageUris, uploadPhotoThreadListener).execute();
    }
}
