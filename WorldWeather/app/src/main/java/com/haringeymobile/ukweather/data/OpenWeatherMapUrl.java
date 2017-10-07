package com.haringeymobile.ukweather.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.haringeymobile.ukweather.utils.SharedPrefsHelper;

import java.net.MalformedURLException;
import java.net.URL;

import static com.haringeymobile.ukweather.settings.SettingsActivity.PREF_PERSONAL_API_KEY;

/**
 * An Open Weather Map web page to fetch some kind of weather information.
 */
public class OpenWeatherMapUrl {

    private static final String OPEN_WEATHER_MAP_URL_PREFIX =
            "http://api.openweathermap.org/data/2.5/";
    private static final String WEATHER = "weather";
    private static final String ID = "?id=";
    private static final String FIND = "find?";
    private static final String FIND_QUERY = "find?q=";
    private static final String LIKE = "&type=like";
    private static final String FORECAST = "forecast";
    private static final String FORECAST_DAILY = "forecast/daily";
    private static final String COUNT = "&cnt=";
    private static final String LATITUDE = "lat=";
    private static final String LONGITUDE = "&lon=";

    private static final String OPEN_WEATHER_MAP_API_KEY_PREFIX = "&APPID=";
    private static final String DEVELOPER_API_KEY = "13d6f372052b76fdc44bd6057ffb9dfc";

    /**
     * How many locations should the query to search cities by the geographical coordinates return.
     */
    private static final int RESULT_COUNT = 10;

    private Context context;

    public OpenWeatherMapUrl(Context context) {
        this.context = context;
    }

    /**
     * Obtains the web address to extract the current weather information for the
     * provided city.
     *
     * @param cityId Open Weather Map city identification number
     * @return web page containing current weather information
     */
    public URL generateCurrentWeatherByCityIdUrl(int cityId) {
        return getUrl(OPEN_WEATHER_MAP_URL_PREFIX + WEATHER + ID + cityId + getApiKey());
    }

    /**
     * Obtains the key required to complete any Open Weather Map query.
     *
     * @return OWM API key with the required prefix
     */
    private String getApiKey() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        boolean shouldUsePersonalApiKey = preferences.getBoolean(PREF_PERSONAL_API_KEY, false);
        String key = shouldUsePersonalApiKey ? SharedPrefsHelper.getPersonalApiKeyFromSharedPrefs(
                context) : DEVELOPER_API_KEY;
        return OPEN_WEATHER_MAP_API_KEY_PREFIX + key;
    }

    /**
     * Parses the provided string to a valid web address, that can be used to fetch data.
     *
     * @param urlString specification for the URL
     * @return a new URL instance
     */
    private URL getUrl(String urlString) {
        URL url;
        try {
            url = new URL(urlString);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
        return url;
    }

    /**
     * Obtains the web address to extract the list of locations matching the provided query.
     *
     * @param query user provided city search query
     * @return web page with the list of cities
     */
    public URL getAvailableCitiesListUrl(String query) {
        return getUrl(OPEN_WEATHER_MAP_URL_PREFIX + FIND_QUERY + query + LIKE + getApiKey());
    }

    /**
     * Obtains the web address to extract the list of locations nearby the provided geographical
     * coordinates.
     *
     * @param latitude  latitude of the location
     * @param longitude longitude of the location
     * @return web page with the list of cities
     */
    public URL getAvailableCitiesListUrlByGeographicalCoordinates(String latitude,
                                                                  String longitude) {
        return getUrl(OPEN_WEATHER_MAP_URL_PREFIX + FIND + LATITUDE + latitude + LONGITUDE
                + longitude + COUNT + RESULT_COUNT + getApiKey());
    }

    /**
     * Obtains the web address to extract the daily weather forecast for the specified number of
     * days.
     *
     * @param cityId Open Weather Map city identification number
     * @param days   number of days that the weather should be forecast
     * @return web page containing daily weather forecast
     */
    public URL generateDailyWeatherForecastUrl(int cityId, int days) {
        return getUrl(OPEN_WEATHER_MAP_URL_PREFIX + FORECAST_DAILY + ID + cityId + COUNT + days +
                getApiKey());
    }

    /**
     * Obtains the web address to extract the three hourly weather forecast for the specified city.
     *
     * @param cityId Open Weather Map city identification number
     * @return web page containing three hourly weather forecast
     */
    public URL generateThreeHourlyWeatherForecastUrl(int cityId) {
        return getUrl(OPEN_WEATHER_MAP_URL_PREFIX + FORECAST + ID + cityId + getApiKey());
    }

}