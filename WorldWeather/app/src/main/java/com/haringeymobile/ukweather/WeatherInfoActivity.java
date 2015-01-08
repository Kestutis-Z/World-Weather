package com.haringeymobile.ukweather;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;

/** An activity displaying some kind of weather information. */
public class WeatherInfoActivity extends ActionBarActivity {

	/**
	 * A tag in the string resources, indicating that the MainActivity currently
	 * has a second pane to contain a WeatherInfoFragment, so this activity is
	 * not necessary and should finish.
	 */
	public static final String DUAL_PANE = "dual_pane";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setTheme(R.style.Theme_AppCompat);
		super.onCreate(savedInstanceState);

		boolean isDualPane = DUAL_PANE.equals(getResources().getString(
				R.string.weather_info_frame_layout_pane_number_tag));
		if (isDualPane) {
			finish();
		} else {
			displayContent();
		}
	}

	/** Sets the action bar and adds the required fragment to the layout. */
	private void displayContent() {
		setContentView(R.layout.activity_weather_info);

		Intent intent = getIntent();
		WeatherInfoType weatherInfoType = intent
				.getParcelableExtra(MainActivity.WEATHER_INFORMATION_TYPE);
		setActionBar(weatherInfoType);

		String jsonString = intent
				.getStringExtra(MainActivity.WEATHER_INFO_JSON_STRING);
		addRequiredFragment(weatherInfoType, jsonString);
	}

	/**
	 * Adjusts the action bar title and icon to the type of weather information
	 * 
	 * @param weatherInfoType
	 *            a kind of weather information to be displayed on the screen
	 */
	private void setActionBar(WeatherInfoType weatherInfoType) {
		ActionBar actionBar = getSupportActionBar();
		try {
			actionBar.setTitle(weatherInfoType.getLabelResourceId());
			actionBar.setIcon(weatherInfoType.getIconResourceId());
		} catch (NullPointerException e) {
			// TODO Perhaps there is a better way to deal with this
			// Seems to cause problems only when testing - a
			// NullPointerException is thrown at line 139 at
			// http://grepcode.com/file/repository.grepcode.com/java/ext/com.google.android/android/4.3_r2.1/android/support/v7/app/ActionBarImplICS.java
			// Similar problem: line 136 at
			// https://github.com/appcelerator/titanium_mobile/blob/master/android/modules/ui/src/java/ti/modules/titanium/ui/widget/tabgroup/TiUIActionBarTabGroup.java
		}
	}

	/**
	 * Creates and adds the correct type of fragment to the activity.
	 * 
	 * @param weatherInfoType
	 *            a kind of weather information to be displayed on the screen
	 * @param jsonString
	 *            a string representing the JSON weather data
	 */
	private void addRequiredFragment(WeatherInfoType weatherInfoType,
			String jsonString) {
		Fragment fragment = weatherInfoType == WeatherInfoType.CURRENT_WEATHER ? WeatherInfoFragment
				.newInstance(weatherInfoType, null, jsonString)
				: WeatherForecastParentFragment.newInstance(weatherInfoType,
						jsonString);
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.weather_info_container, fragment).commit();
	}

}
