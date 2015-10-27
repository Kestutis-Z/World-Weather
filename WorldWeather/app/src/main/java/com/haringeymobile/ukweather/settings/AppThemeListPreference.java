package com.haringeymobile.ukweather.settings;

import android.app.AlertDialog;
import android.content.Context;
import android.content.res.TypedArray;
import android.preference.ListPreference;
import android.util.AttributeSet;
import android.widget.ListAdapter;

import com.haringeymobile.ukweather.R;

/**
 * The list of app theme preferences, where list item view is customized.
 */
public class AppThemeListPreference extends ListPreference {

    private static final int DEFAULT_COLOUR_VALUE = 0;
    /**
     * Default app theme value in the {@link R.array#pref_theme_values}.
     */
    private static final String DEFAULT_APP_THEME_VALUE = "0";

    /**
     * Color resource ids referenced in the {@link R.array#pref_color_values}.
     */
    private int[] resourceIds = null;

    public AppThemeListPreference(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray colors = context.getResources().obtainTypedArray(R.array.pref_color_values);
        resourceIds = new int[colors.length()];
        for (int i = 0; i < colors.length(); i++) {
            resourceIds[i] = colors.getColor(i, DEFAULT_COLOUR_VALUE);
        }

        colors.recycle();
    }

    @Override
    protected void onPrepareDialogBuilder(AlertDialog.Builder builder) {
        int index = findIndexOfValue(getSharedPreferences().getString(getKey(),
                DEFAULT_APP_THEME_VALUE));
        ListAdapter listAdapter = new AppThemeArrayAdapter(getContext(), R.id.app_theme_name,
                getEntries(), resourceIds, index);
        builder.setAdapter(listAdapter, this);
        super.onPrepareDialogBuilder(builder);
    }

}
