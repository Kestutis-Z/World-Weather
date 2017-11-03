package com.haringeymobile.ukweather.data.objects;

import com.google.gson.annotations.SerializedName;

public class Weather {

    public static final String ICON_URL_PREFIX = "https://openweathermap.org/img/w/";
    public static final String ICON_URL_SUFFIX = ".png";

    @SerializedName("description")
    private String description;

    @SerializedName("icon")
    private String icon;

    @SerializedName("id")
    private int id;

    @SerializedName("main")
    private String type;

    public String getDescription() {
        return description;
    }

    public String getIcon() {
        return icon;
    }

    public String getType() {
        return type;
    }

    public int getId() {
        return id;
    }

}
