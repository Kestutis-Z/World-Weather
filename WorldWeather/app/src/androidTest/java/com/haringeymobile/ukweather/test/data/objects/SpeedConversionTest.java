package com.haringeymobile.ukweather.test.data.objects;

import android.test.suitebuilder.annotation.SmallTest;

import com.haringeymobile.ukweather.data.objects.WindSpeedMeasurementUnit;

import junit.framework.TestCase;

public class SpeedConversionTest extends TestCase {

    private static final double SPEED_INPUT_IN_METERS_PER_SECOND = 50.0;
    private static final double ACCEPTABLE_ROUNDING_ERROR = 0.001;

    private static final double[] ACTUAL_INPUT_IN_METERS_PER_SECOND = {0.2, 2.85, 10.0, 50.0};
    private static final long[] EXPECTED_OUTPUT_IN_BEAUFORT_FORCE = {0l, 2l, 5l, 12l};

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

    @SmallTest
    public void testSpeedConversionFromMetersPerSecond_toBeaufortForce() {
        for (int i = 0; i < ACTUAL_INPUT_IN_METERS_PER_SECOND.length; i++) {
            long expectedForce = EXPECTED_OUTPUT_IN_BEAUFORT_FORCE[i];
            long actualForce = Math.round(WindSpeedMeasurementUnit.BEAUFORT_SCALE.convertSpeed(
                    ACTUAL_INPUT_IN_METERS_PER_SECOND[i]));
            String failedAssertMessage = "Incorrect conversion to Beaufort scale: expected Force "
                    + expectedForce + ", but received Force " + actualForce;
            assertEquals(failedAssertMessage, expectedForce, actualForce);
        }
    }

}