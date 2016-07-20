package com.haringeymobile.ukweather.utils;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;

/**
 * A dialog that displays a title and an (optional) message, and can be dismissed by pressing
 * a button.
 */
public class GeneralDialogFragment extends DialogFragment {

    public static final String TITLE = "title";
    public static final String MESSAGE = "message";

    /**
     * Creates a new DialogFragment, and sets the provided title and message arguments.
     *
     * @param title   dialog title
     * @param message dialog message; may be null, in which case the message is not displayed
     * @return a new dialog
     */
    public static GeneralDialogFragment newInstance(String title, String message) {
        GeneralDialogFragment fragment = new GeneralDialogFragment();
        Bundle args = new Bundle();
        args.putString(TITLE, title);
        args.putString(MESSAGE, message);
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle args = getArguments();
        String title = args.getString(TITLE);
        String message = args.getString(MESSAGE);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        DialogInterface.OnClickListener onClickListener = getDialogOnClickListener();
        builder.setTitle(title);
        if (message != null) {
            builder.setMessage(message);
        }
        builder.setPositiveButton(android.R.string.ok, onClickListener);
        return builder.create();
    }

    private DialogInterface.OnClickListener getDialogOnClickListener() {
        return new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int id) {
                dismiss();
            }

        };
    }

}
