package com.ashrof.medyc.activity.user;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.ashrof.medyc.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddMedicinesActivity extends AppCompatActivity {

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    private EditText editTextName;
    private ImageView ivColorWhite, ivColorYellow, ivColorRed, ivColorPink, ivColorTeal, ivColorGreen,
            ivColorBlack, ivColorOrange, ivColorBrown, ivColorPurple, getIvColorBlue, getIvColorDarkGreen;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initFirebase();
        setContentView(R.layout.activity_add_medicines);
        initView();
    }

    private void initView() {
        editTextName = findViewById(R.id.et_medicine_name);

    }

    private void initFirebase() {
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
    }

    @Override
    public boolean onSupportNavigateUp() {
        getSupportFragmentManager().popBackStack();
        return super.onSupportNavigateUp();
    }
}