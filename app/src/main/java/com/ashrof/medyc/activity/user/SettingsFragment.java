package com.ashrof.medyc.activity.user;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.ashrof.medyc.R;
import com.ashrof.medyc.utils.Utils;

import static com.ashrof.medyc.utils.Constant.NOTIFICATION_CHANNEL_ID_PILL_REMINDER;


public class SettingsFragment extends Fragment {

    private View root;
    private Button btnChange;
    private String chosenRingtone;
    private TextView tvRingtone;

    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_settings, container, false);
        initView();
        setBtnChange();
        return root;
    }

    private void initView() {
        btnChange = root.findViewById(R.id.btn_change);
        tvRingtone = root.findViewById(R.id.tv_ringtone);
    }

    private void setBtnChange() {
        btnChange.setOnClickListener(view -> {
            final Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_NOTIFICATION);
            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Select Tone");
            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, (Uri) null);
            this.startActivityForResult(intent, 5);
        });
    }


    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent intent) {
        if (resultCode == Activity.RESULT_OK && requestCode == 5) {
            final Uri uri = intent.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
            if (uri != null) {
                this.chosenRingtone = uri.toString();
                final Ringtone ringtone = RingtoneManager.getRingtone(requireContext(), uri);
                final String title = ringtone.getTitle(requireContext());
                tvRingtone.setText(title);

                final NotificationManager notificationManager;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    notificationManager = requireContext().getSystemService(NotificationManager.class);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                        final AudioAttributes attributes = new AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_NOTIFICATION).build();
                        notificationManager.getNotificationChannel(NOTIFICATION_CHANNEL_ID_PILL_REMINDER).setSound(uri, attributes);

                        Utils.ShowToast(requireContext(), "Updated notification sound");
                    }
                }

            } else {
                this.chosenRingtone = null;
            }

        }
    }
}