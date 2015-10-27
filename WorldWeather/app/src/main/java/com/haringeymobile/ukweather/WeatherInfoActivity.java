package com.haringeymobile.ukweather;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;

/**
 * An activity displaying some kind of weather information.
 */
public class WeatherInfoActivity extends ThemedActivity {

    /**
     * A tag in the string resources, indicating that the MainActivity currently
     * has a second pane to contain a WeatherInfoFragment, so this activity is
     * not necessary and should finish.
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
                MainActivity.WEATHER_INFORMATION_TYPE);
        String jsonString = intent.getStringExtra(MainActivity.WEATHER_INFO_JSON_STRING);
        addRequiredFragment(weatherInfoType, jsonString);

        Toolbar toolbar = (Toolbar) findViewById(R.id.general_toolbar);
        toolbar.setTitle(weatherInfoType.getLabelResourceId());
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationIcon(R.drawable.ic_action_arrow_left);
    }

    /**
     * Creates and adds the correct type of fragment to the activity.
     *
     * @param weatherInfoType a kind of weather information to be displayed on the screen
     * @param jsonString      a string representing the JSON weather data
     */
    private void addRequiredFragment(WeatherInfoType weatherInfoType, String jsonString) {
        Fragment fragment = weatherInfoType == WeatherInfoType.CURRENT_WEATHER ? WeatherInfoFragment
                .newInstance(weatherInfoType, null, jsonString)
                : WeatherForecastParentFragment.newInstance(weatherInfoType, jsonString);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.weather_info_container, fragment).commit();
    }

}
