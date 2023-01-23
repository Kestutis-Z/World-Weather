package com.haringeymobile.ukweather;

import android.content.res.Resources;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.method.MovementMethod;
import android.text.util.Linkify;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;

/**
 * An activity that displays information about the application, as well as credits and licenses
 * for the open source libraries used in the project.
 */
public class AboutActivity extends ThemedActivity {

    private static final int LINKS_COLOUR = R.color.pink3;
    private static final String TRANSLATION_PREFIX = "   # ";
    private static final String TRANSLATION_SEPARATOR = " - ";

    /**
     * Color resource id for email and web links.
     */
    private int linkTextColour;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        Toolbar toolbar = (Toolbar) findViewById(R.id.general_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationIcon(R.drawable.ic_action_arrow_left);

        Resources res = getResources();
        linkTextColour = res.getColor(LINKS_COLOUR);

        TextView aboutTextView_1 = (TextView) findViewById(R.id.about_textview_part_1);
        final SpannableString s1 = new SpannableString(getAboutTextPart_1());
        Linkify.addLinks(s1, Linkify.WEB_URLS | Linkify.EMAIL_ADDRESSES);
        displayTextWithLinks(aboutTextView_1, s1);

        TextView aboutTextView_2 = (TextView) findViewById(R.id.about_textview_part_2);
        final SpannableString s2 = new SpannableString(getAboutTextPart_2());
        Linkify.addLinks(s2, Linkify.WEB_URLS);
        displayTextWithLinks(aboutTextView_2, s2);
    }

    /**
     * Concatenates basic app information string.
     */
    private String getAboutTextPart_1() {
        Resources res = getResources();

        String versionNumber = res.getString(R.string.about_version_number);
        String aboutText_1 = String.format(res.getString(R.string.about_1), versionNumber);
        aboutText_1 += res.getString(R.string.about_2);
        aboutText_1 += res.getString(R.string.about_3);
        aboutText_1 += res.getString(R.string.about_4);
        aboutText_1 += res.getString(R.string.about_5);
        aboutText_1 += res.getString(R.string.about_6);

        return aboutText_1;
    }

    /**
     * Concatenates credits and licenses string.
     */
    private String getAboutTextPart_2() {
        Resources res = getResources();

        int itemCount = 0;

        StringBuilder aboutText_2 = new StringBuilder("\n");
        aboutText_2.append(res.getString(R.string.credits_1));
        aboutText_2.append("\n\n");
        aboutText_2.append(getNextItemNumber(itemCount));
        itemCount++;
        aboutText_2.append(res.getString(R.string.credits_2));
        aboutText_2.append(res.getString(R.string.credits_3));
        aboutText_2.append(res.getString(R.string.credits_4));
        aboutText_2.append(res.getString(R.string.credits_5));
        aboutText_2.append(getNextItemNumber(itemCount));
        itemCount++;
        aboutText_2.append(res.getString(R.string.credits_6));
        aboutText_2.append(res.getString(R.string.credits_7));
        aboutText_2.append(res.getString(R.string.credits_8));
        aboutText_2.append(res.getString(R.string.credits_9));
        aboutText_2.append(getNextItemNumber(itemCount));
        itemCount++;
        aboutText_2.append(res.getString(R.string.credits_10));
        aboutText_2.append(res.getString(R.string.credits_11));
        aboutText_2.append(res.getString(R.string.credits_115));
        aboutText_2.append(res.getString(R.string.credits_12));
        aboutText_2.append(res.getString(R.string.credits_13));
        aboutText_2.append(getNextItemNumber(itemCount));
        itemCount++;
        aboutText_2.append(res.getString(R.string.credits_131));
        aboutText_2.append(res.getString(R.string.credits_132));
        aboutText_2.append(res.getString(R.string.credits_133));
        aboutText_2.append(res.getString(R.string.credits_134));
        aboutText_2.append(res.getString(R.string.credits_135));
        aboutText_2.append(getNextItemNumber(itemCount));
        itemCount++;
        aboutText_2.append(res.getString(R.string.credits_14));
        aboutText_2.append(res.getString(R.string.credits_15));
        aboutText_2.append(getNextItemNumber(itemCount));
        itemCount++;
        aboutText_2.append(res.getString(R.string.credits_16));
        aboutText_2.append(res.getString(R.string.credits_165));
        aboutText_2.append(res.getString(R.string.credits_17));
        aboutText_2.append(getNextItemNumber(itemCount));
        itemCount++;
        aboutText_2.append(res.getString(R.string.credits_18));
        aboutText_2.append("\n\n");

        String[] languages = res.getStringArray(R.array.translated_languages);
        String[] translators = res.getStringArray(R.array.translators);
        if (BuildConfig.DEBUG && languages.length != translators.length) {
            String errorMessage = "Number of languages: " + languages.length +
                    "; number of translators: " + translators.length;
            throw new AssertionError(errorMessage);
        }

        for (int i = 0; i < translators.length; i++) {
            String language = languages[i];
            String translator = translators[i];
            aboutText_2.append(TRANSLATION_PREFIX);
            aboutText_2.append(language);
            aboutText_2.append(TRANSLATION_SEPARATOR);
            aboutText_2.append(translator);
            aboutText_2.append("\n");
        }

        aboutText_2.append("\n");
        aboutText_2.append(res.getString(R.string.credits_19));
        aboutText_2.append(res.getString(R.string.credits_20));
        aboutText_2.append(getNextItemNumber(itemCount));
        aboutText_2.append(res.getString(R.string.credits_21));
        aboutText_2.append(res.getString(R.string.credits_22));
        aboutText_2.append(res.getString(R.string.credits_23));
        aboutText_2.append(res.getString(R.string.credits_24));

        return aboutText_2.toString();
    }

    private String getNextItemNumber(int itemCount) {
        return "(" + (itemCount + 1) + ") ";
    }

    /**
     * Sets text with clickable links in the specified TextView.
     */
    private void displayTextWithLinks(TextView textView, final SpannableString spannableString) {
        MovementMethod m1 = textView.getMovementMethod();
        if (!(m1 instanceof LinkMovementMethod))
            textView.setMovementMethod(LinkMovementMethod.getInstance());
        textView.setLinkTextColor(linkTextColour);
        textView.setText(spannableString);
    }

}