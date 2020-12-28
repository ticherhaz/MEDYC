package com.ashrof.medyc.activity.user;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CalendarView;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ashrof.medyc.R;
import com.ashrof.medyc.enumerator.TypeReminder;
import com.ashrof.medyc.model.Medicines;
import com.ashrof.medyc.model.Reminder;
import com.ashrof.medyc.model.User;
import com.ashrof.medyc.utils.Constant;
import com.ashrof.medyc.utils.NotificationUtil;
import com.ashrof.medyc.utils.Simpan;
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
    private User user;

    private CalendarView calendarView;
    private FirebaseRecyclerOptions<Medicines> firebaseRecyclerOptions;
    private FirebaseRecyclerAdapter<Medicines, MedicinesFragment.MedicinesViewHolder> firebaseRecyclerAdapter;

    private String medicineUid;
    private String medicineName;
    private String reminderUid;
    private int radioChecked = -1;

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
        user = Simpan.getInstance().getObject(Constant.USER_DATA_KEY, User.class);
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
        calendarView.setOnDateChangeListener((calendarView, i, i1, i2) -> {
            final Calendar mCurrentTime = Calendar.getInstance();
            final int hour = mCurrentTime.get(Calendar.HOUR_OF_DAY);
            final int minute = mCurrentTime.get(Calendar.MINUTE);

            final TimePickerDialog mTimePicker = new TimePickerDialog(requireContext(), (timePicker, selectedHour, selectedMinute) -> dialogChooseOption(i1, i2, selectedHour, selectedMinute), hour, minute, true);//Yes 24 hour time
            mTimePicker.setTitle("Select Time");
            mTimePicker.show();
        });
    }

    private void dialogChooseOption(final int month, final int day, final int hour, final int min) {
        final Dialog dialog = new Dialog(requireContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_choose_specific);
        final WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(Objects.requireNonNull(dialog.getWindow()).getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        final RadioGroup rg = dialog.findViewById(R.id.rg_option);
        rg.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rb_0) {
                //Mean everyday
                radioChecked = 0;
                Log.i("???","checked Id: " + radioChecked);
            } else if (checkedId == R.id.rb_1) {
                //Specific only
                radioChecked = 1;
            }
        });


        dialog.findViewById(R.id.btn_continue).setOnClickListener(view -> {
            if (radioChecked == -1){
                Utils.ShowToast(requireContext(),"Please select choose option");
            }else {
                Log.i("???","checked Id: " + radioChecked);
                dialogListMedicine(month, day, hour, min);
                dialog.dismiss();
            }

        });
        dialog.findViewById(R.id.iv_close).setOnClickListener(view -> dialog.dismiss());

        dialog.show();
        dialog.getWindow().setAttributes(lp);
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
                .setQuery(databaseReference.child(DB_MEDICINE).child(user.getUserUid()), Medicines.class)
                .build();

        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Medicines, MedicinesFragment.MedicinesViewHolder>(firebaseRecyclerOptions) {
            @Override
            protected void onBindViewHolder(@NonNull MedicinesFragment.MedicinesViewHolder holder, int position, @NonNull Medicines model) {
                holder.getTvName().setText(model.getName());
                holder.getTvStatus().setText(model.getStatus().name());
                holder.getTvDate().setText(ConvertTarikhMasa2LocalTime(model.getOnCreatedDate()));

                holder.getIvMedicine().setImageDrawable(getResources().getDrawable(Utils.GetDrawableUbat(model.getMedicinePicture())));
                holder.getCardView().setCardBackgroundColor(Color.parseColor(model.getColourMedicine().getCodeColor()));

                holder.getView().setOnClickListener(view -> {
                    final String reminderUid = databaseReference.push().getKey();
                    String typeReminder = null;
                    if (radioChecked == 0){
                        typeReminder = TypeReminder.EVERYDAY.name();
                    }else if (radioChecked ==1 ){
                        typeReminder = TypeReminder.ONE_TIME.name();
                    }

                    NotificationUtil.AlarmManagerPillReminder(getContext(), NOTIFICATION_ID_PILL_REMINDER, month, day, hour, min, model, reminderUid);
                    Utils.ShowToast(requireContext(), "Successfully set reminder for " + model.getName());

                    final Calendar calendarSet = Calendar.getInstance();
                    calendarSet.set(Calendar.HOUR_OF_DAY, hour);
                    calendarSet.set(Calendar.MINUTE, min);
                    calendarSet.set(Calendar.SECOND, 0);
                    calendarSet.set(Calendar.DAY_OF_MONTH, day);
                    calendarSet.set(Calendar.MONTH, month);

                    //After that we add to reminder
                    final Reminder reminder = new Reminder(reminderUid, model.getMedicinesUid(), TarikhMasa.GetTarikhMasa(), null, null, typeReminder, month, day, hour, min, calendarSet.getTimeInMillis());
                    databaseReference.child(DB_REMINDER).child(Objects.requireNonNull(firebaseAuth.getUid())).child(Objects.requireNonNull(reminderUid)).setValue(reminder);
                    dialog.dismiss();
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