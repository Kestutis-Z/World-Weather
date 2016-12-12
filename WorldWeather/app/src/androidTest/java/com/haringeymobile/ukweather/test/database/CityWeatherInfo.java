package com.haringeymobile.ukweather.test.database;

import com.haringeymobile.ukweather.database.CityTable;

import android.content.ContentValues;

/** A utility for converting weather data to a ContentValues map. */
public class CityWeatherInfo {

	int cityId;
	String cityName;
	long lastCurrentWeatherUpdateTime;
	long lastDailyForecastUpdateTime;
	long lastThreeHourlyForecastUpdateTime;
	String cachedCurrentWeatherJson;
	String cachedDailyForecastJson;
	String cachedThreeHourlyForecastJson;

	CityWeatherInfo(int cityId, String cityName,
			long lastCurrentWeatherUpdateTime,
			long lastDailyForecastUpdateTime,
			long lastThreeHourlyForecastUpdateTime,
			String cachedCurrentWeatherJson, String cachedDailyForecastJson,
			String cachedThreeHourlyForecastJson) {
		this.cityId = cityId;
		this.cityName = cityName;
		this.lastCurrentWeatherUpdateTime = lastCurrentWeatherUpdateTime;
		this.lastDailyForecastUpdateTime = lastDailyForecastUpdateTime;
		this.lastThreeHourlyForecastUpdateTime = lastThreeHourlyForecastUpdateTime;
		this.cachedCurrentWeatherJson = cachedCurrentWeatherJson;
		this.cachedDailyForecastJson = cachedDailyForecastJson;
		this.cachedThreeHourlyForecastJson = cachedThreeHourlyForecastJson;
	}

	public ContentValues getContentValues() {
		ContentValues values = new ContentValues();
		values.put(CityTable.COLUMN_CITY_ID, cityId);
		values.put(CityTable.COLUMN_NAME, cityName);
		values.put(CityTable.COLUMN_LAST_QUERY_TIME_FOR_CURRENT_WEATHER,
				lastCurrentWeatherUpdateTime);
		values.put(CityTable.COLUMN_LAST_QUERY_TIME_FOR_DAILY_WEATHER_FORECAST,
				lastDailyForecastUpdateTime);
		values.put(
				CityTable.COLUMN_LAST_QUERY_TIME_FOR_THREE_HOURLY_WEATHER_FORECAST,
				lastThreeHourlyForecastUpdateTime);
		long lastAnyUpdateTime = Math.max(Math.max(
				lastCurrentWeatherUpdateTime, lastDailyForecastUpdateTime),
				lastThreeHourlyForecastUpdateTime);
		values.put(CityTable.COLUMN_ORDERING_VALUE, lastAnyUpdateTime);
		values.put(CityTable.COLUMN_CACHED_JSON_CURRENT,
				cachedCurrentWeatherJson);
		values.put(CityTable.COLUMN_CACHED_JSON_DAILY_FORECAST,
				cachedDailyForecastJson);
		values.put(CityTable.COLUMN_CACHED_JSON_THREE_HOURLY_FORECAST,
				cachedThreeHourlyForecastJson);
		return values;
	}

}
