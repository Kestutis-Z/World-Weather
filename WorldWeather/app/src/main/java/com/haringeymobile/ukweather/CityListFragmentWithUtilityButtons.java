package com.haringeymobile.ukweather;

import android.app.Activity;
import android.view.View;

/**
 * A fragment containing a list of cities with clickable buttons, requesting
 * utility work, such as renaming a city, or removing it from the database.
 */
public class CityListFragmentWithUtilityButtons extends
        BaseCityListFragmentWithButtons {

    /**
     * A listener for utility button clicks.
     */
    public interface OnUtilityButtonClickedListener {

        /**
         * Reacts to the request to remove the specified city from the database.
         *
         * @param cityId   OpenWeatherMap city ID
         * @param cityName city name in the database
         */
        public void onCityRecordDeletionRequested(int cityId, String cityName);

        /**
         * Reacts to the request to ic_action_playback_repeat the specified city.
         *
         * @param cityId           OpenWeatherMap city ID
         * @param cityOriginalName current city name in the database
         */
        public void onCityNameChangeRequested(int cityId,
                                              String cityOriginalName);
    }

    private OnUtilityButtonClickedListener onUtilityButtonClickedListener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            onUtilityButtonClickedListener = (OnUtilityButtonClickedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnUtilityButtonClickedListener");
        }
    }

    @Override
    protected BaseCityCursorAdapter getCityCursorAdapter() {
        return new CityUtilitiesCursorAdapter(parentActivity,
                R.layout.row_city_list_with_weather_buttons, null,
                COLUMNS_TO_DISPLAY, TO, 0, this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        onUtilityButtonClickedListener = null;
    }

    @Override
    public void onClick(View view) {
        int listItemPosition = getListView().getPositionForView(view);
        int cityId = cursorAdapter.getCityId(listItemPosition);
        String cityName = cursorAdapter.getCityName(listItemPosition);

        int viewId = view.getId();
        switch (viewId) {
            case R.id.city_rename_button:
                onUtilityButtonClickedListener.onCityNameChangeRequested(cityId,
                        cityName);
                break;
            case R.id.city_delete_button:
                onUtilityButtonClickedListener.onCityRecordDeletionRequested(
                        cityId, cityName);
                break;
            default:
                throw new IllegalArgumentException("Not supported view ID: "
                        + viewId);
        }
    }
}
