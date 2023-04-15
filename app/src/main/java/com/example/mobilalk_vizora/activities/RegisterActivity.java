package com.example.mobilalk_vizora.activities;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mobilalk_vizora.R;

import com.example.mobilalk_vizora.fireBaseProvider.FireBaseProvider;
import com.example.mobilalk_vizora.validators.InputValidator;
import com.example.mobilalk_vizora.validators.ValidationResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointBackward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.firebase.auth.AuthResult;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

public class RegisterActivity extends AppCompatActivity {

    EditText emailEditText;
    EditText nameEditText;
    EditText birthEditText;
    EditText passwordEditText;
    EditText passwordRepEditText;
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
        nameEditText = findViewById(R.id.registerName);
        birthEditText = findViewById(R.id.registerBirth);
        passwordEditText = findViewById(R.id.registerPass);
        passwordRepEditText = findViewById(R.id.registerPassRep);

        CalendarConstraints.Builder constraintsBuilder = new CalendarConstraints.Builder();
        constraintsBuilder.setValidator(DateValidatorPointBackward.now());
        datePicker = MaterialDatePicker.Builder.datePicker()
                .setInputMode(MaterialDatePicker.INPUT_MODE_CALENDAR)
                .setCalendarConstraints(constraintsBuilder.build())
                .build();

        birthEditText.setInputType(InputType.TYPE_NULL);
        birthEditText.setOnFocusChangeListener(birthFocusListener);
        emailEditText.setOnFocusChangeListener(emailFocusListener);
        nameEditText.setOnFocusChangeListener(nameFocusListener);
        passwordEditText.setOnFocusChangeListener(passwordFocusListener);
        passwordRepEditText.setOnFocusChangeListener(passwordRepFocusListener);

        datePicker.addOnPositiveButtonClickListener(datePickerPositiveListener);
        datePicker.addOnDismissListener(dismissListener);
    }

    public void onRegisterClick(View view) {
        if (validateFields()) {
            String email = emailEditText.getText().toString();
            String password = passwordEditText.getText().toString();
            String userName = nameEditText.getText().toString();
            String birthDate = birthEditText.getText().toString();
            fBaseProvider.registerUser(email,password).addOnCompleteListener(this, task -> {
                if(task.isSuccessful()) {
                    task.getResult().getUser().getUid();
                } else {
                    Toast.makeText(RegisterActivity.this, R.string.error_register_failed, Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    private boolean validateFields() {
        return emailEditText.getError() == null &&
                nameEditText.getError() == null &&
                birthEditText.getError() == null &&
                passwordEditText.getError() == null &&
                passwordRepEditText.getError() == null;

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
        SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd");
        String formattedDate = format.format(calendar.getTime());
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

    View.OnFocusChangeListener nameFocusListener = (v, hasFocus) -> {
        if (!hasFocus) {
            ValidationResult valRes = inputValidator.validateUserName(nameEditText.getText().toString());
            nameEditText.setError(valRes.getError());
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