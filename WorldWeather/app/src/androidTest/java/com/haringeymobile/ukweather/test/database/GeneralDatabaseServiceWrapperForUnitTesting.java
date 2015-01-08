package com.haringeymobile.ukweather.test.database;

import java.util.concurrent.CountDownLatch;

import android.content.Intent;

import com.haringeymobile.ukweather.database.GeneralDatabaseService;

/**
 * An intent service that will be tested instead of it's superclass.
 * <p>
 * More info:
 * http://ics.upjs.sk/~novotnyr/blog/1160/one-does-not-simply-test-the
 * -intentservices.
 */
public class GeneralDatabaseServiceWrapperForUnitTesting extends
		GeneralDatabaseService {

	private CountDownLatch countDownLatch;

	@Override
	protected void onHandleIntent(Intent intent) {
		super.onHandleIntent(intent);
		countDownLatch.countDown();
	}

	public void setLatch(CountDownLatch latch) {
		this.countDownLatch = latch;
	}

}
