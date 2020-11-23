package com.ashrof.medyc.activity.user;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.ashrof.medyc.R;
import com.ashrof.medyc.model.User;
import com.ashrof.medyc.utils.Constant;
import com.ashrof.medyc.utils.Simpan;
import com.ashrof.medyc.utils.Utils;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MedicinesDetailActivity extends AppCompatActivity {

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private String medicineUid;
    private String medicineName;
    private String reminderUid;

    private TextView textViewMedicineName;
    private EditText etReason;

    private Button btnSave;

    private String isTakingMedicine = null;

    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initIntent();
        initFirebase();
        setContentView(R.layout.activity_medicines_detail);
        initView();
    }

    private void initIntent() {
        final Intent intent = getIntent();
        medicineUid = intent.getStringExtra("medicineUid");
        medicineName = intent.getStringExtra("medicineName");
        reminderUid = intent.getStringExtra("reminderUid");
    }

    private void initView() {
        textViewMedicineName = findViewById(R.id.tv_name);
        textViewMedicineName.setText(medicineName);

        etReason = findViewById(R.id.et_reason);
        btnSave = findViewById(R.id.btn_save);

        setRadioButton();
        setBtnSave();
    }

    private void initFirebase() {
        user = Simpan.getInstance().getObject(Constant.USER_DATA_KEY, User.class);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference().child(Constant.DB_REMINDER).child(user.getUserUid());
    }

    private void setRadioButton() {
        final RadioGroup rg = findViewById(R.id.rg_medicine);
        rg.setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId) {
                case R.id.rb_0:
                    isTakingMedicine = "no";
                    break;
                case R.id.rb_1:
                    isTakingMedicine = "yes";
                    break;
            }
        });
    }

    private void setBtnSave() {
        btnSave.setOnClickListener(view -> {
            if (isTakingMedicine != null) {
                databaseReference.child(reminderUid).child("takingMedicine").setValue(isTakingMedicine);
                final String reason = etReason.getText().toString();
                if (reason != null && !reason.isEmpty()) {
                    databaseReference.child(reminderUid).child("reason").setValue(reason);
                }
                Utils.ShowToast(MedicinesDetailActivity.this, "Record saved successfully");
            } else {
                Utils.ShowToast(MedicinesDetailActivity.this, "Please choose the yes or no");
            }
        });
    }
}