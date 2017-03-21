package com.thanhduy.ophuot.base;

import android.app.ProgressDialog;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

/**
 * Created by buivu on 13/01/2017.
 * Lớp baseactivity này để các lớp khác kế thừa, mục đích là để sử dụng lại phương thức getUid
 */
public class BaseActivity extends AppCompatActivity {
    public ProgressDialog mProgressDialog;


    public void showProgessDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage("Đang tải...");
            mProgressDialog.setCancelable(false);

            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    mProgressDialog.dismiss();
                }
            };
            Handler handlerCancel = new Handler();
            handlerCancel.postDelayed(runnable, 10000);
        }
        try {
            mProgressDialog.show();
        } catch (Exception e) {
        }

    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            try {
                mProgressDialog.dismiss();
            } catch (Exception e) {
            }

        }
    }

    @Override
    public void onStop() {
        super.onStop();
        hideProgressDialog();
    }

    public static String getUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }
}
