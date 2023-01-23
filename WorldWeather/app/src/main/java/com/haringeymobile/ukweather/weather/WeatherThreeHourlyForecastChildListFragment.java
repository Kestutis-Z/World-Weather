package com.haringeymobile.ukweather.weather;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.haringeymobile.ukweather.R;
import com.haringeymobile.ukweather.data.objects.CityThreeHourlyWeatherForecast;
import com.haringeymobile.ukweather.utils.ItemDecorationListDivider;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Fragment displaying a list of three-hourly forecasts for one day.
 */
public class WeatherThreeHourlyForecastChildListFragment extends Fragment {

    public interface IconCacheRequestListener {

        /**
         * Obtains the memory cache storing weather icon bitmaps.
         */
        LruCache<String, Bitmap> getIconMemoryCache();

    }

    private static final String JSON_STRING_LIST = "json string list";

    private IconCacheRequestListener iconCacheRequestListener;

    /**
     * Creates and sets {@link WeatherThreeHourlyForecastChildListFragment}.
     *
     * @param threeHourlyForecastJsonStrings JSON strings obtained from the OWM, containing weather
     *                                       information
     * @return fragment, which should display the three-hourly forecasts for one day
     */
    public static WeatherThreeHourlyForecastChildListFragment newInstance(
            ArrayList<String> threeHourlyForecastJsonStrings) {
        WeatherThreeHourlyForecastChildListFragment fragment =
                new WeatherThreeHourlyForecastChildListFragment();
        Bundle args = new Bundle();
        args.putStringArrayList(JSON_STRING_LIST, threeHourlyForecastJsonStrings);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        iconCacheRequestListener = (IconCacheRequestListener) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.general_recycler_view, container, false);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.general_recycler_view);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        int listDividerHeight = (int) getResources().getDimension(R.dimen.list_divider_height);
        recyclerView.addItemDecoration(new ItemDecorationListDivider(listDividerHeight));

        ThreeHourlyForecastAdapter adapter = new ThreeHourlyForecastAdapter(getArguments()
                .getStringArrayList(JSON_STRING_LIST));
        recyclerView.setAdapter(adapter);

        return view;
    }

    private static class ThreeHourlyForecastViewHolder extends RecyclerView.ViewHolder {
        private final TextView forecastStartHourTextView;
        private final TextView temperatureTextView;
        private final TextView conditionsTextView;
        private final ImageView conditionsImageView;
        private final TextView pressureTextView;
        private final TextView humidityTextView;
        private final TextView windTextView;

        ThreeHourlyForecastViewHolder(View itemView) {
            super(itemView);
            forecastStartHourTextView = (TextView) itemView.findViewById(R.id.forecast_hour_start);
            temperatureTextView = (TextView) itemView.findViewById(R.id.temperature_text_view);
            conditionsTextView = (TextView) itemView.findViewById(
                    R.id.weather_conditions_text_view);
            conditionsImageView = (ImageView) itemView.findViewById(
                    R.id.weather_conditions_image_view);
            pressureTextView = (TextView) itemView.findViewById(
                    R.id.atmospheric_pressure_text_view);
            humidityTextView = (TextView) itemView.findViewById(R.id.humidity_text_view);
            windTextView = (TextView) itemView.findViewById(R.id.wind_text_view);
        }
    }

    /**
     * An adapter to bind three-hourly forecast data to the respective rows of the daily
     * three-hourly forecast lists.
     */
    private class ThreeHourlyForecastAdapter extends
            RecyclerView.Adapter<ThreeHourlyForecastViewHolder> {

        private static final String TIME_TEMPLATE = "HH:mm";

        private final ArrayList<String> threeHourlyForecastJsonStrings;
        private final WeatherInformationDisplayer weatherInformationDisplayer;

        ThreeHourlyForecastAdapter(ArrayList<String> threeHourlyForecastJsonStrings) {
            this.threeHourlyForecastJsonStrings = threeHourlyForecastJsonStrings;
            weatherInformationDisplayer = new WeatherInformationDisplayer(requireContext(),
                    iconCacheRequestListener.getIconMemoryCache());
        }

        @NonNull
        @Override
        public ThreeHourlyForecastViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View rowView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.row_three_hourly_forecast, parent, false);
            return new ThreeHourlyForecastViewHolder(rowView);
        }

        @Override
        public void onBindViewHolder(@NonNull ThreeHourlyForecastViewHolder holder, int position) {
            String jsonString = threeHourlyForecastJsonStrings.get(position);
            Gson gson = new Gson();
            CityThreeHourlyWeatherForecast forecast = gson.fromJson(jsonString,
                    CityThreeHourlyWeatherForecast.class);

            displayForecastTime(holder, forecast);
            weatherInformationDisplayer.displayConditions(forecast, holder.conditionsTextView,
                    holder.conditionsImageView);
            weatherInformationDisplayer.displayWeatherNumericParametersText(forecast,
                    holder.temperatureTextView, holder.pressureTextView, holder.humidityTextView);
            weatherInformationDisplayer.displayWindInfo(forecast, holder.windTextView);
        }

        /**
         * Obtains and displays the forecast start and end times.
         *
         * @param holder   helper class, holding various views
         * @param forecast weather forecast for one three hour period
         */
        private void displayForecastTime(ThreeHourlyForecastViewHolder holder,
                                         CityThreeHourlyWeatherForecast forecast) {
            @SuppressLint("SimpleDateFormat")
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat();
            simpleDateFormat.applyLocalizedPattern(TIME_TEMPLATE);

            long startTime = forecast.getDate() * 1000;
            Date date = new Date(startTime);
            holder.forecastStartHourTextView.setText(simpleDateFormat.format(date));
        }

        @Override
        public int getItemCount() {
            return threeHourlyForecastJsonStrings.size();
        }
    }

}