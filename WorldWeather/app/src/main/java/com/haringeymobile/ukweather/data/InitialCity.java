package com.haringeymobile.ukweather.data;

/**
 * A city that will be appear in the city list the first time the app is opened.
 * This city can be removed from the list or renamed by the user.
 */
public enum InitialCity {

    LONDON(2643743, "London"),

    MEXICO_CITY(3530597, "Mexico City"),

    RIO_DE_JANEIRO(3451190, "Rio de Janeiro"),

    CAIRO(360630, "Cairo"),

    MOSCOW(524901, "Moscow"),

    SEOUL(1835848, "Seoul"),

    BEIJING(1816670, "Beijing"),

    LOS_ANGELES(5368361, "Los Angeles"),

    ISTANBUL(745044, "Istanbul"),

    TOKYO(1850147, "Tokyo"),

    KOLKATA(1275004, "Kolkata"),
    ;

    /**
     * A city identification number in the Open Weather Map database.
     */
    private final int openWeatherMapId;
    /**
     * An initial city display name (can be changed by the user).
     */
    private final String displayName;

    private InitialCity(int openWeatherMapId, String displayName) {
        this.openWeatherMapId = openWeatherMapId;
        this.displayName = displayName;
    }

    /**
     * @return a city identification number in the Open Weather Map database
     */
    public int getOpenWeatherMapId() {
        return openWeatherMapId;
    }

    /**
     * @return an initial city display name
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * @return the total number of the initial cities
     */
    public static int getInitialCityCount() {
        return InitialCity.values().length;
    }

}
