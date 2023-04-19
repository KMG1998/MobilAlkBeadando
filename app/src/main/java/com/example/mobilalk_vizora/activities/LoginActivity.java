package com.example.mobilalk_vizora.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mobilalk_vizora.R;
import com.example.mobilalk_vizora.fireBaseProvider.FireBaseProvider;
import com.example.mobilalk_vizora.validators.InputValidator;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    InputValidator inputValidator;
    FireBaseProvider fBaseProvider;
    EditText emailInput;
    EditText passwordInput;

    private static final String LOG_TAG = LoginActivity.class.getName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        inputValidator = new InputValidator(this);
        fBaseProvider = new FireBaseProvider();
        emailInput = findViewById(R.id.loginEmail);
        passwordInput = findViewById(R.id.loginPassword);
    }

    public void goToRegister(View view){
        Intent registerIntent = new Intent(this, RegisterActivity.class);
        startActivity(registerIntent);
    }

    public void loginWithEmail(View view){
        Log.d("LOG_TAG", "in login");
        String email = emailInput.getText().toString();
        String passw = passwordInput.getText().toString();
        fBaseProvider.loginWithEmail(email,passw).addOnSuccessListener(authResult -> {
            Intent maintIntent = new Intent(this, MainActivity.class);
            startActivity(maintIntent);
        });
    }

    private boolean validateInputs(){
        return emailInput.getError() == null && passwordInput.getError() == null;
    }
}