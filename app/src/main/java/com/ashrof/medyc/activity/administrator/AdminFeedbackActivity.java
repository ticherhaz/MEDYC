package com.ashrof.medyc.activity.administrator;

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
import com.ashrof.medyc.model.Feedback;
import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.willy.ratingbar.ScaleRatingBar;

import static net.ticherhaz.tarikhmasa.TarikhMasa.ConvertTarikhMasa2LocalTime;

public class AdminFeedbackActivity extends AppCompatActivity {

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    private FirebaseRecyclerOptions<Feedback> firebaseRecyclerOptions;
    private FirebaseRecyclerAdapter<Feedback, FeedbackViewHolder> firebaseRecyclerAdapter;

    private RecyclerView recyclerView;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initFirebase();
        setContentView(R.layout.activity_admin_feedback);
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
        firebaseRecyclerOptions = new FirebaseRecyclerOptions.Builder<Feedback>()
                .setQuery(databaseReference.child("feedback"), Feedback.class)
                .build();

        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Feedback, FeedbackViewHolder>(firebaseRecyclerOptions) {
            @Override
            protected void onBindViewHolder(@NonNull FeedbackViewHolder holder, int position, @NonNull Feedback model) {
                holder.getTvName().setText(model.getName());
                holder.getTvDescription().setText(model.getDescription());
                holder.getTvDate().setText(ConvertTarikhMasa2LocalTime(model.getDate()));
                Glide.with(AdminFeedbackActivity.this)
                        .load(model.getProfileUrl())
                        .into(holder.getIvProfile());
                holder.getScaleRatingBar().setRating((float) model.getRatingStar());
            }

            @NonNull
            @Override
            public FeedbackViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                final View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_feedback, parent, false);
                return new FeedbackViewHolder(itemView);
            }

            @Override
            public void onDataChanged() {
                progressBar.setVisibility(View.GONE);
            }
        };

        //Display
        firebaseRecyclerAdapter.startListening();
        recyclerView.setAdapter(firebaseRecyclerAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(AdminFeedbackActivity.this));

    }

    private static class FeedbackViewHolder extends RecyclerView.ViewHolder {

        private View view;
        private TextView tvName, tvDate, tvDescription;
        private ImageView ivProfile;
        private ScaleRatingBar scaleRatingBar;

        FeedbackViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;
            tvName = view.findViewById(R.id.tv_name);
            tvDate = view.findViewById(R.id.tv_date);
            tvDescription = view.findViewById(R.id.tv_description);
            ivProfile = view.findViewById(R.id.iv_profile);
            scaleRatingBar = view.findViewById(R.id.simple_rating_bar);
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

        public TextView getTvDescription() {
            return tvDescription;
        }

        public void setTvDescription(TextView tvDescription) {
            this.tvDescription = tvDescription;
        }

        public ImageView getIvProfile() {
            return ivProfile;
        }

        public void setIvProfile(ImageView ivProfile) {
            this.ivProfile = ivProfile;
        }

        public ScaleRatingBar getScaleRatingBar() {
            return scaleRatingBar;
        }

        public void setScaleRatingBar(ScaleRatingBar scaleRatingBar) {
            this.scaleRatingBar = scaleRatingBar;
        }
    }
}