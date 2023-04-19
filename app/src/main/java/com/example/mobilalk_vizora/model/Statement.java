package com.example.mobilalk_vizora.model;

import com.google.firebase.Timestamp;

public class Statement {

    private boolean approved;
    private Timestamp timestamp;
    private String userId;
    private double waterAmount;

    public Statement() {
    }

    public Statement(boolean approved,Timestamp timestamp, String userId, double waterAmount) {
        this.approved = approved;
        this.timestamp = timestamp;
        this.userId = userId;
        this.waterAmount = waterAmount;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public double getWaterAmount() {
        return waterAmount;
    }

    public void setWaterAmount(double waterAmount) {
        this.waterAmount = waterAmount;
    }

    public boolean isApproved() {
        return approved;
    }

    public void setApproved(boolean approved) {
        this.approved = approved;
    }
}
