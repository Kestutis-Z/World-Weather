package com.haringeymobile.ukweather.weather;

import android.app.Activity;
import android.os.Bundle;
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
import com.haringeymobile.ukweather.R;
import com.haringeymobile.ukweather.data.objects.CityDailyWeatherForecast;
import com.haringeymobile.ukweather.data.objects.CityInfo;
import com.haringeymobile.ukweather.data.objects.CityThreeHourlyWeatherForecast;
import com.haringeymobile.ukweather.data.objects.SearchResponseForDailyForecastQuery;
import com.haringeymobile.ukweather.data.objects.SearchResponseForThreeHourlyForecastQuery;
import com.haringeymobile.ukweather.utils.SharedPrefsHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * A fragment with a sliding tab and a view pager, used display a number of nested fragments,
 * containing some kind of weather forecast information.
 */
public class WeatherForecastParentFragment extends Fragment {

    private static final String WEATHER_INFORMATION_TYPE = "weather info type";
    private static final String WEATHER_INFO_JSON_STRING = "forecast json";
    private static final String CITY_NAME_NOT_KNOWN = "??";

    /**
     * For the purposes of displaying the three-hourly forecasts divided into separate daily lists,
     * we consider a "morning" to be the time between 5-7 am. So depending on the data provided by
     * the OWM, the morning hour can be 5, 6, or 7 am.
     */
    private static final int EARLIEST_MORNING_HOUR = 5;

    private FragmentActivity parentActivity;
    private WeatherInfoType weatherInfoType;
    /**
     * The name of the city for which the weather forecast has been obtained.
     */
    private String cityName;
    /**
     * A list of JSON strings to be used to instantiate child fragments.
     */
    private List<String> jsonStringsForChildFragments = new ArrayList<>();
    /**
     * A list of JSON string lists to be used when the three-hourly forecast should be displayed
     * as several separate lists.
     */
    private List<ArrayList<String>> jsonStringListsForChildListFragments;

    /**
     * Obtains a new fragment to display weather forecast.
     *
     * @param weatherInfoType a type of weather forecast
     * @param jsonString      textual representation of JSON weather forecast data
     * @return a fragment to display the weather forecast in a view pager
     */
    public static WeatherForecastParentFragment newInstance(WeatherInfoType weatherInfoType,
                                                            String jsonString) {
        WeatherForecastParentFragment fragment = new WeatherForecastParentFragment();
        Bundle args = new Bundle();
        args.putParcelable(WEATHER_INFORMATION_TYPE, weatherInfoType);
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
        weatherInfoType = getArguments().getParcelable(WEATHER_INFORMATION_TYPE);
        extractJsonDataForChildFragments();
    }

    /**
     * Converts the JSON string (passed as an argument) to the correct weather information object,
     * and extracts the data required to instantiate nested fragments.
     */
    private void extractJsonDataForChildFragments() {
        String jsonString = getArguments().getString(WEATHER_INFO_JSON_STRING);
        Gson gson = new Gson();

        if (weatherInfoType == WeatherInfoType.DAILY_WEATHER_FORECAST) {
            extractDailyForecastJsonData(jsonString, gson);
        } else if (weatherInfoType == WeatherInfoType.THREE_HOURLY_WEATHER_FORECAST) {
            extractThreeHourlyForecastJsonData(jsonString, gson);
            if (SharedPrefsHelper.getForecastDisplayMode(getContext()) ==
                    ThreeHourlyForecastDisplayMode.LIST) {
                splitThreeHourlyForecastsIntoDailyLists();
            }
        } else {
            throw new WeatherInfoType.IllegalWeatherInfoTypeArgumentException(weatherInfoType);
        }
    }

    /**
     * Obtains the city name, and fills the JSON string list with the extracted daily forecast
     * JSON strings.
     *
     * @param jsonString textual representation of JSON 16 day daily weather forecast data
     * @param gson       a converter between JSON strings and Java objects
     */
    private void extractDailyForecastJsonData(String jsonString, Gson gson) {
        SearchResponseForDailyForecastQuery searchResponseForDailyForecastQuery = gson.fromJson(
                jsonString, SearchResponseForDailyForecastQuery.class);
        CityInfo cityInfo = searchResponseForDailyForecastQuery.getCityInfo();
        getCityName(cityInfo);

        List<CityDailyWeatherForecast> dailyForecasts = searchResponseForDailyForecastQuery
                .getDailyWeatherForecasts();
        for (CityDailyWeatherForecast forecast : dailyForecasts) {
            jsonStringsForChildFragments.add(gson.toJson(forecast));
        }
    }

    /**
     * Obtains the city name.
     *
     * @param cityInfo information about the queried city
     */
    private void getCityName(CityInfo cityInfo) {
        // TODO The city can be renamed by a user, so we should query the database for the name
        // It appears that for some cities the query returns with city information missing, in
        // which case cityInfo will be null
        cityName = cityInfo == null ? CITY_NAME_NOT_KNOWN : cityInfo.getCityName();
    }

    /**
     * Obtains the city name, and fills the JSON string list with the extracted three hourly
     * forecast JSON strings.
     *
     * @param jsonString textual representation of JSON 5 days three hourly weather forecast data
     * @param gson       a converter between JSON strings and Java objects
     */
    private void extractThreeHourlyForecastJsonData(String jsonString, Gson gson) {
        SearchResponseForThreeHourlyForecastQuery searchResponseForThreeHourlyForecastQuery = gson
                .fromJson(jsonString, SearchResponseForThreeHourlyForecastQuery.class);
        CityInfo cityInfo = searchResponseForThreeHourlyForecastQuery.getCityInfo();
        // It appears that for some cities the query returns with city information missing, in
        // which case cityInfo will be null
        getCityName(cityInfo);
        List<CityThreeHourlyWeatherForecast> threeHourlyForecasts =
                searchResponseForThreeHourlyForecastQuery.getThreeHourlyWeatherForecasts();
        for (CityThreeHourlyWeatherForecast forecast : threeHourlyForecasts) {
            jsonStringsForChildFragments.add(gson.toJson(forecast));
        }
    }

    /**
     * Divides all the three-hourly forecasts into separate lists, to be displayed as view pager
     * pages. These lists correspond to days. Since there are eight three-hourly forecasts in a 24
     * hour day, the lists should have eight forecasts each. However, as the time of the first
     * forecast can be any time of the day, and we would like (for user convenience) to start each
     * list with the morning forecast (around 5-7 in the morning), the first day ("today") will
     * possibly have less or more than eight forecasts. Also, the last day will usually have less
     * than eight three-hourly forecasts.
     */
    private void splitThreeHourlyForecastsIntoDailyLists() {
        int firstForecastHour = getFirstThreeHourlyForecastHour();
        int morningStartHour = findMorningStartHour(firstForecastHour);
        int unallocatedThreeHourlyForecastCount = jsonStringsForChildFragments.size();

        jsonStringListsForChildListFragments = new ArrayList<>();
        int forecastHour = firstForecastHour;
        for (int i = 0; i < unallocatedThreeHourlyForecastCount; i++) {
            boolean shouldStartNewDailyList = shouldStartNewDailyList(morningStartHour,
                    forecastHour);
            if (shouldStartNewDailyList) {
                @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
                ArrayList<String> threeHourlyForecastsForOneDay = new ArrayList<>();
                jsonStringListsForChildListFragments.add(threeHourlyForecastsForOneDay);
            }

            getLatestDailyThreeHourlyForecastList().add(jsonStringsForChildFragments.get(i));

            forecastHour += 3;
            forecastHour %= 24;
        }
    }

    /**
     * Obtains the hour of the very first three-hourly forecast provided by OWM.
     *
     * @return the hour in range [0..23]
     */
    private int getFirstThreeHourlyForecastHour() {
        long firstForecastTime = 1000 * new Gson().fromJson(jsonStringsForChildFragments.get(0),
                CityThreeHourlyWeatherForecast.class).getDate();
        Date date = new Date(firstForecastTime);
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);

        return calendar.get(Calendar.HOUR_OF_DAY);
    }

    /**
     * Finds the morning hour that the forecasts for each day should start. This will be 5, 6, or 7.
     *
     * @param firstForecastHour hour of the first three-hourly forecast, provided by OWM
     * @return an hour in range [5..7]
     */
    private int findMorningStartHour(int firstForecastHour) {
        int remainder = (firstForecastHour - EARLIEST_MORNING_HOUR) % 3;
        if (remainder < 0) {
            remainder += 3;
        }
        if (remainder == 0) {
            return EARLIEST_MORNING_HOUR;
        } else if (remainder == 1) {
            return EARLIEST_MORNING_HOUR + 1;
        } else if (remainder == 2) {
            return EARLIEST_MORNING_HOUR + 2;
        } else {
            throw new IllegalStateException("Unexpected remainder: " + remainder);
        }
    }

    /**
     * Finds whether the new daily three hourly list should be created. This will be the case if:
     * (a) currently there are no daily lists at all, or
     * (b) the time of the three-hourly forecast under consideration is a morning hour, unless the
     * current daily list is the only daily list so far, and it only contains forecasts at hours
     * in range [0..4]. That is, if the very first three-hourly forecast time is between 0 and 4 am,
     * then the first daily list will contain all the three hourly forecast until the next day's
     * morning.
     *
     * @param morningStartHour hour at which the day starts (around 5-7 am, depending on the data
     *                         OWM provides
     * @param forecastHour     hour of the three-hourly forecast, provided by the OWM
     * @return true if a new daily list should be created and added to the
     * jsonStringListsForChildListFragments
     */
    private boolean shouldStartNewDailyList(int morningStartHour, int forecastHour) {
        if (jsonStringListsForChildListFragments.size() == 0) {
            return true;
        }
        if (forecastHour == morningStartHour) {
            int threeHourlyForecastCountInCurrentDailyList =
                    getLatestDailyThreeHourlyForecastList().size();
            return forecastHour - 3 * threeHourlyForecastCountInCurrentDailyList < 0;
        }
        return false;
    }

    private ArrayList<String> getLatestDailyThreeHourlyForecastList() {
        int currentDailyListCount = jsonStringListsForChildListFragments.size();
        return jsonStringListsForChildListFragments.get(currentDailyListCount - 1);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.sliding_tab_host, container, false);
        PagerSlidingTabStrip pagerSlidingTabStrip = (PagerSlidingTabStrip) view.findViewById(
                R.id.tabs);
        ViewPager viewPager = (ViewPager) view.findViewById(R.id.pager);
        WeatherForecastPagerAdapter pagerAdapter = new WeatherForecastPagerAdapter(
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

    /**
     * An adapter to populate view pager with weather forecast fragments.
     */
    private class WeatherForecastPagerAdapter extends FragmentStatePagerAdapter {

        private static final String DAY_TEMPLATE = "E  MMM dd";
        private static final String TIME_TEMPLATE = "E  HH:mm";

        public WeatherForecastPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            if (weatherInfoType == WeatherInfoType.DAILY_WEATHER_FORECAST) {
                return getPageTitleForDailyWeatherForecast(position);
            } else if (weatherInfoType == WeatherInfoType.THREE_HOURLY_WEATHER_FORECAST) {
                return getPageTitleForThreeHourlyWeatherForecast(position);
            } else {
                throw new WeatherInfoType.IllegalWeatherInfoTypeArgumentException(weatherInfoType);
            }
        }

        /**
         * Obtains the page title for the single day weather forecast.
         *
         * @param position position in a view pager for the requested title
         * @return a formatted date string
         */
        private CharSequence getPageTitleForDailyWeatherForecast(int position) {
            long time = 1000 * new Gson().fromJson(jsonStringsForChildFragments.get(position),
                    CityDailyWeatherForecast.class).getDate();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat();
            simpleDateFormat.applyLocalizedPattern(DAY_TEMPLATE);
            return simpleDateFormat.format(new Date(time));
        }

        /**
         * Obtains the page title for the three hourly weather forecast.
         *
         * @param position position in a view pager for the requested title
         * @return a formatted time or date string
         */
        private CharSequence getPageTitleForThreeHourlyWeatherForecast(int position) {
            String template = isRequestedThreeHourlyForecastInListForm() ?
                    DAY_TEMPLATE : TIME_TEMPLATE;
            String jsonString = isRequestedThreeHourlyForecastInListForm() ?
                    jsonStringListsForChildListFragments.get(position).get(0) :
                    jsonStringsForChildFragments.get(position);

            long time = 1000 * new Gson().fromJson(jsonString,
                    CityThreeHourlyWeatherForecast.class).getDate();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat();

            simpleDateFormat.applyLocalizedPattern(template);
            return simpleDateFormat.format(new Date(time));
        }

        @Override
        public int getCount() {
            return isRequestedThreeHourlyForecastInListForm() ?
                    jsonStringListsForChildListFragments.size() :
                    jsonStringsForChildFragments.size();
        }

        /**
         * Determines whether the requested forecast is a three-hourly forecast that should be
         * displayed as several daily lists.
         */
        private boolean isRequestedThreeHourlyForecastInListForm() {
            return jsonStringListsForChildListFragments != null;
        }

        @Override
        public Fragment getItem(int position) {
            return isRequestedThreeHourlyForecastInListForm() ?
                    WeatherThreeHourlyForecastChildListFragment.newInstance(
                            jsonStringListsForChildListFragments.get(position)) :
                    WeatherInfoFragment.newInstance(weatherInfoType, cityName,
                            jsonStringsForChildFragments.get(position));
        }
    }

}