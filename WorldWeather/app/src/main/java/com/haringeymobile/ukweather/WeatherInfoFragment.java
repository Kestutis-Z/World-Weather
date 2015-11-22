package com.haringeymobile.ukweather;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.haringeymobile.ukweather.WeatherInfoType.IllegalWeatherInfoTypeArgumentException;
import com.haringeymobile.ukweather.data.objects.TemperatureScale;
import com.haringeymobile.ukweather.data.objects.Weather;
import com.haringeymobile.ukweather.data.objects.WeatherInformation;
import com.haringeymobile.ukweather.data.objects.Wind;
import com.haringeymobile.ukweather.data.objects.WindSpeedMeasurementUnit;
import com.haringeymobile.ukweather.settings.SettingsActivity;
import com.haringeymobile.ukweather.utils.MiscMethods;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;

/**
 * A fragment displaying a common weather information.
 */
public abstract class WeatherInfoFragment extends Fragment {

    protected static final String SEPARATOR = ": ";
    protected static final String PERCENT_SIGN = "%";
    protected static final String HECTOPASCAL = "hPa";
    public static final String JSON_STRING = "json string";
    protected static final String CITY_NAME = "city name";

    protected TextView extraInfoTextView;
    protected TextView conditionsTextView;
    protected ImageView conditionsImageView;
    protected TextView temperatureTextView;
    protected TextView pressureTextView;
    protected TextView humidityTextView;
    protected TextView windTextView;

    protected Resources res;

    /**
     * Creates and sets the required weather information fragment.
     *
     * @param weatherInfoType requested weather information type
     * @param cityName        the name of the city for which the weather information was
     *                        requested and obtained
     * @param jsonString      JSON weather information data in textual form
     * @return a fragment to display the requested weather information
     */
    public static WeatherInfoFragment newInstance(WeatherInfoType weatherInfoType, String cityName,
                                                  String jsonString) {
        WeatherInfoFragment weatherInfoFragment = createWeatherInfoFragment(weatherInfoType);
        Bundle args = getArgumentBundle(cityName, jsonString);
        weatherInfoFragment.setArguments(args);
        return weatherInfoFragment;
    }

    /**
     * Creates a fragment, corresponding to the requested weather information type.
     *
     * @param weatherInfoType requested weather information type
     * @return a correct type of weather information fragment
     */
    private static WeatherInfoFragment createWeatherInfoFragment(WeatherInfoType weatherInfoType) {
        switch (weatherInfoType) {
            case CURRENT_WEATHER:
                return new WeatherCurrentInfoFragment();
            case DAILY_WEATHER_FORECAST:
                return new WeatherDailyWeatherForecastChildFragment();
            case THREE_HOURLY_WEATHER_FORECAST:
                return new WeatherThreeHourlyForecastChildFragment();
            default:
                throw new IllegalWeatherInfoTypeArgumentException(weatherInfoType);
        }
    }

    /**
     * Obtains a bundle with the arguments, to be used to instantiate a new weather information
     * fragment.
     *
     * @param cityName   the name of the city for which the weather information was requested and
     *                   obtained
     * @param jsonString JSON weather information data in textual form
     * @return an argument bundle
     */
    private static Bundle getArgumentBundle(String cityName, String jsonString) {
        Bundle args = new Bundle();
        args.putString(CITY_NAME, cityName);
        args.putString(JSON_STRING, jsonString);
        return args;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        res = activity.getResources();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_common_weather_info, container, false);
        getCommonViews(view);
        return view;
    }

    /**
     * Obtain the text and image views to be displayed in all types of weather information
     * fragments.
     *
     * @param view the root view for the fragment
     */
    protected void getCommonViews(View view) {
        extraInfoTextView = (TextView) view.findViewById(R.id.city_extra_info_text_view);
        conditionsTextView = (TextView) view.findViewById(R.id.weather_conditions_text_view);
        conditionsImageView = (ImageView) view.findViewById(R.id.weather_conditions_image_view);
        temperatureTextView = (TextView) view.findViewById(R.id.temperature_text_view);
        pressureTextView = (TextView) view.findViewById(R.id.atmospheric_pressure_text_view);
        humidityTextView = (TextView) view.findViewById(R.id.humidity_text_view);
        windTextView = (TextView) view.findViewById(R.id.wind_text_view);
    }

    /**
     * Displays the specified weather information on the screen.
     *
     * @param weatherInformation various parameters describing weather
     */
    public void displayWeather(WeatherInformation weatherInformation) {
        displayExtraInfo(weatherInformation);
        displayConditions(weatherInformation);
        displayWeatherNumericParametersText(weatherInformation);
        displayWindInfo(weatherInformation);
    }

    /**
     * Displays specific details, depending on the requested weather information type - typically,
     * a city name, and, if applicable, the date and time information.
     *
     * @param weatherInformation various parameters describing weather
     */
    protected abstract void displayExtraInfo(WeatherInformation weatherInformation);

    /**
     * Describes and illustrates the weather.
     *
     * @param weatherInformation various parameters describing weather
     */
    private void displayConditions(WeatherInformation weatherInformation) {
        String weatherDescription = weatherInformation.getType() + " ("
                + weatherInformation.getDescription() + ")";
        conditionsTextView.setText(weatherDescription);
        new SetIconDrawableTask().execute(weatherInformation.getIconName());
    }

    /**
     * Displays weather temperature, pressure, and humidity.
     *
     * @param weatherInformation various parameters describing weather
     */
    private void displayWeatherNumericParametersText(WeatherInformation weatherInformation) {
        displayTemperatureText(weatherInformation);
        displayAtmosphericPressureText(weatherInformation);
        displayHumidity(weatherInformation);
    }

    /**
     * Displays temperature, taking into account the scale preferred by the user.
     *
     * @param weatherInformation various parameters describing weather
     */
    private void displayTemperatureText(WeatherInformation weatherInformation) {
        TemperatureScale temperatureScale = getTemperatureScale();
        String temperatureInfo = MiscMethods.formatDoubleValue(weatherInformation
                .getDayTemperature(temperatureScale)) + res.getString(
                temperatureScale.getDisplayResourceId());
        temperatureTextView.setText(temperatureInfo);
    }

    /**
     * Obtains the temperature scale from the shared preferences.
     *
     * @return the temperature scale preferred by the user
     */
    protected TemperatureScale getTemperatureScale() {
        Context context = getActivity();
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
     */
    private void displayAtmosphericPressureText(WeatherInformation weatherInformation) {
        String pressureInfo = res
                .getString(R.string.weather_info_atmospheric_pressure)
                + SEPARATOR
                + weatherInformation.getPressure()
                + " "
                + HECTOPASCAL;
        pressureTextView.setText(pressureInfo);
    }

    /**
     * Displays humidity.
     *
     * @param weatherInformation various parameters describing the weather
     */
    private void displayHumidity(WeatherInformation weatherInformation) {
        String humidityInfo = res.getString(R.string.weather_info_humidity) + SEPARATOR +
                weatherInformation.getHumidity() + PERCENT_SIGN;
        humidityTextView.setText(humidityInfo);
    }

    /**
     * Displays wind speed and direction.
     *
     * @param weatherInformation various parameters describing weather
     */
    private void displayWindInfo(WeatherInformation weatherInformation) {
        Wind wind = weatherInformation.getWind();
        WindSpeedMeasurementUnit windSpeedMeasurementUnit = getWindSpeedMeasurementUnit();

        String windInfo = res.getString(R.string.weather_info_wind_speed) + SEPARATOR;
        if (windSpeedMeasurementUnit == WindSpeedMeasurementUnit.BEAUFORT_SCALE) {
            windInfo += res.getString(R.string.weather_info_wind_speed_beaufort_scale_force);
            long windForce = Math.round(wind.getSpeed(windSpeedMeasurementUnit));
            windInfo += " " + windForce + " (";
            windInfo += res.getString(WindSpeedMeasurementUnit
                    .getBeaufortScaleWindDescriptionStringResourceId((int) windForce));
            windInfo+=")";
        } else {
            windInfo += MiscMethods.formatDoubleValue(wind.getSpeed(windSpeedMeasurementUnit))
                    + " " + res.getString(windSpeedMeasurementUnit.getDisplayResourceId());
        }

        windInfo += "\n" + res.getString(R.string.weather_info_wind_direction) + SEPARATOR +
                wind.getDirectionInDegrees() + res.getString(R.string.weather_info_degree);
        windInfo += " (" + res.getString(wind.getCardinalDirectionStringResource()) + ")";
        windTextView.setText(windInfo);
    }

    /**
     * Obtains the wind speed measurement units from the shared preferences.
     *
     * @return the wind speed measurement units preferred by a user
     */
    private WindSpeedMeasurementUnit getWindSpeedMeasurementUnit() {
        Context context = getActivity();
        String windSpeedMeasurementUnitIdString = PreferenceManager
                .getDefaultSharedPreferences(context).getString(
                        SettingsActivity.PREF_WIND_SPEED_MEASUREMENT_UNIT,
                        context.getResources().getString(R.string.pref_wind_speed_unit_id_default));
        int windSpeedMeasurementUnitId = Integer.parseInt(windSpeedMeasurementUnitIdString);
        return WindSpeedMeasurementUnit.getWindSpeedMeasurementUnitById(windSpeedMeasurementUnitId);
    }

    protected String getDateString(Context context, Date date) {
        return DateFormat.getMediumDateFormat(context).format(date);
    }

    protected String getTimeString(Context context, Date date) {
        return DateFormat.getTimeFormat(context).format(date);
    }

    /**
     * A task to obtain and display an icon, illustrating the weather.
     */
    private class SetIconDrawableTask extends AsyncTask<String, Void, Drawable> {

        @Override
        protected Drawable doInBackground(String... args) {
            Activity parentActivity = getActivity();
            if (parentActivity == null) {
                return null;
            }
            InputStream iconInputStream = getInputStream(args[0]);
            if (iconInputStream == null) {
                return null;
            } else {
                Bitmap iconBitmap = BitmapFactory.decodeStream(iconInputStream);
                Drawable iconDrawable = new BitmapDrawable(parentActivity.getResources(),
                        iconBitmap);
                return iconDrawable;
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
                MiscMethods.log("MalformedURLException");
                return null;
            } catch (IOException e) {
                MiscMethods.log("IOException");
                return null;
            }
            return input;
        }

        @Override
        protected void onPostExecute(Drawable result) {
            super.onPostExecute(result);
            if (conditionsImageView != null) {
                conditionsImageView.setImageDrawable(result);
            }
        }

    }

}