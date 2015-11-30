package com.haringeymobile.ukweather;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;

/**
 * An activity displaying some kind of weather information.
 */
public class WeatherInfoActivity extends RefreshingActivity {

    /**
     * A tag in the string resources, indicating that the MainActivity currently has a second pane
     * to contain a WeatherInfoFragment, so this activity is not necessary and should finish.
     */
    public static final String DUAL_PANE = "dual_pane";

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
        toolbar.setTitle(weatherInfoType.getLabelResourceId());
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

    @Override
    protected void onRestart() {
        super.onRestart();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        WorkerFragmentToRetrieveJsonString workerFragment =
                (WorkerFragmentToRetrieveJsonString) fragmentManager.findFragmentByTag(
                        MainActivity.WORKER_FRAGMENT_TAG);
        if (workerFragment == null) {
            workerFragment = new WorkerFragmentToRetrieveJsonString();
            fragmentTransaction.add(workerFragment, MainActivity.WORKER_FRAGMENT_TAG);
        }
        fragmentManager.executePendingTransactions();
        workerFragment.retrieveLastRequestedWeatherInfo();
    }

    @Override
    protected void displayRetrievedData(String jsonString, WeatherInfoType weatherInfoType) {
        addRequiredFragment(weatherInfoType, jsonString);
    }

}