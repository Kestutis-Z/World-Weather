package com.haringeymobile.ukweather;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.haringeymobile.ukweather.database.CityTable;

/**
 * An adapter to map the cities stored in the database to the city list.
 */
public abstract class BaseCityCursorAdapter extends SimpleCursorAdapter {

    /**
     * The resource ID for the view corresponding to an even cursor position.
     */
    public static final int BACKGROUND_RESOURCE_EVEN = R.drawable.clickable_dark;
    /**
     * The resource ID for the view corresponding to an odd cursor position. It depends on the app
     * theme, and is resolved at runtime.
     */
    static int BACKGROUND_RESOURCE_ODD;

    /**
     * A listener for button clicks.
     */
    protected OnClickListener onClickListener;

    BaseCityCursorAdapter(Context context, int layout, Cursor c, String[] from,
                          int[] to, int flags, OnClickListener onClickListener) {
        super(context, layout, c, from, to, flags);
        this.onClickListener = onClickListener;

        TypedValue outValue = new TypedValue();
        context.getTheme().resolveAttribute(R.attr.themed_clickable, outValue, true);
        BACKGROUND_RESOURCE_ODD = outValue.resourceId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = super.getView(position, convertView, parent);
        if (position % 2 == 1) {
            view.setBackgroundResource(BACKGROUND_RESOURCE_ODD);
        } else {
            view.setBackgroundResource(BACKGROUND_RESOURCE_EVEN);
        }
        return view;
    }

    /**
     * Obtains the Open Weather Map city ID for the specified list position.
     *
     * @param position the city list position
     * @return Open Weather Map city ID, or -1 if the city list does not contain the
     * specified position
     */
    int getCityId(int position) {
        Cursor cursor = getCursor();
        if (cursor.moveToPosition(position)) {
            return cursor.getInt(cursor.getColumnIndex(CityTable.COLUMN_CITY_ID));
        }
        return CityTable.CITY_ID_DOES_NOT_EXIST;
    }

    /**
     * Obtains the city name stored in the database for the specified list position.
     *
     * @param position city list position
     * @return city name, or null if city list does not contain the specified position
     */
    String getCityName(int position) {
        Cursor cursor = getCursor();
        if (cursor.moveToPosition(position)) {
            return cursor.getString(cursor.getColumnIndex(CityTable.COLUMN_NAME));
        }
        return null;
    }

}