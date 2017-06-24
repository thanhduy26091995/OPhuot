package com.thanhduy.ophuot.forget_password;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.thanhduy.ophuot.R;
import com.thanhduy.ophuot.base.BaseActivity;
import com.thanhduy.ophuot.base.InternetConnection;
import com.thanhduy.ophuot.utils.ShowAlertDialog;
import com.thanhduy.ophuot.utils.ShowSnackbar;

/**
 * Created by buivu on 08/05/2017.
 */

public class ForgetPasswordActivity extends BaseActivity implements View.OnClickListener {

    private Toolbar toolbar;
    private EditText edtEmail;
    private Button btnResetPassword;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        edtEmail = (EditText) findViewById(R.id.edt_email);
        btnResetPassword = (Button) findViewById(R.id.btn_reset_password);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(getResources().getString(R.string.resetPasswordTitle));
        //set event click
        btnResetPassword.setOnClickListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        if (v == btnResetPassword) {
            if (InternetConnection.getInstance().isOnline(ForgetPasswordActivity.this)) {
                if (!TextUtils.isEmpty(edtEmail.getText())) {
                    sendEmailResetPassword(edtEmail.getText().toString());
                } else {
                    edtEmail.setError(getResources().getString(R.string.required));
                }
            } else {
                ShowSnackbar.showSnack(ForgetPasswordActivity.this, getResources().getString(R.string.noInternet));
            }

        }
    }

    private void sendEmailResetPassword(String email) {
        //disable button
        btnResetPassword.setEnabled(false);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    ShowAlertDialog.showAlert(getResources().getString(R.string.sendEmailResetSuccess), ForgetPasswordActivity.this);
                } else {
                    ShowAlertDialog.showAlert(getResources().getString(R.string.sendEmailResetFail), ForgetPasswordActivity.this);
                }
                btnResetPassword.setEnabled(true);
            }
        });
    }
}
