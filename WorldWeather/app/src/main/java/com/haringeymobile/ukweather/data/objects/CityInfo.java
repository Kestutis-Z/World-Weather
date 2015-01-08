package com.haringeymobile.ukweather.data.objects;

import com.google.gson.annotations.SerializedName;

public class CityInfo {

	@SerializedName("coord")
	private Coordinates coordinates;

	@SerializedName("country")
	private String country;

	@SerializedName("id")
	private int cityId;

	@SerializedName("name")
	private String cityName;

	public String getCountry() {
		return country;
	}

	public String getCityName() {
		return cityName;
	}

}
