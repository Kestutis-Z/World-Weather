package com.haringeymobile.ukweather.data.objects;

import com.haringeymobile.ukweather.R;

/**
 * A unit to measure wind speed.
 */
public enum WindSpeedMeasurementUnit {

    METERS_PER_SECOND(10, R.string.weather_info_wind_speed_unit_meters_per_second) {
        @Override
        public double convertSpeed(double speedInMetersPerSecond) {
            return speedInMetersPerSecond;
        }

    },

    KILOMETERS_PER_HOUR(20, R.string.weather_info_wind_speed_unit_kilometers_per_hour) {
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

    },

    BEAUFORT_SCALE(40, R.string.weather_info_wind_speed_beaufort_scale_force) {
        @Override
        public double convertSpeed(double speedInMetersPerSecond) {
            return getWindForce(speedInMetersPerSecond);
        }

        /**
         * Converts wind speed to Beaufort scale force units.
         *
         * @param speedInMetersPerSecond wind speed in mps
         * @return wind speed in force units
         */
        public int getWindForce(double speedInMetersPerSecond) {
            int force;

            if (speedInMetersPerSecond < 0.3) {
                force = 0;
            } else if (speedInMetersPerSecond < 1.5) {
                force = 1;
            } else if (speedInMetersPerSecond < 3.3) {
                force = 2;
            } else if (speedInMetersPerSecond < 5.5) {
                force = 3;
            } else if (speedInMetersPerSecond < 8.0) {
                force = 4;
            } else if (speedInMetersPerSecond < 10.8) {
                force = 5;
            } else if (speedInMetersPerSecond < 13.9) {
                force = 6;
            } else if (speedInMetersPerSecond < 17.2) {
                force = 7;
            } else if (speedInMetersPerSecond < 20.7) {
                force = 8;
            } else if (speedInMetersPerSecond < 24.5) {
                force = 9;
            } else if (speedInMetersPerSecond < 28.4) {
                force = 10;
            } else if (speedInMetersPerSecond < 32.6) {
                force = 11;
            } else {
                force = 12;
            }

            return force;
        }

    };

    /**
     * Internal ID for convenience.
     */
    private final int id;
    /**
     * String resource ID for this unit.
     */
    private final int displayResourceId;

    WindSpeedMeasurementUnit(int id, int displayResourceId) {
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
     * Obtains a wind speed in this measurement unit.
     *
     * @param speedInMetersPerSecond wind speed in mps
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
            case 40:
                return BEAUFORT_SCALE;
            default:
                throw new IllegalArgumentException("Unsupported windSpeedMeasurementUnitId: " + id);
        }
    }

    public static int getBeaufortScaleWindDescriptionStringResourceId(int windForce) {
        switch (windForce) {
            case 0:
                return R.string.weather_info_wind_speed_beaufort_scale_0;
            case 1:
                return R.string.weather_info_wind_speed_beaufort_scale_1;
            case 2:
                return R.string.weather_info_wind_speed_beaufort_scale_2;
            case 3:
                return R.string.weather_info_wind_speed_beaufort_scale_3;
            case 4:
                return R.string.weather_info_wind_speed_beaufort_scale_4;
            case 5:
                return R.string.weather_info_wind_speed_beaufort_scale_5;
            case 6:
                return R.string.weather_info_wind_speed_beaufort_scale_6;
            case 7:
                return R.string.weather_info_wind_speed_beaufort_scale_7;
            case 8:
                return R.string.weather_info_wind_speed_beaufort_scale_8;
            case 9:
                return R.string.weather_info_wind_speed_beaufort_scale_9;
            case 10:
                return R.string.weather_info_wind_speed_beaufort_scale_10;
            case 11:
                return R.string.weather_info_wind_speed_beaufort_scale_11;
            case 12:
                return R.string.weather_info_wind_speed_beaufort_scale_12;
            default:
                throw new IllegalArgumentException("Not supported windForce: " + windForce);
        }

    }

}