package com.ashrof.medyc.model;

import com.ashrof.medyc.enumerator.Colour;
import com.ashrof.medyc.enumerator.Status;

public class Medicines {

    private String medicinesUid, name, onCreatedDate;
    private int medicinePicture;
    private Colour colourMedicine;
    private Status status;

    public Medicines() {
    }

    public Medicines(String medicinesUid, String name, String onCreatedDate, int medicinePicture, Colour colourMedicine, Status status) {
        this.medicinesUid = medicinesUid;
        this.name = name;
        this.onCreatedDate = onCreatedDate;
        this.medicinePicture = medicinePicture;
        this.colourMedicine = colourMedicine;
        this.status = status;
    }

    public String getMedicinesUid() {
        return medicinesUid;
    }

    public void setMedicinesUid(String medicinesUid) {
        this.medicinesUid = medicinesUid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOnCreatedDate() {
        return onCreatedDate;
    }

    public void setOnCreatedDate(String onCreatedDate) {
        this.onCreatedDate = onCreatedDate;
    }

    public int getMedicinePicture() {
        return medicinePicture;
    }

    public void setMedicinePicture(int medicinePicture) {
        this.medicinePicture = medicinePicture;
    }

    public Colour getColourMedicine() {
        return colourMedicine;
    }

    public void setColourMedicine(Colour colourMedicine) {
        this.colourMedicine = colourMedicine;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
