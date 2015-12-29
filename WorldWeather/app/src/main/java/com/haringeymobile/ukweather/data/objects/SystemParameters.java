package com.haringeymobile.ukweather.data.objects;

import com.google.gson.annotations.SerializedName;

public class SystemParameters {

    @SerializedName("country")
    private String country;

    @SerializedName("sunrise")
    private long sunriseTime;

    @SerializedName("sunset")
    private long sunsetTime;

    public String getCountry() {
        return country;
    }

    public long getSunriseTime() {
        return sunriseTime;
    }

    public long getSunsetTime() {
        return sunsetTime;
    }

}