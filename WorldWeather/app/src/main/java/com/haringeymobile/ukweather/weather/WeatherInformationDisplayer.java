package com.haringeymobile.ukweather.weather;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.util.LruCache;
import android.widget.ImageView;
import android.widget.TextView;

import com.haringeymobile.ukweather.R;
import com.haringeymobile.ukweather.data.objects.TemperatureScale;
import com.haringeymobile.ukweather.data.objects.Weather;
import com.haringeymobile.ukweather.data.objects.WeatherConditionFinder;
import com.haringeymobile.ukweather.data.objects.WeatherInformation;
import com.haringeymobile.ukweather.data.objects.Wind;
import com.haringeymobile.ukweather.data.objects.WindSpeedMeasurementUnit;
import com.haringeymobile.ukweather.settings.SettingsActivity;
import com.haringeymobile.ukweather.utils.MiscMethods;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class WeatherInformationDisplayer {

    protected static final String SEPARATOR = ": ";
    protected static final String PERCENT_SIGN = "%";
    protected static final String HECTOPASCAL = "hPa";

    private Context context;
    private Resources res;

    private LruCache<String, Bitmap> iconCache;

    public WeatherInformationDisplayer(Context context, LruCache<String, Bitmap> iconCache) {
        this.context = context;
        res = context.getResources();
        this.iconCache = iconCache;
    }

    /**
     * Describes and illustrates the weather.
     *
     * @param weatherInformation various parameters describing weather
     */
    void displayConditions(WeatherInformation weatherInformation,
                           TextView conditionsTextView, ImageView conditionsImageView) {
        String weatherDescription = res.getString(WeatherConditionFinder.
                findWeatherConditionStringResourceId(weatherInformation.getWeatherConditionsId()));
        String capitalizedWeatherDescription = weatherDescription.substring(0, 1).toUpperCase() +
                weatherDescription.substring(1);
        conditionsTextView.setText(capitalizedWeatherDescription);

        String iconName = weatherInformation.getIconName();
        Bitmap bitmap = getIconFromMemoryCache(iconName);
        if (bitmap != null) {
            conditionsImageView.setImageBitmap(bitmap);
        } else {
            new SetIconDrawableTask(conditionsImageView).execute(iconName);
        }
    }

    private Bitmap getIconFromMemoryCache(String key) {
        return iconCache == null ? null : iconCache.get(key);
    }

    private void addIconToMemoryCache(String key, Bitmap bitmap) {
        if (iconCache != null && getIconFromMemoryCache(key) == null) {
            iconCache.put(key, bitmap);
        }
    }

    /**
     * Displays weather temperature, pressure, and humidity.
     *
     * @param weatherInformation  various parameters describing weather
     * @param temperatureTextView view to display temperature
     * @param pressureTextView    view to display atmospheric pressure
     * @param humidityTextView    view to display humidity
     */
    void displayWeatherNumericParametersText(WeatherInformation weatherInformation,
                                             TextView temperatureTextView,
                                             TextView pressureTextView,
                                             TextView humidityTextView) {
        displayTemperatureText(weatherInformation, temperatureTextView);
        displayAtmosphericPressureText(weatherInformation, pressureTextView);
        displayHumidity(weatherInformation, humidityTextView);
    }

    /**
     * Displays temperature, taking into account the scale preferred by the user.
     *
     * @param weatherInformation  various parameters describing weather
     * @param temperatureTextView view to display temperature
     */
    private void displayTemperatureText(WeatherInformation weatherInformation,
                                        TextView temperatureTextView) {
        TemperatureScale temperatureScale = getTemperatureScale();
        String temperatureInfo = MiscMethods.formatDoubleValue(weatherInformation
                .getDayTemperature(temperatureScale), 1) + res.getString(
                temperatureScale.getDisplayResourceId());
        temperatureTextView.setText(temperatureInfo);
    }

    /**
     * Obtains the temperature scale from the shared preferences.
     *
     * @return the temperature scale preferred by the user
     */
    TemperatureScale getTemperatureScale() {
        String temperatureScaleIdString = PreferenceManager
                .getDefaultSharedPreferences(context).getString(
                        SettingsActivity.PREF_TEMPERATURE_SCALE, context.getResources().getString(
                                R.string.pref_temperature_scale_id_default));
        int temperatureScaleId = Integer.parseInt(temperatureScaleIdString);
        return TemperatureScale.getTemperatureScaleById(temperatureScaleId);
    }

    /**
     * Displays pressure.
     *
     * @param weatherInformation various parameters describing the weather
     * @param pressureTextView   view to display atmospheric pressure
     */
    private void displayAtmosphericPressureText(WeatherInformation weatherInformation,
                                                TextView pressureTextView) {
        long pressure = Math.round(weatherInformation.getPressure());
        String pressureInfo = res
                .getString(R.string.weather_info_atmospheric_pressure)
                + SEPARATOR
                + pressure
                + " "
                + HECTOPASCAL;
        pressureTextView.setText(pressureInfo);
    }

    /**
     * Displays humidity.
     *
     * @param weatherInformation various parameters describing the weather
     * @param humidityTextView   view to display humidity
     */
    private void displayHumidity(WeatherInformation weatherInformation, TextView humidityTextView) {
        long humidity = Math.round(weatherInformation.getHumidity());
        String humidityInfo = res.getString(R.string.weather_info_humidity)
                + SEPARATOR
                + humidity
                + PERCENT_SIGN;
        humidityTextView.setText(humidityInfo);
    }

    /**
     * Displays wind speed and direction.
     *
     * @param weatherInformation various parameters describing weather
     * @param windTextView       view to display wind information
     */
    void displayWindInfo(WeatherInformation weatherInformation, final TextView windTextView) {
        Wind wind = weatherInformation.getWind();
        // It seems that wind information is not always provided by OWM
        if (wind == null) {
            return;
        }
        WindSpeedMeasurementUnit windSpeedMeasurementUnit = getWindSpeedMeasurementUnit();

        String windInfo = res.getString(R.string.weather_info_wind_speed) + SEPARATOR;
        if (windSpeedMeasurementUnit == WindSpeedMeasurementUnit.BEAUFORT_SCALE) {
            windInfo += res.getString(R.string.weather_info_wind_speed_beaufort_scale_force);
            long windForce = Math.round(wind.getSpeed(windSpeedMeasurementUnit));
            windInfo += " " + windForce + " (";
            String beaufortScaleDescription = res.getString(WindSpeedMeasurementUnit
                    .getBeaufortScaleWindDescriptionStringResourceId((int) windForce));
            windInfo += beaufortScaleDescription.substring(0, 1).toUpperCase() +
                    beaufortScaleDescription.substring(1);
            windInfo += ")";
        } else {
            windInfo += MiscMethods.formatDoubleValue(wind.getSpeed(windSpeedMeasurementUnit), 1)
                    + " " + res.getString(windSpeedMeasurementUnit.getDisplayResourceId());
        }

        if (PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(SettingsActivity.PREF_WIND_DIRECTION_DISPLAY, true)) {
            windInfo += "\n" + res.getString(R.string.weather_info_wind_direction) + SEPARATOR +
                    wind.getDirectionInDegrees() + res.getString(R.string.weather_info_degree);
            windInfo += "\n(" + res.getString(wind.getCardinalDirectionStringResource()) + ")";
        }

        windTextView.setText(windInfo);
    }

    /**
     * Obtains the wind speed measurement units from the shared preferences.
     *
     * @return the wind speed measurement units preferred by a user
     */
    private WindSpeedMeasurementUnit getWindSpeedMeasurementUnit() {
        String windSpeedMeasurementUnitIdString = PreferenceManager
                .getDefaultSharedPreferences(context).getString(
                        SettingsActivity.PREF_WIND_SPEED_MEASUREMENT_UNIT,
                        context.getResources().getString(R.string.pref_wind_speed_unit_id_default));
        int windSpeedMeasurementUnitId = Integer.parseInt(windSpeedMeasurementUnitIdString);
        return WindSpeedMeasurementUnit.getWindSpeedMeasurementUnitById(windSpeedMeasurementUnitId);
    }

    /**
     * A task to obtain and display an icon, illustrating the weather.
     */
    private class SetIconDrawableTask extends AsyncTask<String, Void, Drawable> {
        private WeakReference<ImageView> imageViewReference;

        private SetIconDrawableTask(ImageView imageView) {
            this.imageViewReference = new WeakReference<>(imageView);
        }

        @Override
        protected Drawable doInBackground(String... args) {
            if (context == null) {
                return null;
            }

            String iconName = args[0];
            InputStream iconInputStream = getInputStream(iconName);
            if (iconInputStream == null) {
                // we return some placeholder icon
                return res.getDrawable(R.drawable.ic_launcher_weather);
            } else {
                Bitmap iconBitmap = BitmapFactory.decodeStream(iconInputStream);
                iconBitmap = MiscMethods.trimBitmap(iconBitmap);
                addIconToMemoryCache(iconName, iconBitmap);
                return new BitmapDrawable(res, iconBitmap);
            }
        }

        /**
         * Obtains an input stream to be decoded into a bitmap.
         *
         * @param iconCode Open Weather Map code for the weather conditions
         * @return an input stream for the weather icon
         */
        private InputStream getInputStream(String iconCode) {
            String iconUrl = Weather.ICON_URL_PREFIX + iconCode + Weather.ICON_URL_SUFFIX;
            InputStream input;
            try {
                URL url = new URL(iconUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                input = connection.getInputStream();
            } catch (MalformedURLException e) {
                MiscMethods.log("MalformedURLException during SetIconDrawableTask");
                return null;
            } catch (IOException e) {
                MiscMethods.log("IOException during SetIconDrawableTask");
                return null;
            }
            return input;
        }

        @Override
        protected void onPostExecute(Drawable result) {
            super.onPostExecute(result);
            ImageView imageView = imageViewReference.get();
            if (imageView != null) {
                imageView.setImageDrawable(result);
            }
        }

    }

}