package com.haringeymobile.ukweather;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.haringeymobile.ukweather.data.OpenWeatherMapUrl;
import com.haringeymobile.ukweather.data.objects.CityCurrentWeather;
import com.haringeymobile.ukweather.data.objects.SearchResponseForFindQuery;
import com.haringeymobile.ukweather.database.GeneralDatabaseService;
import com.haringeymobile.ukweather.utils.GlobalConstants;
import com.haringeymobile.ukweather.utils.MiscMethods;
import com.haringeymobile.ukweather.utils.SharedPrefsHelper;

/**
 * An activity containing a {@link CityListFragmentWithWeatherButtons}. On
 * screens with larger width it also has tre second pane to embed a
 * {@link WeatherInfoFragment}.
 */
public class MainActivity extends ActionBarActivity implements
		CityListFragmentWithWeatherButtons.OnWeatherInfoButtonClickedListener,
		GetAvailableCitiesTask.OnCitySearchResponseRetrievedListener,
		CitySearchResultsDialog.OnCityNamesListItemClickedListener,
		WorkerFragmentToRetrieveJsonString.OnJsonStringRetrievedListener {

	public static final String CITY_ID = "city id";
	public static final String CITY_NAME = "city name";
	public static final String WEATHER_INFORMATION_TYPE = "weather info type";
	public static final String WEATHER_INFO_JSON_STRING = "json string";
	public static final String LIST_FRAGMENT_TAG = "list fragment";
	public static final String WORKER_FRAGMENT_TAG = "worker fragment";
	private static final String QUERY_STRING_TOO_SHORT_ALERT_DIALOG_FRAGMENT_TAG = "short query fragment";
	/** The shortest acceptable city search query string length. */
	private static final int MINIMUM_SEARCH_QUERY_STRING_LENGTH = 3;

	private SearchResponseForFindQuery searchResponseForFindQuery;
	private WorkerFragmentToRetrieveJsonString workerFragment;
	private boolean isDualPane;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setTheme(R.style.Theme_AppCompat);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		isDualPane = (FrameLayout) findViewById(R.id.weather_info_container) != null;

		FragmentManager fragmentManager = getSupportFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager
				.beginTransaction();
		workerFragment = (WorkerFragmentToRetrieveJsonString) fragmentManager
				.findFragmentByTag(WORKER_FRAGMENT_TAG);
		if (workerFragment == null) {
			workerFragment = new WorkerFragmentToRetrieveJsonString();
			fragmentTransaction.add(workerFragment, WORKER_FRAGMENT_TAG);
		}
		Fragment cityListFragment = fragmentManager
				.findFragmentByTag(LIST_FRAGMENT_TAG);
		if (cityListFragment == null) {
			cityListFragment = new CityListFragmentWithWeatherButtons();
			fragmentTransaction.add(R.id.city_list_container, cityListFragment,
					LIST_FRAGMENT_TAG);
		}
		fragmentTransaction.commit();
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		String jsonString = savedInstanceState
				.getString(WEATHER_INFO_JSON_STRING);
		if (jsonString != null) {
			searchResponseForFindQuery = new Gson().fromJson(jsonString,
					SearchResponseForFindQuery.class);
		}
	}

	@Override
	protected void onStart() {
		super.onStart();
		if (isDualPane) {
			workerFragment.retrieveLastRequestedWeatherInfo();
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if (searchResponseForFindQuery != null) {
			outState.putString(WEATHER_INFO_JSON_STRING,
					new Gson().toJson(searchResponseForFindQuery));
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		setCitySearching(menu);
		return true;
	}

	/**
	 * Locates the search view in the action bar, and prepares it for city
	 * searching.
	 * 
	 * @param menu
	 *            options menu containing the city search view
	 */
	private void setCitySearching(Menu menu) {
		MenuItem searchItem = menu.findItem(R.id.mi_search_cities);
		SearchView searchView = (SearchView) MenuItemCompat
				.getActionView(searchItem);

		SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
		searchView.setSearchableInfo(searchManager
				.getSearchableInfo(getComponentName()));
		searchView.setSubmitButtonEnabled(true);
		searchView.setQueryHint(getResources().getString(
				R.string.city_search_hint));

		SearchView.OnQueryTextListener queryTextListener = getCityQueryTextListener();
		searchView.setOnQueryTextListener(queryTextListener);
	}

	/**
	 * Obtains a listener for the location query.
	 * 
	 * @return an implementation of the query text listener, that will either
	 *         execute the query if it is accepted (long enough), or will alert
	 *         the user if it is too short
	 */
	private SearchView.OnQueryTextListener getCityQueryTextListener() {
		SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener() {

			@Override
			public boolean onQueryTextChange(String newText) {
				return false;
			}

			@Override
			public boolean onQueryTextSubmit(String query) {
				if (query.length() < MINIMUM_SEARCH_QUERY_STRING_LENGTH) {
					showQueryStringTooShortAlertDialog();
				} else {
					processQuery(query);
				}
				return false;
			}

			/**
			 * If there is a network connection, starts the task to search the
			 * cities satisfying the provided query.
			 * 
			 * @param query
			 *            a location search text provided by the user
			 */
			private void processQuery(String query) {
				if (MiscMethods.isUserOnline(MainActivity.this)) {
					new GetAvailableCitiesTask(MainActivity.this)
							.execute(new OpenWeatherMapUrl()
									.getAvailableCitiesListUrl(query));
				} else {
					Toast.makeText(MainActivity.this, R.string.error_message,
							Toast.LENGTH_SHORT).show();
				}
			}
		};
		return queryTextListener;
	}

	/** Informs the user that the entered query is too short. */
	private void showQueryStringTooShortAlertDialog() {
		new DialogFragment() {

			@Override
			public Dialog onCreateDialog(Bundle savedInstanceState) {
				AlertDialog.Builder builder = new AlertDialog.Builder(
						getActivity());
				builder.setTitle(R.string.dialog_title_query_too_short)
						.setPositiveButton(android.R.string.ok,
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int id) {
										dismiss();
									}
								});
				return builder.create();
			}

		}.show(getSupportFragmentManager(),
				QUERY_STRING_TOO_SHORT_ALERT_DIALOG_FRAGMENT_TAG);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.mi_city_management) {
			Intent cityManagementIntent = new Intent(this,
					CityManagementActivity.class);
			startActivity(cityManagementIntent);
		} else if (id == R.id.mi_settings) {
			@SuppressWarnings("rawtypes")
			Class c = GlobalConstants.IS_BUILD_VERSION_AT_LEAST_HONEYCOMB_11 ? SettingsActivity.class
					: SettingsActivityPreHoneycomb.class;
			Intent settingsIntent = new Intent(this, c);
			startActivity(settingsIntent);
		} else if (id == R.id.mi_rate_application) {
			goToPlayStore();
		} else if (id == R.id.mi_about) {
			Intent aboutIntent = new Intent(this, AboutActivity.class);
			startActivity(aboutIntent);
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * Attempts to visit the app's page in the Play Store via the Play Store
	 * app. If this fails (the Play Store app not installed on the user's
	 * device), the second try is to do so via the browser.
	 */
	private void goToPlayStore() {
		final String appPackageName = getPackageName();
		try {
			startActivity(new Intent(Intent.ACTION_VIEW,
					Uri.parse("market://details?id=" + appPackageName)));
		} catch (android.content.ActivityNotFoundException e) {
			startActivity(new Intent(Intent.ACTION_VIEW,
					Uri.parse("http://play.google.com/store/apps/details?id="
							+ appPackageName)));
		}
	}

	@Override
	public void onSearchResponseForFindQueryRetrieved(
			SearchResponseForFindQuery searchResponseForFindQuery) {
		this.searchResponseForFindQuery = searchResponseForFindQuery;
	}

	@Override
	public void onFoundCityNamesItemClicked(int position) {
		if (searchResponseForFindQuery != null) {
			CityCurrentWeather selectedCityWeather = searchResponseForFindQuery
					.getCities().get(position);
			String currentWeatherJsonString = new Gson()
					.toJson(selectedCityWeather);

			if (isDualPane) {
				displayRetrievedDataInThisActivity(currentWeatherJsonString,
						WeatherInfoType.CURRENT_WEATHER);
			}

			// Since the Open Weather Map search response for the 'find
			// cities' query contains the current weather information
			// for each found city, we can cache this weather
			// information for the selected city in the database, just
			// in case the user requests it shortly (quite likely, given
			// that s/he had just selected the city).
			insertNewRecordOrUpdateCity(selectedCityWeather.getCityId(),
					selectedCityWeather.getCityName(), currentWeatherJsonString);
			saveWeatherInfoRequest(selectedCityWeather.getCityId(),
					WeatherInfoType.CURRENT_WEATHER);
		}
	}

	/**
	 * Updates the current weather record for the city if it already exists in
	 * the database, otherwise inserts a new record.
	 * 
	 * @param cityId
	 *            Open Weather Map ID for the city
	 * @param cityName
	 *            the name as provided by the Open Weather Map
	 * @param currentWeatherJsonString
	 *            JSON current weather data
	 */
	private void insertNewRecordOrUpdateCity(int cityId, String cityName,
			String currentWeatherJsonString) {
		Intent intent = new Intent(this, GeneralDatabaseService.class);
		intent.setAction(GeneralDatabaseService.ACTION_INSERT_OR_UPDATE_CITY_RECORD);
		intent.putExtra(CITY_ID, cityId);
		intent.putExtra(CITY_NAME, cityName);
		intent.putExtra(WEATHER_INFO_JSON_STRING, currentWeatherJsonString);
		startService(intent);
	}

	/**
	 * Saves the requested city and weather information type in
	 * SharedPreferences, so they can be retrieved later and a new request
	 * formed automatically.
	 * 
	 * @param cityId
	 *            Open Weather Map ID for the requested city
	 * @param weatherInfoType
	 *            requested weather information type
	 */
	private void saveWeatherInfoRequest(int cityId,
			WeatherInfoType weatherInfoType) {
		SharedPrefsHelper.putCityIdIntoSharedPrefs(this, cityId);
		SharedPrefsHelper.putLastWeatherInfoTypeIntoSharedPrefs(this,
				weatherInfoType);
	}

	@Override
	public void onCityWeatherInfoRequested(int cityId,
			WeatherInfoType weatherInfoType) {
		workerFragment.retrieveWeatherInfoJsonString(cityId, weatherInfoType);
		saveWeatherInfoRequest(cityId, weatherInfoType);
	}

	@Override
	public void onJsonStringRetrieved(String jsonString,
			WeatherInfoType weatherInfoType, boolean saveDataLocally) {
		if (saveDataLocally) {
			saveRetrievedData(jsonString, weatherInfoType);
		}

		if (isDualPane) {
			displayRetrievedDataInThisActivity(jsonString, weatherInfoType);
		} else {
			displayRetrievedDataInNewActivity(jsonString, weatherInfoType);
		}
	}

	/**
	 * Saves the retrieved data in the database, so that it could be reused for
	 * a short period of time.
	 * 
	 * @param jsonString
	 *            JSON weather information data in textual form
	 * @param weatherInfoType
	 *            a type of the retrieved weather data
	 */
	private void saveRetrievedData(String jsonString,
			WeatherInfoType weatherInfoType) {
		Intent intent = new Intent(this, GeneralDatabaseService.class);
		intent.setAction(GeneralDatabaseService.ACTION_UPDATE_WEATHER_INFO);
		intent.putExtra(WEATHER_INFO_JSON_STRING, jsonString);
		intent.putExtra(WEATHER_INFORMATION_TYPE, (Parcelable) weatherInfoType);
		startService(intent);
	}

	/**
	 * Creates and embeds a new fragment of the correct type to display the
	 * obtained weather data in the second pane of this activity.
	 * 
	 * @param jsonString
	 *            JSON weather information data in textual form
	 * @param weatherInfoType
	 *            a type of the retrieved weather data
	 */
	private void displayRetrievedDataInThisActivity(String jsonString,
			WeatherInfoType weatherInfoType) {
		Fragment fragment;
		if (weatherInfoType == WeatherInfoType.CURRENT_WEATHER) {
			fragment = WeatherInfoFragment.newInstance(weatherInfoType, null,
					jsonString);
		} else {
			fragment = WeatherForecastParentFragment.newInstance(
					weatherInfoType, jsonString);
		}
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.weather_info_container, fragment).commit();
	}

	/**
	 * Starts a new activity to display the obtained weather data.
	 * 
	 * @param jsonString
	 *            JSON weather information data in textual form
	 * @param weatherInfoType
	 *            a type of the retrieved weather data
	 */
	private void displayRetrievedDataInNewActivity(String jsonString,
			WeatherInfoType weatherInfoType) {
		Intent intent = new Intent(this, WeatherInfoActivity.class);
		intent.putExtra(WEATHER_INFORMATION_TYPE, (Parcelable) weatherInfoType);
		intent.putExtra(WEATHER_INFO_JSON_STRING, jsonString);
		startActivity(intent);
	}

}
