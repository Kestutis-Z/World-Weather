package com.haringeymobile.ukweather;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

/**
 * A dialog displaying the list of found cities in response for the user's
 * search query.
 */
public class CitySearchResultsDialog extends DialogFragment {

	/** A listener for the found city list item clicks. */
	public interface OnCityNamesListItemClickedListener {

		/**
		 * Reacts to the city list item clicks.
		 * 
		 * @param position
		 *            clicked item position in the city list
		 */
		void onFoundCityNamesItemClicked(int position);

	}

	static final String CITY_NAME_LIST = "city names";

	private OnCityNamesListItemClickedListener onCityNamesListItemClickedListener;
	private ListView listView;
	private CityNameArrayAdapter arrayAdapter;

	/**
	 * Creates a new dialog with the city list.
	 * 
	 * @param cityNames
	 *            an array of city names (including location coordinates) to be
	 *            displayed as a list
	 * @return a dialog displaying the list of specified city names
	 */
	static CitySearchResultsDialog newInstance(ArrayList<String> cityNames) {
		CitySearchResultsDialog dialog = new CitySearchResultsDialog();
		Bundle args = new Bundle();
		args.putStringArrayList(CITY_NAME_LIST, cityNames);
		dialog.setArguments(args);
		return dialog;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			onCityNamesListItemClickedListener = (OnCityNamesListItemClickedListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement OnCityNamesListItemClickedListener");
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_search_results,
				container);

		createCustomDialogTitle(view);

		listView = (ListView) view.findViewById(android.R.id.list);
		setCityListView();

		return view;
	}

	/**
	 * Replaces the default dialog's title with the custom one.
	 * 
	 * @param view
	 *            custom dialog fragment's view
	 */
	private void createCustomDialogTitle(View view) {
		getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		TextView customDialogTitle = (TextView) view
				.findViewById(R.id.city_search_dialog_title);
		customDialogTitle.setText(R.string.dialog_title_search_results);
	}

	/**
	 * Prepares the city list for display and item clicks.
	 */
	private void setCityListView() {
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				onCityNamesListItemClickedListener
						.onFoundCityNamesItemClicked(position);
				dismiss();
			}

		});
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		if (arrayAdapter == null) {
			initialiseArrayAdapter();
		}
		listView.setAdapter(arrayAdapter);
	}

	/**
	 * Creates a new adapter to map city names to the list rows.
	 */
	private void initialiseArrayAdapter() {
		Bundle args = getArguments();
		ArrayList<String> cityNames = args.getStringArrayList(CITY_NAME_LIST);
		arrayAdapter = new CityNameArrayAdapter(getActivity(),
				R.layout.row_city_search_list, cityNames);
	}

	@Override
	public void onDestroyView() {
		listView.setAdapter(null);
		super.onDestroyView();
	}

	@Override
	public void onDetach() {
		super.onDetach();
		onCityNamesListItemClickedListener = null;
	}

	/** A helper to implement the "view holder" design pattern. */
	private static class CityNameViewHolder {
		TextView cityNameTextView;
	}

	/** An adapter to map city names to the list rows. */
	private class CityNameArrayAdapter extends ArrayAdapter<String> {

		private Activity context;
		private int layoutResourceId;
		private final List<String> cityNames;

		private CityNameArrayAdapter(Activity activity, int layoutResourceId,
				List<String> cityNames) {
			super(activity, layoutResourceId, cityNames);
			this.context = activity;
			this.layoutResourceId = layoutResourceId;
			this.cityNames = cityNames;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			CityNameViewHolder holder;
			View rowView = convertView;
			if (rowView == null) {
				rowView = context.getLayoutInflater().inflate(layoutResourceId,
						parent, false);
				holder = new CityNameViewHolder();
				holder.cityNameTextView = (TextView) rowView
						.findViewById(R.id.city_name_in_list_row_text_view);
				rowView.setTag(holder);
			}
			holder = (CityNameViewHolder) rowView.getTag();

			String cityName = cityNames.get(position);
			holder.cityNameTextView.setText(cityName);

			setBackgroundForListRow(position, rowView);

			return rowView;
		}

		/**
		 * Makes the list to look nicer by setting alternating bacgrounds to
		 * it's items (rows).
		 * 
		 * @param position
		 *            city list position
		 * @param rowView
		 *            a view displaying a single list item
		 */
		private void setBackgroundForListRow(int position, View rowView) {
			if (position % 2 == 1) {
				rowView.setBackgroundResource(BaseCityCursorAdapter.BACKGROUND_RESOURCE_ODD);
			} else {
				rowView.setBackgroundResource(BaseCityCursorAdapter.BACKGROUND_RESOURCE_EVEN);
			}
		}

	}

}
