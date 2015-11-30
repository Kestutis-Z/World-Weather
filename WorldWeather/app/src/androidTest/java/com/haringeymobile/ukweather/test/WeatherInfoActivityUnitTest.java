package com.haringeymobile.ukweather.test;

import android.content.Intent;
import android.os.Parcelable;
import android.support.v4.app.FragmentManager;
import android.test.ActivityUnitTestCase;
import android.test.suitebuilder.annotation.SmallTest;
import android.widget.FrameLayout;

import com.haringeymobile.ukweather.R;
import com.haringeymobile.ukweather.WeatherCurrentInfoFragment;
import com.haringeymobile.ukweather.WeatherInfoActivity;
import com.haringeymobile.ukweather.WeatherInfoFragment;
import com.haringeymobile.ukweather.WeatherInfoType;
import com.haringeymobile.ukweather.RefreshingActivity;

/** Unit tests for the {@link com.haringeymobile.ukweather.WeatherInfoActivity}. */
public class WeatherInfoActivityUnitTest extends
		ActivityUnitTestCase<WeatherInfoActivity> {

	private static final String TEST_WEATHER_INFO_TYPE_KEY = RefreshingActivity.WEATHER_INFORMATION_TYPE;
	private static final WeatherInfoType TEST_WEATHER_INFO_TYPE_VALUE = WeatherInfoType.CURRENT_WEATHER;
	private static final String TEST_JSON_STRING_KEY = RefreshingActivity.WEATHER_INFO_JSON_STRING;

	private WeatherInfoActivity weatherInfoActivity;
	private boolean isDualPane;

	public WeatherInfoActivityUnitTest() {
		super(WeatherInfoActivity.class);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		Intent intent = new Intent(getInstrumentation().getTargetContext(),
				WeatherInfoActivity.class);
		intent.putExtra(TEST_WEATHER_INFO_TYPE_KEY,
				(Parcelable) TEST_WEATHER_INFO_TYPE_VALUE);
		intent.putExtra(TEST_JSON_STRING_KEY,
				GlobalConstants.TEST_JSON_STRING_VALUE);
		startActivity(intent, null, null);

		weatherInfoActivity = getActivity();
		isDualPane = WeatherInfoActivity.DUAL_PANE.equals(weatherInfoActivity
				.getString(R.string.weather_info_frame_layout_pane_number_tag));
	}

	@SmallTest
	public void testPreConditions() {
		assertNotNull("Instance of WeatherInfoActivity is null",
				weatherInfoActivity);
		assertNotNull("WeatherInfoActivity's action bar is null",
				weatherInfoActivity.getSupportActionBar());
	}

	@SmallTest
	public void testLayoutContainsWeatherInfoContainer() {
		int weatherInfoContainerFrameLayoutId = R.id.weather_info_container;
		FrameLayout weatherInfoContainer = (FrameLayout) weatherInfoActivity
				.findViewById(weatherInfoContainerFrameLayoutId);

		if (isDualPane) {
			assertNull(weatherInfoContainer);
		} else {
			assertNotNull(weatherInfoContainer);
		}
	}

	@SmallTest
	public void testWeatherInfoFragment() {
		if (isDualPane) {
			return;
		}

		FragmentManager fragmentManager = weatherInfoActivity
				.getSupportFragmentManager();
		fragmentManager.executePendingTransactions();

		WeatherCurrentInfoFragment currentInfoFragment = (WeatherCurrentInfoFragment) fragmentManager
				.findFragmentById(R.id.weather_info_container);
		assertNotNull(
				"Weather current info fragment not found in the layout [weather_info_container]",
				currentInfoFragment);

		assertTrue("Weather current info fragment not added",
				currentInfoFragment.isAdded());

		String actualCurrentInfoFragmentsJsonStringArgument = currentInfoFragment
				.getArguments().getString(WeatherInfoFragment.JSON_STRING);
		assertNotNull(
				"WeatherCurrentInfoFragment received null JSON data argument",
				actualCurrentInfoFragmentsJsonStringArgument);
		assertEquals(
				"WeatherCurrentInfoFragment received incorrect JSON data argument:\n"
						+ actualCurrentInfoFragmentsJsonStringArgument,
				GlobalConstants.TEST_JSON_STRING_VALUE,
				actualCurrentInfoFragmentsJsonStringArgument);
	}

}
