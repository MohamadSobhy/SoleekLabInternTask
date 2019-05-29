package com.example.soleeklabinterntask.activities;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.soleeklabinterntask.R;
import com.example.soleeklabinterntask.utils.InputsValidatorUtils;
import com.example.soleeklabinterntask.utils.network.NetworkUtils;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegistrationActivity extends AppCompatActivity {
    private final int SIGN_IN_TO_GOOGLE = 0;
    private final String USER_NAME_KEY = "user_name";
    private final String EMAIL_KEY = "email";

    private EditText emailField;
    private EditText passwordField;
    private EditText confirmPasswordField;
    private ProgressBar loadingBar;

    FirebaseAuth firebaseAuth;
    CallbackManager facebookCallbackManager;

    GoogleSignInClient googleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registeration);

        TextView loginTxtView = findViewById(R.id.register_tv);
        Typeface fontTypeface = Typeface.createFromAsset(getAssets(), "fonts/montserrat_smibold.otf");
        loginTxtView.setTypeface(fontTypeface);

        firebaseAuth = FirebaseAuth.getInstance();

        // Configure sign-in to request the user's ID, email address, and basic profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        //get google signin client with the options specified in gso object
        googleSignInClient = GoogleSignIn.getClient(this, gso);


        facebookCallbackManager = CallbackManager.Factory.create();

        emailField = findViewById(R.id.register_email_field);
        passwordField = findViewById(R.id.register_password_field);
        confirmPasswordField = findViewById(R.id.register_confirm_password_field);


        emailField.setTypeface(fontTypeface);
        passwordField.setTypeface(fontTypeface);
        confirmPasswordField.setTypeface(fontTypeface);

        loadingBar = findViewById(R.id.loading_bar);

        TextView loginNowBtn = findViewById(R.id.login_now_tv);
        Button registerBtn = findViewById(R.id.register_btn);

        loginNowBtn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                }
        );

        loginNowBtn.setTypeface(fontTypeface);
        registerBtn.setTypeface(fontTypeface);

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailField.getText().toString().trim();
                String password = passwordField.getText().toString();
                String confirmedPassword = confirmPasswordField.getText().toString();

                if (password.equals(confirmedPassword)) {
                    loadingBar.setVisibility(View.VISIBLE);
                    addNewUser(email, password);
                } else {
                    View parentView = findViewById(android.R.id.content);
                    Snackbar.make(parentView, getString(R.string.passwords_not_matched), Snackbar.LENGTH_SHORT).show();
                }
            }
        });


        SignInButton googleSignInButton = findViewById(R.id.sign_in_button);
        googleSignInButton.setSize(SignInButton.SIZE_STANDARD);

        googleSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIntoGoogle();
            }
        });

        LoginButton loginButton = findViewById(R.id.facebook_login_btn);

        loginButton.setReadPermissions("email", "public_profile");

        loginButton.registerCallback(facebookCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                View parentView = findViewById(android.R.id.content);
                Snackbar.make(parentView, "canceled", Snackbar.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException error) {
                View parentView = findViewById(android.R.id.content);
                Snackbar.make(parentView, "failed", Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SIGN_IN_TO_GOOGLE) {
            loadingBar.setVisibility(View.VISIBLE);
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            loadingBar.setVisibility(View.INVISIBLE);

            View parentView = findViewById(android.R.id.content);
            Snackbar.make(parentView, getString(R.string.google_sign_in_success), Snackbar.LENGTH_SHORT).show();

            openHomeActivity();
        } else {
            facebookCallbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void signIntoGoogle() {
        Intent signInToGoogle = googleSignInClient.getSignInIntent();
        startActivityForResult(signInToGoogle, SIGN_IN_TO_GOOGLE);
    }

    private void addNewUser(String email, String password) {

        final View parentView = findViewById(android.R.id.content);

        //validate email and password
        if (!InputsValidatorUtils.validateInputs(this, email, password))
            return;

        if (!NetworkUtils.isConnected(this)) {
            Snackbar.make(parentView, getString(R.string.internet_connection_failed), Snackbar.LENGTH_SHORT).show();
            return;
        }

        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(
                        RegistrationActivity.this,
                        new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task task) {
                                loadingBar.setVisibility(View.INVISIBLE);
                                if (task.isSuccessful()) {
                                    Snackbar.make(parentView, getString(R.string.registered_successfully), Snackbar.LENGTH_SHORT).show();
                                    finish();
                                } else {
                                    Snackbar.make(parentView, getString(R.string.registration_failed_msg), Snackbar.LENGTH_SHORT).show();
                                }
                            }
                        }
                );
    }


    private void openHomeActivity() {
        Intent openHomeScreen = new Intent(RegistrationActivity.this, HomeActivity.class);
        startActivity(openHomeScreen);
    }

    private void handleFacebookAccessToken(AccessToken token) {

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            View parentView = findViewById(android.R.id.content);
                            Snackbar.make(parentView, getString(R.string.facebook_login_success), Snackbar.LENGTH_SHORT).show();
                            openHomeActivity();
                        } else {
                            View parentView = findViewById(android.R.id.content);
                            Snackbar.make(parentView, "Facebook Login failed", Snackbar.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    public void finish() {
        firebaseAuth.signOut();
        super.finish();
    }
}
