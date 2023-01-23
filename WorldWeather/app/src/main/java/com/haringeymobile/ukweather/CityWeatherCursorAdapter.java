package com.haringeymobile.ukweather;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.haringeymobile.ukweather.database.CityTable;

public class CityWeatherCursorAdapter extends BaseCityCursorAdapter {

    /**
     * A helper to implement the "view holder" design pattern.
     */
    private static class CityRowWeatherViewHolder {

        TextView cityNameTextView;
        LinearLayout buttonCurrentWeather;
        LinearLayout buttonDailyForecast;
        LinearLayout buttonThreeHourlyForecast;
    }

    /**
     * An adapter to map the cities stored in the database to the city list rows
     * with buttons requesting various weather information.
     */
    CityWeatherCursorAdapter(Context context, int layout, Cursor c,
                             String[] from, int[] to, int flags, OnClickListener onClickListener) {
        super(context, layout, c, from, to, flags, onClickListener);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View rowView = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                .inflate(R.layout.row_city_list_with_weather_buttons, parent, false);

        CityRowWeatherViewHolder holder = new CityRowWeatherViewHolder();
        holder.cityNameTextView = (TextView) rowView
                .findViewById(R.id.city_name_in_list_row_text_view);
        holder.buttonCurrentWeather = (LinearLayout) rowView
                .findViewById(R.id.city_current_weather_button);
        holder.buttonDailyForecast = (LinearLayout) rowView
                .findViewById(R.id.city_daily_weather_forecast_button);
        holder.buttonThreeHourlyForecast = (LinearLayout) rowView
                .findViewById(R.id.city_three_hourly_weather_forecast_button);

        holder.buttonCurrentWeather.setOnClickListener(onClickListener);
        holder.buttonDailyForecast.setOnClickListener(onClickListener);
        holder.buttonThreeHourlyForecast.setOnClickListener(onClickListener);

        rowView.setTag(holder);

        return rowView;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        CityRowWeatherViewHolder holder = (CityRowWeatherViewHolder) view.getTag();
        int nameColumnsIndex = cursor.getColumnIndexOrThrow(CityTable.COLUMN_NAME);
        holder.cityNameTextView.setText(cursor.getString(nameColumnsIndex));
    }

}