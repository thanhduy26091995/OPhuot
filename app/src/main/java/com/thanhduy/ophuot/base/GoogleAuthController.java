package com.thanhduy.ophuot.base;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.thanhduy.ophuot.R;
import com.thanhduy.ophuot.utils.Constants;

/**
 * Created by buivu on 08/10/2016.
 * Lớp này là lớp singleton, dùng để sử dụng cho việc đăng nhập bằng google
 */
public class GoogleAuthController {
    //có 1 phương thức tĩnh có kiểu là Singleton (GoogleAuthController)
    private static GoogleAuthController instance;

    private GoogleApiClient mGoogleApiClient;

    private GoogleApiClient.Builder builder;

    private GoogleAuthController(FragmentActivity fragmentActivity, GoogleApiClient.OnConnectionFailedListener listener) {
        // Configure GoogleSubmitter Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(OPhuotApplication.getInstance().getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        // [END config_signin]

        builder = new GoogleApiClient.Builder(OPhuotApplication.getInstance())
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso);
        // .addApi(LocationServices.API);

        builder.enableAutoManage(fragmentActivity /* FragmentActivity */, listener /* OnConnectionFailedListener */);
        mGoogleApiClient = builder.build();
    }

    public static void install(FragmentActivity fragmentActivity, GoogleApiClient.OnConnectionFailedListener listener) {
        if (instance == null) {
            instance = new GoogleAuthController(fragmentActivity, listener);
        }
    }

    //hàm có kiểu trả về là Singleton
    public static GoogleAuthController getInstance() {
        return instance;
    }

    public void signIn(Activity context) {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        context.startActivityForResult(signInIntent, Constants.RC_SIGN_IN);
    }

    public void signOut(GoogleApiClient.ConnectionCallbacks callbacks) {
        mGoogleApiClient.connect();
        mGoogleApiClient.registerConnectionCallbacks(callbacks);
    }

    public GoogleApiClient getGoogleApiClient() {
        return mGoogleApiClient;
    }
}
