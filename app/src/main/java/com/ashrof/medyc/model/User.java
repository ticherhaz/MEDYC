package com.ashrof.medyc.model;

import com.ashrof.medyc.enumerator.Gender;
import com.ashrof.medyc.enumerator.Status;

public class User {

    private String userUid;
    private String fullName;
    private String email;
    private String profileUrl;
    private String birthday;
    private String mobile;
    private Gender gender;
    private Status status;
    private String onCreatedDate;

    public User(String userUid, String fullName, String email, String profileUrl, String birthday, String mobile, Gender gender, Status status, String onCreatedDate) {
        this.userUid = userUid;
        this.fullName = fullName;
        this.email = email;
        this.profileUrl = profileUrl;
        this.birthday = birthday;
        this.mobile = mobile;
        this.gender = gender;
        this.status = status;
        this.onCreatedDate = onCreatedDate;
    }

    public User() {
    }

    public String getUserUid() {
        return userUid;
    }

    public void setUserUid(String userUid) {
        this.userUid = userUid;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getProfileUrl() {
        return profileUrl;
    }

    public void setProfileUrl(String profileUrl) {
        this.profileUrl = profileUrl;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getOnCreatedDate() {
        return onCreatedDate;
    }

    public void setOnCreatedDate(String onCreatedDate) {
        this.onCreatedDate = onCreatedDate;
    }
}
