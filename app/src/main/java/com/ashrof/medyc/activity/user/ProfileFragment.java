package com.ashrof.medyc.activity.user;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.ashrof.medyc.R;
import com.ashrof.medyc.model.User;
import com.ashrof.medyc.utils.Constant;
import com.ashrof.medyc.utils.Simpan;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.soundcloud.android.crop.Crop;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

import static android.app.Activity.RESULT_OK;
import static com.ashrof.medyc.utils.Utils.ShowAlertDialog;
import static com.ashrof.medyc.utils.Utils.ShowToast;

public class ProfileFragment extends Fragment {

    final Calendar myCalendar = Calendar.getInstance();
    private View root;
    private User user;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    DatePickerDialog.OnDateSetListener date = (view, year, monthOfYear, dayOfMonth) -> {
        myCalendar.set(Calendar.YEAR, year);
        myCalendar.set(Calendar.MONTH, monthOfYear);
        myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        updateLabel();
    };
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private ImageView ivProfile, ivUploadProfile;
    private TextView tvFullName, tvEmail, tvNumberPhone, tvBirthdayDate;
    private Uri profileUrl;
    private ProgressDialog progressDialog;

    public ProfileFragment() {
        // Required empty public constructor
    }

    private void updateLabel() {
        String myFormat = "MM/dd/yy"; //In which you need put here
        final SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.getDefault());
        final String birthday = sdf.format(myCalendar.getTime());
        tvBirthdayDate.setText(birthday);
        //and then we store the data to database
        databaseReference.child("birthday").setValue(birthday);
        ShowToast(getActivity(), "Updated birthday");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_profile, container, false);

        user = Simpan.getInstance().getObject(Constant.USER_DATA_KEY, User.class);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference().child(Constant.DB_USER).child(user.getUserUid());
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        setProgressDialog();

        ivProfile = root.findViewById(R.id.iv_profile);
        ivUploadProfile = root.findViewById(R.id.iv_upload_profile);
        tvFullName = root.findViewById(R.id.tv_full_name);
        tvEmail = root.findViewById(R.id.tv_email);
        tvNumberPhone = root.findViewById(R.id.tv_number_phone);
        tvBirthdayDate = root.findViewById(R.id.tv_birthday_date);

        tvFullName.setText(user.getFullName());
        tvEmail.setText(user.getEmail());

        Glide.with(requireActivity())
                .load(user.getProfileUrl())
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(ivProfile);

        //call database
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    final User user = snapshot.getValue(User.class);
                    if (user != null) {
                        final String birthday = user.getBirthday();
                        final String mobile = user.getMobile();
                        if (birthday != null) {
                            tvBirthdayDate.setText(birthday);
                        }

                        if (mobile != null) {
                            tvNumberPhone.setText(mobile);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        setTvBirthdayDate();
        setTvNumberPhone();
        setIvUploadProfile();
        return root;
    }

    private void setProgressDialog() {
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setMessage("Uploading profile, please wait...");
        progressDialog.setCancelable(false);
        //This dialog isn't indeterminate
        progressDialog.setIndeterminate(false);
        progressDialog.setMax(100);
        progressDialog.setProgress(0);
    }

    private void setIvUploadProfile() {
        ivUploadProfile.setOnClickListener(view -> Crop.pickImage(getContext(), ProfileFragment.this));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Crop.REQUEST_PICK && resultCode == RESULT_OK) {
            assert data != null;
            beginCrop(data.getData());
        } else if (requestCode == Crop.REQUEST_CROP) {
            handleCrop(resultCode, data);
        }
    }

    private void beginCrop(Uri source) {
        Uri destination = Uri.fromFile(new File(requireContext().getCacheDir(), "cropped"));
        Crop.of(source, destination).asSquare().start(getContext(), ProfileFragment.this);
    }

    private void handleCrop(int resultCode, Intent result) {
        if (resultCode == RESULT_OK) {
            profileUrl = Crop.getOutput(result);
            Glide.with(requireActivity())
                    .load(profileUrl)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .into(ivProfile);
            uploadImage(user.getUserUid(), firebaseUser);
        } else if (resultCode == Crop.RESULT_ERROR) {
            Toast.makeText(getContext(), Crop.getError(result).getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void uploadImage(final String userUid, final FirebaseUser firebaseUser) {
        if (profileUrl != null) {
            progressDialog.show();
            final FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
            final DatabaseReference databaseReference = firebaseDatabase.getReference();

            final String uploadUid = databaseReference.push().getKey();

            final StorageReference storageReference = FirebaseStorage.getInstance().getReference();
            final StorageReference ref = storageReference.child("profileImage/" + userUid + "/" + uploadUid);

            ref.putFile(profileUrl)
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            ShowAlertDialog(getContext(), "Failed Upload", Objects.requireNonNull(e.getMessage()));
                        }
                    })
                    .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            if (task.isSuccessful()) {
                                ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        if (uri != null) {
                                            final String profileUrl = String.valueOf(uri);

                                            UserProfileChangeRequest userProfileChangeRequest = new UserProfileChangeRequest.Builder()
                                                    .setPhotoUri(Uri.parse(profileUrl))
                                                    .build();
                                            firebaseUser.updateProfile(userProfileChangeRequest);

                                            //Store in registeredUser
                                            databaseReference.child("user").child(userUid).child("profileUrl").setValue(profileUrl).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        progressDialog.dismiss();
                                                        ShowToast(getContext(), "Updated new profile picture");
                                                    } else {
                                                        ShowAlertDialog(getContext(), "Failed register", Objects.requireNonNull(task.getException()).getMessage());
                                                    }
                                                }
                                            });

                                        }

                                    }
                                });

                            }

                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                            int progress = (int) (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            progressDialog.setProgress(progress);
                        }
                    });
        } else {
            progressDialog.dismiss();
        }
    }

    private void setTvBirthdayDate() {
        tvBirthdayDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(requireActivity(), date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
    }

    private void setTvNumberPhone() {
        tvNumberPhone.setOnClickListener(v -> dialogUpdatePhone());
    }

    private void dialogUpdatePhone() {
        final Dialog dialog = new Dialog(requireContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_phone_update);
        final WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(Objects.requireNonNull(dialog.getWindow()).getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        final EditText etMobile = dialog.findViewById(R.id.et_mobile);

        dialog.findViewById(R.id.btn_submit).setOnClickListener(v -> {
            if (!etMobile.getText().toString().isEmpty()) {
                //Here we wil update the data
                final String phone = etMobile.getText().toString();
                databaseReference.child("mobile").setValue(phone);
                tvNumberPhone.setText(phone);
                ShowToast(getActivity(), "Updated Number Phone");
                dialog.dismiss();
            } else {
                ShowToast(getActivity(), "Please fill the info to update");
            }
        });

        dialog.findViewById(R.id.iv_close).setOnClickListener(view -> dialog.dismiss());

        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }
}