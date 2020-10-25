package com.ashrof.medyc.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.ashrof.medyc.R;
import com.ashrof.medyc.activity.administrator.AdminHomeActivity;
import com.ashrof.medyc.activity.user.HomeActivity;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

import static com.ashrof.medyc.utils.Utils.IsNetworkAvailable;
import static com.ashrof.medyc.utils.Utils.ShowAlertDialog;

public class SignInActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;

    private TextView textViewForgetPassword, textViewRegister;
    private EditText editTextEmail, editTextPassword;
    private ProgressBar progressBar;

    private Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        editTextEmail = findViewById(R.id.et_email);
        editTextPassword = findViewById(R.id.et_password);
        textViewForgetPassword = findViewById(R.id.tv_forget_password);
        textViewRegister = findViewById(R.id.tv_register);
        progressBar = findViewById(R.id.pb);
        btnLogin = findViewById(R.id.btn_login);
        firebaseAuth = FirebaseAuth.getInstance();
        setBtnLogin();
        setTextViewRegister();
        setTextViewForgetPassword();
    }

    private void setBtnLogin() {
        btnLogin.setOnClickListener(view -> {
            if (!TextUtils.isEmpty(editTextEmail.getText().toString()) || !TextUtils.isEmpty(editTextPassword.getText().toString())) {
                //Check internet
                if (IsNetworkAvailable(SignInActivity.this)) {
                    progressBar.setVisibility(View.VISIBLE);
                    btnLogin.setVisibility(View.INVISIBLE);
                    final String email = editTextEmail.getText().toString();
                    final String password = editTextPassword.getText().toString();

                    firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            //That's mean success to sign in.
                            //Check if admin or not
                            if (editTextEmail.getText().toString().equals("admin_ashrof@gmail.com")) {
                                startActivity(new Intent(SignInActivity.this, AdminHomeActivity.class));
                            } else {
                                startActivity(new Intent(SignInActivity.this, HomeActivity.class));
                            }
                            progressBar.setVisibility(View.INVISIBLE);
                            btnLogin.setVisibility(View.VISIBLE);
                            finish();

                        } else {
                            progressBar.setVisibility(View.INVISIBLE);
                            btnLogin.setVisibility(View.VISIBLE);
                            ShowAlertDialog(SignInActivity.this, "Failed login", Objects.requireNonNull(task.getException()).getMessage());
                        }
                    });
                } else {
                    progressBar.setVisibility(View.INVISIBLE);
                    btnLogin.setVisibility(View.VISIBLE);
                    ShowAlertDialog(SignInActivity.this, "Warn", "Please check your internet connection");
                }
            } else {
                ShowAlertDialog(SignInActivity.this, "Info", "Please fill in email/password");
            }
        });
    }

    private void setTextViewRegister() {
        textViewRegister.setOnClickListener(view -> {
            startActivity(new Intent(SignInActivity.this, SignUpActivity.class));
            finish();
        });
    }

    private void setTextViewForgetPassword() {
        textViewForgetPassword.setOnClickListener(view -> {
            startActivity(new Intent(SignInActivity.this, ForgotActivity.class));
            finish();
        });
    }
}