package com.haringeymobile.ukweather.data.objects;

import com.google.gson.annotations.SerializedName;

public class Temperature {

	public static final double DIFFERENCE_BETWEEN_KELVIN_AND_CELCIUS = 273.15;

	@SerializedName("day")
	private double dayTemperature;

	@SerializedName("eve")
	private double eveningTemperature;

	@SerializedName("morn")
	private double morningTemperature;

	@SerializedName("night")
	private double nightTemperature;

	@SerializedName("max")
	private double maxTemperature;

	@SerializedName("min")
	private double minTemperature;

	public double getDayTemperature(TemperatureScale temperatureScale) {
		return temperatureScale.convertTemperature(dayTemperature);
	}

	public double getEveningTemperature(TemperatureScale temperatureScale) {
		return temperatureScale.convertTemperature(eveningTemperature);
	}

	public double getMorningTemperature(TemperatureScale temperatureScale) {
		return temperatureScale.convertTemperature(morningTemperature);
	}

	public double getNightTemperature(TemperatureScale temperatureScale) {
		return temperatureScale.convertTemperature(nightTemperature);
	}

}
