package com.ashrof.medyc.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;


public class PillReminderBroadcastReceiver extends BroadcastReceiver {

    // private LoginResponse.Data UserData = FastSave.getInstance().getObject(Constant.USER_DATA, LoginResponse.Data.class);

    @Override
    public void onReceive(Context context, Intent intent) {
        //See this note if onReceive not called
        //https://stackoverflow.com/a/60197247/9346054
        final String reminderUid = intent.getStringExtra("reminderUid");
        final String medicinesUid = intent.getStringExtra("medicinesUid");
        final String medicinesName = intent.getStringExtra("medicinesName");
        final String medicinesPicture = intent.getStringExtra("medicinesPicture");
        final String medicinesColor = intent.getStringExtra("medicinesColor");

        if (intent.getAction() != null) {
            if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) { //ok at here we handle if the phone reboot, and on back, it will trigger here, so we need to set back the alarm
                //Just in case if the method not trigger after boot, so we call these method again.
            }
        } else {

        }

        NotificationUtil.PillReminderNotification(context, (int) System.currentTimeMillis(),
                "Pill Reminder", medicinesName, "Please take medicines for " + medicinesName, medicinesColor, medicinesPicture, reminderUid, medicinesUid, medicinesName);

        /*//Example situation, at 12pm, no internet right, then it come here and set for them


        //This is crucial part where when there is no connection, we will call this first
        if (!IsNetworkAvailable(context)) {
            //so kalau tkda internet kita buat alarm manager untuk setiap 5 mins, check internet connection, kalau ada ok la.
            AlarmManager5Minutes(context, notificationUid);
        } else {
            //So when there is internet connection, we call these method again and others too.
            AlarmManagerActivity(context, UserData.getAgentProfileId(), NOTIFICATION_ID_RECEIVE_ALARM_12, IS_RECEIVING_ALARM_ACTIVITY, ALARM_REMINDER_WHAT_TIME_12);
            AlarmManagerActivity(context, UserData.getAgentProfileId(), NOTIFICATION_ID_RECEIVE_ALARM_7, IS_RECEIVING_ALARM_ACTIVITY, ALARM_REMINDER_WHAT_TIME_7);

            if (intent.getAction() != null) {
                if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) { //ok at here we handle if the phone reboot, and on back, it will trigger here, so we need to set back the alarm
                    //Just in case if the method not trigger after boot, so we call these method again.
                    AlarmManagerActivity(context, UserData.getAgentProfileId(), NOTIFICATION_ID_RECEIVE_ALARM_12, IS_RECEIVING_ALARM_ACTIVITY, ALARM_REMINDER_WHAT_TIME_12);
                    AlarmManagerActivity(context, UserData.getAgentProfileId(), NOTIFICATION_ID_RECEIVE_ALARM_7, IS_RECEIVING_ALARM_ACTIVITY, ALARM_REMINDER_WHAT_TIME_7);
                }
            } else {
                //if there is no internet connection, so we start a service
                //https://stackoverflow.com/questions/32243859/android-intent-service-that-pauses-when-no-internet-connection

                //So dia build ni, dan save , kalau ada internt dia akan trigger
                final Constraints constraints = new Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .build();

                // yang dia sama macam intent put extra, tapi ni khas untuk sini, so ktia simpan data
                final Data data = new Data.Builder()
                        //.putInt("agentProfileId", agentProfileId)
                        .putInt("notificationUid", notificationUid)
                        .build();

                //and then kita queue pastu terus ke work manager worker
                final OneTimeWorkRequest onetimeJob = new OneTimeWorkRequest.Builder(ActivityAlarmWorker.class)
                        .setInputData(data)
                        .setConstraints(constraints).build(); // or PeriodicWorkRequest
                WorkManager.getInstance(context).enqueue(onetimeJob);
            }
        }*/
    }
}

