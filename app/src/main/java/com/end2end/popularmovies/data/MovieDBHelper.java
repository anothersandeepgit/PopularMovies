package com.end2end.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by lendevsanadmin on 9/26/2015.
 */
public class MovieDBHelper extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 1;

    static final String DATABASE_NAME = "movie.db";

    public MovieDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        final String SQL_CREATE_MOVIE_TABLE = "CREATE TABLE " + MovieContract.MovieEntry.TABLE_NAME + " (" +
                MovieContract.MovieEntry._ID + " INTEGER PRIMARY KEY," +
                MovieContract.MovieEntry.COLUMN_MOVIE_ID + " TEXT UNIQUE NOT NULL, " +
                MovieContract.MovieEntry.COLUMN_MOVIE_TITLE + " TEXT UNIQUE NOT NULL, " +
                MovieContract.MovieEntry.COLUMN_MOVIE_SUMMARY + " TEXT NOT NULL, " +
                MovieContract.MovieEntry.COLUMN_MOVIE_RELEASE_DATE + " TEXT NOT NULL, " +
                MovieContract.MovieEntry.COLUMN_MOVIE_POSTER_PATH + " TEXT NOT NULL, " +
                MovieContract.MovieEntry.COLUMN_MOVIE_VOTE_AVERAGE + " TEXT NOT NULL " +
                " );";

        final String SQL_CREATE_TRAILER_TABLE = "CREATE TABLE " + MovieContract.TrailerEntry.TABLE_NAME + " (" +

                MovieContract.TrailerEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +

                MovieContract.TrailerEntry.COLUMN_MOVIE_KEY + " INTEGER NOT NULL, " +
                MovieContract.TrailerEntry.COLUMN_TRAILER_NAME + " TEXT NOT NULL, " +
                MovieContract.TrailerEntry.COLUMN_TRAILER_YOUTUBE_PATH + " TEXT NOT NULL, " +

                " FOREIGN KEY (" + MovieContract.TrailerEntry.COLUMN_MOVIE_KEY + ") REFERENCES " +
                MovieContract.MovieEntry.TABLE_NAME + " (" + MovieContract.MovieEntry._ID + ") " +
                " );";

        final String SQL_CREATE_REVIEW_TABLE = "CREATE TABLE " + MovieContract.ReviewEntry.TABLE_NAME + " (" +

                MovieContract.ReviewEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +

                MovieContract.ReviewEntry.COLUMN_MOVIE_KEY + " INTEGER NOT NULL, " +
                MovieContract.ReviewEntry.COLUMN_REVIEW_AUTHOR + " TEXT NOT NULL, " +
                MovieContract.ReviewEntry.COLUMN_REVIEW_CONTENT + " TEXT NOT NULL, " +

                " FOREIGN KEY (" + MovieContract.TrailerEntry.COLUMN_MOVIE_KEY + ") REFERENCES " +
                MovieContract.MovieEntry.TABLE_NAME + " (" + MovieContract.MovieEntry._ID + ") " +
                " );";

        sqLiteDatabase.execSQL(SQL_CREATE_MOVIE_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_TRAILER_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_REVIEW_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        // Note that this only fires if you change the version number for your database.
        // It does NOT depend on the version number for your application.
        // If you want to update the schema without wiping data, commenting out the next 2 lines
        // should be your top priority before modifying this method.
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieContract.MovieEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieContract.TrailerEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieContract.ReviewEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
