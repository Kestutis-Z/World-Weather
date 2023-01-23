package com.haringeymobile.ukweather;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.haringeymobile.ukweather.utils.MiscMethods;

/**
 * A dialog allowing user to search and add new cities to the city list.
 */
public class AddCityFragment extends DialogFragment {

    /**
     * A listener for the city query text submits.
     */
    public interface OnNewCityQueryTextListener {

        /**
         * Displays an informational dialog.
         *
         * @param stringResourceId resource id for the message to be displayed in the dialog
         */
        void showAlertDialog(int stringResourceId);

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
    public void onAttach(@NonNull Activity activity) {
        super.onAttach(activity);
        cityQueryTextListener = (OnNewCityQueryTextListener) activity;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = requireActivity().getLayoutInflater().inflate(
                R.layout.fragment_add_city, null);

        queryEditText = (EditText) view.findViewById(R.id.ac_search_edit_text);
        TextView infoTextView = (TextView) view.findViewById(R.id.ac_info_text_view);
        infoTextView.setText(MiscMethods.getNoCitiesFoundDialogMessage(getResources()));

        ImageButton searchButton = (ImageButton) view.findViewById(R.id.ac_search_button);
        TypedValue outValue = new TypedValue();
        requireActivity().getTheme().resolveAttribute(R.attr.themed_round_drawable, outValue, true);
        searchButton.setBackgroundResource(outValue.resourceId);
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
        query = preProcessQueryString(query);
        if (query.length() < MINIMUM_SEARCH_QUERY_STRING_LENGTH) {
            cityQueryTextListener.showAlertDialog(R.string.dialog_title_query_too_short);
        } else {
            cityQueryTextListener.onQueryTextSubmit(query);
        }
    }

    /**
     * Removes characters unaccepted by OWM, such as spaces and carriage returns.
     *
     * @param query provided by user
     * @return query that can be submitted to OWM
     */
    private String preProcessQueryString(String query) {
        return query.replace(" ", "").replace("\n", "").replace("\r", "");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        cityQueryTextListener = null;
    }

}