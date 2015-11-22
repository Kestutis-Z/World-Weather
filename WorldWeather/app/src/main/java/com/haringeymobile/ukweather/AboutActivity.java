package com.haringeymobile.ukweather;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.method.MovementMethod;
import android.text.util.Linkify;
import android.widget.TextView;

/**
 * An activity that displays information about the application, as well as credits and licenses
 * for the open source libraries used in the project.
 */
public class AboutActivity extends ThemedActivity {

    private static final int LINKS_COLOUR = R.color.pink3;

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
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationIcon(R.drawable.ic_action_arrow_left);

        Resources res = getResources();
        linkTextColour = res.getColor(LINKS_COLOUR);

        TextView aboutTextView_1 = (TextView) findViewById(R.id.about_textview_part_1);
        final SpannableString s1 = new SpannableString(res.getText(R.string.about_text_part_1));
        Linkify.addLinks(s1, Linkify.WEB_URLS | Linkify.EMAIL_ADDRESSES);
        displayTextWithLinks(aboutTextView_1, s1);

        TextView aboutTextView_2 = (TextView) findViewById(R.id.about_textview_part_2);
        final SpannableString s2 = new SpannableString(res.getText(R.string.about_text_part_2));
        Linkify.addLinks(s2, Linkify.WEB_URLS);
        displayTextWithLinks(aboutTextView_2, s2);
    }

    /**
     * Sets text with clickable links in the specified TextView.
     */
    private void displayTextWithLinks(TextView textView, final SpannableString spannableString) {
        MovementMethod m1 = textView.getMovementMethod();
        if ((m1 == null) || !(m1 instanceof LinkMovementMethod))
            textView.setMovementMethod(LinkMovementMethod.getInstance());
        textView.setLinkTextColor(linkTextColour);
        textView.setText(spannableString);
    }

}