package com.haringeymobile.ukweather.data.objects;

/**
 * Common weather details, displayed on the screen.
 */
public interface WeatherInformation {

    public abstract String getDescription();

    public abstract String getType();

    public abstract String getIconName();

    public abstract double getDayTemperature(TemperatureScale temperatureScale);

    public abstract double getHumidity();

    public abstract double getPressure();

    public abstract Wind getWind();
}
