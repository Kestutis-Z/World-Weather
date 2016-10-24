package com.haringeymobile.ukweather.data.objects;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Current weather information.
 */
public class CityCurrentWeather implements WeatherInformation {

    @SerializedName("clouds")
    private Clouds clouds;

    @SerializedName("coord")
    private Coordinates coordinates;

    @SerializedName("dt")
    private long date;

    @SerializedName("id")
    private int cityId;

    @SerializedName("main")
    private NumericParameters numericParameters;

    @SerializedName("name")
    private String cityName;

    @SerializedName("rain")
    private Rain rain;

    @SerializedName("sys")
    private SystemParameters systemParameters;

    @SerializedName("weather")
    private List<Weather> weather;

    @SerializedName("wind")
    private Wind wind;

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

    public int getCityId() {
        return cityId;
    }

    public String getCityName() {
        return cityName;
    }

    public SystemParameters getSystemParameters() {
        return systemParameters;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

}
