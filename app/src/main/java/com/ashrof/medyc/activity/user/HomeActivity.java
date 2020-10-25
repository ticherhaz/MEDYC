package com.ashrof.medyc.activity.user;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.ashrof.medyc.R;
import com.ashrof.medyc.activity.SignInActivity;
import com.ashrof.medyc.model.User;
import com.ashrof.medyc.utils.Constant;
import com.ashrof.medyc.utils.Simpan;
import com.bumptech.glide.Glide;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mikhaellopez.circularimageview.CircularImageView;

import static com.ashrof.medyc.utils.Utils.ShowToast;

public class HomeActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private NavigationView navigationView;
    private DrawerLayout drawer;
    private View viewHeader;

    private TextView tvFullName, tvEmail;
    private CircularImageView ivProfile;

    private FirebaseUser firebaseUser;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    private String userUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawer = findViewById(R.id.drawer_layout);

        //Init nav header
        navigationView = findViewById(R.id.nav_view);
        viewHeader = navigationView.getHeaderView(0);
        tvFullName = viewHeader.findViewById(R.id.tv_full_name);
        tvEmail = viewHeader.findViewById(R.id.tv_email);
        ivProfile = viewHeader.findViewById(R.id.iv_profile);

        //Call data
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser != null) {
            userUid = firebaseUser.getUid();
            firebaseDatabase = FirebaseDatabase.getInstance();
            databaseReference = firebaseDatabase.getReference().child("user").child(userUid);

            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        final User user = snapshot.getValue(User.class);
                        if (user != null) {
                            tvFullName.setText(user.getFullName());
                            tvEmail.setText(user.getEmail());

                            Glide.with(HomeActivity.this)
                                    .load(user.getProfileUrl())
                                    .into(ivProfile);

                            //Save object to sharedpreference
                            Simpan.getInstance().saveObject(Constant.USER_DATA_KEY, user);
                        }

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

        final MenuItem menuItemSignOut = navigationView.getMenu().findItem(R.id.nav_sign_out);
        menuItemSignOut.setOnMenuItemClickListener(menuItem -> {
            firebaseAuth.signOut();
            Simpan.getInstance().clearSession();
            ShowToast(HomeActivity.this, "Signed out successfully");
            startActivity(new Intent(HomeActivity.this, SignInActivity.class));
            finish();
            return true;
        });


        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(R.id.nav_home, R.id.nav_dashboard, R.id.nav_profile, R.id.nav_medicines, R.id.nav_statistics, R.id.nav_feedback, R.id.nav_settings)
                .setOpenableLayout(drawer)
                .build();
        final NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
    }

    @Override
    public boolean onSupportNavigateUp() {
        final NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}