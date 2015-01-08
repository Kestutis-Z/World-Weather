package com.haringeymobile.ukweather;

import android.os.Bundle;
import android.preference.PreferenceActivity;

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
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.userpreferences);
	}
}
