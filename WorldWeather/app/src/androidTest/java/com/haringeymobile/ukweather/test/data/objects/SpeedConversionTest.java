package com.haringeymobile.ukweather.test.data.objects;

import junit.framework.TestCase;
import android.test.suitebuilder.annotation.SmallTest;

import com.haringeymobile.ukweather.data.objects.WindSpeedMeasurementUnit;

public class SpeedConversionTest extends TestCase {

	private static final double SPEED_INPUT_IN_METERS_PER_SECOND = 50.0;
	private static final double ACCEPTABLE_ROUNDING_ERROR = 0.001;

	@SmallTest
	public void testSpeedConversionFromMetersPerSecond_toKilometersPerHour() {
		double expectedKph = SPEED_INPUT_IN_METERS_PER_SECOND * 3.6;
		double actualKph = WindSpeedMeasurementUnit.KILOMETERS_PER_HOUR
				.convertSpeed(SPEED_INPUT_IN_METERS_PER_SECOND);
		assertTrue("Incorrect conversion to kilometers per hour: expected "
				+ expectedKph + ", but received " + actualKph,
				Math.abs(expectedKph - actualKph) < ACCEPTABLE_ROUNDING_ERROR);
	}

	@SmallTest
	public void testSpeedConversionFromMetersPerSecond_toMilesPerHour() {
		double expectedMph = SPEED_INPUT_IN_METERS_PER_SECOND * 3600 / 1609.34;
		double actualMph = WindSpeedMeasurementUnit.MILES_PER_HOUR
				.convertSpeed(SPEED_INPUT_IN_METERS_PER_SECOND);
		assertTrue("Incorrect conversion to miles per hour: expected "
				+ expectedMph + ", but received " + actualMph,
				Math.abs(expectedMph - actualMph) < ACCEPTABLE_ROUNDING_ERROR);
	}

}
