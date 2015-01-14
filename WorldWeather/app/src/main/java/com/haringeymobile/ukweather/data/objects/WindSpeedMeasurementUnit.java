package com.haringeymobile.ukweather.data.objects;

import com.haringeymobile.ukweather.R;

/**
 * A unit to measure wind speed.
 */
public enum WindSpeedMeasurementUnit {

    METERS_PER_SECOND(10,
            R.string.weather_info_wind_speed_unit_meters_per_second) {
        @Override
        public double convertSpeed(double speedInMetersPerSecond) {
            return speedInMetersPerSecond;
        }
    },

    KILOMETERS_PER_HOUR(20,
            R.string.weather_info_wind_speed_unit_kilometers_per_hour) {
        @Override
        public double convertSpeed(double speedInMetersPerSecond) {
            return speedInMetersPerSecond * 3600 / 1000;
        }
    },

    MILES_PER_HOUR(30, R.string.weather_info_wind_speed_unit_miles_per_hour) {
        @Override
        public double convertSpeed(double speedInMetersPerSecond) {
            return speedInMetersPerSecond * 3600 / 1609.344;
        }
    };

    /**
     * Internal ID for convenience.
     */
    private int id;
    /**
     * String resource ID for this unit.
     */
    private int displayResourceId;

    private WindSpeedMeasurementUnit(int id, int displayResourceId) {
        this.id = id;
        this.displayResourceId = displayResourceId;
    }

    public int getId() {
        return id;
    }

    public int getDisplayResourceId() {
        return displayResourceId;
    }

    /**
     * Obtains a wind speed in this measurement units.
     *
     * @param speedInMetersPerSecond
     * @return converted wind speed
     */
    public abstract double convertSpeed(double speedInMetersPerSecond);

    public static WindSpeedMeasurementUnit getWindSpeedMeasurementUnitById(
            int id) {
        switch (id) {
            case 10:
                return METERS_PER_SECOND;
            case 20:
                return KILOMETERS_PER_HOUR;
            case 30:
                return MILES_PER_HOUR;
            default:
                throw new IllegalArgumentException(
                        "Unsupported windSpeedMeasurementUnitId: " + id);
        }
    }

}
