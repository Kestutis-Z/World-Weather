package com.haringeymobile.ukweather.settings;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.haringeymobile.ukweather.R;

/**
 * A fragment to provide settings for the app.
 */
@SuppressLint("NewApi")
public class SettingsFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.userpreferences);
    }

}
