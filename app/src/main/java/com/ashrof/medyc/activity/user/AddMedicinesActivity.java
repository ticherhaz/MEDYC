package com.ashrof.medyc.activity.user;

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
import com.ashrof.medyc.model.User;
import com.ashrof.medyc.utils.Constant;
import com.ashrof.medyc.utils.Simpan;
import com.ashrof.medyc.utils.Utils;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import net.ticherhaz.tarikhmasa.TarikhMasa;

public class AddMedicinesActivity extends AppCompatActivity {

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    private User user;

    private EditText editTextName;
    private ImageView ivColorRed, ivColorGreen, ivColorBrown, ivColorPurple;
    private ImageView ivUbat1, ivUbat2, ivUbat3, ivUbat4;

    private Button buttonSave, buttonReset;
    private Colour pickedColour;
    private Ubat pickedUbat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initFirebase();
        setContentView(R.layout.activity_add_medicines);
        user = Simpan.getInstance().getObject(Constant.USER_DATA_KEY, User.class);
        initView();
        setButtonAdd();
        setButtonReset();
    }

    private void initView() {
        editTextName = findViewById(R.id.et_medicine_name);
        buttonSave = findViewById(R.id.btn_save);
        buttonReset = findViewById(R.id.btn_reset);

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

    private void setButtonAdd() {
        buttonSave.setOnClickListener(view -> {
            if (!editTextName.getText().toString().isEmpty()) {
                if (pickedUbat != null) {
                    if (pickedColour != null) {
                        //here we are now ready to update to database
                        final String medicineUid = databaseReference.push().getKey();
                        final Medicines medicines = new Medicines(medicineUid, editTextName.getText().toString(), TarikhMasa.GetTarikhMasa(), pickedUbat, pickedColour, Status.ACTIVE);
                        assert medicineUid != null;
                        databaseReference.child(user.getUserUid()).child(medicineUid).setValue(medicines).addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Utils.ShowToast(AddMedicinesActivity.this, "Added new medicine");
                                resetAll();
                            }
                        });
                    } else {
                        Utils.ShowToast(AddMedicinesActivity.this, "Please pick colour");
                    }
                } else {
                    Utils.ShowToast(AddMedicinesActivity.this, "Please pick medicine icon");
                }

            }
        });
    }

    private void setButtonReset() {
        buttonReset.setOnClickListener(view -> {
            Utils.ShowToast(AddMedicinesActivity.this, "Reset successfully");
            resetAll();
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

    private void setIvUbat(final ImageView iv, final Ubat ubat) {
        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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