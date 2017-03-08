package com.thanhduy.ophuot.create_homestay.service;

import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.thanhduy.ophuot.utils.Constants;
import com.thanhduy.ophuot.utils.EncodeImage;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.CountDownLatch;

/**
 * Created by buivu on 17/09/2016.
 */
public class UploadPhotoThread extends AsyncTask<String, String, ArrayList<String>> {
    private ArrayList<Uri> imageUris;
    private String homestayId;
    private UploadPhotoThreadListener listener;

    public UploadPhotoThread(String homestayId, ArrayList<Uri> imageUris, UploadPhotoThreadListener listener) {
        this.homestayId = homestayId;
        this.imageUris = imageUris;
        this.listener = listener;
    }

    @Override
    protected ArrayList<String> doInBackground(String... strings) {
        try {
            if (imageUris.size() != 0) {
                return uploadPhotos(homestayId, imageUris);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(ArrayList<String> strings) {
        listener.onUploadPhotoSuccess(strings);
    }

    //Upload Photos need a thread to work with
    private ArrayList<String> uploadPhotos(String homestayId, ArrayList<Uri> imageUris)
            throws InterruptedException, FileNotFoundException {
        try {
            //Storage Reference
            StorageReference mStorage = FirebaseStorage.getInstance().getReference();
            mStorage = mStorage.child(Constants.HOMESTAY).child(homestayId);

            // Create the file metadata
            StorageMetadata metadata = new StorageMetadata.Builder()
                    .setContentType("image/jpeg")
                    .build();

            // Upload files
            String timeStamp = String.valueOf(new Date().getTime());

            final ArrayList<String> _uploadedImages = new ArrayList<String>();
            final CountDownLatch uploadDone = new CountDownLatch(imageUris.size()); //Multi thread sync
            for (int i = 0; i < imageUris.size(); i++) {
                Uri file = imageUris.get(i);
                byte[] arrBytes = EncodeImage.encodeImage(file.getPath());
                String fileName = timeStamp + "_" + i + ".jpg";

                //Upload file
                //UploadTask uploadTask = storageRef.child(eventID + "/" + fileName).putStream(stream, metadata);
                UploadTask uploadTask = mStorage.child(fileName).putBytes(arrBytes, metadata);
                // Listen for state changes, errors, and completion of the upload.
                uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                        System.out.println("Upload is " + progress + "% done");
                    }
                }).addOnPausedListener(new OnPausedListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onPaused(UploadTask.TaskSnapshot taskSnapshot) {
                        System.out.println("Upload is paused");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                        uploadDone.countDown();
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Handle successful uploads on complete
                        uploadDone.countDown();
                        Uri downloadUrl = taskSnapshot.getMetadata().getDownloadUrl();
                        _uploadedImages.add(downloadUrl.toString());
                    }
                });
                arrBytes = null;
            }

            //Wait until upload images done!
            uploadDone.await();

            //Do with uploaded Images
            return _uploadedImages;
        } catch (InterruptedException e) {
            Log.e("ERROR", e.getMessage());
        }
        return null;
    }

}
