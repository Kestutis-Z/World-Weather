package com.haringeymobile.ukweather.test.data.objects;

import com.haringeymobile.ukweather.data.objects.Temperature;
import com.haringeymobile.ukweather.data.objects.TemperatureScale;

import android.test.suitebuilder.annotation.SmallTest;
import junit.framework.TestCase;

public class TemperatureConversionTest extends TestCase {

	private static final double TEMPERATURE_INPUT_IN_KELVINS = 300.0;
	private static final double ACCEPTABLE_ROUNDING_ERROR = 0.001;

	@SmallTest
	public void testTemperatureConversionFromKelvins_toCelcius() {
		double expectedCelsius = TEMPERATURE_INPUT_IN_KELVINS
				- Temperature.DIFFERENCE_BETWEEN_KELVIN_AND_CELCIUS;
		double actualCelsius = TemperatureScale.CELSIUS
				.convertTemperature(TEMPERATURE_INPUT_IN_KELVINS);
		assertTrue(
				"Incorrect conversion to the Celsius scale: expected "
						+ expectedCelsius + ", but received " + actualCelsius,
				Math.abs(expectedCelsius - actualCelsius) < ACCEPTABLE_ROUNDING_ERROR);
	}

	@SmallTest
	public void testTemperatureConversionFromKelvins_toFahrenheit() {
		double expectedFahrenheit = (TEMPERATURE_INPUT_IN_KELVINS - Temperature.DIFFERENCE_BETWEEN_KELVIN_AND_CELCIUS) * 9 / 5 + 32;
		double actualFahrenheit = TemperatureScale.FAHRENHEIT
				.convertTemperature(TEMPERATURE_INPUT_IN_KELVINS);
		assertTrue(
				"Incorrect conversion to the Fahrenheit scale: expected "
						+ expectedFahrenheit + ", but received "
						+ actualFahrenheit,
				Math.abs(expectedFahrenheit - actualFahrenheit) < ACCEPTABLE_ROUNDING_ERROR);
	}

}
