package com.haringeymobile.ukweather;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v4.util.LruCache;
import android.support.v7.app.AlertDialog;

import com.haringeymobile.ukweather.database.GeneralDatabaseService;
import com.haringeymobile.ukweather.weather.IconCacheRetainFragment;
import com.haringeymobile.ukweather.weather.WeatherInfoFragment;
import com.haringeymobile.ukweather.weather.WeatherInfoType;
import com.haringeymobile.ukweather.weather.WeatherThreeHourlyForecastChildListFragment;
import com.haringeymobile.ukweather.weather.WorkerFragmentToRetrieveJsonString;

/**
 * An activity that may display some weather data (e.g. current weather, or weather forecast), and
 * if it does so, it refreshes the data each time it becomes visible.
 */
public abstract class RefreshingActivity extends ThemedActivity implements
        WorkerFragmentToRetrieveJsonString.OnJsonStringRetrievedListener,
        WeatherInfoFragment.IconCacheRequestListener,
        WeatherThreeHourlyForecastChildListFragment.IconCacheRequestListener {

    public static final String WEATHER_INFORMATION_TYPE = "weather info type";
    public static final String WEATHER_INFO_JSON_STRING = "json string";

    protected WorkerFragmentToRetrieveJsonString workerFragment;
    /**
     * LruCache storing icons illustrating weather conditions. The key is the OWM icon code name:
     * http://openweathermap.org/weather-conditions
     */
    protected LruCache<String, Bitmap> iconCache;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setIconMemoryCache();
    }

    /**
     * Obtains or creates a new memory cache to store the weather icons.
     */
    private void setIconMemoryCache() {
        IconCacheRetainFragment retainFragment =
                IconCacheRetainFragment.findOrCreateRetainFragment(getSupportFragmentManager());
        iconCache = retainFragment.iconCache;
        if (iconCache == null) {
            // maximum available VM memory, stored in kilobytes
            int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
            // we use 1/8th of the available memory for this memory cache
            int cacheSize = maxMemory / 8;

            iconCache = new LruCache<String, Bitmap>(cacheSize) {

                @Override
                protected int sizeOf(String key, Bitmap bitmap) {
                    // the cache size will be measured in kilobytes rather than number of items
                    return bitmap.getByteCount() / 1024;
                }
            };

        }
        retainFragment.iconCache = iconCache;
    }

    @Override
    public void onRecentJsonStringRetrieved(String jsonString, WeatherInfoType weatherInfoType,
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
     * @param jsonString      weather information data in JSON format
     * @param weatherInfoType type of the retrieved weather data
     */
    protected abstract void displayRetrievedData(String jsonString, WeatherInfoType
            weatherInfoType);

    @Override
    public void onOldJsonStringRetrieved(final String jsonString,
                                         final WeatherInfoType weatherInfoType, long queryTime) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.dialog_title_no_network_connection)
                .setIcon(R.drawable.ic_alert_error)
                .setMessage(getAlertDialogMessage(queryTime).toString())
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        onRecentJsonStringRetrieved(jsonString, weatherInfoType, false);
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create()
                .show();
    }

    /**
     * Parses the message to be shown to user when there is no network access, but the old weather
     * data stored locally still can be displayed.
     *
     * @param queryTime the time when the old weather data were obtained
     * @return time in millis
     */
    @NonNull
    private StringBuilder getAlertDialogMessage(long queryTime) {
        long weatherDataAge = System.currentTimeMillis() - queryTime;
        int hours = (int) (weatherDataAge / (3600 * 1000));
        int days = hours / 24;
        hours %= 24;

        Resources res = getResources();

        StringBuilder messageBuilder = new StringBuilder();
        messageBuilder.append(res.getString(R.string.old_data_message_1));
        if (days > 0) {
            messageBuilder.append(days);
            messageBuilder.append(" ");
            messageBuilder.append(res.getQuantityString(R.plurals.days, days));
            int messageResourceId = hours == 0 ?
                    R.string.old_data_message_3 :
                    R.string.old_data_message_2;
            messageBuilder.append(res.getString(messageResourceId));
        }
        if (days == 0 && hours == 0) {
            hours = 1;
        }
        if (hours > 0) {
            messageBuilder.append(hours);
            messageBuilder.append(" ");
            messageBuilder.append(res.getQuantityString(R.plurals.hours, hours));
            messageBuilder.append(res.getString(R.string.old_data_message_3));
        }
        return messageBuilder;
    }

    @Override
    public LruCache<String, Bitmap> getIconMemoryCache() {
        return iconCache;
    }

}