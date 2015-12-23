package com.haringeymobile.ukweather.weather;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.util.LruCache;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.haringeymobile.ukweather.R;
import com.haringeymobile.ukweather.data.objects.WeatherInformation;
import com.haringeymobile.ukweather.weather.WeatherInfoType.IllegalWeatherInfoTypeArgumentException;

import java.util.Date;

/**
 * A fragment displaying a common weather information.
 */
public abstract class WeatherInfoFragment extends Fragment {

    public interface IconCacheRequestListener {

        /**
         * Obtains the memory cache storing weather icon bitmaps.
         */
        LruCache<String, Bitmap> getIconMemoryCache();

    }

    public static final String JSON_STRING = "json string";
    protected static final String CITY_NAME = "city name";

    protected TextView extraInfoTextView;
    protected TextView conditionsTextView;
    protected ImageView conditionsImageView;
    protected TextView temperatureTextView;
    protected TextView pressureTextView;
    protected TextView humidityTextView;
    protected TextView windTextView;

    private IconCacheRequestListener iconCacheRequestListener;
    protected WeatherInformationDisplayer weatherInformationDisplayer;

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
                return new WeatherThreeHourlyForecastChildSwipeFragment();
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
    public void onAttach(Context context) {
        super.onAttach(context);
        iconCacheRequestListener = (IconCacheRequestListener) context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        weatherInformationDisplayer = new WeatherInformationDisplayer(getContext(),
                iconCacheRequestListener.getIconMemoryCache());
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
        weatherInformationDisplayer.displayConditions(weatherInformation, conditionsTextView,
                conditionsImageView);
    }

    /**
     * Displays weather temperature, pressure, and humidity.
     *
     * @param weatherInformation various parameters describing weather
     */
    private void displayWeatherNumericParametersText(WeatherInformation weatherInformation) {
        weatherInformationDisplayer.displayWeatherNumericParametersText(weatherInformation,
                temperatureTextView, pressureTextView, humidityTextView);
    }

    /**
     * Displays wind speed and direction.
     *
     * @param weatherInformation various parameters describing weather
     */
    private void displayWindInfo(WeatherInformation weatherInformation) {
        weatherInformationDisplayer.displayWindInfo(weatherInformation, windTextView);
    }

    protected String getDateString(Context context, Date date) {
        return DateFormat.getMediumDateFormat(context).format(date);
    }

    protected String getTimeString(Context context, Date date) {
        return DateFormat.getTimeFormat(context).format(date);
    }

}