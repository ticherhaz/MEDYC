package com.ashrof.medyc.activity.user;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ashrof.medyc.R;
import com.ashrof.medyc.model.Medicines;
import com.ashrof.medyc.model.Reminder;
import com.ashrof.medyc.utils.Utils;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

import static com.ashrof.medyc.utils.Constant.DB_MEDICINE;
import static com.ashrof.medyc.utils.Constant.DB_REMINDER;
import static net.ticherhaz.tarikhmasa.TarikhMasa.ConvertTarikhMasa2LocalTime;


public class DashboardFragment extends Fragment {

    private View root;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;

    private FirebaseRecyclerOptions<Reminder> firebaseRecyclerOptions;
    private FirebaseRecyclerAdapter<Reminder, ReminderViewHolder> firebaseRecyclerAdapter;

    private RecyclerView recyclerView;
    private ProgressBar progressBar;


    public DashboardFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        initFirebase();
        root = inflater.inflate(R.layout.fragment_dashboard, container, false);
        initView();
        return root;
    }

    private void initView() {
        recyclerView = root.findViewById(R.id.rv);
        progressBar = root.findViewById(R.id.pb);

        setFirebaseRecyclerAdapter();
    }

    private void initFirebase() {
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
    }

    private void setFirebaseRecyclerAdapter() {
        recyclerView.setVisibility(View.INVISIBLE);
        firebaseRecyclerOptions = new FirebaseRecyclerOptions.Builder<Reminder>()
                .setQuery(databaseReference.child(DB_REMINDER).child(Objects.requireNonNull(firebaseAuth.getUid())), Reminder.class)
                .build();

        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Reminder, ReminderViewHolder>(firebaseRecyclerOptions) {
            @Override
            protected void onBindViewHolder(@NonNull ReminderViewHolder holder, int position, @NonNull Reminder model) {
                final String takeMedic;

                if (model.getTakingMedicine() != null) {
                    takeMedic = model.getTakingMedicine();
                } else
                    takeMedic = "";
                holder.getTvStatus().setText("Already take medicine: " + takeMedic);
                databaseReference.child(DB_MEDICINE).child(model.getMedicineUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            final Medicines medicines = snapshot.getValue(Medicines.class);
                            if (medicines != null) {
                                holder.getTvName().setText(medicines.getName());

                                holder.getTvDate().setText(ConvertTarikhMasa2LocalTime(medicines.getOnCreatedDate()));

                                holder.getIvMedicine().setImageDrawable(getResources().getDrawable(Utils.GetDrawableUbat(medicines.getMedicinePicture())));
                                holder.getCardView().setCardBackgroundColor(Color.parseColor(medicines.getColourMedicine().getCodeColor()));
                            }

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                holder.getTvName().setText(model.getReminderUid());


              /*  holder.getView().setOnClickListener(view -> {
                    final Intent intent = new Intent(getContext(), MedicinesEditActivity.class);
                    intent.putExtra("medicinesUid", model.getMedicinesUid());
                    intent.putExtra("name", model.getName());
                    intent.putExtra("status", model.getStatus());
                    intent.putExtra("onCreatedDate", model.getOnCreatedDate());
                    intent.putExtra("picture", model.getMedicinePicture().name());
                    intent.putExtra("color", model.getColourMedicine().name());
                    startActivity(intent);
                });*/
            }

            @NonNull
            @Override
            public ReminderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                final View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_reminder, parent, false);
                return new ReminderViewHolder(itemView);
            }

            @Override
            public void onDataChanged() {
                final Handler handler = new Handler();
                handler.postDelayed(() -> {
                    progressBar.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                }, 1300);
            }
        };

        //Display
        firebaseRecyclerAdapter.startListening();
        recyclerView.setAdapter(firebaseRecyclerAdapter);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(requireContext());
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);
        recyclerView.setLayoutManager(linearLayoutManager);
    }


    public static class ReminderViewHolder extends RecyclerView.ViewHolder {

        private View view;
        private CardView cardView;
        private TextView tvName, tvStatus, tvDate;
        private ImageView ivMedicine;

        ReminderViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;
            tvName = view.findViewById(R.id.tv_name);
            tvDate = view.findViewById(R.id.tv_date);
            cardView = view.findViewById(R.id.cv_medicine);
            tvStatus = view.findViewById(R.id.tv_status);
            ivMedicine = view.findViewById(R.id.iv_medicine);
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

        public CardView getCardView() {
            return cardView;
        }

        public void setCardView(CardView cardView) {
            this.cardView = cardView;
        }

        public TextView getTvStatus() {
            return tvStatus;
        }

        public void setTvStatus(TextView tvStatus) {
            this.tvStatus = tvStatus;
        }

        public ImageView getIvMedicine() {
            return ivMedicine;
        }

        public void setIvMedicine(ImageView ivMedicine) {
            this.ivMedicine = ivMedicine;
        }
    }
}