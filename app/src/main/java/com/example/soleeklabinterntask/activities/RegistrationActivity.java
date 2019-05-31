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
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

public class RegistrationActivity extends AppCompatActivity {
    private final int SIGN_IN_TO_GOOGLE = 0;
    private final String FACEBOOK_REQUEST_PARAMETERS_KEY = "fields";
    private final String FACEBOOK_REQUEST_PARAMETERS_VALUE = "first_name,last_name,email";
    public static final String USER_NAME_KEY = "user_name";
    public static final String EMAIL_KEY = "email";

    private EditText emailField;
    private EditText passwordField;
    private EditText confirmPasswordField;
    private ProgressBar loadingBar;

    private FirebaseAuth firebaseAuth;
    private CallbackManager facebookCallbackManager;

    private GoogleSignInClient googleSignInClient;

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

        //check the current access token
        checkCurrentLog();

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

        loginButton.setReadPermissions(Arrays.asList("email", "public_profile"));

        loginButton.registerCallback(facebookCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                AccessToken token = loginResult.getAccessToken();

                if (token == null) {

                    View parentView = findViewById(android.R.id.content);
                    Snackbar.make(parentView, "Login Failed", Snackbar.LENGTH_SHORT).show();
                } else {
                    handleFacebookAccessToken(token);
                }
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

    private void checkCurrentLog() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        boolean isLoggedIn = accessToken != null && !accessToken.isExpired();

        if (isLoggedIn) {
            handleFacebookAccessToken(accessToken);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RESULT_OK && requestCode == SIGN_IN_TO_GOOGLE) {
            loadingBar.setVisibility(View.VISIBLE);
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            loadingBar.setVisibility(View.INVISIBLE);

            View parentView = findViewById(android.R.id.content);
            Snackbar.make(parentView, getString(R.string.google_sign_in_success), Snackbar.LENGTH_SHORT).show();

            openHomeActivity(task.getResult().getDisplayName(), task.getResult().getEmail());
        } else {
            facebookCallbackManager.onActivityResult(requestCode, resultCode, data);
        }
        super.onActivityResult(requestCode, resultCode, data);
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


    private void openHomeActivity(String userName, String userEmail) {
        Intent openHomeScreen = new Intent(RegistrationActivity.this, HomeActivity.class);

        openHomeScreen.putExtra(USER_NAME_KEY, userName);
        openHomeScreen.putExtra(EMAIL_KEY, userEmail);

        startActivity(openHomeScreen);
    }

    private void handleFacebookAccessToken(AccessToken token) {
        GraphRequest request = GraphRequest.newMeRequest(token, new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {

                try {
                    //extracting data from JsonObject and sending them to Home screen with the intent
                    String firstName = object.getString("first_name");
                    String lastName = object.getString("last_name");
                    String email = object.getString("email");

                    openHomeActivity((firstName + " " + lastName), email);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        Bundle requestParameters = new Bundle();
        requestParameters.putString(FACEBOOK_REQUEST_PARAMETERS_KEY, FACEBOOK_REQUEST_PARAMETERS_VALUE);
        request.setParameters(requestParameters);
        request.executeAsync();
    }

    @Override
    public void finish() {
        firebaseAuth.signOut();
        super.finish();
    }
}
