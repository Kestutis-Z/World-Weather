package com.haringeymobile.ukweather.weather;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;

import com.haringeymobile.ukweather.MainActivity;
import com.haringeymobile.ukweather.R;
import com.haringeymobile.ukweather.RefreshingActivity;
import com.haringeymobile.ukweather.database.CityTable;
import com.haringeymobile.ukweather.database.SqlOperation;
import com.haringeymobile.ukweather.utils.SharedPrefsHelper;

/**
 * An activity displaying some kind of weather information.
 */
public class WeatherInfoActivity extends RefreshingActivity {

    /**
     * A tag in the string resources, indicating that the MainActivity currently has a second pane
     * to contain a WeatherInfoFragment, so this activity is not necessary and should finish.
     */
    public static final String DUAL_PANE = "dual_pane";
    /**
     * A string to separate the default toolbar title text, and the queried city name.
     */
    private static final String TOOLBAR_TITLE_AND_CITY_NAME_SEPARATOR = "  |  ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        boolean isDualPane = DUAL_PANE.equals(getResources().getString(
                R.string.weather_info_frame_layout_pane_number_tag));
        if (isDualPane) {
            finish();
        } else {
            displayContent();
        }
    }

    /**
     * Sets the action bar and adds the required fragment to the layout.
     */
    private void displayContent() {
        setContentView(R.layout.activity_weather_info);

        Intent intent = getIntent();
        WeatherInfoType weatherInfoType = intent.getParcelableExtra(
                RefreshingActivity.WEATHER_INFORMATION_TYPE);
        String jsonString = intent.getStringExtra(RefreshingActivity.WEATHER_INFO_JSON_STRING);
        addRequiredFragment(weatherInfoType, jsonString);

        Toolbar toolbar = (Toolbar) findViewById(R.id.general_toolbar);
        setToolbarTitle(weatherInfoType, toolbar);
        setSupportActionBar(toolbar);
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationIcon(R.drawable.ic_action_arrow_left);
    }

    /**
     * Creates and adds the correct type of fragment to the activity.
     *
     * @param weatherInfoType a kind of weather information to be displayed on the screen
     * @param jsonString      a string representing the JSON weather data
     */
    private void addRequiredFragment(WeatherInfoType weatherInfoType, String jsonString) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        Fragment fragment = weatherInfoType == WeatherInfoType.CURRENT_WEATHER ?
                WeatherInfoFragment.newInstance(weatherInfoType, null, jsonString)
                : WeatherForecastParentFragment.newInstance(weatherInfoType, jsonString);
        fragmentTransaction.replace(R.id.weather_info_container, fragment);

        workerFragment = (WorkerFragmentToRetrieveJsonString) fragmentManager
                .findFragmentByTag(MainActivity.WORKER_FRAGMENT_TAG);
        if (workerFragment == null) {
            workerFragment = new WorkerFragmentToRetrieveJsonString();
            fragmentTransaction.add(workerFragment, MainActivity.WORKER_FRAGMENT_TAG);
        }

        fragmentTransaction.commit();
    }

    /**
     * Determines and sets the toolbar text.
     *
     * @param weatherInfoType a kind of weather information to be displayed on the screen
     * @param toolbar         toolbar for this activity
     */
    private void setToolbarTitle(WeatherInfoType weatherInfoType, Toolbar toolbar) {
        String title = getResources().getString(weatherInfoType.getLabelResourceId());
        toolbar.setTitle(title);
        if (weatherInfoType == WeatherInfoType.THREE_HOURLY_WEATHER_FORECAST) {
            updateTitleWithCityNameIfNecessary(toolbar, title);
        }
    }

    /**
     * If the three-hourly forecast should be displayed as a set of daily forecast lists,
     * the toolbar is updated with the queried city name.
     *
     * @param toolbar      toolbar for this activity
     * @param defaultTitle regular title without the city name
     */
    private void updateTitleWithCityNameIfNecessary(final Toolbar toolbar,
                                                    final String defaultTitle) {
        final Context context = this;
        if (SharedPrefsHelper.getForecastDisplayMode(context) ==
                ThreeHourlyForecastDisplayMode.LIST) {
            new Thread(new Runnable() {

                @Override
                public void run() {

                    int lastQueriedCityId = SharedPrefsHelper.getCityIdFromSharedPrefs(context);
                    if (lastQueriedCityId != CityTable.CITY_ID_DOES_NOT_EXIST) {
                        String queriedCityName = new SqlOperation(context)
                                .findCityName(lastQueriedCityId);
                        String updatedTitle = defaultTitle + TOOLBAR_TITLE_AND_CITY_NAME_SEPARATOR +
                                queriedCityName;
                        toolbar.setTitle(updatedTitle);
                    }

                }
            }).start();
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        FragmentManager fragmentManager = getSupportFragmentManager();
        WorkerFragmentToRetrieveJsonString workerFragment =
                (WorkerFragmentToRetrieveJsonString) fragmentManager.findFragmentByTag(
                        MainActivity.WORKER_FRAGMENT_TAG);
        if (workerFragment == null) {
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            workerFragment = new WorkerFragmentToRetrieveJsonString();
            fragmentTransaction.add(workerFragment, MainActivity.WORKER_FRAGMENT_TAG);
            fragmentTransaction.commit();
            fragmentManager.executePendingTransactions();
        }

        workerFragment.retrieveLastRequestedWeatherInfo();
    }

    @Override
    protected void displayRetrievedData(String jsonString, WeatherInfoType weatherInfoType) {
        addRequiredFragment(weatherInfoType, jsonString);
    }

}