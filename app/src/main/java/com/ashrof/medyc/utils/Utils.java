package com.ashrof.medyc.utils;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.Gravity;
import android.widget.Toast;

import com.ashrof.medyc.R;
import com.ashrof.medyc.enumerator.Ubat;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class Utils {

    private static ProgressDialog progressDialog;
    private static Toast toast;

    public static void ShowProgressDialog(Context context) {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(context);
            progressDialog.setMessage("Loading...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }
    }

    public static void DismissProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }

    public static String FormattedDateFromCalendar(final Date calendarDate) {
        final String newDateFormat = "d MMM yyyy";
        final SimpleDateFormat format = new SimpleDateFormat(newDateFormat, Locale.getDefault());
        final Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(calendarDate.getTime());
        return format.format(calendar.getTime());
    }

    public static boolean IsNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = null;
        if (connectivityManager != null) {
            activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        }
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static void ShowAlertDialog(final Context context, final String title, final String message) {
        AlertDialog alertDialog = new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", (dialogInterface, i) -> dialogInterface.dismiss())
                .create();
        alertDialog.show();
    }

    public static void ShowToast(Context context, final String message) {
        if (toast == null) {
            toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }
        toast = null;
    }

    public static boolean NumberPhoneValid(final String numberPhone) {
        return numberPhone.length() <= 17 && numberPhone.length() >= 11;
    }

    public static String GetIPAddress(boolean useIPv4) {
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                List<InetAddress> addrs = Collections.list(intf.getInetAddresses());
                for (InetAddress addr : addrs) {
                    if (!addr.isLoopbackAddress()) {
                        String sAddr = addr.getHostAddress();
                        boolean isIPv4 = sAddr.indexOf(':') < 0;
                        if (useIPv4) {
                            if (isIPv4)
                                return sAddr;
                        } else {
                            if (!isIPv4) {
                                int delim = sAddr.indexOf('%');
                                return delim < 0 ? sAddr.toUpperCase() : sAddr.substring(0, delim).toUpperCase();
                            }
                        }
                    }
                }
            }
        } catch (Exception ignored) {

        }
        return "";
    }

    public static String GetMonth(final int month) {
        String monthString;
        if (month == 1) {
            monthString = "January";
        } else if (month == 2) {
            monthString = "February";
        } else if (month == 3) {
            monthString = "March";
        } else if (month == 4) {
            monthString = "April";
        } else if (month == 5) {
            monthString = "May";
        } else if (month == 6) {
            monthString = "June";
        } else if (month == 7) {
            monthString = "July";
        } else if (month == 8) {
            monthString = "August";
        } else if (month == 9) {
            monthString = "September";
        } else if (month == 10) {
            monthString = "October";
        } else if (month == 11) {
            monthString = "November";
        } else if (month == 12) {
            monthString = "December";
        } else {
            monthString = "";
        }
        return monthString;
    }

    public static int GetDrawableUbat(final Ubat ubat) {
        int drawableMedicine = -1;
        switch (ubat) {
            case UBAT1:
                drawableMedicine = R.drawable.ubat1;
                break;
            case UBAT2:
                drawableMedicine = R.drawable.ubat2;
                break;
            case UBAT3:
                drawableMedicine = R.drawable.ubat3;
                break;
            case UBAT4:
                drawableMedicine = R.drawable.ubat4;
                break;
        }
        return drawableMedicine;
    }
}
