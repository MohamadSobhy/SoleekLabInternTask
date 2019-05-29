package com.example.soleeklabinterntask.utils;

import android.app.Activity;
import android.support.design.widget.Snackbar;
import android.view.View;

import com.example.soleeklabinterntask.R;

public class InputsValidatorUtils {
    private static boolean validateEmailInput(String email) {
        String emailRegex = "^[\\w-\\+]+(\\.[\\w]+)*@[\\w-]+(\\.[\\w]+)*(\\.[a-z]{2,})$";
        return !email.isEmpty() && email.matches(emailRegex);
    }

    private static boolean validatePasswordInput(String password) {
        return !password.isEmpty();
    }

    public static boolean validateInputs(Activity currentActivity, String userEmail, String userPassword) {
        boolean isValidEmail = InputsValidatorUtils.validateEmailInput(userEmail);
        boolean isValidPassword = InputsValidatorUtils.validatePasswordInput(userPassword);

        if (!(isValidEmail && isValidPassword)) {
            View parentView = currentActivity.findViewById(android.R.id.content);
            Snackbar.make(
                    parentView,
                    currentActivity.getString(R.string.invalid_email_or_password_msg),
                    Snackbar.LENGTH_SHORT)
                    .show();
        }

        return isValidEmail && isValidPassword;
    }
}
