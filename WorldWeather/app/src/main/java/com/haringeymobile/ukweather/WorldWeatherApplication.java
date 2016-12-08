package com.haringeymobile.ukweather;

import android.app.Application;
import android.content.res.Configuration;

import java.util.Locale;

public class WorldWeatherApplication extends Application {

    public static String systemLocaleCode;

    @Override
    public void onCreate() {
        super.onCreate();
        Locale locale = Locale.getDefault();
        systemLocaleCode = getSystemLocaleCode(locale);
    }

    /**
     * Obtains code for user-chosen device locale.
     *
     * @param locale system locale
     * @return ISO-defined language and (optionally) country codes
     */
    private String getSystemLocaleCode(Locale locale) {
        return locale.getLanguage() + "-r" + locale.getCountry();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Locale locale = newConfig.locale;
        systemLocaleCode = getSystemLocaleCode(locale);
    }

}