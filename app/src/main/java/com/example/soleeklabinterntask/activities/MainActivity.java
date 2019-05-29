package com.example.soleeklabinterntask.activities;

import android.content.Intent;
import android.graphics.Typeface;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
    private EditText emailField;
    private EditText passwordField;
    private ProgressBar loadingBar;

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseAuth = FirebaseAuth.getInstance();

        Button loginBtn = findViewById(R.id.login_btn);
        TextView registerNowBtn = findViewById(R.id.register_now_tv);

        emailField = findViewById(R.id.login_email_field);
        passwordField = findViewById(R.id.login_password_field);
        loadingBar = findViewById(R.id.loading_bar);

        TextView loginTxtView = findViewById(R.id.login_tv);

        Typeface fontTypeface = Typeface.createFromAsset(getAssets(), "fonts/montserrat_smibold.otf");
        loginTxtView.setTypeface(fontTypeface);

        emailField.setTypeface(fontTypeface);
        passwordField.setTypeface(fontTypeface);

        loginBtn.setTypeface(fontTypeface);
        registerNowBtn.setTypeface(fontTypeface);

        registerNowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openRegistrationActivity();
            }
        });

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailField.getText().toString().trim();
                String password = passwordField.getText().toString();

                openUserAccount(email, password);
            }
        });
    }

    void openUserAccount(String userEmail, String userPassword) {

        final View parentView = findViewById(android.R.id.content);

        //validate inputs
        if (!InputsValidatorUtils.validateInputs(this, userEmail, userPassword))
            return;

        if(!NetworkUtils.isConnected(this)){
            Snackbar.make(parentView, getString(R.string.internet_connection_failed), Snackbar.LENGTH_SHORT).show();
            return;
        }

        loadingBar.setVisibility(View.VISIBLE);

        firebaseAuth.signInWithEmailAndPassword(userEmail, userPassword)
                .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(Task task) {
                if(task.isSuccessful()){
                    loadingBar.setVisibility(View.INVISIBLE);
                    Snackbar.make(parentView, getString(R.string.login_successfully_msg), Snackbar.LENGTH_SHORT).show();
                    //go to list of countries screen
                    openHomeActivity();
                }else {
                    Snackbar.make(parentView, getString(R.string.user_not_found_msg), Snackbar.LENGTH_SHORT).show();
                    loadingBar.setVisibility(View.INVISIBLE);
                }
            }
        });

    }

    private void openHomeActivity(){
        Intent openHomeScreen = new Intent(MainActivity.this, HomeActivity.class);
        startActivity(openHomeScreen);
    }

    void openRegistrationActivity() {
        Intent openRegistrationScreen = new Intent(MainActivity.this, RegistrationActivity.class);
        startActivity(openRegistrationScreen);
    }

    @Override
    public void finish() {
        firebaseAuth.signOut();
        super.finish();
    }
}
