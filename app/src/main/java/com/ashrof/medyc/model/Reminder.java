package com.ashrof.medyc.model;

public class Reminder {

    private String reminderUid, medicineUid, onCreatedDate, takingMedicine, reason, typeReminder;
    private int month, day, hour, min;
    private long timeInMillisSet;


    public Reminder() {
    }

    public String getTypeReminder() {
        return typeReminder;
    }

    public void setTypeReminder(String typeReminder) {
        this.typeReminder = typeReminder;
    }

    public Reminder(String reminderUid, String medicineUid, String onCreatedDate, String takingMedicine, String reason, String typeReminder, int month, int day, int hour, int min, long timeInMillisSet) {
        this.reminderUid = reminderUid;
        this.medicineUid = medicineUid;
        this.onCreatedDate = onCreatedDate;
        this.takingMedicine = takingMedicine;
        this.reason = reason;
        this.typeReminder = typeReminder;
        this.month = month;
        this.day = day;
        this.hour = hour;
        this.min = min;
        this.timeInMillisSet = timeInMillisSet;
    }

    public long getTimeInMillisSet() {
        return timeInMillisSet;
    }

    public void setTimeInMillisSet(long timeInMillisSet) {
        this.timeInMillisSet = timeInMillisSet;
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
