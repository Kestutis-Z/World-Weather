package com.haringeymobile.ukweather;

import android.os.Bundle;

import com.google.gson.Gson;
import com.haringeymobile.ukweather.data.objects.CityCurrentWeather;
import com.haringeymobile.ukweather.data.objects.WeatherInformation;

/**
 * A fragment displaying current weather information.
 */
public class WeatherCurrentInfoFragment extends WeatherInfoFragment {

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Bundle args = getArguments();
        String jsonString = args.getString(WeatherInfoFragment.JSON_STRING);
        Gson gson = new Gson();
        CityCurrentWeather cityCurrentWeather = gson.fromJson(jsonString,
                CityCurrentWeather.class);
        displayWeather(cityCurrentWeather);
    }

    @Override
    protected void displayExtraInfo(WeatherInformation weatherInformation) {
        CityCurrentWeather cityCurrentWeather = (CityCurrentWeather) weatherInformation;
        extraInfoTextView.setText(cityCurrentWeather.getCityName());
    }
}
