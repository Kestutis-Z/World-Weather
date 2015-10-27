package com.haringeymobile.ukweather.data;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * An Open Weather Map web page to fetch some kind of weather information.
 */
public class OpenWeatherMapUrl {

    private static final String OPEN_WEATHER_MAP_URL_PREFIX =
            "http://api.openweathermap.org/data/2.5/";
    private static final String WEATHER = "weather";
    private static final String ID = "?id=";
    private static final String FIND = "find?q=";
    private static final String LIKE = "&type=like";
    private static final String FORECAST = "forecast";
    private static final String FORECAST_DAILY = "forecast/daily";
    private static final String COUNT = "&cnt=";
    private static final String OPEN_WEATHER_MAP_API_KEY =
            "&APPID=13d6f372052b76fdc44bd6057ffb9dfc";

    /**
     * Obtains the web address to extract the current weather information for the
     * provided city.
     *
     * @param cityId Open Weather Map city identification number
     * @return web page containing current weather information
     */
    public URL generateCurrentWeatherByCityIdUrl(int cityId) {
        return getUrl(OPEN_WEATHER_MAP_URL_PREFIX + WEATHER + ID + cityId +
                OPEN_WEATHER_MAP_API_KEY);
    }

    /**
     * Parses the provided string to a valid web address, that can be used to fetch data.
     *
     * @param urlString specification for the URL
     * @return a new URL instance
     */
    private URL getUrl(String urlString) {
        URL url = null;
        try {
            url = new URL(urlString);
        } catch (MalformedURLException e) {
            e.printStackTrace();
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
        return getUrl(OPEN_WEATHER_MAP_URL_PREFIX + FIND + query + LIKE + OPEN_WEATHER_MAP_API_KEY);
    }

    /**
     * Obtains the web address to extract the daily weather forecast for the specified number of
     * days.
     *
     * @param cityId Open Weather Map city identification number
     * @param days   number of days that the weather should be forecasted
     * @return web page containing daily weather forecast
     */
    public URL generateDailyWeatherForecastUrl(int cityId, int days) {
        return getUrl(OPEN_WEATHER_MAP_URL_PREFIX + FORECAST_DAILY + ID
                + cityId + COUNT + days + OPEN_WEATHER_MAP_API_KEY);
    }

    /**
     * Obtains the web address to extract the three hourly weather forecast for the specified city.
     *
     * @param cityId Open Weather Map city identification number
     * @return web page containing three hourly weather forecast
     */
    public URL generateThreeHourlyWeatherForecastUrl(int cityId) {
        return getUrl(OPEN_WEATHER_MAP_URL_PREFIX + FORECAST + ID + cityId +
                OPEN_WEATHER_MAP_API_KEY);
    }

}
