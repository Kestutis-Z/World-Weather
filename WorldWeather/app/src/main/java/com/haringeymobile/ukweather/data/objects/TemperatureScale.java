package com.haringeymobile.ukweather.data.objects;

import com.haringeymobile.ukweather.R;

/**
 * Temperature measurement system.
 */
public enum TemperatureScale {

    CELSIUS(10, R.string.weather_info_degree_celsius) {
        @Override
        public double convertTemperature(double kelvins) {
            return kelvins - Temperature.DIFFERENCE_BETWEEN_KELVIN_AND_CELCIUS;
        }
    },

    FAHRENHEIT(20, R.string.weather_info_degree_fahrenheit) {
        @Override
        public double convertTemperature(double kelvins) {
            double celsius = CELSIUS.convertTemperature(kelvins);
            return celsius * 9 / 5 + 32;
        }
    };

    /**
     * Internal ID for convenience.
     */
    private final int id;
    /**
     * String resource ID for this scale.
     */
    private final int displayResourceId;

    TemperatureScale(int id, int displayResourceId) {
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
     * Obtains a temperature, measured in this scale.
     *
     * @param kelvins temperature measured in Kelvins
     * @return converted temperature
     */
    public abstract double convertTemperature(double kelvins);

    public static TemperatureScale getTemperatureScaleById(int id) {
        switch (id) {
            case 10:
                return CELSIUS;
            case 20:
                return FAHRENHEIT;
            default:
                throw new IllegalArgumentException("Unsupported temperatureScaleId: " + id);
        }
    }

}