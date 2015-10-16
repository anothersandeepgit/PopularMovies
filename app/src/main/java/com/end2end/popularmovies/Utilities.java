package com.end2end.popularmovies;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.end2end.popularmovies.PopularMoviesMainActivityFragment.Callback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

/**
 * Created by lendevsanadmin on 10/10/2015.
 */
public class Utilities {

    private  static final String LOG_TAG = Utilities.class.getSimpleName();

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static String getJsonMovies(String uriString){
        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        String moviesJsonStr = null;

        try {
            URL url = new URL(uriString);
            Log.v(LOG_TAG, "Built URI: " + uriString);
            // Create the request to OpenWeatherMap, and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setRequestProperty("Accept", "application/json");
            urlConnection.connect();
            Log.v(LOG_TAG, "created connection");
            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            Log.v(LOG_TAG, "created buffer");
            if (inputStream == null) {
                // Nothing to do.
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line + "\n");
            }
            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return null;
            }
            moviesJsonStr = buffer.toString();
            Log.v(LOG_TAG, "Movies JSON string: " + moviesJsonStr);
            return moviesJsonStr;
        } catch (IOException e) {
            // If the code didn't successfully get the movies data, there's no point in attemping
            // to parse it.
            return null;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e("...MainFragment", "Error closing stream", e);
                }
            }
        }
    }

    /**
     * Take the String representing the complete movie list in JSON Format and
     * pull out the data we need to construct the Strings needed for the wireframes.
     *
     * Fortunately parsing is easy:  constructor takes the JSON string and converts it
     * into an Object hierarchy for us.
     */
    public static void getMoviesDataFromJson(String moviesJsonStr, List<MovieNode> movieArrayList)
            throws JSONException {

        // These are the names of the JSON objects that need to be extracted.
        //final int numFields2Extract = 5;
        final String MOVIE_ID = "id";
        final String MOVIE_RESULT = "results";
        final String MOVIE_TITLE = "original_title";
        final String MOVIE_SUMMARY = "overview";
        final String MOVIE_RELEASE_DATE = "release_date";
        final String MOVIE_POSTER_PATH = "poster_path";
        final String MOVIE_VOTE_AVERAGE = "vote_average";

        JSONObject moviesJson = new JSONObject(moviesJsonStr);
        JSONArray movieArray = moviesJson.getJSONArray(MOVIE_RESULT);

        String BASE_POSTER_URL = "http://image.tmdb.org/t/p/";
        String POSTER_SIZE = "w185";

        for (int i = 0; i < movieArray.length(); i++) {

            // Get the JSON object representing the day
            JSONObject movie = movieArray.getJSONObject(i);

            String id = movie.getString(MOVIE_ID);
            String title = movie.getString(MOVIE_TITLE);
            String summary = movie.getString(MOVIE_SUMMARY);
            String release_date = movie.getString(MOVIE_RELEASE_DATE);
            String poster_path = BASE_POSTER_URL + POSTER_SIZE + movie.getString(MOVIE_POSTER_PATH);
            String vote_average = movie.getString(MOVIE_VOTE_AVERAGE);
            MovieNode node = new MovieNode(id, title, summary, release_date, poster_path, vote_average);
            movieArrayList.add(node);
        }
    }

    public static void drawPosters(final Context context, GridView gridview, final List<MovieNode> result, boolean isSaveInstanceState) {

        if (result != null) {
            MovieNode firstNode = null;
            String[] posterUrls = new String[result.size()];
            for (int i = 0; i < result.size(); i++) {
                MovieNode node = result.get(i);
                if (i == 0) {
                    firstNode = node;
                }
                posterUrls[i] = node.getPosterPath();
            }
            final ImageAdapter movieImageAdapter = new ImageAdapter(context, posterUrls);
            gridview.setAdapter(movieImageAdapter);
            gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    MovieNode movieNode = result.get(position);
                    Callback callback = (Callback) context;
                    callback.onItemSelected(movieNode, false);
                }
            });
            if (!isSaveInstanceState) {
                Callback callback = (Callback) context;
                callback.onItemSelected(firstNode, true);
            }
        } else if (!Utilities.isNetworkAvailable(context)){
            Integer[] netWorkProblemIds = { R.drawable.connection_issue };
            gridview.setAdapter(new ImageAdapter(context, netWorkProblemIds));
            Callback callback = (Callback) context;
            callback.onItemSelected(null, true);
        }
    }

}
