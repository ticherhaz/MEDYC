package com.ashrof.medyc.activity.user;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.ashrof.medyc.R;
import com.ashrof.medyc.model.Reminder;
import com.ashrof.medyc.model.User;
import com.ashrof.medyc.utils.Constant;
import com.ashrof.medyc.utils.Simpan;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import net.ticherhaz.tarikhmasa.TarikhMasa;

import java.util.Calendar;

import static com.ashrof.medyc.utils.Utils.FormattedDateFromCalendar;
import static com.ashrof.medyc.utils.Utils.ShowToast;

public class StatisticsFragment extends Fragment {

    //--------
    private final Calendar myCalendar = Calendar.getInstance();
    private View root;
    private User user;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private TextView tvFilterResult;
    private CardView cvFilter;
    private ProgressDialog progressDialog;
    private String calendarFromDatePicker = FormattedDateFromCalendar(myCalendar.getTime());
    private TextView tvTotalReminder, tvTotalMedicines, tvTookMedicine;

    public StatisticsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        initFirebase();
        root = inflater.inflate(R.layout.fragment_statistics, container, false);
        user = Simpan.getInstance().getObject(Constant.USER_DATA_KEY, User.class);
        initView();
        return root;
    }

    private void initView() {
        cvFilter = root.findViewById(R.id.cv_filter);
        tvFilterResult = root.findViewById(R.id.tv_filter_result);
        tvFilterResult.setText(FormattedDateFromCalendar(myCalendar.getTime()));
        tvTotalMedicines = root.findViewById(R.id.tv_total_medicines);
        tvTotalReminder = root.findViewById(R.id.tv_total_reminder);
        tvTookMedicine = root.findViewById(R.id.tv_took_medicine);

        callTotalMedicines();
        callTotalReminder();
        callTookMedicine();
        setCvFilter();
    }

    private void initFirebase() {
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
    }

    private void setCvFilter() {
        cvFilter.setOnClickListener(v -> {
            cvFilter.setEnabled(false);
            initDatePicker();
        });
    }

    private void initDatePicker() {
        final DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(), (view, year, month, dayOfMonth) -> {
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, month);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            //after that we update the label
            updateLabel();
        }, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.setOnDismissListener(dialog -> cvFilter.setEnabled(true));
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        datePickerDialog.show();
    }

    private void updateLabel() {
        tvFilterResult.setText(FormattedDateFromCalendar(myCalendar.getTime()));
        calendarFromDatePicker = FormattedDateFromCalendar(myCalendar.getTime());
        callData();
    }

    private void callData() {
        databaseReference.child(Constant.DB_REMINDER).child(user.getUserUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                        final Reminder reminder = snapshot1.getValue(Reminder.class);
                        if (reminder != null) {
                            final String date = reminder.getOnCreatedDate();
                            final String dateCustom = TarikhMasa.ConvertTarikhMasa2LocalTimePattern(date, "d MMM yyyy");
                            ShowToast(requireContext(), dateCustom);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void callTotalMedicines() {
        databaseReference.child(Constant.DB_MEDICINE).child(user.getUserUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    final long totalMedicine = snapshot.getChildrenCount();
                    final String messageMedicines = "Total Medicines: " + totalMedicine;
                    tvTotalMedicines.setText(messageMedicines);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void callTotalReminder() {
        databaseReference.child(Constant.DB_REMINDER).child(user.getUserUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    final long totalReminder = snapshot.getChildrenCount();
                    final String messageReminder = "Total Reminder: " + totalReminder;
                    tvTotalReminder.setText(messageReminder);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void callTookMedicine() {
        final String[] message = {"Took Medicine"};
        final Query query = databaseReference.child(Constant.DB_REMINDER).child(user.getUserUid()).orderByChild("takingMedicine").equalTo("yes");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    final long totalReminder = snapshot.getChildrenCount();
                    final String messageHere = "\nYes: " + totalReminder;
                    message[0] += messageHere;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        final Query query2 = databaseReference.child(Constant.DB_REMINDER).child(user.getUserUid()).orderByChild("takingMedicine").equalTo("no");
        query2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    final long totalReminder = snapshot.getChildrenCount();
                    final String messageHere = "\nNo: " + totalReminder;
                    message[0] += messageHere;
                    tvTookMedicine.setText(message[0]);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

}