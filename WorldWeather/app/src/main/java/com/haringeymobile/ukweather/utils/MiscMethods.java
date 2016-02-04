package com.haringeymobile.ukweather.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

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
     * Formats and represents the provided {@code double} value.
     *
     * @param d a {@code double} value
     * @return a textual representation of the decimal number with one decimal place
     */
    public static String formatDoubleValue(double d, int decimalPlaces) {
        String pattern;
        switch (decimalPlaces) {
            case 1:
                pattern = "##.#";
                break;
            case 2:
                pattern = "##.##";
                break;
            default:
                throw new IllegalArgumentException("Provide a pattern for " + decimalPlaces +
                        " decimal places!");
        }
        DecimalFormat df = new DecimalFormat(pattern);
        return df.format(d);
    }

    /**
     * Determines whether the user's device can connect to network at the moment.
     */
    public static boolean isUserOnline(Context context) {
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(
                Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    /**
     * Obtains a day of the week name.
     *
     * @return weekday name in abbreviated form, e.g., Mon, Fri
     */
    public static String getAbbreviatedWeekdayName(Date date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("E");
        return simpleDateFormat.format(date);
    }

    /**
     * Removes surrounding transparent pixels from bitmap (if there are any).
     *
     * @param originalBitmap bitmap that may contain transparent pixels we would like to remove
     * @return new bitmap, which looks like an original bitmap, but with the surrounding
     * whitespace removed
     */
    public static Bitmap trimBitmap(Bitmap originalBitmap) {
        int originalHeight = originalBitmap.getHeight();
        int originalWidth = originalBitmap.getWidth();

        //trimming width from left
        int xCoordinateOfFirstPixel = 0;
        for (int x = 0; x < originalWidth; x++) {
            if (xCoordinateOfFirstPixel == 0) {
                for (int y = 0; y < originalHeight; y++) {
                    if (originalBitmap.getPixel(x, y) != Color.TRANSPARENT) {
                        xCoordinateOfFirstPixel = x;
                        break;
                    }
                }
            } else break;
        }

        //trimming width from right
        int xCoordinateOfLastPixel = 0;
        for (int x = originalWidth - 1; x >= 0; x--) {
            if (xCoordinateOfLastPixel == 0) {
                for (int y = 0; y < originalHeight; y++) {
                    if (originalBitmap.getPixel(x, y) != Color.TRANSPARENT) {
                        xCoordinateOfLastPixel = x;
                        break;
                    }
                }
            } else break;
        }

        //trimming height from top
        int yCoordinateOfFirstPixel = 0;
        for (int y = 0; y < originalHeight; y++) {
            if (yCoordinateOfFirstPixel == 0) {
                for (int x = 0; x < originalWidth; x++) {
                    if (originalBitmap.getPixel(x, y) != Color.TRANSPARENT) {
                        yCoordinateOfFirstPixel = y;
                        break;
                    }
                }
            } else break;
        }

        //trimming height from bottom
        int yCoordinateOfLastPixel = 0;
        for (int y = originalHeight - 1; y >= 0; y--) {
            if (yCoordinateOfLastPixel == 0) {
                for (int x = 0; x < originalWidth; x++) {
                    if (originalBitmap.getPixel(x, y) != Color.TRANSPARENT) {
                        yCoordinateOfLastPixel = y;
                        break;
                    }
                }
            } else break;
        }

        int newBitmapWidth = xCoordinateOfLastPixel - xCoordinateOfFirstPixel;
        int newBitmapHeight = yCoordinateOfLastPixel - yCoordinateOfFirstPixel;
        return Bitmap.createBitmap(originalBitmap, xCoordinateOfFirstPixel,
                yCoordinateOfFirstPixel, newBitmapWidth, newBitmapHeight);
    }

}