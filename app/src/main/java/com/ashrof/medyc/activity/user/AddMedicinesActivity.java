package com.ashrof.medyc.activity.user;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.ashrof.medyc.R;
import com.ashrof.medyc.utils.Constant;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddMedicinesActivity extends AppCompatActivity {

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    private EditText editTextName;
    private ImageView ivColorWhite, ivColorYellow, ivColorRed, ivColorPink, ivColorTeal, ivColorGreen,
            ivColorBlack, ivColorOrange, ivColorBrown, ivColorPurple, ivColorBlue, ivColorDarkGreen;

    private Button buttonAdd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initFirebase();
        setContentView(R.layout.activity_add_medicines);
        initView();
        setButtonAdd();
    }

    private void initView() {
        editTextName = findViewById(R.id.et_medicine_name);
        buttonAdd = findViewById(R.id.btn_add);

        ivColorWhite = findViewById(R.id.iv_white);
        ivColorYellow = findViewById(R.id.iv_yellow);
        ivColorRed = findViewById(R.id.iv_red);
        ivColorPink = findViewById(R.id.iv_pink);
        ivColorTeal = findViewById(R.id.iv_teal);
        ivColorGreen = findViewById(R.id.iv_light_green);
        ivColorBlack = findViewById(R.id.iv_black);
        ivColorOrange = findViewById(R.id.iv_orange);
        ivColorBrown = findViewById(R.id.iv_brown);
        ivColorPurple = findViewById(R.id.iv_purple);
        ivColorBlue = findViewById(R.id.iv_blue);
        ivColorDarkGreen = findViewById(R.id.iv_green);

    }

    private void initFirebase() {
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference().child(Constant.DB_MEDICINE);
    }

    @Override
    public boolean onSupportNavigateUp() {
        getSupportFragmentManager().popBackStack();
        return super.onSupportNavigateUp();
    }

    private void setButtonAdd(){
        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!editTextName.getText().toString().isEmpty()){

                }
            }
        });
    }
}