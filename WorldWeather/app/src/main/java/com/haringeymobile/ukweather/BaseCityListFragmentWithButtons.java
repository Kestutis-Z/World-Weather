package com.haringeymobile.ukweather;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.View.OnClickListener;
import android.widget.ListView;

import com.haringeymobile.ukweather.database.CityTable;
import com.haringeymobile.ukweather.database.WeatherContentProvider;

/**
 * A fragment containing a list of cities with clickable buttons.
 */
public abstract class BaseCityListFragmentWithButtons extends ListFragment
        implements LoaderCallbacks<Cursor>, OnClickListener {

    /**
     * Columns in the database that will be displayed in a list row.
     */
    protected static final String[] COLUMNS_TO_DISPLAY = new String[]{CityTable.COLUMN_NAME};
    /**
     * Resource IDs of views that will display the data mapped from the
     * database.
     */
    protected static final int[] TO = new int[]{R.id.city_name_in_list_row_text_view};
    /**
     * Loader ID.
     */
    private static final int LOADER_ALL_CITY_RECORDS = 0;

    protected Activity parentActivity;
    protected BaseCityCursorAdapter cursorAdapter;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        parentActivity = activity;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        prepareCityList();
        getLoaderManager().initLoader(LOADER_ALL_CITY_RECORDS, null, this);
    }

    /**
     * Prepares the list view to load and display data.
     */
    private void prepareCityList() {
        cursorAdapter = getCityCursorAdapter();
        setListAdapter(cursorAdapter);
        setListViewForClicks();
    }

    /**
     * Obtains a concrete adapter with specific button set.
     *
     * @return an adapter with specific functionality
     */
    protected abstract BaseCityCursorAdapter getCityCursorAdapter();

    /**
     * Enables the buttons contained by the list items gain focus and react to
     * clicks.
     */
    private void setListViewForClicks() {
        ListView listView = getListView();
        listView.setItemsCanFocus(true);
        listView.setFocusable(false);
        listView.setFocusableInTouchMode(false);
        listView.setClickable(false);
    }

    @Override
    public void onResume() {
        super.onResume();
        // Starts a new or restarts an existing Loader in this manager
        getLoaderManager().restartLoader(0, null, this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        parentActivity = null;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = null;
        String selection = null;
        String[] selectionArgs = null;
        String sortOrder = CityTable.COLUMN_LAST_OVERALL_QUERY_TIME + " DESC";

        CursorLoader cursorLoader = new CursorLoader(parentActivity,
                WeatherContentProvider.CONTENT_URI_CITY_RECORDS, projection,
                selection, selectionArgs, sortOrder);
        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        cursorAdapter.swapCursor(data);
        // Jump to the top of the list
        ListView listView = getListView();
        if (listView != null) {
            listView.setSelection(0);
        }
        cursorAdapter.notifyDataSetChanged();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        cursorAdapter.swapCursor(null);
    }

}