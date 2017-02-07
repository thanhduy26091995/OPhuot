package com.thanhduy.ophuot.login_and_register.presenter;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.thanhduy.ophuot.R;
import com.thanhduy.ophuot.base.GoogleAuthController;
import com.thanhduy.ophuot.login_and_register.model.LoginRegisterSubmitter;
import com.thanhduy.ophuot.login_and_register.view.LoginActivity;
import com.thanhduy.ophuot.utils.Constants;
import com.thanhduy.ophuot.utils.ShowAlertDialog;

import static android.content.ContentValues.TAG;

/**
 * Created by buivu on 07/02/2017.
 */

public class LoginGooglePresenter {
    private LoginRegisterSubmitter submitter;
    private DatabaseReference mDatabase;
    private LoginActivity view;
    private FirebaseAuth mAuth;

    public LoginGooglePresenter(LoginActivity view) {
        this.view = view;
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        submitter = new LoginRegisterSubmitter(mDatabase);
    }

    public void signIn() {
        GoogleAuthController.getInstance().signIn(view);
    }

    //auth với Google, nếu Auth thành công thì gọi hàm onAuthSuccess
    public void firebaseAuthWithGoogle(Activity activity, final GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());
        // [START_EXCLUDE silent]
        view.showProgessDialog();
        // [END_EXCLUDE]

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential).addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());
                // If sign in fails, display a message to the user. If sign in succeeds
                // the auth state listener will be notified and logic to handle the
                // signed in user can be handled in the listener.
                if (!task.isSuccessful()) {
                    ShowAlertDialog.showAlert(view.getResources().getString(R.string.authenFail), view);

                } else {

                    onAuthSuccess(task.getResult().getUser(), acct);
                }
                view.hideProgressDialog();
            }
        });
    }
    // [END auth_with_google]

    //đăng nhập thành công thì lưu user và chuyển tới MainActivity
    public void onAuthSuccess(final FirebaseUser user, final GoogleSignInAccount acct) {

        mDatabase.child(Constants.USERS).child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    view.moveToMainActivity();
                } else {
                    try {
                        submitter.addUser(user.getUid(), acct.getDisplayName(), "", acct.getEmail());
                        view.moveToMainActivity();
                    } catch (Exception e) {
                        Log.d("onAuthSuccessGoogle", "" + e.getMessage());
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "getUser:onCancelled", databaseError.toException());
            }
        });
    }

}
