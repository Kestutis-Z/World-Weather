package com.haringeymobile.ukweather.test;

import android.app.Instrumentation;
import android.app.Instrumentation.ActivityMonitor;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.test.ActivityInstrumentationTestCase2;
import android.test.ViewAsserts;
import android.test.suitebuilder.annotation.MediumTest;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ListView;

import com.haringeymobile.ukweather.MainActivity;
import com.haringeymobile.ukweather.R;
import com.haringeymobile.ukweather.weather.WeatherCurrentInfoFragment;
import com.haringeymobile.ukweather.weather.WeatherForecastParentFragment;
import com.haringeymobile.ukweather.weather.WeatherInfoActivity;
import com.haringeymobile.ukweather.weather.WorkerFragmentToRetrieveJsonString;

import java.util.ArrayList;

public class MainActivityFunctionalTest extends
        ActivityInstrumentationTestCase2<MainActivity> {

    private static final long ACTIVITY_MONITOR_TIMEOUT = 5000;
    private static final long LIST_VIEW_INITIALIZATION_DELAY = 2000;

    private Instrumentation instrumentation;
    private MainActivity mainActivity;

    public MainActivityFunctionalTest() {
        super(MainActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        instrumentation = getInstrumentation();
        setActivityInitialTouchMode(false);
        mainActivity = getActivity();
    }

    @MediumTest
    public void testPreConditions() {
        assertNotNull("Instance of MainActivity is null", mainActivity);
    }

    /*
     * This actually should be a unit test, not a functional one. However, it
     * fails if the activity is in a dual pane mode due to the
     * ActivityUnitTestCase's bug - inability to create dialogs (when in dual
     * mode, worker fragment starts an async task which displays a circular
     * progress bar). So for the test to pass, it has to extend
     * ActivityInstrumentationTestCase2.
     *
     * https://code.google.com/p/android/issues/detail?id=14616
     * http://stackoverflow
     * .com/questions/2365561/testing-dialog-in-androids-activityunittestcase
     */
    @MediumTest
    public void testWorkerFragment_setRetainInstance() {
        mainActivity.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                instrumentation.callActivityOnStart(mainActivity);
            }

        });

        WorkerFragmentToRetrieveJsonString workerFragment = (WorkerFragmentToRetrieveJsonString) mainActivity
                .getSupportFragmentManager().findFragmentByTag(
                        MainActivity.WORKER_FRAGMENT_TAG);
        assertTrue("Worker fragment has not called setRetainInstance(true)",
                workerFragment.getRetainInstance());
    }

    @MediumTest
    public void testCityList() {
        mainActivity.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                instrumentation.callActivityOnStart(mainActivity);
            }

        });

        final ListView cityListView = (ListView) mainActivity
                .findViewById(android.R.id.list);
        assertNotNull("City list view not found", cityListView);

        // List items are loaded asynchronously, so we need to wait a little bit
        // to test them
        Runnable testListViewItemButtonClicksRunnable = testCityListRowButtonsRunnable(cityListView);
        new Handler().postDelayed(testListViewItemButtonClicksRunnable,
                LIST_VIEW_INITIALIZATION_DELAY);
    }

    private Runnable testCityListRowButtonsRunnable(final ListView cityListView) {
        return new Runnable() {

            private boolean isDualPane = (FrameLayout) mainActivity
                    .findViewById(R.id.weather_info_container) != null;

            @Override
            public void run() {
                int cityListViewItemCount = cityListView.getChildCount();
                if (cityListViewItemCount > 0) {
                    View listRowView = cityListView.getChildAt(cityListView
                            .getFirstVisiblePosition());
                    ArrayList<View> weatherButtons = listRowView
                            .getFocusables(View.FOCUS_FORWARD);
                    assertEquals(
                            "City list row should contain three views (weather buttons)",
                            3, weatherButtons.size());

                    testWeatherButtons(weatherButtons);
                }
            }

            private void testWeatherButtons(ArrayList<View> weatherButtons) {
                final View decorView = mainActivity.getWindow().getDecorView();

                Button currentWeatherButton = (Button) weatherButtons.get(0);
                assertNotNull("Weather button is null", currentWeatherButton);
                ViewAsserts.assertOnScreen(decorView, currentWeatherButton);
                testCurrentWeatherButtonClick(currentWeatherButton);

                Button dailyForecastButton = (Button) weatherButtons.get(1);
                assertNotNull("Daily forecast button is null",
                        dailyForecastButton);
                ViewAsserts.assertOnScreen(decorView, currentWeatherButton);
                testDailyWeatherForecastButtonClick(dailyForecastButton);

                Button threeHourlyForecastButton = (Button) weatherButtons
                        .get(2);
                assertNotNull("Three hourly forecast button is null",
                        threeHourlyForecastButton);
                ViewAsserts.assertOnScreen(decorView, currentWeatherButton);
                testThreeHourlyWeatherForecastButtonClick(threeHourlyForecastButton);
            }

            private void testCurrentWeatherButtonClick(
                    Button currentWeatherButton) {
                currentWeatherButton.performClick();
                if (isDualPane) {
                    testCurrentWeatherInfoFragment();
                } else {
                    testStartActivity_WeatherInfoActivity();
                }
            }

            private void testCurrentWeatherInfoFragment() {
                FragmentManager fragmentManager = mainActivity
                        .getSupportFragmentManager();
                fragmentManager.executePendingTransactions();

                WeatherCurrentInfoFragment weatherCurrentInfoFragment = (WeatherCurrentInfoFragment) fragmentManager
                        .findFragmentById(R.id.weather_info_container);
                assertNotNull(
                        "Weather current  info fragment not found in the layout [weather_info_container]",
                        weatherCurrentInfoFragment);
                assertTrue("Weather current  info fragment not added",
                        weatherCurrentInfoFragment.isAdded());
            }

            private void testStartActivity_WeatherInfoActivity() {
                ActivityMonitor activityMonitor = instrumentation.addMonitor(
                        WeatherInfoActivity.class.getName(), null, false);
                WeatherInfoActivity weatherInfoActivity = (WeatherInfoActivity) instrumentation
                        .waitForMonitorWithTimeout(activityMonitor,
                                ACTIVITY_MONITOR_TIMEOUT);

                assertTrue(
                        "Monitor for WeatherInfoActivity has not been called",
                        instrumentation.checkMonitorHit(activityMonitor, 1));

                instrumentation.removeMonitor(activityMonitor);
                weatherInfoActivity.finish();
            }

            private void testDailyWeatherForecastButtonClick(
                    Button dailyForecastButton) {
                dailyForecastButton.performClick();
                if (isDualPane) {
                    testWeatherForecastParentFragment();
                } else {
                    testStartActivity_WeatherInfoActivity();
                }
            }

            private void testWeatherForecastParentFragment() {
                FragmentManager fragmentManager = mainActivity
                        .getSupportFragmentManager();
                fragmentManager.executePendingTransactions();

                WeatherForecastParentFragment forecastParentFragment = (WeatherForecastParentFragment) fragmentManager
                        .findFragmentById(R.id.weather_info_container);
                assertNotNull(
                        "Weather forecast parent fragment not found in the layout [weather_info_container]",
                        forecastParentFragment);
                assertTrue("Weather forecast parent fragment not added",
                        forecastParentFragment.isAdded());
            }

            private void testThreeHourlyWeatherForecastButtonClick(
                    Button threeHourlyForecastButton) {
                threeHourlyForecastButton.performClick();
                if (isDualPane) {
                    testWeatherForecastParentFragment();
                } else {
                    testStartActivity_WeatherInfoActivity();
                }
            }

        };
    }

    // It seems the following tests always fail due to instrumentation.waitForMonitor(WithTimeout) bug, see https://code.google.com/p/android/issues/detail?id=2576

    /*@MediumTest
    public void testStartActivity_CityManagementActivity() {
        ActivityMonitor activityMonitor = instrumentation.addMonitor(
                CityManagementActivity.class.getName(), null, false);
        instrumentation.invokeMenuActionSync(mainActivity,
                R.id.mi_city_management, 0);
        CityManagementActivity cityManagementActivity = (CityManagementActivity) instrumentation
                .waitForMonitorWithTimeout(activityMonitor,
                        ACTIVITY_MONITOR_TIMEOUT);

        assertNotNull("The started CityManagementActivity is null",
                cityManagementActivity);
        assertTrue("Monitor for CityManagementActivity has not been called",
                instrumentation.checkMonitorHit(activityMonitor, 1));

        instrumentation.removeMonitor(activityMonitor);
        cityManagementActivity.finish();
    }

    @MediumTest
    public void testStartActivity_SettingsActivity() {
        @SuppressWarnings("rawtypes")
        Class settingsClass = GlobalConstants.IS_BUILD_VERSION_AT_LEAST_HONEYCOMB_11 ? SettingsActivity.class
                : SettingsActivityPreHoneycomb.class;
        ActivityMonitor activityMonitor = instrumentation.addMonitor(
                settingsClass.getName(), null, false);
        instrumentation.invokeMenuActionSync(mainActivity,
                R.id.mi_settings, 0);
        Activity settingsActivity = instrumentation.waitForMonitorWithTimeout(
                activityMonitor, ACTIVITY_MONITOR_TIMEOUT);

        assertNotNull("The started ic_action_gear activity is null", settingsActivity);
        assertTrue("Monitor for ic_action_gear activity has not been called",
                instrumentation.checkMonitorHit(activityMonitor, 1));

        instrumentation.removeMonitor(activityMonitor);
        settingsActivity.finish();
    }

    @MediumTest
    public void testStartActivity_AboutActivity() {
        ActivityMonitor activityMonitor = instrumentation.addMonitor(
                AboutActivity.class.getName(), null, false);
        instrumentation.invokeMenuActionSync(mainActivity,
                R.id.mi_about, 0);
        AboutActivity aboutActivity = (AboutActivity) instrumentation
                .waitForMonitorWithTimeout(activityMonitor,
                        ACTIVITY_MONITOR_TIMEOUT);

        assertNotNull("The started AboutActivity is null", aboutActivity);
        assertTrue("Monitor for AboutActivity has not been called",
                instrumentation.checkMonitorHit(activityMonitor, 1));

        instrumentation.removeMonitor(activityMonitor);
        aboutActivity.finish();
    }*/

}
