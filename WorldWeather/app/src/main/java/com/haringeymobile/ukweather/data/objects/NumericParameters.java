package com.haringeymobile.ukweather.data.objects;

import com.google.gson.annotations.SerializedName;

public class NumericParameters {

	@SerializedName("humidity")
	private double humidity;

	@SerializedName("pressure")
	private double pressure;

	@SerializedName("temp")
	private double temperature;

	@SerializedName("temp_max")
	private double maxTemperature;

	@SerializedName("temp_min")
	private double minTemperature;

	public double getHumidity() {
		return humidity;
	}

	public double getPressure() {
		return pressure;
	}

	public double getTemperature(TemperatureScale temperatureScale) {
		return temperatureScale.convertTemperature(temperature);
	}

}
