package com.haringeymobile.ukweather;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

/**
 * A dialog asking for the city deletion confirmation.
 */
public class DeleteCityDialog extends DialogFragment {

    /**
     * A listener for dialog's button clicks.
     */
    public interface OnDialogButtonClickedListener {

        /**
         * Reacts to the confirmation that the specified city should be deleted.
         *
         * @param cityId OpenWeatherMap ID for the city to be deleted
         */
        void onCityRecordDeletionConfirmed(int cityId);
    }

    private static final String CITY_NAME = "city name";

    private Activity parentActivity;
    private OnDialogButtonClickedListener onDialogButtonClickedListener;

    /**
     * Creates a new dialog asking for the city deletion confirmation.
     *
     * @param cityId   OpenWeatherMap ID for the city to be deleted
     * @param cityName city name in the database
     * @return a new dialog fragment with the specified arguments
     */
    public static DeleteCityDialog newInstance(int cityId, String cityName) {
        DeleteCityDialog dialogFragment = new DeleteCityDialog();
        Bundle b = new Bundle();
        b.putInt(CityManagementActivity.CITY_ID, cityId);
        b.putString(CITY_NAME, cityName);
        dialogFragment.setArguments(b);
        return dialogFragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        parentActivity = getActivity();
        try {
            onDialogButtonClickedListener = (OnDialogButtonClickedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnDialogButtonClickedListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        parentActivity = null;
        onDialogButtonClickedListener = null;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String title = getDialogTitle();
        OnClickListener dialogOnClickListener = getDialogOnClickListener();
        CharSequence positiveButtonText = parentActivity.getResources()
                .getString(android.R.string.ok);
        return new AlertDialog.Builder(parentActivity)
                .setIcon(R.drawable.ic_delete)
                .setTitle(title)
                .setPositiveButton(positiveButtonText, dialogOnClickListener)
                .create();
    }

    /**
     * Obtains the city deletion dialog title.
     *
     * @return text asking for the city deletion confirmation
     */
    private String getDialogTitle() {
        Resources res = parentActivity.getResources();
        final String cityName = getArguments().getString(CITY_NAME);
        return String.format(res.getString(R.string.dialog_title_delete_city), cityName);
    }

    /**
     * Obtains a listener for dialog's button clicks.
     *
     * @return a listener to handle button clicks
     */
    private OnClickListener getDialogOnClickListener() {
        OnClickListener dialogOnClickListener = new OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int whichButton) {
                onDialogButtonClickedListener
                        .onCityRecordDeletionConfirmed(getArguments().getInt(
                                CityManagementActivity.CITY_ID));
            }
        };
        return dialogOnClickListener;
    }
}
