package com.haringeymobile.ukweather.data.objects;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Weather forecast for one three hour period.
 */
public class CityThreeHourlyWeatherForecast implements WeatherInformation {

    @SerializedName("dt")
    private long date;

    @SerializedName("main")
    private NumericParameters numericParameters;

    @SerializedName("weather")
    private List<Weather> weather;

    @SerializedName("clouds")
    private Clouds clouds;

    @SerializedName("wind")
    private Wind wind;

    @SerializedName("dt_txt")
    private String dateText;

    @Override
    public int getWeatherConditionsId() {
        return weather.get(0).getId();
    }

    @Override
    public String getType() {
        return weather.get(0).getType();
    }

    @Override
    public String getIconName() {
        return weather.get(0).getIcon();
    }

    @Override
    public double getDayTemperature(TemperatureScale temperatureScale) {
        return numericParameters.getTemperature(temperatureScale);
    }

    @Override
    public double getHumidity() {
        return numericParameters.getHumidity();
    }

    @Override
    public double getPressure() {
        return numericParameters.getPressure();
    }

    @Override
    public Wind getWind() {
        return wind;
    }

    @Override
    public boolean isDayTemperatureProvided() {
        return numericParameters != null;
    }

    @Override
    public boolean isPressureProvided() {
        return numericParameters != null;
    }

    @Override
    public boolean isHumidityProvided() {
        return numericParameters != null;
    }

    public long getDate() {
        return date;
    }

}