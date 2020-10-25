package com.ashrof.medyc.model;

public class Feedback {

    private String name, date, description, profileUrl;
    private double ratingStar;

    public Feedback() {
    }

    public Feedback(String name, String date, String description, String profileUrl, double ratingStar) {
        this.name = name;
        this.date = date;
        this.description = description;
        this.profileUrl = profileUrl;
        this.ratingStar = ratingStar;
    }

    public double getRatingStar() {
        return ratingStar;
    }

    public void setRatingStar(double ratingStar) {
        this.ratingStar = ratingStar;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getProfileUrl() {
        return profileUrl;
    }

    public void setProfileUrl(String profileUrl) {
        this.profileUrl = profileUrl;
    }
}
