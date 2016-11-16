package com.haringeymobile.ukweather.database;

import android.app.IntentService;
import android.content.Intent;

import com.haringeymobile.ukweather.CityManagementActivity;
import com.haringeymobile.ukweather.MainActivity;
import com.haringeymobile.ukweather.RefreshingActivity;
import com.haringeymobile.ukweather.utils.SharedPrefsHelper;
import com.haringeymobile.ukweather.weather.WeatherInfoType;

public class GeneralDatabaseService extends IntentService {

    private static final String APP_PACKAGE = "com.haringeymobile.ukweather";
    public static final String ACTION_INSERT_OR_UPDATE_CITY_RECORD = APP_PACKAGE
            + ".insert_or_update_city_records";
    public static final String ACTION_UPDATE_WEATHER_INFO = APP_PACKAGE
            + ".update_weather_info_records";
    public static final String ACTION_RENAME_CITY = APP_PACKAGE
            + ".rename_city";
    public static final String ACTION_DELETE_CITY_RECORDS = APP_PACKAGE
            + ".delete_city_records";
    public static final String ACTION_SWITCH_CITIES = APP_PACKAGE
            + ".switch_two_cities";

    private static final String WORKER_THREAD_NAME = "General database service thread";

    public GeneralDatabaseService() {
        super(WORKER_THREAD_NAME);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String action = intent.getAction();
        switch (action) {
            case ACTION_INSERT_OR_UPDATE_CITY_RECORD: {
                int cityId = intent.getIntExtra(MainActivity.CITY_ID, CityTable.
                        CITY_ID_DOES_NOT_EXIST);
                String cityName = intent.getStringExtra(MainActivity.CITY_NAME);
                String currentWeatherJsonString = intent.getStringExtra(RefreshingActivity.
                        WEATHER_INFO_JSON_STRING);
                new SqlOperation(this, WeatherInfoType.CURRENT_WEATHER).
                        updateOrInsertCityWithCurrentWeather(cityId, cityName,
                                currentWeatherJsonString);
                break;
            }
            case ACTION_UPDATE_WEATHER_INFO: {
                int cityId = SharedPrefsHelper.getCityIdFromSharedPrefs(this);
                String jsonString = intent.getStringExtra(RefreshingActivity.
                        WEATHER_INFO_JSON_STRING);
                WeatherInfoType weatherInfoType = intent.getParcelableExtra(RefreshingActivity.
                        WEATHER_INFORMATION_TYPE);
                new SqlOperation(this, weatherInfoType).updateWeatherInfo(cityId,
                        jsonString);
                break;
            }
            case ACTION_RENAME_CITY: {
                int cityId = intent.getIntExtra(CityManagementActivity.CITY_ID,
                        CityTable.CITY_ID_DOES_NOT_EXIST);
                String newName = intent.getStringExtra(CityManagementActivity.CITY_NEW_NAME);
                new SqlOperation(this).renameCity(cityId, newName);
                break;
            }
            case ACTION_DELETE_CITY_RECORDS: {
                int cityId = intent.getIntExtra(CityManagementActivity.CITY_ID, CityTable.
                        CITY_ID_DOES_NOT_EXIST);
                new SqlOperation(this).deleteCity(cityId);
                break;
            }
            case ACTION_SWITCH_CITIES:
                int cityOrder_x = intent.getIntExtra(CityManagementActivity.CITY_ORDER_X, CityTable.
                        CITY_ID_DOES_NOT_EXIST);
                int cityOrder_y = intent.getIntExtra(CityManagementActivity.CITY_ORDER_Y, CityTable.
                        CITY_ID_DOES_NOT_EXIST);
                new SqlOperation(this).switchCityOrder(cityOrder_x, cityOrder_y);
                break;
            default:
                throw new IllegalArgumentException("Unsupported action: " + action);
        }
    }
}
