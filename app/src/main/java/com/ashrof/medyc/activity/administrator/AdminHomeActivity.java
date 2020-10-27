package com.ashrof.medyc.activity.administrator;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.ashrof.medyc.R;
import com.ashrof.medyc.activity.SignInActivity;
import com.ashrof.medyc.utils.Utils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AdminHomeActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private TextView tvSignOut;

    private CardView cardViewUser, cardViewFeedback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        setContentView(R.layout.activity_admin_home);
        initView();

        setTvSignOut();
        setCardViewUser();
        setCardViewFeedback();
    }

    private void initView() {
        cardViewUser = findViewById(R.id.cv_user);
        cardViewFeedback = findViewById(R.id.cv_feedback);

        tvSignOut = findViewById(R.id.tv_sign_out);
    }

    private void setCardViewUser() {
        cardViewUser.setOnClickListener(view -> startActivity(new Intent(AdminHomeActivity.this, AdminUserActivity.class)));
    }

    private void setCardViewFeedback() {
        cardViewFeedback.setOnClickListener(view -> startActivity(new Intent(AdminHomeActivity.this, AdminFeedbackActivity.class)));
    }

    private void setTvSignOut() {
        tvSignOut.setOnClickListener(view -> {
            firebaseAuth.signOut();
            Utils.ShowToast(AdminHomeActivity.this, "Cya admin...");
            startActivity(new Intent(AdminHomeActivity.this, SignInActivity.class));
            finish();
        });
    }
}