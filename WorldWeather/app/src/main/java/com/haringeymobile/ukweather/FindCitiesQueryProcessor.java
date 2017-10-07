package com.haringeymobile.ukweather;

import android.content.Context;
import android.support.v4.app.FragmentActivity;

import com.haringeymobile.ukweather.data.OpenWeatherMapUrl;

import java.net.URL;

/**
 * Processor of the user query to find cities in the OWM database.
 */
class FindCitiesQueryProcessor {

    interface InvalidQueryListener {

        /**
         * Displays an informational dialog.
         *
         * @param stringResourceId resource id for the message to be displayed in the dialog
         */
        void showAlertDialog(int stringResourceId);

    }

    private static final int LATITUDE_MIN_VALUE = -90;
    private static final int LATITUDE_MAX_VALUE = 90;
    private static final int LONGITUDE_MIN_VALUE = -180;
    private static final int LONGITUDE_MAX_VALUE = 180;

    private static final String DECIMAL_MARK = ".";
    private static final String DECIMAL_MARK_REGEX = "\\.";
    private static final String COORDINATES_SEPARATOR = ",";

    private InvalidQueryListener invalidQueryListener;
    private String query;

    FindCitiesQueryProcessor(FragmentActivity callingActivity, String query) {
        this.invalidQueryListener = (InvalidQueryListener) callingActivity;
        this.query = query;
    }

    /**
     * Obtains the URL to be used to retrieve the cities, satisfying user's query.
     */
    URL getUrlForFindCitiesQuery(Context context) {
        URL url;
        boolean doesQueryContainAnyLetters = doesQueryContainAnyLetters();
        if (doesQueryContainAnyLetters) {
            url = new OpenWeatherMapUrl(context).getAvailableCitiesListUrl(query);
        } else {
            url = getUrlUsingGeographicalCoordinates(context);
        }

        invalidQueryListener = null;
        return url;
    }

    /**
     * If the query doesn't contain any letters, it is assumed to contain a latitude and longitude.
     *
     * @return URL to be used to retrieve the cities, satisfying user's query
     */
    private URL getUrlUsingGeographicalCoordinates(Context context) {
        // we split the query into latitude and longitude
        String providedLatitude;
        String providedLongitude;

        if (query.contains(COORDINATES_SEPARATOR)) {
            String[] coordinates = query.split(COORDINATES_SEPARATOR);
            providedLatitude = coordinates[0];
            providedLongitude = coordinates[1];

            String processedLatitude = processProvidedCoordinate(providedLatitude, true);
            String processedLongitude = processProvidedCoordinate(providedLongitude, false);
            if (processedLatitude == null || processedLongitude == null) {
                return null;
            } else {
                return new OpenWeatherMapUrl(context).
                        getAvailableCitiesListUrlByGeographicalCoordinates(providedLatitude,
                                providedLongitude);
            }
        } else {
            invalidQueryListener.showAlertDialog(
                    R.string.coordinates_error_message_missing_separator);
            return null;
        }

    }

    /**
     * Checks the validity of the user provided text, and makes the necessary changes, or asks the
     * user to correct it.
     *
     * @param providedCoordinate coordinate, provided by a user
     * @param isLatitude         whether the provided coordinate is a latitude, or a longitude
     * @return coordinate that can be submitted for the OWM query, or null if it is invalid
     */
    private String processProvidedCoordinate(String providedCoordinate, boolean isLatitude) {
        // we split the provided number into integer and decimal parts
        String integer;
        String decimal;
        if (providedCoordinate.contains(DECIMAL_MARK)) {
            String[] decimalFraction = providedCoordinate.split(DECIMAL_MARK_REGEX);
            integer = decimalFraction[0];
            decimal = decimalFraction[1];
        } else {
            integer = providedCoordinate;
            decimal = null;
        }

        int integerPart;
        int decimalPart = 0;

        try {
            integerPart = Integer.parseInt(integer);
        } catch (NumberFormatException e) {
            invalidQueryListener.showAlertDialog(
                    R.string.coordinates_error_message_number_format);
            return null;
        }

        int minValue = isLatitude ? LATITUDE_MIN_VALUE : LONGITUDE_MIN_VALUE;
        int maxValue = isLatitude ? LATITUDE_MAX_VALUE : LONGITUDE_MAX_VALUE;
        if (integerPart < minValue || integerPart > maxValue) {
            invalidQueryListener.showAlertDialog(isLatitude ?
                    R.string.coordinates_error_message_latitude_range :
                    R.string.coordinates_error_message_longitude_range);
            return null;
        }

        if (decimal != null) {
            // we leave at most three decimal places, which is approximately 100 meters precision
            int places = Math.min(decimal.length(), 3);
            decimal = decimal.substring(0, places);
            try {
                decimalPart = Integer.parseInt(decimal);
            } catch (NumberFormatException e) {
                invalidQueryListener.showAlertDialog(
                        R.string.coordinates_error_message_number_format);
                return null;
            }
        }

        String coordinate = Integer.toString(integerPart);
        if (decimalPart > 0) {
            coordinate += DECIMAL_MARK + decimalPart;
        }
        return coordinate;
    }

    private boolean doesQueryContainAnyLetters() {
        for (int i = 0; i < query.length(); i++) {
            if (Character.isLetter(query.charAt(i))) {
                return true;
            }
        }
        return false;
    }

}