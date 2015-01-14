package com.haringeymobile.ukweather.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import java.text.DecimalFormat;

public class MiscMethods {

    private static final boolean LOGS_ON = false;
    private static final String LOGS = "Logs";

    /**
     * A convenience method to send a log message.
     */
    public static void log(String s) {
        if (LOGS_ON)
            Log.d(LOGS, s);
    }

    /**
     * Formats and represents the provided {@code double} value as a number with
     * one decimal place.
     *
     * @param d a {@code double} value
     * @return a textual representation of the decimal number with one decimal
     * place
     */
    public static String formatDoubleValue(double d) {
        DecimalFormat df = new DecimalFormat("##.#");
        return df.format(d);
    }

    /**
     * Determines whether the user's device can connect to network at the
     * moment.
     */
    public static boolean isUserOnline(Context context) {
        ConnectivityManager connMgr = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }
}
