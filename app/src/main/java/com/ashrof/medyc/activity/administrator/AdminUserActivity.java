package com.ashrof.medyc.activity.administrator;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ashrof.medyc.R;
import com.ashrof.medyc.model.User;
import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static net.ticherhaz.tarikhmasa.TarikhMasa.ConvertTarikhMasa2LocalTime;

public class AdminUserActivity extends AppCompatActivity {

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    private FirebaseRecyclerOptions<User> firebaseRecyclerOptions;
    private FirebaseRecyclerAdapter<User, UserViewHolder> firebaseRecyclerAdapter;

    private RecyclerView recyclerView;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initFirebase();
        setContentView(R.layout.activity_admin_user);
        initView();
        setFirebaseRecyclerAdapter();
    }

    private void initFirebase() {
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
    }

    private void initView() {
        recyclerView = findViewById(R.id.rv);
        progressBar = findViewById(R.id.pb);
    }

    private void setFirebaseRecyclerAdapter() {
        firebaseRecyclerOptions = new FirebaseRecyclerOptions.Builder<User>()
                .setQuery(databaseReference.child("user"), User.class)
                .build();

        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<User, UserViewHolder>(firebaseRecyclerOptions) {
            @Override
            protected void onBindViewHolder(@NonNull UserViewHolder holder, int position, @NonNull User model) {
                holder.getTvName().setText(model.getFullName());
                holder.getTvEmail().setText(model.getEmail());

                String mobileFixed;
                if (model.getMobile() == null || model.getMobile().equals(""))
                    mobileFixed = "Mobile number not found";
                else mobileFixed = model.getMobile();
                holder.getTvMobile().setText(mobileFixed);
                holder.getTvMobile().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final Uri number = Uri.parse("tel:+" + model.getMobile());
                        final Intent callIntent = new Intent(Intent.ACTION_DIAL, number);
                        startActivity(callIntent);
                    }
                });


                holder.getTvDate().setText(ConvertTarikhMasa2LocalTime(model.getOnCreatedDate()));
                Glide.with(AdminUserActivity.this)
                        .load(model.getProfileUrl())
                        .into(holder.getIvProfile());

                holder.getView().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //
                    }
                });
            }

            @NonNull
            @Override
            public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                final View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);
                return new UserViewHolder(itemView);
            }

            @Override
            public void onDataChanged() {
                progressBar.setVisibility(View.GONE);
            }
        };

        //Display
        firebaseRecyclerAdapter.startListening();
        recyclerView.setAdapter(firebaseRecyclerAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(AdminUserActivity.this));
    }


    private static class UserViewHolder extends RecyclerView.ViewHolder {

        private View view;
        private TextView tvName, tvEmail, tvMobile, tvDate;
        private ImageView ivProfile;

        UserViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;
            tvName = view.findViewById(R.id.tv_name);
            tvDate = view.findViewById(R.id.tv_date);
            tvMobile = view.findViewById(R.id.tv_mobile);
            tvEmail = view.findViewById(R.id.tv_email);
            ivProfile = view.findViewById(R.id.iv_profile);
        }

        public View getView() {
            return view;
        }

        public void setView(View view) {
            this.view = view;
        }

        public TextView getTvName() {
            return tvName;
        }

        public void setTvName(TextView tvName) {
            this.tvName = tvName;
        }

        public TextView getTvDate() {
            return tvDate;
        }

        public void setTvDate(TextView tvDate) {
            this.tvDate = tvDate;
        }

        public TextView getTvEmail() {
            return tvEmail;
        }

        public void setTvEmail(TextView tvEmail) {
            this.tvEmail = tvEmail;
        }

        public ImageView getIvProfile() {
            return ivProfile;
        }

        public void setIvProfile(ImageView ivProfile) {
            this.ivProfile = ivProfile;
        }

        public TextView getTvMobile() {
            return tvMobile;
        }

        public void setTvMobile(TextView tvMobile) {
            this.tvMobile = tvMobile;
        }
    }
}