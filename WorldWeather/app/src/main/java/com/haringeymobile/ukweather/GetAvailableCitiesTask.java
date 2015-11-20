package com.haringeymobile.ukweather;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import com.google.gson.Gson;
import com.haringeymobile.ukweather.data.JsonFetcher;
import com.haringeymobile.ukweather.data.objects.CityCurrentWeather;
import com.haringeymobile.ukweather.data.objects.Coordinates;
import com.haringeymobile.ukweather.data.objects.SearchResponseForFindQuery;
import com.haringeymobile.ukweather.utils.AsyncTaskWithProgressBar;
import com.haringeymobile.ukweather.utils.MiscMethods;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * A task to process the city search URL and deal with the obtained result.
 */
class GetAvailableCitiesTask extends
        AsyncTaskWithProgressBar<URL, Void, SearchResponseForFindQuery> {

    /**
     * A listener for search response retrieval completion.
     */
    public interface OnCitySearchResponseRetrievedListener {

        /**
         * Reacts to the obtained city search result.
         *
         * @param searchResponseForFindQuery an object corresponding to the JSON string provided by
         *                                   the Open Weather Map 'find cities' query
         */
        public void onSearchResponseForFindQueryRetrieved(SearchResponseForFindQuery
                                                                  searchResponseForFindQuery);

    }

    private static final String CITY_SEARCH_RESULTS_FRAGMENT_TAG = "ic_action_search results";
    private static final String NO_CITIES_FOUND_DIALOG_FRAGMENT_TAG = "no cities fragment";

    private final FragmentActivity activity;

    /**
     * @param activity an activity from which this task is started
     */
    GetAvailableCitiesTask(FragmentActivity activity) {
        this.activity = activity;
        setContext(activity);
    }

    @Override
    protected SearchResponseForFindQuery doInBackground(URL... params) {
        String jsonString = null;
        try {
            jsonString = new JsonFetcher().getJsonString(params[0]);
        } catch (IOException e) {
            MiscMethods.log("IOException in SearchResponseForFindQuery doInBackground()");
            return null;
        }
        return jsonString == null ? null : new Gson().fromJson(jsonString,
                SearchResponseForFindQuery.class);
    }

    @Override
    protected void onPostExecute(SearchResponseForFindQuery result) {
        super.onPostExecute(result);
        if (result == null || result.getCode() != JsonFetcher.HTTP_STATUS_CODE_OK) {
            displayErrorMessage();
        } else if (result.getCount() < 1) {
            showNoCitiesFoundAlertDialog();
        } else {
            dealWithSearchResponseForFindCitiesQuery(result);
        }
    }

    /**
     * Displays the network connection error message.
     */
    private void displayErrorMessage() {
        if (activity != null) {
            Toast.makeText(activity, R.string.error_message_no_connection, Toast.LENGTH_SHORT)
                    .show();
        }
    }

    /**
     * Shows an alert dialog informing that no cities were found for the query.
     */
    void showNoCitiesFoundAlertDialog() {
        DialogFragment dialogFragment = new DialogFragment() {

            @Override
            public Dialog onCreateDialog(Bundle savedInstanceState) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                OnClickListener onClickListener = getDialogOnClickListener();
                builder.setTitle(R.string.dialog_title_no_cities_found)
                        .setMessage(R.string.message_no_cities_found)
                        .setPositiveButton(android.R.string.ok, onClickListener);
                return builder.create();
            }

            private OnClickListener getDialogOnClickListener() {
                OnClickListener onClickListener = new OnClickListener() {

                    public void onClick(DialogInterface dialog, int id) {
                        dismiss();
                    }

                };
                return onClickListener;
            }

        };

        dialogFragment.show(activity.getSupportFragmentManager(),
                NO_CITIES_FOUND_DIALOG_FRAGMENT_TAG);
    }

    /**
     * Handles the city search response.
     *
     * @param result a city search response, containing found cities and related data
     */
    private void dealWithSearchResponseForFindCitiesQuery(SearchResponseForFindQuery result) {
        informActivityAboutObtainedSearchResponse(result);
        showDialogWithSearchResults(result);
    }

    /**
     * Passes the city search response to the activity that started this task for further
     * processing.
     *
     * @param result a city search response, containing found cities and related data
     */
    private void informActivityAboutObtainedSearchResponse(SearchResponseForFindQuery result) {
        try {
            OnCitySearchResponseRetrievedListener listener =
                    (OnCitySearchResponseRetrievedListener) activity;
            listener.onSearchResponseForFindQueryRetrieved(result);
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() +
                    " must implement OnCitySearchResponseRetrievedListener");
        }
    }

    /**
     * Creates and shows a dialog with the list of found city names (so the user can choose one of
     * them).
     *
     * @param result a city search response, containing found cities and related data
     */
    private void showDialogWithSearchResults(SearchResponseForFindQuery result) {
        ArrayList<String> foundCityNames = getFoundCityNames(result);
        CitySearchResultsDialog citySearchResultsDialog = CitySearchResultsDialog
                .newInstance(foundCityNames);
        citySearchResultsDialog.show(activity.getSupportFragmentManager(),
                CITY_SEARCH_RESULTS_FRAGMENT_TAG);
    }

    /**
     * Obtains a list of city names satisfying the user's search query.
     *
     * @param result a city search response, containing found cities and related data
     * @return a list of city names (with coordinates)
     */
    private ArrayList<String> getFoundCityNames(SearchResponseForFindQuery result) {
        ArrayList<String> foundCityNames = new ArrayList<>();
        List<CityCurrentWeather> cities = result.getCities();
        for (CityCurrentWeather city : cities) {
            String cityName = getCityName(city);
            foundCityNames.add(cityName);
        }
        return foundCityNames;
    }

    /**
     * Obtains the city name to be displayed in the found city list.
     *
     * @param cityCurrentWeather weather and other information about the city
     * @return a city name (with latitude and longitude)
     */
    private String getCityName(CityCurrentWeather cityCurrentWeather) {
        Coordinates cityCoordinates = cityCurrentWeather.getCoordinates();
        String cityName = cityCurrentWeather.getCityName() + ", "
                + cityCurrentWeather.getSystemParameters().getCountry() + "\n("
                + MiscMethods.formatDoubleValue(cityCoordinates.getLatitude())
                + ", "
                + MiscMethods.formatDoubleValue(cityCoordinates.getLongitude())
                + ")";
        return cityName;
    }

}