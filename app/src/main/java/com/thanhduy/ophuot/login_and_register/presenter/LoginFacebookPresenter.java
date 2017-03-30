package com.thanhduy.ophuot.login_and_register.presenter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.thanhduy.ophuot.R;
import com.thanhduy.ophuot.login_and_register.model.LoginRegisterSubmitter;
import com.thanhduy.ophuot.login_and_register.view.LoginActivity;
import com.thanhduy.ophuot.utils.Constants;
import com.thanhduy.ophuot.utils.ShowAlertDialog;

import java.util.Arrays;

import static com.thanhduy.ophuot.login_and_register.view.LoginActivity.TAG;

/**
 * Created by buivu on 07/02/2017.
 */

public class LoginFacebookPresenter {
    private LoginActivity view;
    private DatabaseReference mDatabase;
    private LoginRegisterSubmitter submitter;
    private FirebaseAuth mAuth;

    public LoginFacebookPresenter(LoginActivity view) {
        this.view = view;
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        submitter = new LoginRegisterSubmitter(mDatabase);
    }

    //Login bằng Facebook
    public void setUpFacebook(Context context, CallbackManager callbackManager) {

        Log.d(TAG, "signWithFacebook");
        //khởi tạo Facebook SDK
        FacebookSdk.sdkInitialize(context);

        final AccessToken accessToken = AccessToken.getCurrentAccessToken();
        final LoginManager loginManager;
        loginManager = LoginManager.getInstance();
        loginManager.logInWithReadPermissions(view, Arrays.asList(Constants.FACEBOOK_PERMISSION));
        loginManager.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(final LoginResult loginResult) {
                //cập nhật lại accessToken
                accessToken.setCurrentAccessToken(loginResult.getAccessToken());
                handleFacebookAccessToken(loginResult.getAccessToken(), mAuth);
            }


            @Override
            public void onCancel() {
                Log.d(TAG, "facebook:onCancel:");
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, error.getMessage());
            }
        });

    }

    //bắt đầu Auth với Facebook
    private void handleFacebookAccessToken(AccessToken accessToken, FirebaseAuth mAuth) {
        Log.d(TAG, "handleFacebookAccessToken:" + accessToken);
        view.showProgessDialog();

        AuthCredential credential = FacebookAuthProvider.getCredential(accessToken.getToken());
        mAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());
                //login fail
                if (!task.isSuccessful()) {
                    ShowAlertDialog.showAlert(view.getResources().getString(R.string.authenFail), view);
                    Log.w(TAG, "signInWithCredential", task.getException());
                } else {
                    onAuthSuccess(task.getResult().getUser());
                }
                view.hideProgressDialog();
            }
        });
    }

    //đăng nhập thành công thì lưu user và chuyển tới MainActivity
    private void onAuthSuccess(final FirebaseUser user) {
        mDatabase.child(Constants.USERS).child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    view.moveToMainActivity();
                } else {
                    submitter.addUser(user.getUid(), user.getDisplayName(), "", user.getEmail(), view.getResources().getString(R.string.myFavorite));
                    view.moveToMainActivity();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "getUser:onCancelled", databaseError.toException());
            }
        });
    }
}
