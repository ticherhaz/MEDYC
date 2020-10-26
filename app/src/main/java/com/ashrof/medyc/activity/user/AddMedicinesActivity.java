package com.ashrof.medyc.activity.user;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.ashrof.medyc.R;

public class AddMedicinesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_medicines);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}