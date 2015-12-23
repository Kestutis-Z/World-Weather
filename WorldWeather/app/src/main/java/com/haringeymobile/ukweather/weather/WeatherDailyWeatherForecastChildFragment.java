package com.haringeymobile.ukweather.weather;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.haringeymobile.ukweather.R;
import com.haringeymobile.ukweather.data.objects.CityDailyWeatherForecast;
import com.haringeymobile.ukweather.data.objects.Temperature;
import com.haringeymobile.ukweather.data.objects.TemperatureScale;
import com.haringeymobile.ukweather.data.objects.WeatherInformation;
import com.haringeymobile.ukweather.utils.MiscMethods;

import java.util.Date;

/**
 * A fragment displaying weather forecast for one day.
 */
public class WeatherDailyWeatherForecastChildFragment extends WeatherInfoFragment {

    private TextView extraTemperaturesTextView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_daily_weather_forecast, container, false);
        getCommonViews(view);
        extraTemperaturesTextView = (TextView) view.findViewById(
                R.id.night_morning_evening_temperatures_text_view);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Bundle args = getArguments();
        String jsonString = args.getString(JSON_STRING);
        Gson gson = new Gson();
        CityDailyWeatherForecast cityWeatherForecast = gson.fromJson(jsonString,
                CityDailyWeatherForecast.class);
        displayWeather(cityWeatherForecast);
    }

    @Override
    protected void displayExtraInfo(WeatherInformation weatherInformation) {
        CityDailyWeatherForecast cityDailyWeatherForecast =
                (CityDailyWeatherForecast) weatherInformation;

        String extraInfoText = getExtraInfoText(cityDailyWeatherForecast);
        extraInfoTextView.setText(extraInfoText);

        String temperatureInfo = getExtraTemperatureText(cityDailyWeatherForecast);
        extraTemperaturesTextView.setText(temperatureInfo);
    }

    /**
     * Obtains a text to be displayed in the extraInfoTextView.
     *
     * @param cityDailyWeatherForecast Java object, corresponding to the Open Weather Map JSON
     *                                 weather forecast data for one day
     * @return a weather forecast date, time, and location
     */
    private String getExtraInfoText(CityDailyWeatherForecast cityDailyWeatherForecast) {
        Context context = getActivity();
        Date date = new Date(cityDailyWeatherForecast.getDate() * 1000);

        String weekdayName = MiscMethods.getAbbreviatedWeekdayName(date);
        String dateString = getDateString(context, date);
        String timeString = getTimeString(context, date);

        return weekdayName + ", " + dateString + "\n" + timeString + "\n" +
                getArguments().getString(CITY_NAME);
    }

    /**
     * Obtains a text to be displayed in the extraTemperaturesTextView.
     *
     * @param cityDailyWeatherForecast Java object, corresponding to the Open Weather Map JSON
     *                                 weather forecast data for one day
     * @return the night, morning, and evening temperatures
     */
    private String getExtraTemperatureText(CityDailyWeatherForecast cityDailyWeatherForecast) {
        Temperature temperature = cityDailyWeatherForecast.getTemperature();
        TemperatureScale temperatureScale = weatherInformationDisplayer.getTemperatureScale();
        String temperatureScaleDegree = getResources().getString(
                temperatureScale.getDisplayResourceId());
        String temperatureInfo = MiscMethods.formatDoubleValue(temperature
                .getNightTemperature(temperatureScale)) + temperatureScaleDegree;
        temperatureInfo += "\n" + MiscMethods.formatDoubleValue(temperature
                .getMorningTemperature(temperatureScale)) + temperatureScaleDegree;
        temperatureInfo += "\n" + MiscMethods.formatDoubleValue(temperature
                .getEveningTemperature(temperatureScale)) + temperatureScaleDegree;
        return temperatureInfo;
    }

}