package com.haringeymobile.ukweather.data.objects;

/**
 * Common weather details, displayed on the screen.
 */
public interface WeatherInformation {

    String getDescription();

    String getType();

    String getIconName();

    double getDayTemperature(TemperatureScale temperatureScale);

    double getHumidity();

    double getPressure();

    Wind getWind();

}