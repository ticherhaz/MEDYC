package com.ashrof.medyc.activity.user;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.ashrof.medyc.R;
import com.ashrof.medyc.model.Feedback;
import com.ashrof.medyc.model.User;
import com.ashrof.medyc.utils.Constant;
import com.ashrof.medyc.utils.Simpan;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.willy.ratingbar.BaseRatingBar;
import com.willy.ratingbar.ScaleRatingBar;

import net.ticherhaz.tarikhmasa.TarikhMasa;

import static com.ashrof.medyc.utils.Utils.ShowToast;


public class FeedbackFragment extends Fragment {

    private View root;

    private User user;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    private EditText etFeedback;
    private Button btnSubmit;
    private ScaleRatingBar scaleRatingBar;
    private double mRating = -1;

    public FeedbackFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_feedback, container, false);

        user = Simpan.getInstance().getObject(Constant.USER_DATA_KEY, User.class);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference().child("feedback");

        etFeedback = root.findViewById(R.id.et_feedback);
        btnSubmit = root.findViewById(R.id.btn_submit);
        scaleRatingBar = root.findViewById(R.id.simple_rating_bar);

        scaleRatingBar.setOnRatingChangeListener((ratingBar, rating, fromUser) -> mRating = rating);
        btnSubmit.setOnClickListener(view -> {
            if (!etFeedback.getText().toString().equals("") && mRating != -1) {
                final String feedbackDescription = etFeedback.getText().toString();
                final Feedback feedback = new Feedback(user.getFullName(), TarikhMasa.GetTarikhMasa(), feedbackDescription, user.getProfileUrl(), mRating);
                databaseReference.child(user.getUserUid()).setValue(feedback).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            etFeedback.getText().clear();
                            scaleRatingBar.setRating(0);

                            ShowToast(getContext(), "Submitted feedback. Thank you");
                        }
                    }
                });
            } else {
                ShowToast(getContext(), "Please fill in the blank to submit");
            }
        });
        return root;
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