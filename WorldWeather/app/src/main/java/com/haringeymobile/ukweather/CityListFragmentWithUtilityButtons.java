package com.haringeymobile.ukweather;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.haringeymobile.ukweather.utils.SharedPrefsHelper;

/**
 * A fragment containing a list of cities with clickable buttons, requesting utility work, such as
 * renaming a city, or removing it from the database.
 */
public class CityListFragmentWithUtilityButtons extends BaseCityListFragmentWithButtons {

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
        void onCityRecordDeletionRequested(int cityId, String cityName);

        /**
         * Reacts to the request to rename the specified city.
         *
         * @param cityId           OpenWeatherMap city ID
         * @param cityOriginalName current city name in the database
         */
        void onCityNameChangeRequested(int cityId, String cityOriginalName);
    }

    private OnUtilityButtonClickedListener onUtilityButtonClickedListener;

    @Override
    public void onAttach(@NonNull Activity activity) {
        super.onAttach(activity);
        try {
            onUtilityButtonClickedListener = (OnUtilityButtonClickedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnUtilityButtonClickedListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        boolean isRemovalModeButton = SharedPrefsHelper.isRemovalModeButton(parentActivity);

        int layoutResourceId = isRemovalModeButton ?
                R.layout.general_drag_sort_list_remove_disabled :
                R.layout.general_drag_sort_list_remove_enabled;

        return inflater.inflate(layoutResourceId, container, false);
    }

    @Override
    protected BaseCityCursorAdapter getCityCursorAdapter() {
        return new CityUtilitiesCursorAdapter(parentActivity,
                R.layout.row_city_list_with_weather_buttons, null, COLUMNS_TO_DISPLAY, TO, 0, this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        onUtilityButtonClickedListener = null;
    }

    @Override
    protected boolean jumpToTheTopOfList() {
        return false;
    }

    @Override
    public void onClick(View view) {
        int listItemPosition = getListView().getPositionForView(view);
        int cityId = cursorAdapter.getCityId(listItemPosition);
        String cityName = cursorAdapter.getCityName(listItemPosition);

        int viewId = view.getId();
        switch (viewId) {
            case R.id.city_rename_button:
                onUtilityButtonClickedListener.onCityNameChangeRequested(cityId, cityName);
                break;
            case R.id.city_remove_button:
                onUtilityButtonClickedListener.onCityRecordDeletionRequested(cityId, cityName);
                break;
            default:
                throw new IllegalArgumentException("Not supported view ID: " + viewId);
        }
    }

}