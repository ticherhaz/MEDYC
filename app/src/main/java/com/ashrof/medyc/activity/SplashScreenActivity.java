package com.ashrof.medyc.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.ashrof.medyc.R;
import com.ashrof.medyc.activity.administrator.AdminHomeActivity;
import com.ashrof.medyc.activity.user.HomeActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        final FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        boolean isLoggedIn;
        isLoggedIn = firebaseUser != null;

        new Handler().postDelayed(() -> {
            if (isLoggedIn) {
                if (Objects.requireNonNull(firebaseUser.getEmail()).equals("admin_ashrof@gmail.com")) {
                    startActivity(new Intent(this, AdminHomeActivity.class));
                } else {
                    startActivity(new Intent(this, HomeActivity.class));
                }
            } else {
                startActivity(new Intent(this, SignInActivity.class));
            }
            finish();
        }, 1000);
    }
}