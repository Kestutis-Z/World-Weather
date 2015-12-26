package com.haringeymobile.ukweather.database;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.util.Pair;

import com.haringeymobile.ukweather.weather.WeatherInfoType;

/**
 * A layer between the app and the SQLite database, responsible for the CRUD functions.
 */
public class SqlOperation {

    private Context context;
    /**
     * The name of the {@link com.haringeymobile.ukweather.database.CityTable} column holding
     * weather information as a JSON string.
     */
    private String columnNameForJsonString;
    /**
     * The name of the {@link com.haringeymobile.ukweather.database.CityTable} column holding the
     * time for the last weather information update.
     */
    private String columnNameForLastQueryTime;

    /**
     * A constructor of SQLOperation, where weather information type is not important (for
     * instance, an operation to delete a record).
     */
    public SqlOperation(Context context) {
        this.context = context;
    }

    /**
     * A constructor of SQLOperation, where the specified weather information type determines
     * which columns will be queried or updated.
     *
     * @param weatherInfoType a kind of weather information
     */
    public SqlOperation(Context context, WeatherInfoType weatherInfoType) {
        this.context = context;
        switch (weatherInfoType) {
            case CURRENT_WEATHER:
                columnNameForJsonString = CityTable.COLUMN_CACHED_JSON_CURRENT;
                columnNameForLastQueryTime = CityTable.COLUMN_LAST_QUERY_TIME_FOR_CURRENT_WEATHER;
                break;
            case DAILY_WEATHER_FORECAST:
                columnNameForJsonString = CityTable.COLUMN_CACHED_JSON_DAILY_FORECAST;
                columnNameForLastQueryTime =
                        CityTable.COLUMN_LAST_QUERY_TIME_FOR_DAILY_WEATHER_FORECAST;
                break;
            case THREE_HOURLY_WEATHER_FORECAST:
                columnNameForJsonString = CityTable.COLUMN_CACHED_JSON_THREE_HOURLY_FORECAST;
                columnNameForLastQueryTime =
                        CityTable.COLUMN_LAST_QUERY_TIME_FOR_THREE_HOURLY_WEATHER_FORECAST;
                break;
            default:
                throw new WeatherInfoType.IllegalWeatherInfoTypeArgumentException(
                        weatherInfoType);
        }
    }

    /**
     * Updates current weather record for the specified city if it already exists in the database,
     * otherwise inserts a new record.
     *
     * @param cityId         Open Weather Map city ID
     * @param cityName       Open Weather Map city name
     * @param currentWeather Json string for the current city weather
     */
    public void updateOrInsertCityWithCurrentWeather(int cityId, String cityName,
                                                     String currentWeather) {
        if (!CityTable.COLUMN_CACHED_JSON_CURRENT.equals(columnNameForJsonString)) {
            throw new IllegalStateException(
                    "This method is expected to deal with current weather information only");
        }

        Cursor cursor = getCursorWithCityId(cityId);
        if (cursor == null) {
            return;
        }
        boolean cityIdExists = cursor.moveToFirst();
        if (cityIdExists) {
            Uri rowUri = getRowUri(cursor);
            ContentValues newValues =
                    createContentValuesWithDateAndWeatherJsonString(currentWeather);
            context.getContentResolver().update(rowUri, newValues, null, null);
            cursor.close();
        } else {
            insertNewCityWithCurrentWeather(cityId, cityName, currentWeather);
        }
    }

    /**
     * Inserts a new current weather record for the specified city.
     *
     * @param cityId         Open Weather Map city ID
     * @param cityName       Open Weather Map city name
     * @param currentWeather Json string for the current city weather
     */
    public void insertNewCityWithCurrentWeather(int cityId, String cityName,
                                                String currentWeather) {
        ContentValues newValues = new ContentValues();
        newValues.put(CityTable.COLUMN_CITY_ID, cityId);
        newValues.put(CityTable.COLUMN_NAME, cityName);
        long currentTime = System.currentTimeMillis();
        newValues.put(CityTable.COLUMN_LAST_QUERY_TIME_FOR_CURRENT_WEATHER, currentTime);
        newValues.put(CityTable.COLUMN_LAST_OVERALL_QUERY_TIME, currentTime);
        newValues.put(CityTable.COLUMN_CACHED_JSON_CURRENT, currentWeather);
        newValues.put(CityTable.COLUMN_LAST_QUERY_TIME_FOR_DAILY_WEATHER_FORECAST,
                CityTable.CITY_NEVER_QUERIED);
        newValues.putNull(CityTable.COLUMN_CACHED_JSON_DAILY_FORECAST);
        newValues.put(CityTable.COLUMN_LAST_QUERY_TIME_FOR_THREE_HOURLY_WEATHER_FORECAST,
                CityTable.CITY_NEVER_QUERIED);
        newValues.putNull(CityTable.COLUMN_CACHED_JSON_THREE_HOURLY_FORECAST);
        context.getContentResolver().insert(WeatherContentProvider.CONTENT_URI_CITY_RECORDS,
                newValues);
    }

    /**
     * Obtains a cursor over the specified city record ID in the
     * {@link com.haringeymobile.ukweather.database.CityTable}.
     *
     * @param cityId Open Weather Map city ID
     */
    private Cursor getCursorWithCityId(int cityId) {
        if (context == null) {
            return null;
        }
        return context.getContentResolver().query(
                WeatherContentProvider.CONTENT_URI_CITY_RECORDS,
                new String[]{CityTable._ID, CityTable.COLUMN_CITY_ID},
                CityTable.COLUMN_CITY_ID + "=?",
                new String[]{Integer.toString(cityId)}, null);
    }

    /**
     * Obtains the uri of the row pointed to by the provided cursor.
     */
    private Uri getRowUri(Cursor cursor) {
        int columnIndex = cursor.getColumnIndexOrThrow(CityTable._ID);
        long rowId = cursor.getLong(columnIndex);
        return getRowUri(rowId);
    }

    /**
     * Obtains the uri of the row with the given ID.
     */
    private Uri getRowUri(long rowId) {
        return ContentUris.withAppendedId(WeatherContentProvider.CONTENT_URI_CITY_RECORDS, rowId);
    }

    /**
     * Updates the specified city with new weather data.
     *
     * @param cityId     Open Weather Map city ID
     * @param jsonString JSON string for the weather information of some kind
     */
    public void updateWeatherInfo(int cityId, String jsonString) {
        Cursor cursor = getCursorWithWeatherInfo(cityId);
        if (cursor == null) {
            return;
        }
        if (!cursor.moveToFirst()) {
            cursor.close();
            return;
        }
        Uri rowUri = getRowUri(cursor);
        ContentValues newValues = createContentValuesWithDateAndWeatherJsonString(jsonString);
        context.getContentResolver().update(rowUri, newValues, null, null);
        cursor.close();
    }

    /**
     * Obtains a cursor over the specified city weather information record in
     * the {@link com.haringeymobile.ukweather.database.CityTable}.
     *
     * @param cityId Open Weather Map city ID
     */
    private Cursor getCursorWithWeatherInfo(int cityId) {
        if (context == null) {
            return null;
        }
        return context.getContentResolver().query(
                WeatherContentProvider.CONTENT_URI_CITY_RECORDS,
                new String[]{CityTable._ID, columnNameForLastQueryTime,
                        columnNameForJsonString,
                        CityTable.COLUMN_LAST_OVERALL_QUERY_TIME},
                CityTable.COLUMN_CITY_ID + "=?",
                new String[]{Integer.toString(cityId)}, null);
    }

    /**
     * Creates {@link android.content.ContentValues} with the added time and weather information
     * values.
     *
     * @param jsonString JSON string for the weather information of some kind
     */
    private ContentValues createContentValuesWithDateAndWeatherJsonString(String jsonString) {
        ContentValues newValues = new ContentValues();
        long currentTime = System.currentTimeMillis();
        newValues.put(columnNameForLastQueryTime, currentTime);
        newValues.put(CityTable.COLUMN_LAST_OVERALL_QUERY_TIME, currentTime);
        newValues.put(columnNameForJsonString, jsonString);
        return newValues;
    }

    /**
     * Obtains cached JSON data for the specified city.
     *
     * @param cityId Open Weather Map city ID
     * @return a string, representing JSON weather data, or null, if no cached data is stored
     */
    public Pair<String, Long> getJsonStringForWeatherInfo(int cityId) {
        Cursor cursor = getCursorWithWeatherInfo(cityId);
        if (cursor == null) {
            return null;
        }
        if (!cursor.moveToFirst()) {
            cursor.close();
            return null;
        }

        String weatherInfoJson = getJsonStringForWeatherInfo(cursor);
        Long lastQueryTime = CityTable.CITY_NEVER_QUERIED;
        if (weatherInfoJson != null) {
            int columnIndexForLastQueryTime = cursor.getColumnIndexOrThrow(
                    columnNameForLastQueryTime);
            lastQueryTime = cursor.getLong(columnIndexForLastQueryTime);

            int columnIndex = cursor.getColumnIndexOrThrow(CityTable._ID);
            long rowId = cursor.getLong(columnIndex);
            setLastOverallQueryTimeToCurrentTime(rowId);
        }

        Pair<String, Long> storedWeatherInfo = Pair.create(weatherInfoJson, lastQueryTime);
        cursor.close();
        return storedWeatherInfo;
    }

    /**
     * Obtains cached JSON data using the specified cursor.
     *
     * @param cursor a cursor pointing to the {@link
     *               com.haringeymobile.ukweather.database.CityTable} row with the cached
     *               weather data
     * @return a string, representing JSON weather data, or null, if the cached weather data is
     * outdated
     */
    private String getJsonStringForWeatherInfo(Cursor cursor) {
        int columnIndexForWeatherInfo = cursor.getColumnIndexOrThrow(columnNameForJsonString);
        return cursor.getString(columnIndexForWeatherInfo);
    }

    /**
     * Removes the specified city record from the {@link
     * com.haringeymobile.ukweather.database.CityTable}.
     *
     * @param cityId Open Weather Map city ID
     */
    public void deleteCity(int cityId) {
        context.getContentResolver().delete(
                WeatherContentProvider.CONTENT_URI_CITY_RECORDS,
                CityTable.COLUMN_CITY_ID + "=?",
                new String[]{Integer.toString(cityId)});
    }

    /**
     * Changes the name of the specified city in the {@link
     * com.haringeymobile.ukweather.database.CityTable}.
     *
     * @param cityId  Open Weather Map city ID
     * @param newName the new (user-chosen) name for the city
     */
    public void renameCity(int cityId, String newName) {
        ContentValues newValues = new ContentValues();
        newValues.put(CityTable.COLUMN_NAME, newName);
        context.getContentResolver().update(
                WeatherContentProvider.CONTENT_URI_CITY_RECORDS, newValues,
                CityTable.COLUMN_CITY_ID + "=?",
                new String[]{Integer.toString(cityId)});
    }

    /**
     * Obtains the city name stored in the database.
     *
     * @param cityId Open Weather Map city ID
     * @return city name stored in the database (note that the city name provided by OWM may be
     * changed by a user)
     */
    public String findCityName(int cityId) {
        if (context == null) {
            return null;
        }
        Cursor cursor = context.getContentResolver().query(
                WeatherContentProvider.CONTENT_URI_CITY_RECORDS,
                new String[]{CityTable._ID, CityTable.COLUMN_NAME},
                CityTable.COLUMN_CITY_ID + "=?",
                new String[]{Integer.toString(cityId)}, null);
        if (cursor == null) {
            return null;
        }
        if (!cursor.moveToFirst()) {
            cursor.close();
            return null;
        }
        int columnIndexForCityName = cursor.getColumnIndexOrThrow(CityTable.COLUMN_NAME);
        String cityName = cursor.getString(columnIndexForCityName);
        cursor.close();
        return cityName;
    }

    /**
     * Sets the last query time for the record to the current time. This is useful when we want a
     * record to appear first in the result set (as results are ordered by the last query time)
     * without requesting weather info from the web; for instance, when the user searches cities
     * already added to the database.
     *
     * @param rowId a unique id of a table record
     */
    public void setLastOverallQueryTimeToCurrentTime(long rowId) {
        long currentTime = System.currentTimeMillis();
        ContentValues newValues = new ContentValues();
        newValues.put(CityTable.COLUMN_LAST_OVERALL_QUERY_TIME, currentTime);
        Uri rowUri = getRowUri(rowId);
        context.getContentResolver().update(rowUri, newValues, null, null);
    }

    /**
     * Sets the last query time to the current time for all the records with the given row IDs.
     *
     * @param rowIds unique ids of the table records
     */
    public void setLastOverallQueryTimeToCurrentTime(long[] rowIds) {
        ContentResolver contentResolver = context.getContentResolver();
        long currentTime = System.currentTimeMillis();
        ContentValues newValues = new ContentValues();
        newValues.put(CityTable.COLUMN_LAST_OVERALL_QUERY_TIME, currentTime);
        for (long rowId : rowIds) {
            Uri rowUri = getRowUri(rowId);
            contentResolver.update(rowUri, newValues, null, null);
        }
    }

}