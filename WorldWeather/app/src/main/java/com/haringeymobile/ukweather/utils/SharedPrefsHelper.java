package com.haringeymobile.ukweather.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.preference.PreferenceManager;

import com.haringeymobile.ukweather.R;
import com.haringeymobile.ukweather.database.CityTable;
import com.haringeymobile.ukweather.settings.SettingsActivity;
import com.haringeymobile.ukweather.weather.ThreeHourlyForecastDisplayMode;
import com.haringeymobile.ukweather.weather.WeatherInfoType;

public class SharedPrefsHelper {

    public static final String SHARED_PREFS_KEY =
            "com.haringeymobile.ukweather.PREFERENCE_FILE_KEY";

    private static final String LAST_SELECTED_CITY_ID = "city id";
    private static final String LAST_SELECTED_WEATHER_INFO_TYPE = "weather info type";
    private static final String PERSONAL_API_KEY = "personal api key";

    /**
     * Obtains the ID of the city that was last queried by the user.
     */
    public static int getCityIdFromSharedPrefs(Context context) {
        return getSharedPreferences(context).getInt(LAST_SELECTED_CITY_ID,
                CityTable.CITY_ID_DOES_NOT_EXIST);
    }

    private static SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences(SHARED_PREFS_KEY, Activity.MODE_PRIVATE);
    }

    /**
     * Saves the specified city ID.
     *
     * @param cityId OpenWeatherMap city ID
     * @param commit commit if true, apply if false
     */
    public static void putCityIdIntoSharedPrefs(Context context, int cityId, boolean commit) {
        SharedPreferences.Editor editor = getEditor(context).putInt(LAST_SELECTED_CITY_ID, cityId);
        if (commit) {
            editor.commit();
        } else {
            editor.apply();
        }
    }

    private static SharedPreferences.Editor getEditor(Context context) {
        return getSharedPreferences(context).edit();
    }

    /**
     * Obtains the {@link WeatherInfoType} for the last user's query.
     */
    public static WeatherInfoType getLastWeatherInfoTypeFromSharedPrefs(Context context) {
        int lastSelectedWeatherInfoTypeId = getSharedPreferences(context).getInt(
                LAST_SELECTED_WEATHER_INFO_TYPE, WeatherInfoType.CURRENT_WEATHER.getId());
        return WeatherInfoType.getTypeById(lastSelectedWeatherInfoTypeId);
    }

    /**
     * Saves the {@link WeatherInfoType} that was last queried by the user.
     *
     * @param weatherInfoType a kind of weather information
     */
    public static void putLastWeatherInfoTypeIntoSharedPrefs(Context context,
                                                             WeatherInfoType weatherInfoType) {
        getEditor(context).putInt(LAST_SELECTED_WEATHER_INFO_TYPE, weatherInfoType.getId()).apply();
    }

    public static String getPersonalApiKeyFromSharedPrefs(Context context) {
        return getSharedPreferences(context).getString(PERSONAL_API_KEY, "");
    }

    /**
     * Saves users personal API key.
     *
     * @param key OWM developer key
     */
    public static void putPersonalApiKeyIntoSharedPrefs(Context context, String key) {
        getEditor(context).putString(PERSONAL_API_KEY, key).apply();
    }

    /**
     * Obtains the three-hourly forecast display mode from the shared preferences.
     *
     * @return weather forecast display mode preferred by the user, such as a list, or a horizontal
     * swipe view
     */
    public static ThreeHourlyForecastDisplayMode getForecastDisplayMode(Context context) {
        String forecastDisplayModeIdString = PreferenceManager.getDefaultSharedPreferences(context)
                .getString(SettingsActivity.PREF_FORECAST_DISPLAY_MODE, context.getResources()
                        .getString(R.string.pref_forecast_display_mode_id_default));
        int forecastDisplayModeId = Integer.parseInt(forecastDisplayModeIdString);
        return ThreeHourlyForecastDisplayMode.getForecastDisplayModeById(forecastDisplayModeId);
    }

    /**
     * Determines if cities are deleted using button.
     */
    public static boolean isRemovalModeButton(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        Resources res = context.getResources();
        String cityRemovalModeIdString = preferences.getString(SettingsActivity.
                        PREF_CITY_REMOVAL_MODE,
                res.getString(R.string.pref_city_removal_mode_id_default));
        return cityRemovalModeIdString.equals(res.getString(
                R.string.pref_city_removal_mode_button_id));
    }

}