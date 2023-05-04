package com.example.mobilalk_vizora.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mobilalk_vizora.R;
import com.example.mobilalk_vizora.fireBaseProvider.FireBaseProvider;
import com.example.mobilalk_vizora.formatters.DateFormatters;
import com.example.mobilalk_vizora.validators.InputValidator;
import com.example.mobilalk_vizora.validators.ValidationResult;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointBackward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

public class RegisterActivity extends AppCompatActivity {

    EditText emailEditText;
    EditText birthEditText;
    EditText passwordEditText;
    EditText passwordRepEditText;
    Button registerButton;
    MaterialDatePicker<Long> datePicker;
    InputValidator inputValidator;
    FireBaseProvider fBaseProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        inputValidator = new InputValidator(this);
        fBaseProvider = new FireBaseProvider();
        emailEditText = findViewById(R.id.registerEmail);
        birthEditText = findViewById(R.id.registerBirth);
        passwordEditText = findViewById(R.id.registerPass);
        passwordRepEditText = findViewById(R.id.registerPassRep);
        registerButton = findViewById(R.id.registerButton);

        CalendarConstraints.Builder constraintsBuilder = new CalendarConstraints.Builder();
        constraintsBuilder.setValidator(DateValidatorPointBackward.now());
        datePicker = MaterialDatePicker.Builder.datePicker().setInputMode(MaterialDatePicker.INPUT_MODE_CALENDAR).setCalendarConstraints(constraintsBuilder.build()).build();

        birthEditText.setInputType(InputType.TYPE_NULL);
        birthEditText.setOnFocusChangeListener(birthFocusListener);
        emailEditText.setOnFocusChangeListener(emailFocusListener);
        passwordEditText.setOnFocusChangeListener(passwordFocusListener);
        passwordRepEditText.setOnFocusChangeListener(passwordRepFocusListener);

        datePicker.addOnPositiveButtonClickListener(datePickerPositiveListener);
        datePicker.addOnDismissListener(dismissListener);
    }

    public void onRegisterClick(View view) {
        if (validateFields()) {
            registerButton.setEnabled(false);
            String email = emailEditText.getText().toString();
            String password = passwordEditText.getText().toString();
            String birthDate = birthEditText.getText().toString();
            fBaseProvider.registerUser(email, password).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    fBaseProvider.createUserData(birthDate, task.getResult().getUser().getUid()).addOnCompleteListener(documentReference -> {
                        if (documentReference.isSuccessful()) {
                            Toast.makeText(RegisterActivity.this, R.string.register_success_toast, Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(this, LoginActivity.class);
                            startActivity(intent);
                        } else
                            Toast.makeText(RegisterActivity.this, R.string.error_register_failed, Toast.LENGTH_LONG).show();
                    });
                } else {
                    Toast.makeText(RegisterActivity.this, R.string.error_register_failed, Toast.LENGTH_LONG).show();
                    registerButton.setEnabled(true);
                }
            });
        }else{
            Toast.makeText(this,R.string.error_invalid_input,Toast.LENGTH_SHORT).show();
        }
    }

    private boolean validateFields() {
        return (emailEditText.getError() == null && !TextUtils.isEmpty(emailEditText.getText()))
                && (birthEditText.getError() == null && !TextUtils.isEmpty(birthEditText.getText()))
                && (passwordEditText.getError() == null && !TextUtils.isEmpty(emailEditText.getText()))
                && (passwordRepEditText.getError() == null && !TextUtils.isEmpty(emailEditText.getText()));

    }

    View.OnFocusChangeListener birthFocusListener = (v, hasFocus) -> {
        if (hasFocus) {
            datePicker.show(getSupportFragmentManager(), "DATE_PICKER");
        } else {
            if (birthEditText.getText().toString().length() == 0) {
                birthEditText.setError(getString(R.string.error_required_field));
            } else {
                birthEditText.setError(null);
            }
        }
    };

    MaterialPickerOnPositiveButtonClickListener<Long> datePickerPositiveListener = selection -> {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        calendar.setTimeInMillis(selection);
        String formattedDate = DateFormatters.getDateFormat().format(calendar.getTime());
        birthEditText.setText(formattedDate);
    };

    DialogInterface.OnDismissListener dismissListener = dialog -> birthEditText.clearFocus();

    View.OnFocusChangeListener emailFocusListener = (v, hasFocus) -> {
        if (!hasFocus) {
            ValidationResult valRes = inputValidator.validateEmail(emailEditText.getText().toString());
            emailEditText.setError(valRes.getError());
        }
    };

    View.OnFocusChangeListener passwordFocusListener = (v, hasFocus) -> {
        if (!hasFocus) {
            ValidationResult valRes = inputValidator.validatePassword(passwordEditText.getText().toString());
            passwordEditText.setError(valRes.getError());
        }
    };

    View.OnFocusChangeListener passwordRepFocusListener = (v, hasFocus) -> {
        if (!hasFocus) {
            String password = passwordEditText.getText().toString();
            String passwordRep = passwordRepEditText.getText().toString();
            if (password.compareTo(passwordRep) == 0) {
                passwordRepEditText.setError(null);
            } else {
                passwordRepEditText.setError(getString(R.string.error_passw_mismatch));
            }
        }
    };
}