package com.example.mobilalk_vizora.validators;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;

import androidx.annotation.RequiresApi;

import com.example.mobilalk_vizora.R;
import com.example.mobilalk_vizora.formatters.DateFormatters;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public final class InputValidator {
    Context ctx;

    public InputValidator(Context ctx){
        this.ctx = ctx;
    }

    public ValidationResult validateEmail(String email){
        if (TextUtils.isEmpty(email)) {
            return new ValidationResult(false,ctx.getString(R.string.error_required_field));
        } else if(!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return new ValidationResult(false,ctx.getString(R.string.error_email_format)) ;
        }
        return new ValidationResult(true,null);
    }

    public ValidationResult validatePassword(String password){
        if (TextUtils.isEmpty(password)) {
            return new ValidationResult(false,ctx.getString(R.string.error_required_field));
        } else if(password.length() < 8){
            return new ValidationResult(false,"A jelszónak legalább 8 karakter hosszúnak kell lennie");
        }
        return new ValidationResult(true,null);
    }

    public ValidationResult validateUserName(String username){
        if(TextUtils.isEmpty(username)){
            return new ValidationResult(false,ctx.getString(R.string.error_required_field));
        }else if(username.length() < 4){
            return new ValidationResult(false,ctx.getString(R.string.error_username_length));
        }
        return new ValidationResult(true,null);
    }
}
