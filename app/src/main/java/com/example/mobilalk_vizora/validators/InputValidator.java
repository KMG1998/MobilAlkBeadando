package com.example.mobilalk_vizora.validators;

import android.content.Context;
import android.text.TextUtils;

import com.example.mobilalk_vizora.R;

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
}
