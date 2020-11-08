package com.ashrof.medyc.activity.user;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.ashrof.medyc.R;

public class MedicinesDetailActivity extends AppCompatActivity {

    private String medicineUid;
    private String medicineName;

    private TextView textViewMedicineName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initIntent();
        setContentView(R.layout.activity_medicines_detail);
        initView();
    }

    private void initIntent() {
        final Intent intent = getIntent();
        medicineUid = intent.getStringExtra("medicineUid");
        medicineName = intent.getStringExtra("medicineName");

    }

    private void initView() {
        textViewMedicineName = findViewById(R.id.tv_name);
        textViewMedicineName.setText(medicineName);
    }
}