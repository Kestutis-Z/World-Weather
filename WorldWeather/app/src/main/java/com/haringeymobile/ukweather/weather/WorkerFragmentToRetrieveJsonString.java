package com.haringeymobile.ukweather.weather;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.widget.Toast;

import com.haringeymobile.ukweather.R;
import com.haringeymobile.ukweather.data.JsonFetcher;
import com.haringeymobile.ukweather.database.CityTable;
import com.haringeymobile.ukweather.database.SqlOperation;
import com.haringeymobile.ukweather.utils.AsyncTaskWithProgressBar;
import com.haringeymobile.ukweather.utils.MiscMethods;
import com.haringeymobile.ukweather.utils.SharedPrefsHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;

/**
 * A fragment to asynchronously obtain the specified JSON weather data. This fragment has no UI,
 * and simply acts as an executor of the {@link WorkerFragmentToRetrieveJsonString.RetrieveWeatherInformationJsonStringTask}.
 */
public class WorkerFragmentToRetrieveJsonString extends Fragment {

    /**
     * A listener for the requested JSON weather data retrieval.
     */
    public interface OnJsonStringRetrievedListener {

        /**
         * Reacts to the JSON weather information retrieval.
         *
         * @param jsonString        JSON weather data
         * @param weatherInfoType   the kind of weather information
         * @param shouldSaveLocally whether the retrieved data should be saved in the database
         */
        void onJsonStringRetrieved(String jsonString, WeatherInfoType weatherInfoType,
                                   boolean shouldSaveLocally);
    }

    private static final String OPEN_WEATHER_MAP_API_HTTP_CODE_KEY = "cod";

    private Activity parentActivity;
    private OnJsonStringRetrievedListener listener;
    /**
     * An Open Weather Map city ID.
     */
    private int cityId;
    private WeatherInfoType weatherInfoType;
    private RetrieveWeatherInformationJsonStringTask retrieveWeatherInformationJsonStringTask;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        parentActivity = activity;
        listener = (OnJsonStringRetrievedListener) activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    /**
     * Repeats the last weather data request, i.e. retrieves the weather information using
     * parameters (city and information type) used for the last attempt to obtain weather
     * information.
     */
    public void retrieveLastRequestedWeatherInfo() {
        int lastCityId = SharedPrefsHelper.getCityIdFromSharedPrefs(parentActivity);
        if (lastCityId != CityTable.CITY_ID_DOES_NOT_EXIST) {
            WeatherInfoType lastWeatherInfoType = SharedPrefsHelper
                    .getLastWeatherInfoTypeFromSharedPrefs(parentActivity);
            retrieveWeatherInfoJsonString(lastCityId, lastWeatherInfoType);
        }
    }

    /**
     * Starts an {@link android.os.AsyncTask} to obtain the requested JSON weather data.
     *
     * @param cityId          an Open Weather Map city ID
     * @param weatherInfoType a type of the requested weather data
     */
    public void retrieveWeatherInfoJsonString(int cityId, WeatherInfoType weatherInfoType) {
        if (MiscMethods.isUserOnline(parentActivity)) {
            this.cityId = cityId;
            this.weatherInfoType = weatherInfoType;

            URL openWeatherMapUrl = weatherInfoType.getOpenWeatherMapUrl(cityId);
            retrieveWeatherInformationJsonStringTask =
                    new RetrieveWeatherInformationJsonStringTask();
            retrieveWeatherInformationJsonStringTask.setContext(parentActivity);
            retrieveWeatherInformationJsonStringTask.execute(openWeatherMapUrl);
        } else {
            Toast.makeText(parentActivity, R.string.error_message_no_connection,
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (retrieveWeatherInformationJsonStringTask != null) {
            retrieveWeatherInformationJsonStringTask.cancel(true);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        parentActivity = null;
        listener = null;
    }

    /**
     * A task to obtain JSON weather data from the provided Open Weather Map
     * URL.
     * <p/>
     * <p/>
     * Since weather data don't change that often, retrieved JSON strings are saved locally, and
     * reused for a short period of time (chosen by user). So the task first checks if there
     * already exists a recently requested and saved data in the local SQLite Database, and
     * connects to internet only if such data is too old or does not exist.
     */
    private class RetrieveWeatherInformationJsonStringTask extends
            AsyncTaskWithProgressBar<URL, Void, String> {

        /**
         * If the data is obtained from the web service, it should be saved and reused for a short
         * period of time for efficiency reasons.
         */
        private boolean saveDataLocally = false;

        @Override
        protected String doInBackground(URL... params) {
            SqlOperation sqlOperation = new SqlOperation(parentActivity, weatherInfoType);
            String jsonString = sqlOperation.getJsonStringForWeatherInfo(cityId);
            if (jsonString == null && !isCancelled()) {
                jsonString = getJsonStringFromWebService(params[0]);
                saveDataLocally = jsonString != null;
            }
            return jsonString;
        }

        /**
         * Attempts to obtain the weather data from the provided Open Weather Map URL.
         *
         * @param url Open Weather Map URL
         * @return Json data for the requested city and weather information type, or {@code null}
         * in case of network problems
         */
        private String getJsonStringFromWebService(URL url) {
            try {
                return new JsonFetcher().getJsonString(url);
            } catch (IOException e) {
                MiscMethods.log("IOException in getJsonStringFromWebService()");
                return null;
            }
        }

        @Override
        protected void onPostExecute(String jsonString) {
            super.onPostExecute(jsonString);
            if (jsonString == null) {
                if (parentActivity != null) {
                    Toast.makeText(parentActivity, R.string.error_message_no_connection,
                            Toast.LENGTH_SHORT).show();
                }
            } else if (listener != null) {
                boolean isDataAvailable = isWeatherDataAvailable(jsonString);
                if (isDataAvailable) {
                    listener.onJsonStringRetrieved(jsonString, weatherInfoType, saveDataLocally);
                } else {
                    Toast.makeText(parentActivity, R.string.error_message_no_data,
                            Toast.LENGTH_LONG).show();
                }
            }
        }

        /**
         * Checks if the obtained JSON contains any useful data. This is necessary due to
         * a strange way HTTP status codes are handled in the Open Weather Map API - see
         * https://claudiosparpaglione.wordpress.com/tag/openweathermap for more details.
         *
         * @param jsonString JSON data for the requested city and weather information type
         * @return true if there are meaningful data to display, false otherwise
         */
        private boolean isWeatherDataAvailable(String jsonString) {
            try {
                JSONObject obj = new JSONObject(jsonString);
                if (obj.has(OPEN_WEATHER_MAP_API_HTTP_CODE_KEY)) {
                    int httpStatusCode = obj.getInt(OPEN_WEATHER_MAP_API_HTTP_CODE_KEY);
                    if (JsonFetcher.HTTP_STATUS_CODE_OK != httpStatusCode) {
                        return false;
                    }
                }
            } catch (JSONException e) {
                return false;
            }
            return true;
        }
    }

}