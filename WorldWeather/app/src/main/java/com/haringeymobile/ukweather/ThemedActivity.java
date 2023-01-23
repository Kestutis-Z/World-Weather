package com.haringeymobile.ukweather;

import static com.haringeymobile.ukweather.settings.SettingsActivity.LANGUAGE_DEFAULT;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MenuItem;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.haringeymobile.ukweather.settings.SettingsActivity;
import com.haringeymobile.ukweather.utils.MiscMethods;

/**
 * A base activity for all other app's activities, that sets the app theme upon creation.
 */
public abstract class ThemedActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setAppTheme();
        super.onCreate(savedInstanceState);
        setTitle(getActivityLabelResourceId());
    }

    protected int getActivityLabelResourceId() {
        int labelRes;
        try {
            labelRes = getPackageManager().getActivityInfo(getComponentName(), 0).labelRes;
        } catch (PackageManager.NameNotFoundException e) {
            throw new RuntimeException();
        }
        return labelRes;
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String appLanguage = preferences.getString(SettingsActivity.
                PREF_APP_LANGUAGE, LANGUAGE_DEFAULT);
        if (!appLanguage.equals(LANGUAGE_DEFAULT)) {
            MiscMethods.updateLocale(appLanguage, getResources());
            resetActionBarTitle();
        }
    }

    protected void resetActionBarTitle() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            int labelRes = getActivityLabelResourceId();
            if (labelRes > 0) {
                actionBar.setTitle(labelRes);
            }
        }
    }

    /**
     * Sets the user preferred (or default, if no preference was set) app theme.
     */
    private void setAppTheme() {
        Resources res = getResources();
        String appThemeValue = PreferenceManager.getDefaultSharedPreferences(this).getString(
                SettingsActivity.PREF_APP_THEME,
                res.getString(R.string.pref_app_theme_default));

        int themeResourceId = getThemeResourceId(appThemeValue);
        setTheme(themeResourceId);
    }

    /**
     * Obtains the resource id for the app theme.
     *
     * @param appThemeValue one of the values defined in {@link R.array#pref_theme_values}
     * @return app theme resource id
     */
    private int getThemeResourceId(String appThemeValue) {
        int theme;
        switch (appThemeValue) {
            case "0":
                theme = R.style.AppThemePink;
                break;
            case "1":
                theme = R.style.AppThemeRed;
                break;
            case "2":
                theme = R.style.AppThemePurple;
                break;
            case "3":
                theme = R.style.AppThemeDeepPurple;
                break;
            case "4":
                theme = R.style.AppThemeIndigo;
                break;
            case "5":
                theme = R.style.AppThemeBlue;
                break;
            case "6":
                theme = R.style.AppThemeCyan;
                break;
            case "7":
                theme = R.style.AppThemeTeal;
                break;
            case "8":
                theme = R.style.AppThemeGreen;
                break;
            case "9":
                theme = R.style.AppThemeBrown;
                break;
            case "10":
                theme = R.style.AppThemeBlueGray;
                break;
            default:
                throw new IllegalArgumentException("Unsupported value: " + appThemeValue + ". " +
                        "(The " + "value must be defined in xml: res->values" +
                        "->strings_for_settings->pref_theme_values");
        }
        return theme;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        playAnimation();
    }

    /**
     * Plays the activity transition animation.
     */
    @SuppressLint("PrivateResource")
    protected void playAnimation() {
        overridePendingTransition(R.anim.abc_slide_in_top, R.anim.abc_slide_out_bottom);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                playAnimation();
                return true;
        }
        return false;
    }

}