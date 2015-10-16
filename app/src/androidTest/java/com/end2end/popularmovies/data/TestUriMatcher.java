package com.end2end.popularmovies.data;

import android.content.UriMatcher;
import android.net.Uri;
import android.test.AndroidTestCase;

/**
 * Created by lendevsanadmin on 10/10/2015.
 */
public class TestUriMatcher extends AndroidTestCase {
    private static final long MOVIE_QUERY = 135397;
    private static final long TEST_DATE = 1419033600L;  // December 20th, 2014
    private static final long TEST_LOCATION_ID = 10L;

    // content://com.end2end.popularmovies/movie"
    private static final Uri TEST_Movie_DIR = MovieContract.MovieEntry.CONTENT_URI;
    private static final Uri TEST_TRAILER_FOR_MOVIE_DIR = MovieContract.TrailerEntry.buildTrailerForMovie(MOVIE_QUERY);
    private static final Uri TEST_REVIEW_FOR_MOVIE_DIR = MovieContract.ReviewEntry.buildReviewForMovie(MOVIE_QUERY);
    // content://com.example.android.sunshine.app/location"
    private static final Uri TEST_REVIEW_DIR = MovieContract.ReviewEntry.CONTENT_URI;
    private static final Uri TEST_TRAILER_DIR = MovieContract.TrailerEntry.CONTENT_URI;

    /*
        Students: This function tests that your UriMatcher returns the correct integer value
        for each of the Uri types that our ContentProvider can handle.  Uncomment this when you are
        ready to test your UriMatcher.
     */
    public void testUriMatcher() {
        UriMatcher testMatcher = MovieProvider.buildUriMatcher();

        assertEquals("Error: The MOVIE URI was matched incorrectly.",
                testMatcher.match(TEST_Movie_DIR), MovieProvider.MOVIE);
        assertEquals("Error: The TRAILER FOR MOVIE URI was matched incorrectly.",
                testMatcher.match(TEST_TRAILER_FOR_MOVIE_DIR), MovieProvider.TRAILER_FOR_MOVIE);
        assertEquals("Error: The REVIEW FOR MOVIE URI was matched incorrectly.",
                testMatcher.match(TEST_REVIEW_FOR_MOVIE_DIR), MovieProvider.REVIEW_FOR_MOVIE);
        assertEquals("Error: The TRAILER URI was matched incorrectly.",
                testMatcher.match(TEST_TRAILER_DIR), MovieProvider.TRAILER);
        assertEquals("Error: The REVIEW URI was matched incorrectly.",
                testMatcher.match(TEST_REVIEW_DIR), MovieProvider.REVIEW);
    }
}
