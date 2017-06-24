package com.thanhduy.ophuot.login_and_register.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.facebook.CallbackManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.thanhduy.ophuot.R;
import com.thanhduy.ophuot.base.BaseActivity;
import com.thanhduy.ophuot.base.GoogleAuthController;
import com.thanhduy.ophuot.base.InternetConnection;
import com.thanhduy.ophuot.forget_password.ForgetPasswordActivity;
import com.thanhduy.ophuot.login_and_register.presenter.LoginFacebookPresenter;
import com.thanhduy.ophuot.login_and_register.presenter.LoginGooglePresenter;
import com.thanhduy.ophuot.login_and_register.presenter.LoginRegisterPresenter;
import com.thanhduy.ophuot.main.view.MainActivity;
import com.thanhduy.ophuot.utils.Constants;
import com.thanhduy.ophuot.utils.EmailValidate;
import com.thanhduy.ophuot.utils.ShowAlertDialog;
import com.thanhduy.ophuot.utils.ShowSnackbar;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by buivu on 13/01/2017.
 */
public class LoginActivity extends BaseActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {

    @BindView(R.id.edt_email)
    EditText edtEmail;
    @BindView(R.id.edt_password)
    EditText edtPassword;
    @BindView(R.id.edt_register_email)
    EditText edtRegisterEmail;
    @BindView(R.id.edt_register_password)
    EditText edtRegisterPassword;
    @BindView(R.id.edt_register_confirm_password)
    EditText edtConfirmPassword;
    @BindView(R.id.edt_register_phone)
    EditText edtRegisterPhone;
    @BindView(R.id.edt_register_username)
    EditText edtRegisterUsername;
    @BindView(R.id.linear_forget_pass)
    LinearLayout linearForget;
    @BindView(R.id.linear_login)
    LinearLayout linearLogin;
    @BindView(R.id.linear_register)
    LinearLayout linearRegister;
    @BindView(R.id.content_login)
    LinearLayout contentLogin;
    @BindView(R.id.content_register)
    LinearLayout contentRegister;
    @BindView(R.id.btn_register)
    Button btnRegister;
    @BindView(R.id.btn_login)
    Button btnLogin;
    @BindView(R.id.img_facebook)
    ImageButton btnFacebook;
    @BindView(R.id.img_google)
    ImageButton btnGoogle;
    @BindView(R.id.toolbar)
    android.support.v7.widget.Toolbar toolbar;

    public static final String TAG = "LoginActivity";
    private LoginRegisterPresenter presenter;
    private LoginGooglePresenter googlePresenter;
    private CallbackManager callbackManager;
    private LoginFacebookPresenter facebookPresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getResources().getString(R.string.loginOrRegister));

        presenter = new LoginRegisterPresenter(this);
        googlePresenter = new LoginGooglePresenter(this);
        facebookPresenter = new LoginFacebookPresenter(this);
        callbackManager = CallbackManager.Factory.create();
        //event click
        linearLogin.setOnClickListener(this);
        linearRegister.setOnClickListener(this);
        btnRegister.setOnClickListener(this);
        btnLogin.setOnClickListener(this);
        btnFacebook.setOnClickListener(this);
        btnGoogle.setOnClickListener(this);
        linearForget.setOnClickListener(this);
        initData();

    }

    private void initData() {
        if (!InternetConnection.getInstance().isOnline(LoginActivity.this)) {
            Snackbar snackbar = Snackbar.make(findViewById(R.id.activity), getResources().getString(R.string.noInternet), Snackbar.LENGTH_INDEFINITE)
                    .setAction(getResources().getString(R.string.retry), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            initData();
                        }
                    });
            snackbar.show();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        GoogleAuthController.install(this, this);
    }

    @Override
    public void onClick(View view) {
        if (view == linearLogin) {
            contentLogin.setVisibility(View.VISIBLE);
            contentRegister.setVisibility(View.GONE);
        } else if (view == linearRegister) {
            contentLogin.setVisibility(View.GONE);
            contentRegister.setVisibility(View.VISIBLE);
        } else if (view == btnRegister) {
            if (InternetConnection.getInstance().isOnline(LoginActivity.this)) {
                register();
            } else {
                ShowSnackbar.showSnack(LoginActivity.this, getResources().getString(R.string.noInternet));
            }
        } else if (view == btnLogin) {
            if (InternetConnection.getInstance().isOnline(LoginActivity.this)) {
                login();
            } else {
                ShowSnackbar.showSnack(LoginActivity.this, getResources().getString(R.string.noInternet));
            }
        } else if (view == btnGoogle) {
            if (InternetConnection.getInstance().isOnline(LoginActivity.this)) {
                loginWithGoogle();
            } else {
                ShowSnackbar.showSnack(LoginActivity.this, getResources().getString(R.string.noInternet));
            }
        } else if (view == btnFacebook) {
            if (InternetConnection.getInstance().isOnline(LoginActivity.this)) {
                loginWithFacebook();
            } else {
                ShowSnackbar.showSnack(LoginActivity.this, getResources().getString(R.string.noInternet));
            }
        } else if (view == linearForget) {
            Intent intent = new Intent(LoginActivity.this, ForgetPasswordActivity.class);
            startActivity(intent);
        }
    }

    private void loginWithFacebook() {
        facebookPresenter.setUpFacebook(this, callbackManager);
    }

    private void loginWithGoogle() {
        googlePresenter.signIn();
    }

    private void login() {
        boolean isSuccess = true;
        String email = edtEmail.getText().toString();
        String password = edtPassword.getText().toString();
        if (email.length() == 0 || password.length() == 0) {
            ShowAlertDialog.showAlert(getResources().getString(R.string.fillAllData), this);
            isSuccess = false;
            return;
        } else {
            if (!EmailValidate.IsOk(email)) {
                ShowAlertDialog.showAlert(getResources().getString(R.string.wrongFormatEmail), this);
                isSuccess = false;
                return;
            }
            if (password.length() < 6) {
                ShowAlertDialog.showAlert(getResources().getString(R.string.largerThan6Letter), this);
                isSuccess = false;
                return;
            }
        }

        if (isSuccess) {
            presenter.signIn(email, password);
        }

    }

    private void register() {
        boolean isSuccess = true;
        String email = edtRegisterEmail.getText().toString().trim();
        String password = edtRegisterPassword.getText().toString().trim();
        String confirmPassword = edtConfirmPassword.getText().toString().trim();
        String phone = edtRegisterPhone.getText().toString().trim();
        String name = edtRegisterUsername.getText().toString().trim();
        if (email.length() == 0 || password.length() == 0 || confirmPassword.length() == 0
                || phone.length() == 0 || name.length() == 0) {
            ShowAlertDialog.showAlert(getResources().getString(R.string.fillAllData), this);
            isSuccess = false;
            return;
        } else {
            if (!EmailValidate.IsOk(email)) {
                ShowAlertDialog.showAlert(getResources().getString(R.string.wrongFormatEmail), this);
                isSuccess = false;
                return;
            }
            if (password.length() < 6) {
                ShowAlertDialog.showAlert(getResources().getString(R.string.largerThan6Letter), this);
                isSuccess = false;
                return;
            }
            if (!password.equals(confirmPassword)) {
                ShowAlertDialog.showAlert(getResources().getString(R.string.passwordDoesntMatch), this);
                isSuccess = false;
                return;
            }
            if (phone.length() < 10 || phone.length() > 11) {
                ShowAlertDialog.showAlert(getResources().getString(R.string.numberOfPhone), this);
                isSuccess = false;
                return;
            }
        }
        if (isSuccess) {
            presenter.createAccount(name, phone, email, password);
        }

    }

    //chuyển tới MainActivity
    public void moveToMainActivity() {
        if (MainActivity.mainActivity != null) {
            MainActivity.mainActivity.finish();
        }
        Intent myIntent = new Intent(this, MainActivity.class);
        myIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(myIntent);
        finish();
    }

    //trả về kết quả sau khi chọn account trong Google Login
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                GoogleSignInAccount account = result.getSignInAccount();
                googlePresenter.firebaseAuthWithGoogle(this, account);
            } else {
                Log.d(TAG, "GoogleSubmitter login fail");
            }

        }
        if (requestCode != Constants.RC_SIGN_IN && resultCode == RESULT_OK) {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
