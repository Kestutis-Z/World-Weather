package com.haringeymobile.ukweather.test;

import java.util.ArrayList;

import android.app.Instrumentation;
import android.os.Handler;
import android.test.ActivityInstrumentationTestCase2;
import android.test.ViewAsserts;
import android.test.suitebuilder.annotation.MediumTest;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.haringeymobile.ukweather.CityManagementActivity;

public class CityManagementActivityFunctionalTest extends
		ActivityInstrumentationTestCase2<CityManagementActivity> {

	private static final long LIST_VIEW_INITIALIZATION_DELAY = 2000;

	private Instrumentation instrumentation;

	private CityManagementActivity cityManagementActivity;

	public CityManagementActivityFunctionalTest() {
		super(CityManagementActivity.class);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		instrumentation = getInstrumentation();
		setActivityInitialTouchMode(false);
		cityManagementActivity = getActivity();
	}

	@MediumTest
	public void testPreConditions() {
		assertNotNull("Instance of MainActivity is null",
				cityManagementActivity);
	}

	@MediumTest
	public void testCityList() {
		cityManagementActivity.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				instrumentation.callActivityOnStart(cityManagementActivity);
			}

		});

		final ListView cityListView = (ListView) cityManagementActivity
				.findViewById(android.R.id.list);
		assertNotNull("City list view not found", cityListView);

		// List items are loaded asynchronously, so we need to wait a little bit
		// to test them
		Runnable testListViewItemButtonClicksRunnable = testCityListRowUtilityButtonsRunnable(cityListView);
		new Handler().postDelayed(testListViewItemButtonClicksRunnable,
				LIST_VIEW_INITIALIZATION_DELAY);
	}

	private Runnable testCityListRowUtilityButtonsRunnable(final ListView cityListView) {
		return new Runnable() {

			@Override
			public void run() {
				int cityListViewItemCount = cityListView.getChildCount();
				if (cityListViewItemCount > 0) {
					View listRowView = cityListView.getChildAt(cityListView
							.getFirstVisiblePosition());
					ArrayList<View> utilityButtons = listRowView
							.getFocusables(View.FOCUS_FORWARD);
					assertEquals(
							"City list row should contain two views (utility buttons)",
							2, utilityButtons.size());

					testUtilityButtons(utilityButtons);
				}
			}

			private void testUtilityButtons(ArrayList<View> utilityButtons) {
				final View decorView = cityManagementActivity.getWindow()
						.getDecorView();

				Button deleteButton = (Button) utilityButtons.get(0);
				assertNotNull("Delete button is null", deleteButton);
				ViewAsserts.assertOnScreen(decorView, deleteButton);

				Button renameButton = (Button) utilityButtons.get(1);
				assertNotNull("Rename city button is null", renameButton);
				ViewAsserts.assertOnScreen(decorView, renameButton);
			}

		};
	}

}
