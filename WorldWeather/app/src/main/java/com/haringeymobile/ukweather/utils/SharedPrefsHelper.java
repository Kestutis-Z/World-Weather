package com.haringeymobile.ukweather.utils;

import com.haringeymobile.ukweather.WeatherInfoType;
import com.haringeymobile.ukweather.database.CityTable;

import android.app.Activity;
import android.content.Context;

public class SharedPrefsHelper {

	public static final String SHARED_PREFS_KEY = "com.haringeymobile.ukweather.PREFERENCE_FILE_KEY";

	private static final String LAST_SELECTED_CITY_ID = "city id";
	private static final String LAST_SELECTED_WEATHER_INFO_TYPE = "weather info type";

	/** Obtains the ID of the city that was last queried by the user. */
	public static int getCityIdFromSharedPrefs(Context context) {
		return context.getSharedPreferences(SHARED_PREFS_KEY,
				Activity.MODE_PRIVATE).getInt(LAST_SELECTED_CITY_ID,
				CityTable.CITY_ID_DOES_NOT_EXIST);
	}

	/**
	 * Saves the specified city ID.
	 * 
	 * @param cityId
	 *            OpenWeatherMap city ID
	 */
	public static void putCityIdIntoSharedPrefs(Context context, int cityId) {
		context.getSharedPreferences(SHARED_PREFS_KEY, Activity.MODE_PRIVATE)
				.edit().putInt(LAST_SELECTED_CITY_ID, cityId).apply();
	}

	/** Obtains the {@link WeatherInfoType} for the last user's query. */
	public static WeatherInfoType getLastWeatherInfoTypeFromSharedPrefs(
			Context context) {
		int lastSelectedWeatherInfoTypeId = context.getSharedPreferences(
				SHARED_PREFS_KEY, Activity.MODE_PRIVATE).getInt(
				LAST_SELECTED_WEATHER_INFO_TYPE,
				WeatherInfoType.CURRENT_WEATHER.getId());
		return WeatherInfoType.getTypeById(lastSelectedWeatherInfoTypeId);
	}

	/**
	 * Saves the {@link WeatherInfoType} that was last queried by the user.
	 * 
	 * @param weatherInfoType
	 *            a kind of weather information
	 */
	public static void putLastWeatherInfoTypeIntoSharedPrefs(Context context,
			WeatherInfoType weatherInfoType) {
		context.getSharedPreferences(SHARED_PREFS_KEY, Activity.MODE_PRIVATE)
				.edit()
				.putInt(LAST_SELECTED_WEATHER_INFO_TYPE,
						weatherInfoType.getId()).apply();
	}

}
