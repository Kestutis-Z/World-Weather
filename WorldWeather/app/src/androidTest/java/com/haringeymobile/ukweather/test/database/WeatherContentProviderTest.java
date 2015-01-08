/* Based on the NotePadProviderTest in legacy samples. */

package com.haringeymobile.ukweather.test.database;

import java.util.Arrays;

import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.test.ProviderTestCase2;
import android.test.mock.MockContentResolver;
import android.test.suitebuilder.annotation.MediumTest;
import android.test.suitebuilder.annotation.SmallTest;

import com.haringeymobile.ukweather.data.InitialCity;
import com.haringeymobile.ukweather.database.CityTable;
import com.haringeymobile.ukweather.database.WeatherContentProvider;

/** Tests the {@link com.haringeymobile.ukweather.database.WeatherContentProvider}. */
public class WeatherContentProviderTest extends
		ProviderTestCase2<WeatherContentProvider> {

	/** A URI that the provider does not offer, for testing error handling. */
	private static final Uri INVALID_URI = Uri.withAppendedPath(
			WeatherContentProvider.CONTENT_URI_CITY_RECORDS, "invalid");

	private MockContentResolver mockContentResolver;

	private SQLiteDatabase sqLiteDatabase;

	/** Test data, as an array of {@link com.haringeymobile.ukweather.test.database.CityWeatherInfo} instances. */
	private static final CityWeatherInfo[] TEST_DATA = {

			new CityWeatherInfo(100, "City 1", 11, 12, 13, "json current 1",
					"json daily 1", "json three hourly 1"),

			new CityWeatherInfo(200, "City 2", 11, 22, 33, "json current 2",
					"json daily 2", "json three hourly 2"),

			new CityWeatherInfo(300, "City 3", 10, 2, 13, "json current 3",
					"json daily 3", "json three hourly 3"),

			new CityWeatherInfo(400, "City 4", 1, 2, 3, "json current 4",
					"json daily 4", "json three hourly 4") };

	/**
	 * Constructor for the test case class. Calls the super constructor with the
	 * class name of the provider under test and the authority name of the
	 * provider.
	 */
	public WeatherContentProviderTest() {
		super(WeatherContentProvider.class, WeatherContentProvider.AUTHORITY);
	}

	/**
	 * Sets up the test environment before each test method. Creates a mock
	 * content resolver, gets the provider under test, and creates a new
	 * database for the provider.
	 */
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		mockContentResolver = getMockContentResolver();

		/*
		 * Gets a handle to the database underlying the provider. Gets the
		 * provider instance created in super.setUp(), gets the
		 * DatabaseOpenHelper for the provider, and gets a database object from
		 * the helper.
		 */
		sqLiteDatabase = getProvider().getOpenHelperForTest()
				.getWritableDatabase();
	}

	/**
	 * This method is called after each test method, to clean up the current
	 * fixture. Since this sample test case runs in an isolated context, no
	 * cleanup is necessary.
	 */
	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	/**
	 * Tests the provider's publicly available URIs. If the URI is not one that
	 * the provider understands, the provider should throw an exception. It also
	 * tests the provider's getType() method for each URI, which should return
	 * the MIME type associated with the URI.
	 */
	@SmallTest
	public void testUriAndGetType() {
		String mimeType = mockContentResolver
				.getType(WeatherContentProvider.CONTENT_URI_CITY_RECORDS);
		assertEquals(
				"mockContentResolver returned incorrect MIME type for the city table URI",
				WeatherContentProvider.CONTENT_TYPE_CITY_RECORDS, mimeType);

		// Creates a test URI with a pattern for city row ids. (The id doesn't
		// have to exist.)
		Uri noteIdUri = ContentUris.withAppendedId(
				WeatherContentProvider.CONTENT_URI_CITY_RECORDS, 1);

		mimeType = mockContentResolver.getType(noteIdUri);
		assertEquals(
				"mockContentResolver returned incorrect MIME type for the city table row URI",
				WeatherContentProvider.CONTENT_ITEM_TYPE_CITY_RECORDS, mimeType);

		mimeType = mockContentResolver.getType(INVALID_URI);
		assertNull("MIME type for invalid URI should be null", mimeType);
	}

	@SmallTest
	public void testQueriesOnCityWeatherUri_noTestDataInserted() {
		Cursor cursor = mockContentResolver.query(
				WeatherContentProvider.CONTENT_URI_CITY_RECORDS, null, null,
				null, null);
		int actualRowCount = cursor.getCount();
		int expectedRowCount = InitialCity.getInitialCityCount();

		assertEquals("No data inserted so far, but the cursor has "
				+ actualRowCount + " rows", expectedRowCount, actualRowCount);
	}

	@MediumTest
	public void testQueriesOnCityWeatherUri_testDataInserted() {
		insertInitialTestData();

		Cursor cursor = mockContentResolver.query(
				WeatherContentProvider.CONTENT_URI_CITY_RECORDS, null, null,
				null, null);
		int actualRowCount = cursor.getCount();
		int expectedRowCount = InitialCity.getInitialCityCount()
				+ TEST_DATA.length;

		assertEquals("Inserted the initial " + expectedRowCount
				+ " records, but the cursor has " + actualRowCount + " rows",
				expectedRowCount, actualRowCount);
	}

	/** Inserts initial test data. */
	private void insertInitialTestData() {
		for (int index = 0; index < TEST_DATA.length; index++) {
			sqLiteDatabase.insertOrThrow(CityTable.TABLE_CITIES, null,
					TEST_DATA[index].getContentValues());
		}
	}

	@SmallTest
	public void testQueriesOnCityWeatherUri_projectionColumns() {

		final String[] TEST_PROJECTION = { CityTable.COLUMN_CITY_ID,
				CityTable.COLUMN_NAME,
				CityTable.COLUMN_LAST_QUERY_TIME_FOR_CURRENT_WEATHER,
				CityTable.COLUMN_CACHED_JSON_CURRENT };

		Cursor cursor = mockContentResolver.query(
				WeatherContentProvider.CONTENT_URI_CITY_RECORDS,
				TEST_PROJECTION, null, null, null);
		int expectedColumnCount = TEST_PROJECTION.length;
		int actualColumnCount = cursor.getColumnCount();

		assertEquals("Projection contains " + expectedColumnCount
				+ " columns, but the cursor has " + actualColumnCount,
				expectedColumnCount, actualColumnCount);

		// Asserts that the names of the columns in the cursor and in the
		// projection are the same, and in the same order.
		assertEquals(TEST_PROJECTION[0], cursor.getColumnName(0));
		assertEquals(TEST_PROJECTION[1], cursor.getColumnName(1));
		assertEquals(TEST_PROJECTION[2], cursor.getColumnName(2));
		assertEquals(TEST_PROJECTION[3], cursor.getColumnName(3));
	}

	@MediumTest
	public void testQueriesOnCityWeatherUri_queryRows() {
		insertInitialTestData();

		final String[] TEST_PROJECTION = { CityTable.COLUMN_CITY_ID,
				CityTable.COLUMN_NAME,
				CityTable.COLUMN_LAST_QUERY_TIME_FOR_CURRENT_WEATHER,
				CityTable.COLUMN_CACHED_JSON_CURRENT };
		final String ID_SELECTION = CityTable.COLUMN_CITY_ID + " = " + "?";
		final String TEST_SELECTION = ID_SELECTION + " OR " + ID_SELECTION
				+ " OR " + ID_SELECTION;
		final String[] SELECTION_ARGS = { "100", "400", "200" };
		final String SORT_ORDER = CityTable.COLUMN_CITY_ID + " ASC";

		Cursor cursor = mockContentResolver.query(
				WeatherContentProvider.CONTENT_URI_CITY_RECORDS,
				TEST_PROJECTION, TEST_SELECTION, SELECTION_ARGS, SORT_ORDER);
		int expectedRowCount = SELECTION_ARGS.length;
		int actualRowCount = cursor.getCount();

		assertEquals(
				"Passed "
						+ expectedRowCount
						+ " city Ids as selection arguments to the query, but the cursor has "
						+ actualRowCount + " rows", expectedRowCount,
				actualRowCount);

		String[] SELECTION_ARGS_SORTED = Arrays.copyOf(SELECTION_ARGS,
				SELECTION_ARGS.length);
		Arrays.sort(SELECTION_ARGS_SORTED);
		int CITY_ID_COLUMN_INDEX_IN_TEST_QUERY = 0;

		int cursorPosition = 0;
		while (cursor.moveToNext()) {
			String expectedCityId = SELECTION_ARGS_SORTED[cursorPosition];
			String actualCityId = cursor
					.getString(CITY_ID_COLUMN_INDEX_IN_TEST_QUERY);

			assertEquals("Incorrect city ID value at corsor position: "
					+ cursorPosition, expectedCityId, actualCityId);

			cursorPosition++;
		}

		int expectedTestedRowCount = SELECTION_ARGS_SORTED.length;
		int actualTestedRowCount = cursorPosition;
		assertEquals("Not all rows returned by the cursor were tested",
				expectedTestedRowCount, actualTestedRowCount);
	}

	@MediumTest
	public void testRecordDeletions() {
		final String SELECTION_COLUMNS = CityTable.COLUMN_CITY_ID + " = " + "?";
		final String[] SELECTION_ARGS = { "100" };

		int rowsDeletedBeforeDataInsertion = mockContentResolver.delete(
				WeatherContentProvider.CONTENT_URI_CITY_RECORDS,
				SELECTION_COLUMNS, SELECTION_ARGS);

		assertEquals(
				"No rows should have been deleted since test data is not yet inserted",
				0, rowsDeletedBeforeDataInsertion);

		insertInitialTestData();

		int rowsDeletedAfterDataInsertion = mockContentResolver.delete(
				WeatherContentProvider.CONTENT_URI_CITY_RECORDS,
				SELECTION_COLUMNS, SELECTION_ARGS);

		assertEquals("Exactly one row should have been deleted)", 1,
				rowsDeletedAfterDataInsertion);
	}

	@MediumTest
	public void testRecordUpdates() {
		final String SELECTION_COLUMNS = CityTable.COLUMN_CITY_ID + " = " + "?";
		final String[] SELECTION_ARGS = { "100" };

		ContentValues newTestValues = new ContentValues();
		int NEW_CITY_ID = 1000;
		newTestValues.put(CityTable.COLUMN_CITY_ID, NEW_CITY_ID);

		int rowsUpdatedBeforeDataInsertion = mockContentResolver.update(
				WeatherContentProvider.CONTENT_URI_CITY_RECORDS, newTestValues,
				SELECTION_COLUMNS, SELECTION_ARGS);

		assertEquals(
				"No rows should have been updated since test data is not yet inserted",
				0, rowsUpdatedBeforeDataInsertion);

		insertInitialTestData();

		int rowsUpdatedAfterDataInsertion = mockContentResolver.update(
				WeatherContentProvider.CONTENT_URI_CITY_RECORDS, newTestValues,
				SELECTION_COLUMNS, SELECTION_ARGS);

		assertEquals("Exactly one row should have been updated)", 1,
				rowsUpdatedAfterDataInsertion);
	}

}
