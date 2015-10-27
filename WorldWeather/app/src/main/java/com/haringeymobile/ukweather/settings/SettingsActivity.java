package com.haringeymobile.ukweather.settings;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;

import com.haringeymobile.ukweather.R;
import com.haringeymobile.ukweather.ThemedActivity;

/**
 * An activity containing the {@link SettingsFragment}.
 */
public class SettingsActivity extends ThemedActivity
        implements SharedPreferences.OnSharedPreferenceChangeListener {

    // The keys to store preference values. They must be the same as specified
    // in the res/userpreferences.xml.
    public static final String PREF_TEMPERATURE_SCALE = "temperature_scale";
    public static final String PREF_WIND_SPEED_MEASUREMENT_UNIT = "wind_speed_measurement_unit";
    public static final String PREF_DATA_CACHE_PERIOD = "data_cache_period";
    public static final String PREF_APP_THEME = "app_theme";

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Toolbar toolbar = (Toolbar) findViewById(R.id.general_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationIcon(R.drawable.ic_action_arrow_left);

        getFragmentManager().beginTransaction().replace(
                R.id.settings_content_frame_layout, new SettingsFragment()).commit();

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (PREF_APP_THEME.equals(key)) {
            recreate();
        }
    }

}
