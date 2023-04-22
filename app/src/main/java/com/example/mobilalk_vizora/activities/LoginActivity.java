package com.example.mobilalk_vizora.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mobilalk_vizora.R;
import com.example.mobilalk_vizora.fireBaseProvider.FireBaseProvider;
import com.example.mobilalk_vizora.validators.InputValidator;
import com.example.mobilalk_vizora.validators.ValidationResult;

public class LoginActivity extends AppCompatActivity {

    InputValidator inputValidator;
    FireBaseProvider fBaseProvider;
    EditText emailInput;
    EditText passwordInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        inputValidator = new InputValidator(this);
        fBaseProvider = new FireBaseProvider();
        emailInput = findViewById(R.id.loginEmail);
        passwordInput = findViewById(R.id.loginPassword);

        emailInput.setOnFocusChangeListener(emailFocusListener);
        passwordInput.setOnFocusChangeListener(passwordFocusListener);
    }

    public void goToRegister(View view) {
        Intent registerIntent = new Intent(this, RegisterActivity.class);
        startActivity(registerIntent);
    }

    public void loginWithEmail(View view) {
        String email = emailInput.getText().toString();
        String passw = passwordInput.getText().toString();
        if (validateInputs()) {
            fBaseProvider.loginWithEmail(email, passw).addOnSuccessListener(authResult -> {
                Intent maintIntent = new Intent(this, MainActivity.class);
                startActivity(maintIntent);
            });
        }
    }

    private boolean validateInputs() {
        return (emailInput.getError() == null && emailInput.getText().length() > 0) &&
                (passwordInput.getError() == null && passwordInput.getText().length() > 0);
    }

    public void showForgottenPassDialog(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.password_reset);
        LinearLayout linearLayout = new LinearLayout(this);
        final EditText emailet = new EditText(this);

        // write the email using which you registered
        emailet.setHint(R.string.email);
        emailet.setMinEms(16);
        emailet.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        linearLayout.addView(emailet);
        linearLayout.setPadding(10, 10, 10, 10);
        builder.setView(linearLayout);

        // Click on Recover and a email will be sent to your registered email id
        builder.setPositiveButton(R.string.send, (dialog, which) -> {

            String email = emailet.getText().toString().trim();
            fBaseProvider.sendPasswReset(email).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(LoginActivity.this, R.string.email_send_success, Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(LoginActivity.this, R.string.email_send_failed, Toast.LENGTH_LONG).show();
                }
            });
        });

        builder.setNegativeButton(R.string.cancel, (dialog, which) -> dialog.dismiss());
        builder.create().show();
    }

    View.OnFocusChangeListener emailFocusListener = (v, hasFocus) -> {
        if (!hasFocus && emailInput.getText().length() > 0) {
            ValidationResult valRes = inputValidator.validateEmail(emailInput.getText().toString());
            emailInput.setError(valRes.getError());
        }
    };

    View.OnFocusChangeListener passwordFocusListener = (v, hasFocus) -> {
        if (!hasFocus) {
            ValidationResult valRes = inputValidator.validatePassword(passwordInput.getText().toString());
            passwordInput.setError(valRes.getError());
        }
    };
}