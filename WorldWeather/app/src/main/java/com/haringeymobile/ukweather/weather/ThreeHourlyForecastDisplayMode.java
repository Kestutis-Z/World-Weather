package com.haringeymobile.ukweather.weather;

/**
 * The way three-hourly weather forecast is displayed.
 */
public enum ThreeHourlyForecastDisplayMode {

    VIEW_PAGER(1),
    LIST(2);

    /**
     * Internal ID for convenience.
     */
    private int id;

    ThreeHourlyForecastDisplayMode(int id) {
        this.id = id;
    }

    public static ThreeHourlyForecastDisplayMode getForecastDisplayModeById(int id) {
        switch (id) {
            case 1:
                return VIEW_PAGER;
            case 2:
                return LIST;
            default:
                throw new IllegalArgumentException("Unsupported id: " + id);
        }
    }

}