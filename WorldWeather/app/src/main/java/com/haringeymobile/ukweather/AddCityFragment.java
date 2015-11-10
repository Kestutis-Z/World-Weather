package com.haringeymobile.ukweather;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/**
 * A dialog allowing user to search and add new cities to the city list.
 */
public class AddCityFragment extends DialogFragment {

    /**
     * A listener for the city query text submits.
     */
    public interface OnNewCityQueryTextListener {

        /**
         * Displays a dialog, informing the user that the query text was too short.
         */
        void showQueryStringTooShortAlertDialog();

        /**
         * Processes the new city query.
         *
         * @param queryText the query text that is to be submitted
         */
        void onQueryTextSubmit(String queryText);

    }

    /**
     * The shortest acceptable city search query string length.
     */
    private static final int MINIMUM_SEARCH_QUERY_STRING_LENGTH = 3;

    private OnNewCityQueryTextListener cityQueryTextListener;
    private EditText queryEditText;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        cityQueryTextListener = (OnNewCityQueryTextListener) activity;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = getActivity().getLayoutInflater().inflate(R.layout.fragment_add_city, null);

        queryEditText = (EditText) view.findViewById(R.id.ac_search_edit_text);

        Button searchButton = (Button) view.findViewById(R.id.ac_search_button);
        searchButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                onNewCityQuerySubmitted();
            }

        });

        return new AlertDialog.Builder(getActivity())
                .setView(view)
                .setIcon(R.drawable.ic_add_content)
                .setTitle(R.string.dialog_title_add_city)
                .create();
    }

    /**
     * Called when the user submits a query. Obtains the query text, and performs the primary
     * processing.
     */
    private void onNewCityQuerySubmitted() {
        String query = queryEditText.getText().toString();
        if (query.length() < MINIMUM_SEARCH_QUERY_STRING_LENGTH) {
            cityQueryTextListener.showQueryStringTooShortAlertDialog();
        } else {
            cityQueryTextListener.onQueryTextSubmit(query);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        cityQueryTextListener = null;
    }

}