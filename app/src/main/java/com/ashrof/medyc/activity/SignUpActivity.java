package com.ashrof.medyc.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.ashrof.medyc.R;
import com.ashrof.medyc.activity.user.HomeActivity;
import com.ashrof.medyc.enumerator.Gender;
import com.ashrof.medyc.enumerator.Status;
import com.ashrof.medyc.model.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.soundcloud.android.crop.Crop;

import java.io.File;
import java.util.Objects;

import static com.ashrof.medyc.utils.Utils.IsNetworkAvailable;
import static com.ashrof.medyc.utils.Utils.ShowAlertDialog;
import static com.ashrof.medyc.utils.Utils.ShowToast;
import static net.ticherhaz.tarikhmasa.TarikhMasa.GetTarikhMasa;

public class SignUpActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;

    private EditText etFullName, etEmail, etPassword, etConfirmPassword;
    private Button btnRegister;
    private ImageView ivCamera, ivProfile;
    private ProgressBar progressBar;

    private Uri profileUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        etFullName = findViewById(R.id.et_full_name);
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        etConfirmPassword = findViewById(R.id.et_confirm_password);

        btnRegister = findViewById(R.id.btn_register);
        ivCamera = findViewById(R.id.iv_upload_profile);
        ivProfile = findViewById(R.id.iv_profile);
        progressBar = findViewById(R.id.pb);

        firebaseAuth = FirebaseAuth.getInstance();
        setBtnRegister();
        setIvCamera();
    }

    private void setBtnRegister() {
        btnRegister.setOnClickListener(view -> {
            if (!TextUtils.isEmpty(etFullName.getText().toString()) || !TextUtils.isEmpty(etEmail.getText().toString()) || !TextUtils.isEmpty(etPassword.getText().toString()) || !TextUtils.isEmpty(etConfirmPassword.getText().toString())) {
                //Check internet
                if (IsNetworkAvailable(SignUpActivity.this)) {
                    progressBar.setVisibility(View.VISIBLE);
                    btnRegister.setVisibility(View.INVISIBLE);

                    final String fullName = etFullName.getText().toString();
                    final String email = etEmail.getText().toString();
                    final String password = etPassword.getText().toString();
                    final String confirmPassword = etConfirmPassword.getText().toString();

                    if (!password.equals(confirmPassword)) {
                        progressBar.setVisibility(View.INVISIBLE);
                        btnRegister.setVisibility(View.VISIBLE);
                        ShowAlertDialog(SignUpActivity.this, "Warn", "Password is not match");
                        return;
                    }

                    firebaseAuth.createUserWithEmailAndPassword(email, confirmPassword).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            //That's mean success to sign in.
                            final FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                            if (firebaseUser != null) {
                                final String userUid = firebaseUser.getUid();
                                final String onCreatedDate = GetTarikhMasa();

                                //ready to upload the image to firebase storage
                                uploadImage(userUid, fullName, email, onCreatedDate, firebaseUser);
                            }

                        } else {
                            progressBar.setVisibility(View.INVISIBLE);
                            btnRegister.setVisibility(View.VISIBLE);
                            ShowAlertDialog(SignUpActivity.this, "Failed", Objects.requireNonNull(task.getException()).getMessage());
                        }
                    });
                } else {
                    ShowAlertDialog(SignUpActivity.this, "Warn", "Please check your internet connection");
                    progressBar.setVisibility(View.INVISIBLE);
                    btnRegister.setVisibility(View.VISIBLE);
                }
            } else {
                ShowAlertDialog(SignUpActivity.this, "Info", "Please fill in email/password");
                progressBar.setVisibility(View.INVISIBLE);
                btnRegister.setVisibility(View.VISIBLE);

            }
        });
    }

    private void setIvCamera() {
        ivCamera.setOnClickListener(view -> Crop.pickImage(SignUpActivity.this));
    }

    private void uploadImage(final String userUid, final String fullName, final String email, final String onCreatedDate, final FirebaseUser firebaseUser) {
        if (profileUrl != null) {
            final FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
            final DatabaseReference databaseReference = firebaseDatabase.getReference();

            final String uploadUid = databaseReference.push().getKey();

            final StorageReference storageReference = FirebaseStorage.getInstance().getReference();
            final StorageReference ref = storageReference.child("profileImage/" + userUid + "/" + uploadUid);

            ref.putFile(profileUrl)
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressBar.setVisibility(View.INVISIBLE);
                            btnRegister.setVisibility(View.VISIBLE);
                            ShowAlertDialog(SignUpActivity.this, "Failed Upload", Objects.requireNonNull(e.getMessage()));
                        }
                    })
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            ref.getDownloadUrl().addOnSuccessListener(uri -> {
                                if (uri != null) {
                                    final String profileUrl = String.valueOf(uri);

                                    UserProfileChangeRequest userProfileChangeRequest = new UserProfileChangeRequest.Builder()
                                            .setPhotoUri(Uri.parse(profileUrl))
                                            .build();
                                    firebaseUser.updateProfile(userProfileChangeRequest);

                                    final User user = new User(userUid, fullName, email, profileUrl, null, null, Gender.MALE, Status.ACTIVE, onCreatedDate);

                                    //Store in registeredUser
                                    databaseReference.child("user").child(userUid).setValue(user).addOnCompleteListener(task1 -> {
                                        if (task1.isSuccessful()) {
                                            progressBar.setVisibility(View.INVISIBLE);
                                            btnRegister.setVisibility(View.VISIBLE);

                                            ShowToast(SignUpActivity.this, "Hi, " + fullName);
                                            startActivity(new Intent(SignUpActivity.this, HomeActivity.class));
                                            finish();

                                            //go to main activity
                                        } else {
                                            progressBar.setVisibility(View.INVISIBLE);
                                            btnRegister.setVisibility(View.VISIBLE);
                                            ShowAlertDialog(SignUpActivity.this, "Failed register", Objects.requireNonNull(task1.getException()).getMessage());
                                        }
                                    });

                                }

                            });

                        }

                    })
                    .addOnProgressListener(taskSnapshot -> {
                        double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot
                                .getTotalByteCount());
                        // progressBar.setProgress((int) progress);
                    });
        } else {
            ShowToast(SignUpActivity.this, "Please upload your profile picture");
            progressBar.setVisibility(View.INVISIBLE);
            btnRegister.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Crop.REQUEST_PICK && resultCode == RESULT_OK) {
            assert data != null;
            beginCrop(data.getData());
        } else if (requestCode == Crop.REQUEST_CROP) {
            handleCrop(resultCode, data);
        }
    }

    private void beginCrop(Uri source) {
        Uri destination = Uri.fromFile(new File(getCacheDir(), "cropped"));
        Crop.of(source, destination).asSquare().start(this);
    }

    private void handleCrop(int resultCode, Intent result) {
        if (resultCode == RESULT_OK) {
            profileUrl = Crop.getOutput(result);
            ivProfile.setImageURI(Crop.getOutput(result));
        } else if (resultCode == Crop.RESULT_ERROR) {
            Toast.makeText(this, Crop.getError(result).getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this, SignInActivity.class));
        finish();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}