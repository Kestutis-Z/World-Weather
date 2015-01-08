package com.haringeymobile.ukweather;

import java.util.Date;

import android.content.Context;
import android.os.Bundle;
import android.text.format.DateFormat;

import com.google.gson.Gson;
import com.haringeymobile.ukweather.data.objects.CityThreeHourlyWeatherForecast;
import com.haringeymobile.ukweather.data.objects.WeatherInformation;

/** A fragment displaying weather forecast for a three hour period. */
public class WeatherThreeHourlyForecastChildFragment extends
		WeatherInfoFragment {

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		Bundle args = getArguments();
		String jsonString = args.getString(WeatherInfoFragment.JSON_STRING);
		Gson gson = new Gson();
		CityThreeHourlyWeatherForecast threeHourlyWeatherForecast = gson
				.fromJson(jsonString, CityThreeHourlyWeatherForecast.class);
		displayWeather(threeHourlyWeatherForecast);
	}

	@Override
	protected void displayExtraInfo(WeatherInformation weatherInformation) {
		CityThreeHourlyWeatherForecast threeHourlyWeatherForecast = (CityThreeHourlyWeatherForecast) weatherInformation;
		Context context = getActivity();
		Date date = new Date(threeHourlyWeatherForecast.getDate() * 1000);
		String dateString = DateFormat.getLongDateFormat(context).format(date);
		String timeString = DateFormat.getTimeFormat(context).format(date);
		extraInfoTextView.setText(dateString + "\n" + timeString + "\n"
				+ getArguments().getString(CITY_NAME));
	}

}
