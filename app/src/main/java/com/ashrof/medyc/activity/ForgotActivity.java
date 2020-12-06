package com.ashrof.medyc.activity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.ashrof.medyc.R;
import com.ashrof.medyc.utils.Utils;
import com.google.firebase.auth.FirebaseAuth;

import java.util.regex.Pattern;

public class ForgotActivity extends AppCompatActivity {

    private EditText etEmail;
    private Button btnSubmit;

    private static boolean isValid(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\." +
                "[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                "A-Z]{2,7}$";
        Pattern pat = Pattern.compile(emailRegex);
        if (email == null)
            return false;
        return pat.matcher(email).matches();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot);
        btnSubmit = findViewById(R.id.btn_submit);
        etEmail = findViewById(R.id.et_email);

        setBtnSubmit();
    }

    private void setBtnSubmit() {
        btnSubmit.setOnClickListener(view -> {
            if (etEmail.getText().toString().isEmpty()) {
                Utils.ShowToast(ForgotActivity.this, "Please type your email");
            } else {
                if (isValid(etEmail.getText().toString())) {
                    FirebaseAuth.getInstance().sendPasswordResetEmail("user@example.com")
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    Utils.ShowToast(ForgotActivity.this, "Please check your email to reset password.");
                                    etEmail.getText().clear();
                                }
                            });
                } else {
                    Utils.ShowToast(ForgotActivity.this, "Email is not valid. Please try again.");
                }

            }
        });
    }
}