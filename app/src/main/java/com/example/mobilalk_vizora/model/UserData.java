package com.example.mobilalk_vizora.model;

import com.google.firebase.Timestamp;

public class UserData {
    private String birthDate;
    private String userId;
    private Timestamp lastLogin;

    public UserData(){}

    public UserData(String birthDate, Timestamp lastLogin,String userId) {
        this.birthDate = birthDate;
        this.userId = userId;
        this.lastLogin = lastLogin;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public String getUserId() {
        return userId;
    }

    public Timestamp getLastLogin() {
        return lastLogin;
    }


    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public void setLastLogin(Timestamp lastLogin) {
        this.lastLogin = lastLogin;
    }

}
