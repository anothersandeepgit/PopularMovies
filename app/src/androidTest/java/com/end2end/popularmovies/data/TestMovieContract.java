package com.end2end.popularmovies.data;

import android.net.Uri;
import android.test.AndroidTestCase;

/**
 * Created by lendevsanadmin on 10/10/2015.
 */
public class TestMovieContract extends AndroidTestCase {

    // intentionally includes a slash to make sure Uri is getting quoted correctly
    private static final int TEST_MOVIE_ID = 286217;

    /*
        Students: Uncomment this out to test your weather location function.
     */
    public void testBuildWeatherLocation() {
        Uri movieUri = MovieContract.MovieEntry.buildMovieUri(TEST_MOVIE_ID);
        assertNotNull("Error: Null Uri returned.  You must fill-in buildMovieUri in " +
                        "MovieContract.",
                movieUri);
        assertEquals("Error: Weather location not properly appended to the end of the Uri",
                Integer.toString(TEST_MOVIE_ID), movieUri.getLastPathSegment());
        assertEquals("Error: Weather location Uri doesn't match our expected result",
                movieUri.toString(),
                "content://com.end2end.popularmovies/movie/286217");
    }
}
