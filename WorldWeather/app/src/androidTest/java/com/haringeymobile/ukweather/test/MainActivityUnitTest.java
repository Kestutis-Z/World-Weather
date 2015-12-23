package com.haringeymobile.ukweather.test;

import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.test.ActivityUnitTestCase;
import android.test.suitebuilder.annotation.SmallTest;

import com.haringeymobile.ukweather.CityListFragmentWithWeatherButtons;
import com.haringeymobile.ukweather.MainActivity;
import com.haringeymobile.ukweather.weather.WorkerFragmentToRetrieveJsonString;

/** Unit tests for the {@link com.haringeymobile.ukweather.MainActivity}. */
public class MainActivityUnitTest extends ActivityUnitTestCase<MainActivity> {

	private MainActivity mainActivity;

	public MainActivityUnitTest() {
		super(MainActivity.class);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		Intent intent = new Intent(getInstrumentation().getTargetContext(),
				MainActivity.class);
		startActivity(intent, null, null);
		mainActivity = getActivity();
	}

	@SmallTest
	public void testPreConditions() {
		assertNotNull("Instance of MainActivity is null", mainActivity);
	}

	@SmallTest
	public void testLayout_ContainsCityListContainer() {
		int cityListContainerFrameLayoutId = com.haringeymobile.ukweather.R.id.city_list_container;
		assertNotNull(mainActivity.findViewById(cityListContainerFrameLayoutId));
	}

	@SmallTest
	public void testFragmentsAdded() {
		FragmentManager fragmentManager = mainActivity
				.getSupportFragmentManager();
		fragmentManager.executePendingTransactions();

		CityListFragmentWithWeatherButtons cityListFragment = (CityListFragmentWithWeatherButtons) fragmentManager
				.findFragmentById(com.haringeymobile.ukweather.R.id.city_list_container);
		assertNotNull(
				"City list fragment not found in the layout [city_list_container]",
				cityListFragment);

		cityListFragment = (CityListFragmentWithWeatherButtons) fragmentManager
				.findFragmentByTag(MainActivity.LIST_FRAGMENT_TAG);
		assertNotNull("City list fragment not found by tag "
				+ MainActivity.LIST_FRAGMENT_TAG, cityListFragment);

		WorkerFragmentToRetrieveJsonString workerFragment = (WorkerFragmentToRetrieveJsonString) fragmentManager
				.findFragmentByTag(MainActivity.WORKER_FRAGMENT_TAG);
		assertNotNull("Worker fragment not found by tag "
				+ MainActivity.WORKER_FRAGMENT_TAG, workerFragment);

		assertTrue("City list fragment not added", cityListFragment.isAdded());
		assertTrue("Worker fragment not added", workerFragment.isAdded());
	}

}
