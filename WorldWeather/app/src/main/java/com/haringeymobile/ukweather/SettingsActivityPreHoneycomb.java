package com.haringeymobile.ukweather;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.view.MenuItem;

/**
 * An activity to provide settings for the app for Android versions before
 * Honeycomb (version 11).
 */
public class SettingsActivityPreHoneycomb extends PreferenceActivity {

    // The keys to store preference values. They must be the same as specified
    // in the res/userpreferences.xml.
    public static final String PREF_TEMPERATURE_SCALE = "temperature_scale";
    public static final String PREF_WIND_SPEED_MEASUREMENT_UNIT = "wind_speed_measurement_unit";
    public static final String PREF_DATA_CACHE_PERIOD = "data_cache_period";

    @SuppressWarnings("deprecation")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.userpreferences);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        playAnimation();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                playAnimation();
                return true;
        }
        return false;
    }

    private void playAnimation() {
        overridePendingTransition(R.anim.abc_slide_in_top,
                R.anim.abc_slide_out_bottom);
    }
}
