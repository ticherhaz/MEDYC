package com.ashrof.medyc.utils;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.ashrof.medyc.R;
import com.ashrof.medyc.activity.user.MedicinesDetailActivity;

import java.util.Calendar;

import static android.content.Context.ALARM_SERVICE;
import static com.ashrof.medyc.utils.Constant.NOTIFICATION_CHANNEL_DESC_PILL_REMINDER;
import static com.ashrof.medyc.utils.Constant.NOTIFICATION_CHANNEL_ID_PILL_REMINDER;
import static com.ashrof.medyc.utils.Constant.NOTIFICATION_CHANNEL_NAME_PILL_REMINDER;
import static com.ashrof.medyc.utils.Constant.NOTIFICATION_ID_RECEIVE_ALARM_2_MINUTES;


public class NotificationUtil {

    private static String createNotificationChannel(final Context context, final String channelId, final String channelName, final String channelDescription) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            final Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            final AudioAttributes attributes = new AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_NOTIFICATION).build();
            final NotificationChannel notificationChannel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                notificationChannel.setAllowBubbles(true);
            }
            notificationChannel.setName(channelName);
            notificationChannel.setDescription(channelDescription);

            notificationChannel.enableVibration(true);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.GREEN);
            notificationChannel.setShowBadge(true);

            notificationChannel.setSound(soundUri, attributes);
            notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);

            final NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(notificationChannel);
            return channelId;
        } else {
            return null;
        }
    }

    public static void PillReminderNotification(final Context context, final String key, final int notificationUid,
                                        final String summary, final String title, final String description) {
        // 1. Create/Retrieve Notification Channel for O and beyond devices (26+).
        final String notificationChannelId = createNotificationChannel(context, NOTIFICATION_CHANNEL_ID_PILL_REMINDER, NOTIFICATION_CHANNEL_NAME_PILL_REMINDER, NOTIFICATION_CHANNEL_DESC_PILL_REMINDER);

        // 2. Build the BIG_TEXT_STYLE.
        final NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle()
                .bigText(description)
                .setBigContentTitle(title)
                .setSummaryText(summary); //This is a small text at right of title bold notification

        // 3. Set up main Intent for notification.
        final Intent notifyIntent = new Intent(context, MedicinesDetailActivity.class);
        notifyIntent.putExtra("medicineId", key); //so kita boleh trasnfer value daripada noti pergi ke activity
        notifyIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        final PendingIntent notifyPendingIntent = PendingIntent.getActivity(context, notificationUid, notifyIntent, PendingIntent.FLAG_ONE_SHOT);
        final Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_logo);

        assert notificationChannelId != null;
        final NotificationCompat.Builder notificationCompatBuilder = new NotificationCompat.Builder(context.getApplicationContext(), notificationChannelId);
        GlobalNotificationBuilder.setNotificationCompatBuilderInstance(notificationCompatBuilder);

        final Notification notification;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            notification = notificationCompatBuilder
                    .setStyle(bigTextStyle)
                    .setContentTitle(title)
                    .setContentText(description)
                    .setSmallIcon(R.drawable.ic_logo)
                    .setLargeIcon(bitmap)
                    .setContentIntent(notifyPendingIntent)
                    .setDefaults(NotificationCompat.DEFAULT_ALL) //ini untuk sound light and vibrate
                    .setColor(ContextCompat.getColor(context.getApplicationContext(), R.color.colorPrimary))
                    .setColorized(true)
                    .setCategory(Notification.CATEGORY_REMINDER)
                    .setPriority(NotificationCompat.PRIORITY_MAX)
                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                    .setAutoCancel(true)
                    .setChannelId(NOTIFICATION_CHANNEL_ID_PILL_REMINDER)
                    .build();
        } else {
            notification = notificationCompatBuilder
                    .setStyle(bigTextStyle)
                    .setContentTitle(title)
                    .setContentText(description)
                    .setSmallIcon(R.drawable.ic_logo)
                    .setLargeIcon(bitmap)
                    .setContentIntent(notifyPendingIntent)
                    .setDefaults(NotificationCompat.DEFAULT_ALL) //ini untuk sound light and vibrate
                    .setColor(ContextCompat.getColor(context.getApplicationContext(), R.color.colorPrimary))
                    .setColorized(true)
                    .setPriority(NotificationCompat.PRIORITY_MAX)
                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                    .setAutoCancel(true)
                    .setChannelId(NOTIFICATION_CHANNEL_ID_PILL_REMINDER)
                    .build();
        }

        final NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context.getApplicationContext());
        notificationManagerCompat.notify(notificationUid, notification);
    }


    public static void AlarmManagerCancelPillReminder(final Context context, final int KEY_ALARM) {
        final Intent intent = new Intent(context, PillReminderBroadcastReceiver.class);
        final PendingIntent pendingIntent = PendingIntent.getBroadcast(context, KEY_ALARM, intent, 0);
        final AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
    }

    public static void AlarmManagerActivity(final Context context, final int agentProfileId, final int notificationUid, final String channelUid, final int WHAT_TIME) {
        final Calendar calendar = Calendar.getInstance();
        final Calendar now = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, WHAT_TIME); //13 ni maksudnya kul 1pm //WHAT_TIME
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0); //so conclusion = 7.00 pm esok

        //check whether the time is earlier than current time. If so, set it to tomorrow. Otherwise, all alarms for earlier time will fire
        if (calendar.before(now)) {
            calendar.add(Calendar.DATE, 1);
        }
        final long timeTrigger = calendar.getTimeInMillis();

        //Setting intent to class where Alarm broadcast message will be handled
        Intent intent = new Intent(context, PillReminderBroadcastReceiver.class);
        intent.putExtra("channelName", "Activity Tomorrow Task");
        intent.putExtra("channelUid", channelUid);
        intent.putExtra("agentProfileId", agentProfileId);
        intent.putExtra("notificationUid", notificationUid);

        //Setting alarm pending intent
        enableBootReceiver(context);
        final PendingIntent pendingIntentActivity = PendingIntent.getBroadcast(context, notificationUid, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        //getting instance of AlarmManager service
        final AlarmManager alarmManagerActivity = (AlarmManager) context.getSystemService(ALARM_SERVICE);

        //We are NOT using setRepeating and setInexactRepeating because there will be problems for Android API above.
        //Can see this https://stackoverflow.com/a/39739886

        if (Build.VERSION.SDK_INT >= 23) {
            alarmManagerActivity.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, timeTrigger, pendingIntentActivity);
        } else if (Build.VERSION.SDK_INT >= 19) {
            alarmManagerActivity.setExact(AlarmManager.RTC_WAKEUP, timeTrigger, pendingIntentActivity);
        } else {
            alarmManagerActivity.set(AlarmManager.RTC_WAKEUP, timeTrigger, pendingIntentActivity);
        }
    }

    //Not use, but dont delete
  /*  public static void AlarmManagerStaff(final Context context, final int notificationUid) {
        final long timeTrigger = System.currentTimeMillis() + (5 * 60 * 60 * 1000); //5 hours

        //Setting intent to class where Alarm broadcast message will be handled
        Intent intent = new Intent(context, StaffBroadcastReceiver.class);
        intent.putExtra("module", "Staff Reminder");

        //Setting alarm pending intent
        enableBootReceiver(context);
        final PendingIntent pendingIntentStaff = PendingIntent.getBroadcast(context, notificationUid, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        //getting instance of AlarmManager service
        final AlarmManager alarmManagerStaff = (AlarmManager) context.getSystemService(ALARM_SERVICE);

        //We are NOT using setRepeating and setInexactRepeating because there will be problems for Android API above.
        //Can see this https://stackoverflow.com/a/39739886
        if (Build.VERSION.SDK_INT >= 23) {
            alarmManagerStaff.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, timeTrigger, pendingIntentStaff);
        } else if (Build.VERSION.SDK_INT >= 19) {
            alarmManagerStaff.setExact(AlarmManager.RTC_WAKEUP, timeTrigger, pendingIntentStaff);
        } else {
            alarmManagerStaff.set(AlarmManager.RTC_WAKEUP, timeTrigger, pendingIntentStaff);
        }
    }*/


    /**
     * Enable boot receiver to persist alarms set for notifications across device reboots
     */
    private static void enableBootReceiver(Context context) {
        final ComponentName receiver = new ComponentName(context, PillReminderBroadcastReceiver.class);
        final PackageManager pm = context.getPackageManager();
        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);
    }

    public static void AlarmManager5Minutes(final Context context, final int notificationUid) {
        final Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        Intent intent = new Intent(context, PillReminderBroadcastReceiver.class);
        intent.putExtra("notificationUid", notificationUid);

        final long timeTrigger = System.currentTimeMillis() + (5 * 60 * 1000); //5 mins

        final PendingIntent pendingIntentActivity = PendingIntent.getBroadcast(context, NOTIFICATION_ID_RECEIVE_ALARM_2_MINUTES, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        final AlarmManager alarmManagerActivity = (AlarmManager) context.getSystemService(ALARM_SERVICE);

        if (Build.VERSION.SDK_INT >= 23) {
            alarmManagerActivity.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, timeTrigger, pendingIntentActivity);
        } else if (Build.VERSION.SDK_INT >= 19) {
            alarmManagerActivity.setExact(AlarmManager.RTC_WAKEUP, timeTrigger, pendingIntentActivity);
        } else {
            alarmManagerActivity.set(AlarmManager.RTC_WAKEUP, timeTrigger, pendingIntentActivity);
        }
    }
}
