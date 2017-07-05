package com.thanhduy.ophuot.chat.view;

import android.Manifest;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.github.chrisbanes.photoview.PhotoView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.thanhduy.ophuot.R;
import com.thanhduy.ophuot.base.BaseActivity;
import com.thanhduy.ophuot.utils.Constants;
import com.thanhduy.ophuot.utils.ShowAlertDialog;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by buivu on 14/04/2017.
 */

public class DisplayImageActivity extends BaseActivity {


    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.ivImage)
    PhotoView imgImage;

    private String image;
    private static final int REQUEST_CODE_WRITE_EXTERNAL_STORAGE = 1001;
    private static final String[] PERMISSIONS_STORAGE = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_image);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //get intent
        image = getIntent().getStringExtra(Constants.IMAGES);
        Glide.with(this)
                .load(image)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imgImage);
        changeColorStatusBar();
    }

    private boolean verifyStoragePermissions() {
        // Check if we have read or write permission
        int readPermission = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (readPermission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    this,
                    PERMISSIONS_STORAGE,
                    REQUEST_CODE_WRITE_EXTERNAL_STORAGE
            );

            return false;
        }

        return true;
    }

    @Override
    public void onRequestPermissionsResult(int permsRequestCode, String[] permissions, int[] grantResults) {
        switch (permsRequestCode) {
            case REQUEST_CODE_WRITE_EXTERNAL_STORAGE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    saveFile();
                }
                break;

        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        } else if (item.getItemId() == R.id.action_save) {
            if (verifyStoragePermissions()) {
                saveFile();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveFile() {
        Bitmap bitmap = ImageLoader.getInstance().loadImageSync(image);
        storeImageInSDCard(bitmap);
    }

    private void storeImageInSDCard(Bitmap bitmap) {
        File pictureFile = getOutputImageFile();
        if (pictureFile != null) {
            try {
                FileOutputStream fos = new FileOutputStream(pictureFile);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                fos.close();
                //using content provider to inform os know this image
                addImageToGallery(pictureFile.getAbsolutePath());
                //inform
                Toast.makeText(this, String.format("%s tại %s", getResources().getString(R.string.imageSaved), pictureFile.getPath()),
                        Toast.LENGTH_LONG).show();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            ShowAlertDialog.showAlert(getResources().getString(R.string.errorWhenSaveImage), this);
        }
    }

    private void addImageToGallery(String filePath) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
        contentValues.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        contentValues.put(MediaStore.Images.ImageColumns.DATA, filePath);

        getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
    }

    //get the path for saving image
    private File getOutputImageFile() {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.
        File mediaStorageDir = new File(Environment.getExternalStorageDirectory() + "/Pictures/OPhuot");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.
        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }
        // Create a media file name
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = String.format("IMG_%s.jpg", timestamp);
        File imageFile = new File(mediaStorageDir.getPath() + File.separator + imageFileName);
        return imageFile;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_display_image, menu);
        return super.onCreateOptionsMenu(menu);
    }

    private void changeColorStatusBar() {
        Window window = getWindow();

        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        // finally change the color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(getResources().getColor(R.color.colorBlack));
        }
    }
}
