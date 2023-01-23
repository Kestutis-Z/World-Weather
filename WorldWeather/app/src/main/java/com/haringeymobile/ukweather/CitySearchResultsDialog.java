package com.haringeymobile.ukweather;

import android.app.Activity;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.haringeymobile.ukweather.utils.ItemDecorationListDivider;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A dialog displaying the list of found cities in response to the user's search query.
 */
public class CitySearchResultsDialog extends DialogFragment {

    private static final String TITLE_TEXT_LINE_SEPARATOR = "\n--------------\n";

    /**
     * A listener for the found city list item clicks.
     */
    public interface OnCityNamesListItemClickedListener {

        /**
         * Reacts to the city list item clicks.
         *
         * @param position clicked item position in the city list
         */
        void onFoundCityNamesItemClicked(int position);
    }

    static final String CITY_NAME_LIST = "city names";

    private OnCityNamesListItemClickedListener onCityNamesListItemClickedListener;

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;

    /**
     * Creates a new dialog with the city list.
     *
     * @param cityNames an array of city names (including location coordinates) to be
     *                  displayed as a list
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
    public void onAttach(@NonNull Activity activity) {
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
        View view = inflater.inflate(R.layout.fragment_search_results, container);

        createCustomDialogTitle(view);

        recyclerView = (RecyclerView) view.findViewById(R.id.general_recycler_view);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        int listDividerHeight = (int) getResources().getDimension(R.dimen.list_divider_height);
        recyclerView.addItemDecoration(new ItemDecorationListDivider(listDividerHeight));

        return view;
    }

    /**
     * Replaces the default dialog's title with the custom one.
     *
     * @param view custom dialog fragment's view
     */
    private void createCustomDialogTitle(View view) {
        Objects.requireNonNull(getDialog()).getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        TextView customDialogTitle = (TextView) view.findViewById(R.id.city_search_dialog_title);
        String citySearchResultsDialogTitle = getCitySearchResultsDialogTitle();
        customDialogTitle.setText(citySearchResultsDialogTitle);
    }

    private String getCitySearchResultsDialogTitle() {
        Resources res = getResources();
        String dialogTitle = res.getString(R.string.dialog_title_search_results_part_1);
        dialogTitle += TITLE_TEXT_LINE_SEPARATOR;
        dialogTitle += res.getString(R.string.dialog_title_search_results_part_2);
        return dialogTitle;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (adapter == null) {
            initialiseRecyclerViewAdapter();
        }
        recyclerView.setAdapter(adapter);
    }

    /**
     * Creates a new adapter to map city names to the list rows.
     */
    private void initialiseRecyclerViewAdapter() {
        Bundle args = getArguments();
        assert args != null;
        ArrayList<String> cityNames = args.getStringArrayList(CITY_NAME_LIST);
        adapter = new CityNameAdapter(cityNames);
    }

    @Override
    public void onDestroyView() {
        recyclerView.setAdapter(null);
        super.onDestroyView();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        onCityNamesListItemClickedListener = null;
    }

    /**
     * A helper to implement the "view holder" design pattern.
     */
    private static class CityNameViewHolder extends RecyclerView.ViewHolder {
        private final TextView cityNameTextView;

        public CityNameViewHolder(TextView view) {
            super(view);
            cityNameTextView = view;
        }
    }

    /**
     * An adapter to map city names to the list rows.
     */
    private class CityNameAdapter extends RecyclerView.Adapter<CityNameViewHolder> {
        final List<String> cityNames;

        public CityNameAdapter(List<String> cityNames) {
            this.cityNames = cityNames;
        }

        @NonNull
        @Override
        public CityNameViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            TextView v = (TextView) LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.row_city_search_list, parent, false);
            return new CityNameViewHolder(v);
        }

        @Override
        public void onBindViewHolder(CityNameViewHolder holder, final int position) {
            String cityName = cityNames.get(position);
            holder.cityNameTextView.setText(cityName);

            setBackgroundForListRow(position, holder.cityNameTextView);

            int padding = (int) getResources().getDimension(R.dimen.padding_very_large);
            holder.cityNameTextView.setPadding(padding, padding, padding, padding);

            holder.cityNameTextView.setOnClickListener(v -> {
                Objects.requireNonNull(getDialog()).dismiss();
                onCityNamesListItemClickedListener.onFoundCityNamesItemClicked(position);
            });
        }

        /**
         * Makes the list to look nicer by setting alternating backgrounds to it's items (rows).
         *
         * @param position city list position
         * @param rowView  a view displaying a single list item
         */
        private void setBackgroundForListRow(int position, View rowView) {
            if (position % 2 == 1) {
                rowView.setBackgroundResource(BaseCityCursorAdapter.BACKGROUND_RESOURCE_ODD);
            } else {
                rowView.setBackgroundResource(BaseCityCursorAdapter.BACKGROUND_RESOURCE_EVEN);
            }
        }

        @Override
        public int getItemCount() {
            return cityNames.size();
        }
    }

}
