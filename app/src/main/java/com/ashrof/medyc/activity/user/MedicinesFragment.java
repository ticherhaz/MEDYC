package com.ashrof.medyc.activity.user;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ashrof.medyc.R;
import com.ashrof.medyc.enumerator.Ubat;
import com.ashrof.medyc.model.Medicines;
import com.ashrof.medyc.utils.Utils;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static com.ashrof.medyc.utils.Constant.DB_MEDICINE;
import static net.ticherhaz.tarikhmasa.TarikhMasa.ConvertTarikhMasa2LocalTime;

public class MedicinesFragment extends Fragment {

    private View root;
    private Button buttonAdd;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    private FirebaseRecyclerOptions<Medicines> firebaseRecyclerOptions;
    private FirebaseRecyclerAdapter<Medicines, MedicinesViewHolder> firebaseRecyclerAdapter;

    private RecyclerView recyclerView;
    private ProgressBar progressBar;

    public MedicinesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_medicines, container, false);
        initFirebase();
        initView();
        setFirebaseRecyclerAdapter();
        return root;
    }

    private void initFirebase() {
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
    }

    private void initView() {
        recyclerView = root.findViewById(R.id.rv);
        progressBar = root.findViewById(R.id.pb);
        buttonAdd = root.findViewById(R.id.btn_add);
        buttonAdd.setOnClickListener(view -> startActivity(new Intent(getActivity(), AddMedicinesActivity.class)));
    }

    private void setFirebaseRecyclerAdapter() {
        firebaseRecyclerOptions = new FirebaseRecyclerOptions.Builder<Medicines>()
                .setQuery(databaseReference.child(DB_MEDICINE), Medicines.class)
                .build();

        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Medicines, MedicinesViewHolder>(firebaseRecyclerOptions) {
            @Override
            protected void onBindViewHolder(@NonNull MedicinesViewHolder holder, int position, @NonNull Medicines model) {
                holder.getTvName().setText(model.getName());
                holder.getTvStatus().setText(model.getStatus().name());
                holder.getTvDate().setText(ConvertTarikhMasa2LocalTime(model.getOnCreatedDate()));

                holder.getIvMedicine().setImageDrawable(getResources().getDrawable(Utils.GetDrawableUbat(model.getMedicinePicture())));
                holder.getCardView().setCardBackgroundColor(Color.parseColor(model.getColourMedicine().getCodeColor()));

                holder.getView().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //
                    }
                });
            }

            @NonNull
            @Override
            public MedicinesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                final View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_medicine, parent, false);
                return new MedicinesViewHolder(itemView);
            }

            @Override
            public void onDataChanged() {
                progressBar.setVisibility(View.GONE);
            }
        };

        //Display
        firebaseRecyclerAdapter.startListening();
        recyclerView.setAdapter(firebaseRecyclerAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
    }

    public static class MedicinesViewHolder extends RecyclerView.ViewHolder {

        private View view;
        private CardView cardView;
        private TextView tvName, tvStatus, tvDate;
        private ImageView ivMedicine;

        MedicinesViewHolder(@NonNull View itemView) {
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