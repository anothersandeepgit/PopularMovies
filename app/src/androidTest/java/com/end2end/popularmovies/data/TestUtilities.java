package com.end2end.popularmovies.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.test.AndroidTestCase;

import java.util.Map;
import java.util.Set;

/**
 * Created by lendevsanadmin on 10/10/2015.
 */
public class TestUtilities extends AndroidTestCase {
    public static ContentValues createMovieVaues() {
        // Create a new map of values, where column names are the keys
        ContentValues testValues = new ContentValues();
        testValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, "135397");
        testValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_POSTER_PATH, "/jjBgi2r5cRt36xF6iNUEhzscEcb.jpg");
        testValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_RELEASE_DATE, "2015-06-12");
        testValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_SUMMARY, "Twenty-two years after the events of Jurassic Park, Isla Nublar now features a fully functioning dinosaur theme park, Jurassic World, as originally envisioned by John Hammond.");
        testValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_TITLE, "Jurassic World");
        testValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_VOTE_AVERAGE, "7.0");
        testValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_RELEASE_DATE, "2015-06-12");
        return testValues;
    }

    public static void validateCurrentRecord(String error, Cursor valueCursor, ContentValues expectedValues) {
        Set<Map.Entry<String, Object>> valueSet = expectedValues.valueSet();
        for (Map.Entry<String, Object> entry : valueSet) {
            String columnName = entry.getKey();
            int idx = valueCursor.getColumnIndex(columnName);
            assertFalse("Column '" + columnName + "' not found. " + error, idx == -1);
            String expectedValue = entry.getValue().toString();
            assertEquals("Value '" + entry.getValue().toString() +
                    "' did not match the expected value '" +
                    expectedValue + "'. " + error, expectedValue, valueCursor.getString(idx));
        }
    }
}
