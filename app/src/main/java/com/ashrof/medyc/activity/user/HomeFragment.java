package com.ashrof.medyc.activity.user;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CalendarView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ashrof.medyc.R;
import com.ashrof.medyc.model.Medicines;
import com.ashrof.medyc.model.Reminder;
import com.ashrof.medyc.utils.NotificationUtil;
import com.ashrof.medyc.utils.Utils;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import net.ticherhaz.tarikhmasa.TarikhMasa;

import java.util.Calendar;
import java.util.Objects;

import static com.ashrof.medyc.utils.Constant.DB_MEDICINE;
import static com.ashrof.medyc.utils.Constant.DB_REMINDER;
import static com.ashrof.medyc.utils.Constant.NOTIFICATION_ID_PILL_REMINDER;
import static net.ticherhaz.tarikhmasa.TarikhMasa.ConvertTarikhMasa2LocalTime;


public class HomeFragment extends Fragment {

    private static final int HOW_MANY_DAYS = 120;
    private View root;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;

    private CalendarView calendarView;
    private FirebaseRecyclerOptions<Medicines> firebaseRecyclerOptions;
    private FirebaseRecyclerAdapter<Medicines, MedicinesFragment.MedicinesViewHolder> firebaseRecyclerAdapter;

    private String medicineUid;
    private String medicineName;
    private String reminderUid;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        initIntent();
        root = inflater.inflate(R.layout.fragment_home, container, false);
        initFirebase();
        initView();
        return root;
    }

    private void initIntent() {
        final Intent intent = requireActivity().getIntent();
        medicineUid = intent.getStringExtra("medicineUid");
        reminderUid = intent.getStringExtra("reminderUid");
        medicineName = intent.getStringExtra("medicineName");
        if (medicineUid != null) {
            final Intent intent1 = new Intent(getActivity(), MedicinesDetailActivity.class);
            intent1.putExtra("medicineUid", medicineUid);
            intent1.putExtra("reminderUid", reminderUid);
            requireActivity().startActivity(intent1);
        }
    }


    private void initView() {
        calendarView = root.findViewById(R.id.calendarView);
        setCalendarView();
    }

    private void initFirebase() {
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
    }

    private void setCalendarView() {
        //Init calendar view
        final Calendar maxDate = Calendar.getInstance();
        maxDate.add(Calendar.DATE, HOW_MANY_DAYS);
        final Calendar minDate = Calendar.getInstance();
        minDate.add(Calendar.DATE, 0);
        calendarView.setMaxDate(maxDate.getTimeInMillis());
        calendarView.setMinDate(minDate.getTimeInMillis());

        //set on click
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int i, int i1, int i2) {
                Calendar mCurrentTime = Calendar.getInstance();
                int hour = mCurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mCurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(requireContext(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        dialogListMedicine(i1, i2, selectedHour, selectedMinute);
                    }
                }, hour, minute, true);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();
            }
        });
    }

    private void dialogListMedicine(final int month, final int day, final int hour, final int min) {
        final Dialog dialog = new Dialog(requireContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_list_medicines);
        final WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(Objects.requireNonNull(dialog.getWindow()).getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        setFirebaseRecyclerAdapter(dialog, month, day, hour, min);

        dialog.findViewById(R.id.iv_close).setOnClickListener(view -> dialog.dismiss());

        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }

    private void setFirebaseRecyclerAdapter(final Dialog dialog, final int month, final int day, final int hour, final int min) {
        firebaseRecyclerOptions = new FirebaseRecyclerOptions.Builder<Medicines>()
                .setQuery(databaseReference.child(DB_MEDICINE), Medicines.class)
                .build();

        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Medicines, MedicinesFragment.MedicinesViewHolder>(firebaseRecyclerOptions) {
            @Override
            protected void onBindViewHolder(@NonNull MedicinesFragment.MedicinesViewHolder holder, int position, @NonNull Medicines model) {
                holder.getTvName().setText(model.getName());
                holder.getTvStatus().setText(model.getStatus().name());
                holder.getTvDate().setText(ConvertTarikhMasa2LocalTime(model.getOnCreatedDate()));

                holder.getIvMedicine().setImageDrawable(getResources().getDrawable(Utils.GetDrawableUbat(model.getMedicinePicture())));
                holder.getCardView().setCardBackgroundColor(Color.parseColor(model.getColourMedicine().getCodeColor()));

                holder.getView().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final String reminderUid = databaseReference.push().getKey();

                        NotificationUtil.AlarmManagerPillReminder(getContext(), NOTIFICATION_ID_PILL_REMINDER, month, day, hour, min, model, reminderUid);
                        Utils.ShowToast(requireContext(), "Successfully set reminder for " + model.getName());

                        //After that we add to reminder
                        final Reminder reminder = new Reminder(reminderUid, model.getMedicinesUid(), TarikhMasa.GetTarikhMasa(), null, null, month, day, hour, min);
                        databaseReference.child(DB_REMINDER).child(Objects.requireNonNull(firebaseAuth.getUid())).child(Objects.requireNonNull(reminderUid)).setValue(reminder);
                        dialog.dismiss();
                    }
                });
            }

            @NonNull
            @Override
            public MedicinesFragment.MedicinesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                final View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_medicine, parent, false);
                return new MedicinesFragment.MedicinesViewHolder(itemView);
            }

            @Override
            public void onDataChanged() {
                dialog.findViewById(R.id.pb).setVisibility(View.GONE);
            }
        };

        //Display
        firebaseRecyclerAdapter.startListening();
        ((RecyclerView) dialog.findViewById(R.id.rv)).setAdapter(firebaseRecyclerAdapter);
        ((RecyclerView) dialog.findViewById(R.id.rv)).setLayoutManager(new LinearLayoutManager(requireContext()));
    }

}