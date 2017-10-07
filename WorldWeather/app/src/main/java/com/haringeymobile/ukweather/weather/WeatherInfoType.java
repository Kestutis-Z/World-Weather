package com.haringeymobile.ukweather.weather;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import com.haringeymobile.ukweather.R;
import com.haringeymobile.ukweather.data.OpenWeatherMapUrl;
import com.haringeymobile.ukweather.data.objects.CityCurrentWeather;
import com.haringeymobile.ukweather.data.objects.CityDailyWeatherForecast;
import com.haringeymobile.ukweather.data.objects.CityThreeHourlyWeatherForecast;
import com.haringeymobile.ukweather.data.objects.WeatherInformation;

import java.net.URL;

/**
 * The kind of weather information that can be requested by the user and
 * displayed on the screen.
 */
public enum WeatherInfoType implements Parcelable {

    /**
     * Current weather conditions.
     */
    CURRENT_WEATHER(1, CityCurrentWeather.class,
            R.string.weather_info_type_label_current_weather,
            R.drawable.ic_assignment_returned),

    /**
     * Daily weather forecast for up to 14 days.
     */
    DAILY_WEATHER_FORECAST(2, CityDailyWeatherForecast.class,
            R.string.weather_info_type_label_daily_forecast,
            R.drawable.ic_octicon_calendar),

    /**
     * Three hourly weather forecast for up to 5 days.
     */
    THREE_HOURLY_WEATHER_FORECAST(3, CityThreeHourlyWeatherForecast.class,
            R.string.weather_info_type_label_three_hourly_forecast,
            R.drawable.ic_assignment);

    /**
     * The number of days for the daily weather forecast. Currently this is the
     * maximum number allowed by the Open Weather Map.
     */
    private static final int DEFAULT_DAYS_COUNT_FOR_DAILY_FORECAST = 16;

    /**
     * An internal id for convenience.
     */
    private int id;
    /**
     * A class corresponding to this weather information type.
     */
    Class<? extends WeatherInformation> clazz;
    /**
     * A resource id for this weather information type's title to be presented
     * to users.
     */
    private int labelResourceId;
    /**
     * A resource id for this weather information type's icon to be presented to
     * users.
     */
    private int iconResourceId;

    WeatherInfoType(int id, Class<? extends WeatherInformation> clazz, int labelResourceId,
                    int iconResourceId) {
        this.id = id;
        this.clazz = clazz;
        this.labelResourceId = labelResourceId;
        this.iconResourceId = iconResourceId;
    }

    /**
     * Obtains the weather information type corresponding to the provided ID.
     *
     * @param id a weather information type ID
     * @return a weather information type
     */
    public static WeatherInfoType getTypeById(int id) {
        switch (id) {
            case 1:
                return CURRENT_WEATHER;
            case 2:
                return DAILY_WEATHER_FORECAST;
            case 3:
                return THREE_HOURLY_WEATHER_FORECAST;
            default:
                throw new IllegalArgumentException("Unsupported id: " + id);
        }
    }

    /**
     * @return an internal id for this weather information type
     */
    public int getId() {
        return id;
    }

    /**
     * @return a resource id for this weather information type's title to be
     * presented to users
     */
    public int getLabelResourceId() {
        return labelResourceId;
    }

    /**
     * @return a resource id for this weather information type's icon to be
     * presented to users.
     */
    public int getIconResourceId() {
        return iconResourceId;
    }

    /**
     * Obtains an Open Weather Map url for this weather info type.
     *
     * @param cityId an Open Weather Map city ID
     * @return a url containing the weather information for the specified city
     */
    public URL getOpenWeatherMapUrl(Context context, int cityId) {
        OpenWeatherMapUrl openWeatherMapUrl = new OpenWeatherMapUrl(context);
        switch (this) {
            case CURRENT_WEATHER:
                return openWeatherMapUrl.generateCurrentWeatherByCityIdUrl(cityId);
            case DAILY_WEATHER_FORECAST:
                return openWeatherMapUrl.generateDailyWeatherForecastUrl(cityId,
                        DEFAULT_DAYS_COUNT_FOR_DAILY_FORECAST);
            case THREE_HOURLY_WEATHER_FORECAST:
                return openWeatherMapUrl
                        .generateThreeHourlyWeatherForecastUrl(cityId);
            default:
                throw new IllegalWeatherInfoTypeArgumentException(this);
        }
    }

    public static final Creator<WeatherInfoType> CREATOR = new Creator<WeatherInfoType>() {

        @Override
        public WeatherInfoType createFromParcel(Parcel in) {
            WeatherInfoType weatherInfoType;
            try {
                weatherInfoType = valueOf(in.readString());
            } catch (IllegalArgumentException ex) {
                weatherInfoType = null;
            }
            return weatherInfoType;
        }

        @Override
        public WeatherInfoType[] newArray(int size) {
            return new WeatherInfoType[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this == null ? "" : name());
    }

    /**
     * An IllegalArgumentException to be throwed in switch or if/else statements
     * when a certain weather information type is not expected as an argument.
     */
    public static class IllegalWeatherInfoTypeArgumentException extends
            IllegalArgumentException {

        /**
         * For serialisation.
         */
        private static final long serialVersionUID = -3666143975910277111L;

        public IllegalWeatherInfoTypeArgumentException(
                WeatherInfoType weatherInfoType) {
            super("Unsupported weatherInfoType: " + weatherInfoType);
        }
    }

}
