package com.haringeymobile.ukweather.data.objects;

import com.google.gson.annotations.SerializedName;

public class SystemParameters {

	@SerializedName("country")
	private String country;

	public String getCountry() {
		return country;
	}

}
