package com.haringeymobile.ukweather.data.objects;

import com.google.gson.annotations.SerializedName;

public class Coordinates {

    @SerializedName("lat")
    private double latitude;

    @SerializedName("lon")
    private double longitude;

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }
}
