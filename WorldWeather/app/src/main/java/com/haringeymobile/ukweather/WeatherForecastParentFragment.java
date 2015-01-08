package com.haringeymobile.ukweather;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.astuetz.PagerSlidingTabStrip;
import com.google.gson.Gson;
import com.haringeymobile.ukweather.WeatherInfoType.IllegalWeatherInfoTypeArgumentException;
import com.haringeymobile.ukweather.data.objects.CityDailyWeatherForecast;
import com.haringeymobile.ukweather.data.objects.CityThreeHourlyWeatherForecast;
import com.haringeymobile.ukweather.data.objects.SearchResponseForDailyForecastQuery;
import com.haringeymobile.ukweather.data.objects.SearchResponseForThreeHourlyForecastQuery;

/**
 * A fragment with a sliding tab and a view pager to display a number of nested
 * fragments containing some kind of weather forecast information.
 */
public class WeatherForecastParentFragment extends Fragment {

	private static final String WEATHER_INFORMATION_TYPE = "weather info type";
	private static final String WEATHER_INFO_JSON_STRING = "forecast json";

	private FragmentActivity parentActivity;
	private PagerSlidingTabStrip pagerSlidingTabStrip;
	private ViewPager viewPager;
	private WeatherForecastPagerAdapter pagerAdapter;
	private WeatherInfoType weatherInfoType;
	/** The name of the city for which the weather forecast has been obtained. */
	private String cityName;
	/** A list of JSON strings to be used to instantiate child fragments. */
	private List<String> jsonStringsForChildFragments = new ArrayList<>();

	/**
	 * Obtains a new fragment to display weather forecast.
	 * 
	 * @param weatherInfoType
	 *            a type of weather forecast
	 * @param jsonString
	 *            textual representation of JSON weather forecast data
	 * @return a fragment to display the weather forecast in a view pager
	 */
	public static WeatherForecastParentFragment newInstance(
			WeatherInfoType weatherInfoType, String jsonString) {
		WeatherForecastParentFragment fragment = new WeatherForecastParentFragment();
		Bundle args = new Bundle();
		args.putParcelable(WEATHER_INFORMATION_TYPE,
				(Parcelable) weatherInfoType);
		args.putString(WEATHER_INFO_JSON_STRING, jsonString);
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		parentActivity = (FragmentActivity) activity;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		weatherInfoType = getArguments()
				.getParcelable(WEATHER_INFORMATION_TYPE);
		extractJsonDataForChildFragments();
	}

	/**
	 * Converts the JSON string (passed as an argument) to the correct weather
	 * information object, and extracts the data required to instantiate nested
	 * fragments.
	 */
	private void extractJsonDataForChildFragments() {
		String jsonString = getArguments().getString(WEATHER_INFO_JSON_STRING);
		Gson gson = new Gson();

		if (weatherInfoType == WeatherInfoType.DAILY_WEATHER_FORECAST) {
			extractDailyForecastJsonData(jsonString, gson);
		} else if (weatherInfoType == WeatherInfoType.THREE_HOURLY_WEATHER_FORECAST) {
			extractThreeHourlyForecastJsonData(jsonString, gson);
		} else {
			throw new IllegalWeatherInfoTypeArgumentException(weatherInfoType);
		}
	}

	/**
	 * Obtains the city name, and fills the JSON string list with the extracted
	 * daily forecast JSON strings.
	 * 
	 * @param jsonString
	 *            textual representation of JSON 14 days daily weather forecast
	 *            data
	 * @param gson
	 *            a converter between JSON strings and Java objects
	 */
	private void extractDailyForecastJsonData(String jsonString, Gson gson) {
		SearchResponseForDailyForecastQuery searchResponseForDailyForecastQuery = gson
				.fromJson(jsonString, SearchResponseForDailyForecastQuery.class);
		cityName = searchResponseForDailyForecastQuery.getCityInfo()
				.getCityName();
		List<CityDailyWeatherForecast> dailyForecasts = searchResponseForDailyForecastQuery
				.getDailyWeatherForecasts();
		for (CityDailyWeatherForecast forecast : dailyForecasts) {
			jsonStringsForChildFragments.add(gson.toJson(forecast));
		}
	}

	/**
	 * Obtains the city name, and fills the JSON string list with the extracted
	 * three hourly forecast JSON strings.
	 * 
	 * @param jsonString
	 *            textual representation of JSON 5 days three hourly weather
	 *            forecast data
	 * @param gson
	 *            a converter between JSON strings and Java objects
	 */
	private void extractThreeHourlyForecastJsonData(String jsonString, Gson gson) {
		SearchResponseForThreeHourlyForecastQuery searchResponseForThreeHourlyForecastQuery = gson
				.fromJson(jsonString,
						SearchResponseForThreeHourlyForecastQuery.class);
		cityName = searchResponseForThreeHourlyForecastQuery.getCityInfo()
				.getCityName();
		List<CityThreeHourlyWeatherForecast> threeHourlyForecasts = searchResponseForThreeHourlyForecastQuery
				.getThreeHourlyWeatherForecasts();
		for (CityThreeHourlyWeatherForecast forecast : threeHourlyForecasts) {
			jsonStringsForChildFragments.add(gson.toJson(forecast));
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.sliding_tab_host, container,
				false);
		pagerSlidingTabStrip = (PagerSlidingTabStrip) view
				.findViewById(R.id.tabs);
		viewPager = (ViewPager) view.findViewById(R.id.pager);
		pagerAdapter = new WeatherForecastPagerAdapter(
				parentActivity.getSupportFragmentManager());
		viewPager.setAdapter(pagerAdapter);
		pagerSlidingTabStrip.setViewPager(viewPager);

		return view;
	}

	@Override
	public void onDetach() {
		super.onDetach();
		parentActivity = null;
	}

	/** An adapter to populate view pager with weather forecast fragments. */
	private class WeatherForecastPagerAdapter extends FragmentStatePagerAdapter {

		public WeatherForecastPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public CharSequence getPageTitle(int position) {
			if (weatherInfoType == WeatherInfoType.DAILY_WEATHER_FORECAST) {
				return getPageTitleForDailyWeatherForecast(position);
			} else if (weatherInfoType == WeatherInfoType.THREE_HOURLY_WEATHER_FORECAST) {
				return getPageTitleForThreeHourlyWeatherForecast(position);
			} else {
				throw new IllegalWeatherInfoTypeArgumentException(
						weatherInfoType);
			}
		}

		/**
		 * Obtains a page title for a single day weather forecast.
		 * 
		 * @param position
		 *            position in a view pager for the requested title
		 * @return a formatted date string
		 */
		private CharSequence getPageTitleForDailyWeatherForecast(int position) {
			long time = 1000 * new Gson().fromJson(
					jsonStringsForChildFragments.get(position),
					CityDailyWeatherForecast.class).getDate();
			return android.text.format.DateFormat.getDateFormat(getActivity())
					.format(new Date(time));
		}

		/**
		 * Obtains a page title for a single three hourly weather forecast.
		 * 
		 * @param position
		 *            position in a view pager for the requested title
		 * @return a formatted time string
		 */
		private CharSequence getPageTitleForThreeHourlyWeatherForecast(
				int position) {
			long time = 1000 * new Gson().fromJson(
					jsonStringsForChildFragments.get(position),
					CityThreeHourlyWeatherForecast.class).getDate();
			return android.text.format.DateFormat.getTimeFormat(getActivity())
					.format(new Date(time));
		}

		@Override
		public int getCount() {
			return jsonStringsForChildFragments.size();
		}

		@Override
		public Fragment getItem(int position) {
			return WeatherInfoFragment.newInstance(weatherInfoType, cityName,
					jsonStringsForChildFragments.get(position));
		}

	}

}
