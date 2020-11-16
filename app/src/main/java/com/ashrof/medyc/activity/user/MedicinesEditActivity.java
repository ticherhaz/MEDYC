package com.ashrof.medyc.activity.user;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.ashrof.medyc.R;
import com.ashrof.medyc.enumerator.Colour;
import com.ashrof.medyc.enumerator.Status;
import com.ashrof.medyc.enumerator.Ubat;
import com.ashrof.medyc.model.Medicines;
import com.ashrof.medyc.utils.Constant;
import com.ashrof.medyc.utils.Utils;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import net.ticherhaz.tarikhmasa.TarikhMasa;

public class MedicinesEditActivity extends AppCompatActivity {

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    private EditText editTextName;
    private ImageView ivColorRed, ivColorGreen, ivColorBrown, ivColorPurple;
    private ImageView ivUbat1, ivUbat2, ivUbat3, ivUbat4;

    private Button buttonUpdate, buttonDelete, buttonChangePicture, buttonChangeColour;
    private Colour pickedColour;
    private Ubat pickedUbat;

    private String medicinesUid, name, onCreatedDate, status, picture, color;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initIntent();
        initFirebase();
        setContentView(R.layout.activity_medicines_edit);
        initView();
        initDisplay();
        setButtonUpdate();
    }

    private void initIntent() {
        final Intent intent = getIntent();
        medicinesUid = intent.getStringExtra("medicinesUid");
        name = intent.getStringExtra("name");
        onCreatedDate = intent.getStringExtra("onCreatedDate");
        status = intent.getStringExtra("status");
        picture = intent.getStringExtra("picture");
        color = intent.getStringExtra("color");
    }

    private void initDisplay() {
        editTextName.setText(name);
        initUbat(Ubat.valueOf(picture));
        initColour(Colour.valueOf(color));
    }

    private void initView() {
        editTextName = findViewById(R.id.et_medicine_name);
        buttonUpdate = findViewById(R.id.btn_update);
        buttonDelete = findViewById(R.id.btn_delete);
        buttonChangeColour = findViewById(R.id.btn_change_colour);
        buttonChangePicture = findViewById(R.id.btn_change_picture);

        ivColorRed = findViewById(R.id.iv_red);
        ivColorGreen = findViewById(R.id.iv_light_green);
        ivColorBrown = findViewById(R.id.iv_brown);
        ivColorPurple = findViewById(R.id.iv_purple);

        ivUbat1 = findViewById(R.id.iv_ubat1);
        ivUbat2 = findViewById(R.id.iv_ubat2);
        ivUbat3 = findViewById(R.id.iv_ubat3);
        ivUbat4 = findViewById(R.id.iv_ubat4);

        setIvColorClicked(ivColorRed, Colour.RED);
        setIvColorClicked(ivColorGreen, Colour.GREEN);
        setIvColorClicked(ivColorBrown, Colour.BROWN);
        setIvColorClicked(ivColorPurple, Colour.PURPLE);

        setIvUbat(ivUbat1, Ubat.UBAT1);
        setIvUbat(ivUbat2, Ubat.UBAT2);
        setIvUbat(ivUbat3, Ubat.UBAT3);
        setIvUbat(ivUbat4, Ubat.UBAT4);

        setButtonChangeColour();
        setButtonChangePicture();
        setButtonDelete();
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

    private void setButtonUpdate() {
        buttonUpdate.setOnClickListener(view -> {
            if (!editTextName.getText().toString().isEmpty()) {
                if (pickedUbat != null) {
                    if (pickedColour != null) {
                        //here we are now ready to update to database
                        final String medicineUid = medicinesUid;
                        final Medicines medicines = new Medicines(medicineUid, editTextName.getText().toString(), TarikhMasa.GetTarikhMasa(), pickedUbat, pickedColour, Status.ACTIVE);
                        databaseReference.child(medicineUid).setValue(medicines).addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Utils.ShowToast(MedicinesEditActivity.this, "Updated medicine");
                                resetAll();
                                finish();
                            }
                        });
                    } else {
                        Utils.ShowToast(MedicinesEditActivity.this, "Please pick colour");
                    }
                } else {
                    Utils.ShowToast(MedicinesEditActivity.this, "Please pick medicine icon");
                }
            }
        });
    }

    private void setIvColorClicked(final ImageView iv, final Colour colour) {
        iv.setOnClickListener(view -> {
            switch (colour) {
                case RED:
                    pickedColour = Colour.RED;
                    ivColorRed.setVisibility(View.VISIBLE);
                    ivColorBrown.setVisibility(View.INVISIBLE);
                    ivColorGreen.setVisibility(View.INVISIBLE);
                    ivColorPurple.setVisibility(View.INVISIBLE);
                    break;
                case BROWN:
                    pickedColour = Colour.BROWN;
                    ivColorRed.setVisibility(View.INVISIBLE);
                    ivColorBrown.setVisibility(View.VISIBLE);
                    ivColorGreen.setVisibility(View.INVISIBLE);
                    ivColorPurple.setVisibility(View.INVISIBLE);
                    break;
                case GREEN:
                    pickedColour = Colour.GREEN;
                    ivColorRed.setVisibility(View.INVISIBLE);
                    ivColorBrown.setVisibility(View.INVISIBLE);
                    ivColorGreen.setVisibility(View.VISIBLE);
                    ivColorPurple.setVisibility(View.INVISIBLE);
                    break;
                case PURPLE:
                    pickedColour = Colour.PURPLE;
                    ivColorRed.setVisibility(View.INVISIBLE);
                    ivColorBrown.setVisibility(View.INVISIBLE);
                    ivColorGreen.setVisibility(View.INVISIBLE);
                    ivColorPurple.setVisibility(View.VISIBLE);
                    break;
            }
        });
    }

    private void initColour(final Colour colour) {
        switch (colour) {
            case RED:
                pickedColour = Colour.RED;
                ivColorRed.setVisibility(View.VISIBLE);
                ivColorBrown.setVisibility(View.INVISIBLE);
                ivColorGreen.setVisibility(View.INVISIBLE);
                ivColorPurple.setVisibility(View.INVISIBLE);
                break;
            case BROWN:
                pickedColour = Colour.BROWN;
                ivColorRed.setVisibility(View.INVISIBLE);
                ivColorBrown.setVisibility(View.VISIBLE);
                ivColorGreen.setVisibility(View.INVISIBLE);
                ivColorPurple.setVisibility(View.INVISIBLE);
                break;
            case GREEN:
                pickedColour = Colour.GREEN;
                ivColorRed.setVisibility(View.INVISIBLE);
                ivColorBrown.setVisibility(View.INVISIBLE);
                ivColorGreen.setVisibility(View.VISIBLE);
                ivColorPurple.setVisibility(View.INVISIBLE);
                break;
            case PURPLE:
                pickedColour = Colour.PURPLE;
                ivColorRed.setVisibility(View.INVISIBLE);
                ivColorBrown.setVisibility(View.INVISIBLE);
                ivColorGreen.setVisibility(View.INVISIBLE);
                ivColorPurple.setVisibility(View.VISIBLE);
                break;
        }
    }

    private void setIvUbat(final ImageView iv, final Ubat ubat) {
        iv.setOnClickListener(view -> {
            switch (ubat) {
                case UBAT1:
                    pickedUbat = Ubat.UBAT1;
                    ivUbat1.setVisibility(View.VISIBLE);
                    ivUbat2.setVisibility(View.INVISIBLE);
                    ivUbat3.setVisibility(View.INVISIBLE);
                    ivUbat4.setVisibility(View.INVISIBLE);
                    break;
                case UBAT2:
                    pickedUbat = Ubat.UBAT2;
                    ivUbat1.setVisibility(View.INVISIBLE);
                    ivUbat2.setVisibility(View.VISIBLE);
                    ivUbat3.setVisibility(View.INVISIBLE);
                    ivUbat4.setVisibility(View.INVISIBLE);
                    break;
                case UBAT3:
                    pickedUbat = Ubat.UBAT3;
                    ivUbat1.setVisibility(View.INVISIBLE);
                    ivUbat2.setVisibility(View.INVISIBLE);
                    ivUbat3.setVisibility(View.VISIBLE);
                    ivUbat4.setVisibility(View.INVISIBLE);
                    break;
                case UBAT4:
                    pickedUbat = Ubat.UBAT4;
                    ivUbat1.setVisibility(View.INVISIBLE);
                    ivUbat2.setVisibility(View.INVISIBLE);
                    ivUbat3.setVisibility(View.INVISIBLE);
                    ivUbat4.setVisibility(View.VISIBLE);
                    break;
            }
        });
    }

    private void initUbat(final Ubat ubat) {
        switch (ubat) {
            case UBAT1:
                pickedUbat = Ubat.UBAT1;
                ivUbat1.setVisibility(View.VISIBLE);
                ivUbat2.setVisibility(View.INVISIBLE);
                ivUbat3.setVisibility(View.INVISIBLE);
                ivUbat4.setVisibility(View.INVISIBLE);
                break;
            case UBAT2:
                pickedUbat = Ubat.UBAT2;
                ivUbat1.setVisibility(View.INVISIBLE);
                ivUbat2.setVisibility(View.VISIBLE);
                ivUbat3.setVisibility(View.INVISIBLE);
                ivUbat4.setVisibility(View.INVISIBLE);
                break;
            case UBAT3:
                pickedUbat = Ubat.UBAT3;
                ivUbat1.setVisibility(View.INVISIBLE);
                ivUbat2.setVisibility(View.INVISIBLE);
                ivUbat3.setVisibility(View.VISIBLE);
                ivUbat4.setVisibility(View.INVISIBLE);
                break;
            case UBAT4:
                pickedUbat = Ubat.UBAT4;
                ivUbat1.setVisibility(View.INVISIBLE);
                ivUbat2.setVisibility(View.INVISIBLE);
                ivUbat3.setVisibility(View.INVISIBLE);
                ivUbat4.setVisibility(View.VISIBLE);
                break;
        }
    }

    private void setButtonChangePicture() {
        buttonChangePicture.setOnClickListener(v -> {
            ivUbat1.setVisibility(View.VISIBLE);
            ivUbat2.setVisibility(View.VISIBLE);
            ivUbat3.setVisibility(View.VISIBLE);
            ivUbat4.setVisibility(View.VISIBLE);
        });
    }

    private void setButtonChangeColour() {
        buttonChangeColour.setOnClickListener(v -> {
            ivColorRed.setVisibility(View.VISIBLE);
            ivColorBrown.setVisibility(View.VISIBLE);
            ivColorGreen.setVisibility(View.VISIBLE);
            ivColorPurple.setVisibility(View.VISIBLE);
        });
    }

    private void setButtonDelete() {
        buttonDelete.setOnClickListener(v -> {
            Utils.ShowProgressDialog(MedicinesEditActivity.this);
            databaseReference.child(medicinesUid).removeValue().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Utils.ShowToast(MedicinesEditActivity.this, "Successfully deleted " + name);
                    resetAll();
                    Utils.DismissProgressDialog();
                    finish();
                }
            });
        });
    }

    private void resetAll() {
        editTextName.getText().clear();
        pickedUbat = null;
        pickedColour = null;

        ivColorRed.setVisibility(View.VISIBLE);
        ivColorBrown.setVisibility(View.VISIBLE);
        ivColorGreen.setVisibility(View.VISIBLE);
        ivColorPurple.setVisibility(View.VISIBLE);

        ivUbat1.setVisibility(View.VISIBLE);
        ivUbat2.setVisibility(View.VISIBLE);
        ivUbat3.setVisibility(View.VISIBLE);
        ivUbat4.setVisibility(View.VISIBLE);
    }
}