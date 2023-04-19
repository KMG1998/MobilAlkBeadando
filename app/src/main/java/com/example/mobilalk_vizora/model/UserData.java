package com.example.mobilalk_vizora.model;

import com.google.firebase.Timestamp;

public class UserData {
    private String userName;
    private String birthDate;
    private String userId;
    private Timestamp lastLogin;

    public UserData(){}

    public UserData(String birthDate, Timestamp lastLogin,String userId,String userName) {
        this.userName = userName;
        this.birthDate = birthDate;
        this.userId = userId;
        this.lastLogin = lastLogin;
    }

    public String getUserName() {
        return userName;
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

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public void setLastLogin(Timestamp lastLogin) {
        this.lastLogin = lastLogin;
    }

}
