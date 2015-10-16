package com.end2end.popularmovies;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.end2end.popularmovies.data.MovieContract;
import com.end2end.popularmovies.data.MovieDBHelper;

import java.util.ArrayList;

/**
 * Created by lendevsanadmin on 10/12/2015.
 */
public class DBUtilities {

    public static boolean isMovieInDB(Context context, MovieNode movie) {

        Cursor resultCursor = context.getContentResolver().query(
                MovieContract.MovieEntry.CONTENT_URI,
                new String[]{MovieContract.MovieEntry.COLUMN_MOVIE_ID},
                MovieContract.MovieEntry.COLUMN_MOVIE_ID + "=" + movie.getId(),
                null, null
        );
        return resultCursor.moveToFirst() ?  true: false;
    }

    public static long insertMovieInDB(Context context, MovieNode movie) {
        ContentValues movieValueToInsert = new ContentValues();
        movieValueToInsert.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, movie.getId());
        movieValueToInsert.put(MovieContract.MovieEntry.COLUMN_MOVIE_TITLE, movie.getTitle());
        movieValueToInsert.put(MovieContract.MovieEntry.COLUMN_MOVIE_SUMMARY, movie.getSummary());
        movieValueToInsert.put(MovieContract.MovieEntry.COLUMN_MOVIE_RELEASE_DATE, movie.getReleaseDate());
        movieValueToInsert.put(MovieContract.MovieEntry.COLUMN_MOVIE_POSTER_PATH, movie.getPosterPath());
        movieValueToInsert.put(MovieContract.MovieEntry.COLUMN_MOVIE_VOTE_AVERAGE, movie.getVoteAverage());

        MovieDBHelper dbHelper = new MovieDBHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        long movieRowId = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, movieValueToInsert);
        return movieRowId;
    }

    public static void fetchMoviesFromDB(Context context, ArrayList<MovieNode> movieFavoriteArrayList) {

        MovieDBHelper dbHelper = new MovieDBHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] projection = {
                MovieContract.MovieEntry.COLUMN_MOVIE_ID,
                MovieContract.MovieEntry.COLUMN_MOVIE_TITLE,
                MovieContract.MovieEntry.COLUMN_MOVIE_SUMMARY,
                MovieContract.MovieEntry.COLUMN_MOVIE_RELEASE_DATE,
                MovieContract.MovieEntry.COLUMN_MOVIE_POSTER_PATH,
                MovieContract.MovieEntry.COLUMN_MOVIE_VOTE_AVERAGE
        };
        Cursor moviesCursor = db.query(
                MovieContract.MovieEntry.TABLE_NAME,  // Table to Query
                projection, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null, // columns to group by
                null, // columns to filter by row groups
                null  // sort order
        );
        if (moviesCursor.moveToFirst()) {
            do {
                MovieNode tempMovie = new MovieNode(moviesCursor.getString(0),
                        moviesCursor.getString(1),
                        moviesCursor.getString(2),
                        moviesCursor.getString(3),
                        moviesCursor.getString(4),
                        moviesCursor.getString(5));
                movieFavoriteArrayList.add(tempMovie);
            }while (moviesCursor.moveToNext());
        }
    }
}
