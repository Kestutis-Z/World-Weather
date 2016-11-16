package com.haringeymobile.ukweather;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.haringeymobile.ukweather.database.CityTable;
import com.haringeymobile.ukweather.utils.SharedPrefsHelper;

/**
 * An adapter to map the cities stored in the database to the city list rows with buttons
 * requesting utility features, such as removing or renaming the city.
 */
class CityUtilitiesCursorAdapter extends BaseCityCursorAdapter {

    /**
     * A listener for city reordering and deletion-by-swiping requests.
     */
    interface Listener {

        /**
         * Removes the specified city from the database.
         *
         * @param cityId OpenWeatherMap ID for the city to be deleted
         */
        void removeCityById(int cityId);

        /**
         * Switches two cities in the ordered city list.
         *
         * @param cityOrder_x first city order in the list
         * @param cityOrder_y second city order in the list
         */
        void switchCities(int cityOrder_x, int cityOrder_y);

    }

    /**
     * A helper to implement the "view holder" design pattern.
     */
    private static class CityRowUtilitiesViewHolder {

        TextView cityNameTextView;
        ImageView buttonRename;
        ImageView buttonDelete;
    }

    CityUtilitiesCursorAdapter(Context context, int layout, Cursor c, String[] from, int[] to,
                               int flags, OnClickListener onClickListener) {
        super(context, layout, c, from, to, flags, onClickListener);
        listener = (Listener) context;
        isRemovalModeButton = SharedPrefsHelper.isRemovalModeButton(context);
    }

    private Listener listener;
    private boolean isRemovalModeButton;

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View rowView = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                .inflate(R.layout.row_city_list_with_utils_buttons, parent, false);

        CityRowUtilitiesViewHolder holder = new CityRowUtilitiesViewHolder();
        holder.cityNameTextView = (TextView) rowView
                .findViewById(R.id.city_name_in_list_row_text_view);
        holder.buttonRename = (ImageView) rowView.findViewById(R.id.city_rename_button);
        holder.buttonRename.setOnClickListener(onClickListener);
        holder.buttonDelete = (ImageView) rowView.findViewById(R.id.city_remove_button);
        if (isRemovalModeButton) {
            holder.buttonDelete.setVisibility(View.VISIBLE);
            holder.buttonDelete.setOnClickListener(onClickListener);
        } else {
            holder.buttonDelete.setVisibility(View.GONE);
        }

        rowView.setTag(holder);
        return rowView;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        CityRowUtilitiesViewHolder holder = (CityRowUtilitiesViewHolder) view.getTag();
        int nameColumnsIndex = cursor.getColumnIndexOrThrow(CityTable.COLUMN_NAME);
        holder.cityNameTextView.setText(cursor.getString(nameColumnsIndex));
    }

    @Override
    public void drop(int from, int to) {
        super.drop(from, to);
        listener.switchCities(from, to);
    }

    @Override
    public void remove(int which) {
        super.remove(which);
        listener.removeCityById(getCityId(which));
    }

}