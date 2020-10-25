package com.ashrof.medyc.model;

public class Payment {

    private String userUid, type, onCreatedDate;
    private double amount;

    public Payment() {
    }

    public Payment(String userUid, String type, String onCreatedDate, double amount) {
        this.userUid = userUid;
        this.type = type;
        this.onCreatedDate = onCreatedDate;
        this.amount = amount;
    }

    public String getUserUid() {
        return userUid;
    }

    public void setUserUid(String userUid) {
        this.userUid = userUid;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getOnCreatedDate() {
        return onCreatedDate;
    }

    public void setOnCreatedDate(String onCreatedDate) {
        this.onCreatedDate = onCreatedDate;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}
