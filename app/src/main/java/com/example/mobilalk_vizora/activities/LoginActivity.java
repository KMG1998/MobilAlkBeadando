package com.example.mobilalk_vizora.activities;

import static androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG;
import static androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_WEAK;
import static androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.mobilalk_vizora.R;
import com.example.mobilalk_vizora.fireBaseProvider.FireBaseProvider;
import com.example.mobilalk_vizora.validators.InputValidator;
import com.example.mobilalk_vizora.validators.ValidationResult;

import java.util.concurrent.Executor;

public class LoginActivity extends AppCompatActivity {
    InputValidator inputValidator;
    FireBaseProvider fBaseProvider;
    EditText emailInput;
    EditText passwordInput;
    BiometricManager bioManager;
    Executor bioExecutor;
    BiometricPrompt biometricPrompt;

    final int REQUEST_CODE_ASK_PERMISSIONS = 123;

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
        bioManager = BiometricManager.from(this);
        bioExecutor = ContextCompat.getMainExecutor(this);
    }

    @Override
    public void onStart() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.USE_BIOMETRIC) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.USE_BIOMETRIC}, REQUEST_CODE_ASK_PERMISSIONS);
            return;
        }
        super.onStart();
    }

    public void goToRegister(View view) {
        Intent registerIntent = new Intent(this, RegisterActivity.class);
        startActivity(registerIntent);
    }

    public void onLoginClick(View view) {
        if (validateInputs()) {
            int bioSupport = checkBiometricSupport();
            BiometricPrompt.PromptInfo.Builder promptInfo = new BiometricPrompt.PromptInfo.Builder();
            promptInfo.setTitle(getString(R.string.two_step_verify))
                    .setNegativeButtonText(getString(R.string.cancel))
                    .setAllowedAuthenticators(BIOMETRIC_STRONG | DEVICE_CREDENTIAL);
            if (bioSupport == 1) {
                biometricPrompt = new BiometricPrompt(LoginActivity.this, bioExecutor, new BiometricPrompt.AuthenticationCallback() {
                    @Override
                    public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                        Toast.makeText(getApplicationContext(), getString(R.string.auth_error), Toast.LENGTH_LONG).show();
                        super.onAuthenticationError(errorCode, errString);
                    }

                    @Override
                    public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                        super.onAuthenticationSucceeded(result);
                        loginWithEmail();
                    }

                    @Override
                    public void onAuthenticationFailed() {
                        Toast.makeText(getApplicationContext(), R.string.auth_failed, Toast.LENGTH_LONG).show();
                        super.onAuthenticationFailed();
                    }
                });
                biometricPrompt.authenticate(promptInfo.build());
            } else if (bioSupport == 2) {
                final Intent enrollIntent = new Intent(Settings.ACTION_BIOMETRIC_ENROLL);
                enrollIntent.putExtra(Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
                        BIOMETRIC_STRONG | DEVICE_CREDENTIAL);
                intentActivityResultLauncher.launch(enrollIntent);
            }else {
                loginWithEmail();
            }
        } else {
            Toast.makeText(this, getString(R.string.error_invalid_input), Toast.LENGTH_LONG).show();
        }
    }

    public void loginWithEmail() {
        String email = emailInput.getText().toString();
        String passw = passwordInput.getText().toString();
        fBaseProvider.loginWithEmail(email, passw).addOnSuccessListener(authResult -> {
            Intent mainIntent = new Intent(this, MainActivity.class);
            mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(mainIntent);
        });
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

    @Override
    public void onBackPressed() {
        //empty on back pressed to prevent accidental closing of the app
    }

    private int checkBiometricSupport() {
        switch (bioManager.canAuthenticate(BIOMETRIC_WEAK | BIOMETRIC_STRONG)) {
            case BiometricManager.BIOMETRIC_SUCCESS:
                return 1;
            case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
                return 2;
            case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
            case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:
            default:
                Toast.makeText(this, R.string.unknown_error, Toast.LENGTH_LONG).show();
                return 0;
        }
    }

    ActivityResultLauncher<Intent> intentActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
            });

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    onLoginClick(new View(this));
                } else {
                    Toast.makeText(this, "Permission denied!", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}