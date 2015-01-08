package com.haringeymobile.ukweather.data.objects;

import java.util.List;

import com.google.gson.annotations.SerializedName;

/**
 * An object corresponding to the JSON data for the Open Weather Map daily
 * weather forecast query.
 */
public class SearchResponseForDailyForecastQuery {

	@SerializedName("city")
	private CityInfo cityInfo;

	@SerializedName("cnt")
	private int dayCount;

	@SerializedName("cod")
	private int code;

	@SerializedName("list")
	private List<CityDailyWeatherForecast> dailyWeatherForecasts;

	@SerializedName("message")
	private String message;

	public CityInfo getCityInfo() {
		return cityInfo;
	}

	public List<CityDailyWeatherForecast> getDailyWeatherForecasts() {
		return dailyWeatherForecasts;
	}

}
