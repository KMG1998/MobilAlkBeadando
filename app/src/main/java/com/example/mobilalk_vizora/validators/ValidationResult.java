package com.example.mobilalk_vizora.validators;

import androidx.annotation.Nullable;

public class ValidationResult {
    private boolean isValid;
    @Nullable
    private String error;

    public ValidationResult(boolean isValid, @Nullable String error){
        this.isValid = isValid;
        this.error = error;
    }

    public boolean isValid() {
        return isValid;
    }

    @Nullable
    public String getError() {
        return error;
    }
}
