package com.haringeymobile.ukweather.utils;

import android.os.Build;

public class GlobalConstants {

    public static final boolean IS_BUILD_VERSION_AT_LEAST_JELLY_BEAN_16 =
            android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN;
    public static final boolean IS_BUILD_VERSION_AT_LEAST_JELLY_BEAN_17 =
            android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1;
    public static final boolean IS_BUILD_VERSION_AT_LEAST_KITKAT_19 =
            android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT;
    public static final boolean IS_BUILD_VERSION_AT_LEAST_LOLLIPOP_21 =
            android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;

}