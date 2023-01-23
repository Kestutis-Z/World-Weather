package com.haringeymobile.ukweather.data.objects;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * An object corresponding to the JSON data for the Open Weather Map three
 * hourly weather forecast query.
 */
public class SearchResponseForThreeHourlyForecastQuery {

    @SerializedName("cod")
    private int code;

    @SerializedName("message")
    private String message;

    @SerializedName("city")
    private CityInfo cityInfo;

    @SerializedName("cnt")
    private int forecastCount;

    @SerializedName("list")
    private List<CityThreeHourlyWeatherForecast> threeHourlyWeatherForecasts;

    public CityInfo getCityInfo() {
        return cityInfo;
    }

    public List<CityThreeHourlyWeatherForecast> getThreeHourlyWeatherForecasts() {
        return threeHourlyWeatherForecasts;
    }

}
