package com.haringeymobile.ukweather.test;

import android.content.Intent;
import android.test.ActivityUnitTestCase;
import android.test.suitebuilder.annotation.MediumTest;
import android.widget.TextView;

import com.haringeymobile.ukweather.AboutActivity;

public class AboutActivityUnitTest extends ActivityUnitTestCase<AboutActivity> {

    private AboutActivity aboutActivity;
    private TextView aboutTextView_1;
    private TextView aboutTextView_2;

    public AboutActivityUnitTest() {
        super(AboutActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        Intent intent = new Intent(getInstrumentation().getTargetContext(), AboutActivity.class);
        startActivity(intent, null, null);
        aboutActivity = getActivity();
        aboutTextView_1 = (TextView) aboutActivity.findViewById(com.haringeymobile.ukweather.R.id.
                about_textview_part_1);
        aboutTextView_2 = (TextView) aboutActivity.findViewById(com.haringeymobile.ukweather.R.id.
                about_textview_part_2);
    }

    @MediumTest
    public void testPreConditions() {
        assertNotNull("Instance of AboutActivity is null", aboutActivity);
        assertNotNull("Text view for the first part is null", aboutTextView_1);
        assertNotNull("Text view for the first part is null", aboutTextView_2);
    }

}