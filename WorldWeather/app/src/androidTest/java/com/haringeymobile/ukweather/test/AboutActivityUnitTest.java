package com.haringeymobile.ukweather.test;

import android.content.Intent;
import android.test.ActivityUnitTestCase;
import android.test.suitebuilder.annotation.MediumTest;
import android.test.suitebuilder.annotation.SmallTest;
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
		Intent intent = new Intent(getInstrumentation().getTargetContext(),
				AboutActivity.class);
		startActivity(intent, null, null);
		aboutActivity = getActivity();
		aboutTextView_1 = (TextView) aboutActivity
				.findViewById(com.haringeymobile.ukweather.R.id.about_textview_part_1);
		aboutTextView_2 = (TextView) aboutActivity
				.findViewById(com.haringeymobile.ukweather.R.id.about_textview_part_2);
	}

	@MediumTest
	public void testPreConditions() {
		assertNotNull("Instance of AboutActivity is null", aboutActivity);
		assertNotNull("Text view for the first part is null", aboutTextView_1);
		assertNotNull("Text view for the first part is null", aboutTextView_2);
	}

	@SmallTest
	public void testTextViews_labelText() {
		final String expected_1 = aboutActivity
				.getString(com.haringeymobile.ukweather.R.string.about_text_part_1);
		final String actual_1 = aboutTextView_1.getText().toString();
		assertEquals("aboutTextView_1 contains wrong text", expected_1,
				actual_1);

		final String expected_2 = aboutActivity
				.getString(com.haringeymobile.ukweather.R.string.about_text_part_2);
		final String actual_2 = aboutTextView_2.getText().toString();
		assertEquals("aboutTextView_2 contains wrong text", expected_2,
				actual_2);
	}

}
