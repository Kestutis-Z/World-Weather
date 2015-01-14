package com.haringeymobile.ukweather.utils;

import android.os.Build;

public class GlobalConstants {

    public static final boolean IS_BUILD_VERSION_AT_LEAST_HONEYCOMB_11 =
            android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB;
    public static final boolean IS_BUILD_VERSION_AT_LEAST_ICS_14 =
            android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH;
    public static final boolean IS_BUILD_VERSION_AT_LEAST_KITKAT_19 =
            android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT;
    public static final boolean IS_BUILD_VERSION_AT_LEAST_LOLLIPOP_21 =
            android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
}
