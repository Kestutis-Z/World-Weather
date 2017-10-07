package com.haringeymobile.ukweather.settings;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.EditText;

import com.haringeymobile.ukweather.R;
import com.haringeymobile.ukweather.utils.SharedPrefsHelper;

public class EnterPersonalApiKeyFragment extends DialogFragment {

    interface Listener {

        void onCancelUpdatingPersonalApiKey();

    }

    private Listener listener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        listener = (Listener) activity;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final Activity activity = getActivity();

        final EditText personalKeyEditText = new EditText(activity);
        String currentPersonalApiKey = SharedPrefsHelper.getPersonalApiKeyFromSharedPrefs(activity);
        if (!"".equals(currentPersonalApiKey)) {
            personalKeyEditText.append(currentPersonalApiKey);
        }

        return new AlertDialog.Builder(getActivity())
                .setView(personalKeyEditText)
                .setIcon(R.drawable.ic_edit)
                .setTitle(R.string.dialog_title_enter_personal_key)

                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        listener.onCancelUpdatingPersonalApiKey();
                        dismiss();
                    }
                })

                .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String newKew = personalKeyEditText.getText().toString();
                        SharedPrefsHelper.putPersonalApiKeyIntoSharedPrefs(activity, newKew);
                        dismiss();
                    }
                })

                .create();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

}
