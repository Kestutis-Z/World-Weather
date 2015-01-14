package com.haringeymobile.ukweather.test.database;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import android.content.Intent;
import android.test.ServiceTestCase;
import android.test.suitebuilder.annotation.LargeTest;

import com.haringeymobile.ukweather.CityManagementActivity;
import com.haringeymobile.ukweather.MainActivity;
import com.haringeymobile.ukweather.database.GeneralDatabaseService;
import com.haringeymobile.ukweather.test.GlobalConstants;

/** Test for the insert, ic_action_playback_repeat, and delete city intent services. */
public class GeneralDatabaseServiceTest extends
		ServiceTestCase<GeneralDatabaseServiceWrapperForUnitTesting> {

	private static final int HANDLED_INTENT_COUNT = 3;

	private static final int TEST_CITY_ID = 1;
	private static final String TEST_CITY_NAME = "OldTestCity";
	private static final String TEST_NEW_CITY_NAME = "New Test City";

	private static final long SECONDS_TO_HANDLE_ALL_TESTED_INTENTS = 10;

	private CountDownLatch countDownLatch;

	public GeneralDatabaseServiceTest() {
		super(GeneralDatabaseServiceWrapperForUnitTesting.class);
	}

	@Override
	protected void setupService() {
		super.setupService();

		countDownLatch = new CountDownLatch(HANDLED_INTENT_COUNT);
		getService().setLatch(countDownLatch);
	}

	@LargeTest
	public void testHandleIntent() throws InterruptedException {
		Intent insertNewCityIntent = getInsertNewCityIntent();
		startService(insertNewCityIntent);

		Intent renameCityIntent = getRenameCityIntent();
		startService(renameCityIntent);

		Intent deleteCityIntent = getDeleteCityIntent();
		startService(deleteCityIntent);

		boolean wereIntentsHandledInTime = countDownLatch.await(
				SECONDS_TO_HANDLE_ALL_TESTED_INTENTS, TimeUnit.SECONDS);

		assertTrue("Failed to handle all " + HANDLED_INTENT_COUNT
				+ " intents in a reasonable time", wereIntentsHandledInTime);
	}

	private Intent getInsertNewCityIntent() {
		Intent intent = getNewIntentForDatabaseOperation();
		intent.setAction(GeneralDatabaseService.ACTION_INSERT_OR_UPDATE_CITY_RECORD);
		intent.putExtra(MainActivity.CITY_ID, TEST_CITY_ID);
		intent.putExtra(MainActivity.CITY_NAME, TEST_CITY_NAME);
		intent.putExtra(MainActivity.WEATHER_INFO_JSON_STRING,
				GlobalConstants.TEST_JSON_STRING_VALUE);
		return intent;
	}

	private Intent getNewIntentForDatabaseOperation() {
		Intent intent = new Intent(new Intent(getSystemContext(),
				GeneralDatabaseService.class));
		return intent;
	}

	private Intent getRenameCityIntent() {
		Intent intent = getNewIntentForDatabaseOperation();
		intent.setAction(GeneralDatabaseService.ACTION_RENAME_CITY);
		intent.putExtra(CityManagementActivity.CITY_ID, TEST_CITY_ID);
		intent.putExtra(CityManagementActivity.CITY_NEW_NAME,
				TEST_NEW_CITY_NAME);
		return intent;
	}

	private Intent getDeleteCityIntent() {
		Intent intent = getNewIntentForDatabaseOperation();
		intent.setAction(GeneralDatabaseService.ACTION_DELETE_CITY_RECORDS);
		intent.putExtra(MainActivity.CITY_ID, TEST_CITY_ID);
		return intent;
	}

}
