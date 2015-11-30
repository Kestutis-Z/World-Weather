package com.haringeymobile.ukweather;

import android.content.Intent;
import android.os.Parcelable;

import com.haringeymobile.ukweather.database.GeneralDatabaseService;

public abstract class RefreshingActivity extends ThemedActivity
        implements WorkerFragmentToRetrieveJsonString.OnJsonStringRetrievedListener {

    public static final String WEATHER_INFORMATION_TYPE = "weather info type";
    public static final String WEATHER_INFO_JSON_STRING = "json string";

    protected WorkerFragmentToRetrieveJsonString workerFragment;

    @Override
    public void onJsonStringRetrieved(String jsonString, WeatherInfoType weatherInfoType,
                                      boolean shouldSaveLocally) {
        if (shouldSaveLocally) {
            saveRetrievedData(jsonString, weatherInfoType);
        }
        displayRetrievedData(jsonString, weatherInfoType);
    }

    /**
     * Saves the retrieved data in the database, so that it could be reused for a short period of
     * time.
     *
     * @param jsonString      Weather information data in JSON format
     * @param weatherInfoType type of the retrieved weather data
     */
    protected void saveRetrievedData(String jsonString, WeatherInfoType weatherInfoType) {
        Intent intent = new Intent(this, GeneralDatabaseService.class);
        intent.setAction(GeneralDatabaseService.ACTION_UPDATE_WEATHER_INFO);
        intent.putExtra(WEATHER_INFO_JSON_STRING, jsonString);
        intent.putExtra(WEATHER_INFORMATION_TYPE, (Parcelable) weatherInfoType);
        startService(intent);
    }

    /**
     * @param jsonString      Weather information data in JSON format
     * @param weatherInfoType type of the retrieved weather data
     */
    protected abstract void displayRetrievedData(String jsonString, WeatherInfoType
            weatherInfoType);

}