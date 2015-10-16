package com.end2end.popularmovies;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.GridView;

import org.json.JSONException;

import java.util.List;

/**
 * Created by lendevsanadmin on 10/10/2015.
 */
public class FetchMoviesTask extends AsyncTask<String, Void, Void> {

    //before using this app put the API key in the variable (api_key) below
    final static String api_key = "PLACEHOLDER__FOR__ACTUAL__API__KEY";

    private final String LOG_TAG = FetchMoviesTask.class.getSimpleName();
    final static String BASE_URL = "https://api.themoviedb.org/3/";
    private final String MOVIES_BASE_URL = BASE_URL + "discover/movie?";
    private final String SORT_PARAM = "sort_by";
    private final String API_KEY_PARAM = "api_key";
    private final String ID_MOVIE_BASE_URL = BASE_URL + "movie?";

    private List<MovieNode> movieArrayList = null;
    private String sort_order = "";
    private Context context;
    private GridView gridview;

    FetchMoviesTask(List<MovieNode> movieArrayList, String sort_order, Context context, GridView gridview){
        super();
        this.movieArrayList = movieArrayList;
        this.sort_order = sort_order;
        this.context = context;
        this.gridview = gridview;
    }

    @Override
    protected Void doInBackground(String... params) {

        // Will contain the raw JSON response as a string.
        String moviesJsonStr = null;
        Log.i(LOG_TAG, "params.length = " + params.length);

        if (params.length == 0) {
            Uri builtUri = Uri.parse(MOVIES_BASE_URL).buildUpon()
                    .appendQueryParameter(SORT_PARAM, sort_order)
                    .appendQueryParameter(API_KEY_PARAM, api_key)
                    .build();
            String uriString = builtUri.toString();
            Log.i(LOG_TAG, "URI MOVIE = " + uriString);
            moviesJsonStr = Utilities.getJsonMovies(uriString);
        }
        moviesJsonStr = moviesJsonStr.substring(0,moviesJsonStr.length() -1) + "]}";
        try {
            Utilities.getMoviesDataFromJson(moviesJsonStr, movieArrayList);
        }catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void placeholder) {
        Utilities.drawPosters(context, gridview, movieArrayList, false);
    }
}
