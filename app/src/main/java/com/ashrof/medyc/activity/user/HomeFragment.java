package com.ashrof.medyc.activity.user;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.ashrof.medyc.R;
import com.ashrof.medyc.utils.Utils;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;


public class HomeFragment extends Fragment {

    private static final int HOW_MANY_DAYS = 120;
    private View root;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private CalendarView calendarView;

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
        root = inflater.inflate(R.layout.fragment_home, container, false);
        initFirebase();
        initView();
        return root;
    }

    private void initView() {
        calendarView = root.findViewById(R.id.calendarView);
        setCalendarView();
    }

    private void initFirebase() {
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
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
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(requireContext(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        Utils.ShowToast(requireContext(), selectedHour + ":" + selectedMinute);
                    }
                }, hour, minute, true);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();
            }
        });
    }

}