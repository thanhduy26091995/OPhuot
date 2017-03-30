package com.thanhduy.ophuot.login_and_register.presenter;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.thanhduy.ophuot.R;
import com.thanhduy.ophuot.login_and_register.model.LoginRegisterSubmitter;
import com.thanhduy.ophuot.login_and_register.view.LoginActivity;
import com.thanhduy.ophuot.utils.ShowAlertDialog;

/**
 * Created by buivu on 07/02/2017.
 */

public class LoginRegisterPresenter {
    private LoginRegisterSubmitter submitter;
    private LoginActivity view;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    public LoginRegisterPresenter(LoginActivity view) {
        this.view = view;
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        submitter = new LoginRegisterSubmitter(mDatabase);
    }

    //register
    public void createAccount(final String name, final String phone, final String email, String password) {
        view.showProgessDialog();
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                view.hideProgressDialog();
                //nếu tạo tài khoản thành công
                if (task.isSuccessful()) {
                    //thêm data vào database
                    submitter.addUser(task.getResult().getUser().getUid(), name, phone, email, view.getResources().getString(R.string.myFavorite));
                    view.moveToMainActivity();
                } else {
                    ShowAlertDialog.showAlert(view.getResources().getString(R.string.emailDuplicate), view);
                    Log.d(view.TAG, task.getException().getMessage());
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                view.hideProgressDialog();
            }
        });
    }

    //sign in
    public void signIn(String email, String password) {
        view.showProgessDialog();
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    view.moveToMainActivity();
                } else {
                    ShowAlertDialog.showAlert(view.getResources().getString(R.string.loginFail), view);
                }
                view.hideProgressDialog();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                view.hideProgressDialog();
            }
        });
    }
}
