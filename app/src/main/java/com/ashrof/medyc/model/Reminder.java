package com.ashrof.medyc.model;

public class Reminder {

    private String reminderUid, medicineUid, onCreatedDate, takingMedicine, reason;
    private int month, day, hour, min;


    public Reminder() {
    }

    public Reminder(String reminderUid, String medicineUid, String onCreatedDate, String takingMedicine, String reason, int month, int day, int hour, int min) {
        this.reminderUid = reminderUid;
        this.medicineUid = medicineUid;
        this.onCreatedDate = onCreatedDate;
        this.takingMedicine = takingMedicine;
        this.reason = reason;
        this.month = month;
        this.day = day;
        this.hour = hour;
        this.min = min;
    }

    public String getTakingMedicine() {
        return takingMedicine;
    }

    public void setTakingMedicine(String takingMedicine) {
        this.takingMedicine = takingMedicine;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getReminderUid() {
        return reminderUid;
    }

    public void setReminderUid(String reminderUid) {
        this.reminderUid = reminderUid;
    }

    public String getMedicineUid() {
        return medicineUid;
    }

    public void setMedicineUid(String medicineUid) {
        this.medicineUid = medicineUid;
    }

    public String getOnCreatedDate() {
        return onCreatedDate;
    }

    public void setOnCreatedDate(String onCreatedDate) {
        this.onCreatedDate = onCreatedDate;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMin() {
        return min;
    }

    public void setMin(int min) {
        this.min = min;
    }
}
