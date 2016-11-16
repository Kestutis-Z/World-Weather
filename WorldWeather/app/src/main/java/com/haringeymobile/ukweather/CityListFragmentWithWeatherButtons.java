package com.haringeymobile.ukweather;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.haringeymobile.ukweather.weather.WeatherInfoType;

/**
 * A fragment containing a list of cities with clickable buttons, requesting various weather
 * information.
 */
public class CityListFragmentWithWeatherButtons extends
        BaseCityListFragmentWithButtons {

    /**
     * A listener for weather information button clicks.
     */
    public interface OnWeatherInfoButtonClickedListener {

        /**
         * Reacts to the request to obtain weather information.
         *
         * @param cityId          OpenWeatherMap city ID
         * @param weatherInfoType a kind of weather information requested
         */
        void onCityWeatherInfoRequested(int cityId, WeatherInfoType weatherInfoType);

    }

    private OnWeatherInfoButtonClickedListener onWeatherInfoButtonClickedListener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        onWeatherInfoButtonClickedListener = (OnWeatherInfoButtonClickedListener) activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_city_list, container, false);
    }

    @Override
    protected BaseCityCursorAdapter getCityCursorAdapter() {
        return new CityWeatherCursorAdapter(parentActivity,
                R.layout.row_city_list_with_weather_buttons, null,
                COLUMNS_TO_DISPLAY, TO, 0, this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        onWeatherInfoButtonClickedListener = null;
    }

    @Override
    public void onClick(View view) {
        int listItemPosition = getListView().getPositionForView(view);
        int cityId = cursorAdapter.getCityId(listItemPosition);
        WeatherInfoType weatherInfoType = getRequestedWeatherInfoType(view);
        onWeatherInfoButtonClickedListener.onCityWeatherInfoRequested(cityId, weatherInfoType);
    }

    /**
     * Obtains the kind of weather information associated with the view.
     *
     * @param view the clicked view (button)
     * @return requested weather information type
     */
    private WeatherInfoType getRequestedWeatherInfoType(View view) {
        int viewId = view.getId();
        switch (viewId) {
            case R.id.city_current_weather_button:
                return WeatherInfoType.CURRENT_WEATHER;
            case R.id.city_daily_weather_forecast_button:
                return WeatherInfoType.DAILY_WEATHER_FORECAST;
            case R.id.city_three_hourly_weather_forecast_button:
                return WeatherInfoType.THREE_HOURLY_WEATHER_FORECAST;
            default:
                throw new IllegalArgumentException("Not supported view ID: " + viewId);
        }
    }

}