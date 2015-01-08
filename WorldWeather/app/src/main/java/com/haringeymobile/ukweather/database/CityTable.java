package com.haringeymobile.ukweather.database;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

import com.haringeymobile.ukweather.data.InitialCity;
import com.haringeymobile.ukweather.utils.MiscMethods;

/** The database table to hold city current weather and weather forecast records. */
public final class CityTable implements BaseColumns {

	public static final long CITY_NEVER_QUERIED = -1;
	public static final int CITY_ID_DOES_NOT_EXIST = -1;

	public static final String TABLE_CITIES = "Cities";
	public static final String COLUMN_CITY_ID = "Id";
	public static final String COLUMN_NAME = "Name";

	public static final String COLUMN_LAST_QUERY_TIME_FOR_CURRENT_WEATHER = "TimeCurrent";
	public static final String COLUMN_CACHED_JSON_CURRENT = "JsonCurrent";
	public static final String COLUMN_LAST_QUERY_TIME_FOR_DAILY_WEATHER_FORECAST = "TimeDailyForecast";
	public static final String COLUMN_CACHED_JSON_DAILY_FORECAST = "JsonDailyForecast";
	public static final String COLUMN_LAST_QUERY_TIME_FOR_THREE_HOURLY_WEATHER_FORECAST = "TimeThreeHourlyForecast";
	public static final String COLUMN_CACHED_JSON_THREE_HOURLY_FORECAST = "JsonThreeHourlyForecast";
	public static final String COLUMN_LAST_OVERALL_QUERY_TIME = "LastQueryTime";

	private static final String TABLE_TEMP = "tempTable";
	private static final String COLUMN_LAST_QUERY_TIME_FOR_CURRENT_WEATHER_VERSION_1 = "Date";
	private static final String COLUMN_CACHED_JSON_CURRENT_VERSION_1 = "Current";
	// Not used in version 1:
	// private static final String COLUMN_LAST_QUERY_TIME_FOR_WEATHER_FORECAST_VERSION_1 = "DateForecast";
	// private static final String COLUMN_CACHED_JSON_FORECAST_VERSION_1 = "Forecast";

	private static final String TABLE_CREATE = "CREATE TABLE "
			+ TABLE_CITIES 
			+ "(" 
			+ _ID + " integer primary key autoincrement, "
			+ COLUMN_CITY_ID + " integer, " 
			+ COLUMN_NAME + " text not null, "
			+ COLUMN_LAST_QUERY_TIME_FOR_CURRENT_WEATHER + " integer, "
			+ COLUMN_CACHED_JSON_CURRENT + " text, "
			+ COLUMN_LAST_QUERY_TIME_FOR_DAILY_WEATHER_FORECAST + " integer, "
			+ COLUMN_CACHED_JSON_DAILY_FORECAST + " text, "
			+ COLUMN_LAST_QUERY_TIME_FOR_THREE_HOURLY_WEATHER_FORECAST + " integer, "
			+ COLUMN_CACHED_JSON_THREE_HOURLY_FORECAST + " text, "
			+ COLUMN_LAST_OVERALL_QUERY_TIME + " integer"
			+ ");";

	public static void onCreate(SQLiteDatabase database) {
		database.execSQL(TABLE_CREATE);
		insertInitialData(database);
	}

	/**
	 * Inserts initial cities with their default values into the database.
	 * 
	 * @see com.haringeymobile.ukweather.data.InitialCity
	 */
	private static void insertInitialData(SQLiteDatabase database) {
		for (InitialCity city : InitialCity.values()) {
			ContentValues newValues = new ContentValues();
			newValues.put(COLUMN_CITY_ID, city.getOpenWeatherMapId());
			newValues.put(COLUMN_NAME, city.getDisplayName());
			newValues.put(COLUMN_LAST_QUERY_TIME_FOR_CURRENT_WEATHER,
					CITY_NEVER_QUERIED);
			newValues.putNull(COLUMN_CACHED_JSON_CURRENT);
			putInitialDataForVersion2(newValues);
			database.insert(TABLE_CITIES, null, newValues);
		}
	}

	/**
	 * Puts default values for the new columns in databse version 2 into the
	 * provided {@link android.content.ContentValues}.
	 * 
	 * @param newValues
	 *            values for the new record to be inserted into the database
	 */
	private static void putInitialDataForVersion2(ContentValues newValues) {
		newValues.put(COLUMN_LAST_QUERY_TIME_FOR_DAILY_WEATHER_FORECAST,
				CITY_NEVER_QUERIED);
		newValues.putNull(COLUMN_CACHED_JSON_DAILY_FORECAST);
		newValues.put(COLUMN_LAST_QUERY_TIME_FOR_THREE_HOURLY_WEATHER_FORECAST,
				CITY_NEVER_QUERIED);
		newValues.putNull(COLUMN_CACHED_JSON_THREE_HOURLY_FORECAST);
		newValues.put(COLUMN_LAST_OVERALL_QUERY_TIME, CITY_NEVER_QUERIED);
	}

	public static void onUpgrade(SQLiteDatabase database, int oldVersion,
			int newVersion) {
		MiscMethods.log("Upgrading database from version " + oldVersion
				+ " to version " + newVersion);
		if (oldVersion == 1 && newVersion > 1) {
			alterDatabaseVersion_1(database);
		} else {
			database.execSQL("DROP TABLE IF EXISTS " + TABLE_CITIES);
			onCreate(database);
		}
	}

	/** Performs transactions required to upgrade the database from version 1. */
	private static void alterDatabaseVersion_1(SQLiteDatabase database) {
		database.beginTransaction();
		try {
			alterCityTable(database);
			database.setTransactionSuccessful();
		} catch (Exception e) {
			throw new RuntimeException(
					"Error upgrading database from version 1");
		} finally {
			database.endTransaction();
		}
	}

	/**
	 * Prepares the "Cities" table for alteration, and inserts the new columns;
	 * this is required to upgrade the database from version 1.
	 */
	private static void alterCityTable(SQLiteDatabase database) {
		String RENAME_ORIGINAL_TABLE = "ALTER TABLE " + TABLE_CITIES
				+ " RENAME TO " + TABLE_TEMP;
		String COPY_OLD_TABLE_TO_NEW_TABLE = "INSERT INTO " + TABLE_CITIES
				+ "(" + COLUMN_CITY_ID + ", " + COLUMN_NAME + ", "
				+ COLUMN_LAST_QUERY_TIME_FOR_CURRENT_WEATHER + ", "
				+ COLUMN_CACHED_JSON_CURRENT + ") SELECT " + COLUMN_CITY_ID
				+ ", " + COLUMN_NAME + ", "
				+ COLUMN_LAST_QUERY_TIME_FOR_CURRENT_WEATHER_VERSION_1 + ", "
				+ COLUMN_CACHED_JSON_CURRENT_VERSION_1 + " FROM " + TABLE_TEMP
				+ ";";

		database.execSQL(RENAME_ORIGINAL_TABLE);
		database.execSQL(TABLE_CREATE);
		database.execSQL(COPY_OLD_TABLE_TO_NEW_TABLE);
		database.execSQL("DROP TABLE " + TABLE_TEMP);

		insertInitialWeatherForecastValues(database);
	}

	/** Inserts new columns into the "Cities" table. */
	private static void insertInitialWeatherForecastValues(
			SQLiteDatabase database) {
		ContentValues initialForecastValues = new ContentValues();
		putInitialDataForVersion2(initialForecastValues);
		database.insert(TABLE_CITIES, null, initialForecastValues);
	}

}
