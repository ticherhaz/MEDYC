package com.ashrof.medyc.activity.user;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.util.Log;
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
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.MPPointF;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import net.ticherhaz.tarikhmasa.TarikhMasa;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static com.ashrof.medyc.utils.Utils.FormattedDateFromCalendar;

public class StatisticsFragment extends Fragment {

    protected final String[] parties = new String[]{
            "Yes", "No"
    };
    //--------
    private final Calendar myCalendar = Calendar.getInstance();
    private final List<ChartData> chartData = new ArrayList<>();
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
    private PieChart chart;

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

        chart = root.findViewById(R.id.chart1);
        chart.setUsePercentValues(true);
        chart.getDescription().setEnabled(false);
        chart.setExtraOffsets(5, 10, 5, 5);

        chart.setDragDecelerationFrictionCoef(0.95f);

        //chart.setCenterTextTypeface(tfLight);
        chart.setCenterText(generateCenterSpannableText());

        chart.setDrawHoleEnabled(true);
        chart.setHoleColor(Color.WHITE);

        chart.setTransparentCircleColor(Color.WHITE);
        chart.setTransparentCircleAlpha(110);

        chart.setHoleRadius(58f);
        chart.setTransparentCircleRadius(61f);

        chart.setDrawCenterText(true);

        chart.setRotationAngle(0);
        // enable rotation of the chart by touch
        chart.setRotationEnabled(true);
        chart.setHighlightPerTapEnabled(true);


        chart.animateY(1400, Easing.EaseInOutQuad);
        // chart.spin(2000, 0, 360);

        Legend l = chart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setDrawInside(false);
        l.setXEntrySpace(7f);
        l.setYEntrySpace(0f);
        l.setYOffset(0f);

        // entry label styling
        chart.setEntryLabelColor(Color.WHITE);
        //chart.setEntryLabelTypeface(tfRegular);
        chart.setEntryLabelTextSize(12f);

        setData(chartData.size());

        callTotalMedicines();
        callTotalReminder();
        callTookMedicine();
        setCvFilter();
    }

    private SpannableString generateCenterSpannableText() {
        SpannableString s = new SpannableString("Medicine Taken Results");
       /* s.setSpan(new RelativeSizeSpan(1.7f), 0, 14, 0);
        s.setSpan(new StyleSpan(Typeface.NORMAL), 14, s.length() - 15, 0);
        s.setSpan(new ForegroundColorSpan(Color.GRAY), 14, s.length() - 15, 0);
        s.setSpan(new RelativeSizeSpan(.8f), 14, s.length() - 15, 0);
        s.setSpan(new StyleSpan(Typeface.ITALIC), s.length() - 14, s.length(), 0);
        s.setSpan(new ForegroundColorSpan(ColorTemplate.getHoloBlue()), s.length() - 14, s.length(), 0);*/
        return s;
    }

    private void setData(final int count) {
        chart.clear();
        final ArrayList<PieEntry> entries = new ArrayList<>();

        // NOTE: The order of the entries when being added to the entries array determines their position around the center of
        // the chart.
        for (int i = 0; i < count; i++) {
            //  entries.add(new PieEntry(10, parties[i % parties.length], getResources().getDrawable(R.drawable.star)));
            entries.add(new PieEntry(chartData.get(i).getValue(), chartData.get(i).getTitle(), getResources().getDrawable(R.drawable.star)));
        }

        PieDataSet dataSet = new PieDataSet(entries, "");

        dataSet.setDrawIcons(false);
        dataSet.setSliceSpace(3f);
        dataSet.setIconsOffset(new MPPointF(0, 40));
        dataSet.setSelectionShift(5f);

        // add a lot of colors

        ArrayList<Integer> colors = new ArrayList<>();

        for (int c : ColorTemplate.VORDIPLOM_COLORS)
            colors.add(c);
        for (int c : ColorTemplate.JOYFUL_COLORS)
            colors.add(c);
        for (int c : ColorTemplate.COLORFUL_COLORS)
            colors.add(c);
        for (int c : ColorTemplate.LIBERTY_COLORS)
            colors.add(c);
        for (int c : ColorTemplate.PASTEL_COLORS)
            colors.add(c);

        colors.add(ColorTemplate.getHoloBlue());

        dataSet.setColors(colors);
        //dataSet.setSelectionShift(0f);

        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(11f);
        data.setValueTextColor(Color.WHITE);
        // data.setValueTypeface(tfLight);
        chart.setData(data);

        // undo all highlights
        chart.highlightValues(null);
        chart.invalidate();
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
        chartData.clear();
        databaseReference.child(Constant.DB_REMINDER).child(user.getUserUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    int totalYes = 0;
                    int totalNo = 0;
                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                        final Reminder reminder = snapshot1.getValue(Reminder.class);
                        if (reminder != null) {
                            final String date = reminder.getOnCreatedDate();
                            final String dateCustom = TarikhMasa.ConvertTarikhMasa2LocalTimePattern(date, "d MMM yyyy");
                            final String takingMedicine = reminder.getTakingMedicine();
                            Log.i("???", "Va: " + reminder.getTakingMedicine());

                            if (dateCustom.equals(FormattedDateFromCalendar(myCalendar.getTime()))) {
                                if (takingMedicine != null) {
                                    if (takingMedicine.equals("yes")) {
                                        totalYes++;
                                    } else {
                                        totalNo++;
                                    }
                                }
                            }
                        }
                    }

                    chartData.add(new ChartData("Yes", totalYes));
                    chartData.add(new ChartData("No", totalNo));

                }
                setData(chartData.size());
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

    public class ChartData {
        private String title;
        private float value;

        public ChartData(String title, float value) {
            this.title = title;
            this.value = value;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public float getValue() {
            return value;
        }

        public void setValue(float value) {
            this.value = value;
        }
    }

}