package com.end2end.popularmovies;

import android.app.Activity;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 * http://api.themoviedb.org/3/discover/movie?sort_by=popularity.desc&api_key=3f9ba3a7cf26793007032a520fbe0cff
 */
public class PopularMoviesMainActivityFragment extends Fragment {

    //before using this app put the API key in the variable (api_key) below
    final static String api_key = "PLACEHOLDER__FOR__ACTUAL__API__KEY";


    String sort_order = "popularity.desc";

    private final String LOG_TAG = PopularMoviesMainActivityFragment.class.getSimpleName();

    GridView gridview;
    List<MovieNode> moviePopularityArrayList;
    List<MovieNode> movieRatingArrayList;
    List<MovieNode> movieFavoriteArrayList;
    String saveInstanceFavoriteState_key = "MOVIES_FAVORITE_LIST";
    String saveInstancePopularityState_key = "MOVIES_POPULARITY_LIST";
    String saveInstanceRatingState_key = "MOVIES_Rating_LIST";
    String saveInstanceSortOrder_key = "SORT_ORDER";

    //references to our initial image
    private Integer[] mThumbsIds = { R.drawable.loading };

    public interface Callback {
        /**
         * DetailFragmentCallback for when an item has been selected.
         */
        public void onItemSelected(MovieNode movieNode, boolean ignore);
    }
    public PopularMoviesMainActivityFragment() {}

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Log.i(LOG_TAG, "MainActivityFragment onAttach");
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        Log.v(LOG_TAG, "In onCreate");

        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_popular_movies_main, container, false);
        gridview = (GridView) rootView.findViewById(R.id.gridview);

        if(savedInstanceState == null) {
            gridview.setAdapter(new ImageAdapter(getActivity(), mThumbsIds));
        } else {
            moviePopularityArrayList = (List<MovieNode>)savedInstanceState.get(saveInstancePopularityState_key);
            if (moviePopularityArrayList != null) {
                Log.v(LOG_TAG, "onCreateView moviePopularityArrayList.size()=" + " " + moviePopularityArrayList.size());
            }
            movieRatingArrayList = (List<MovieNode>)savedInstanceState.get(saveInstanceRatingState_key);
            if (movieRatingArrayList != null) {
                Log.v(LOG_TAG, "onCreateView movieRatingArrayList.size()=" + " " + movieRatingArrayList.size());
            }
            movieFavoriteArrayList = (List<MovieNode>)savedInstanceState.get(saveInstanceFavoriteState_key);
            if (movieFavoriteArrayList != null) {
                Log.v(LOG_TAG, "onCreateView movieFavoriteArrayList.size()=" + " " + movieFavoriteArrayList.size());
            }
            sort_order = savedInstanceState.getString(saveInstanceSortOrder_key);
        }

        return rootView;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        Log.i(LOG_TAG, "onActivityCreated");
    }

    @Override
    public void onStart(){
        super.onStart();
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        //sort_order = prefs.getString(getString(R.string.sort_criteria_key),
        //        getString(R.string.sort_criteria_default));

        if(moviePopularityArrayList != null) {
            Log.v(LOG_TAG, "onStart" + " " + moviePopularityArrayList.size());
        } else {
            Log.v(LOG_TAG, "onStart");
        }
        updateMovies("");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(LOG_TAG, "onResume");
    }


    @Override
    public void onCreateOptionsMenu(Menu menu,  MenuInflater inflater) {
        inflater.inflate(R.menu.menu_popular_movies_main_fragment, menu);

        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        //sort_order = prefs.getString(getString(R.string.sort_criteria_key),
        //                                getString(R.string.sort_criteria_default));
        Log.v(LOG_TAG, "onCreateOptionsMenu sort_order=" + sort_order);
        if (sort_order.equals("vote_average.desc")) {
            menu.findItem(R.id.sort_rating).setChecked(true);
        } else if (sort_order.equals("")){
            menu.findItem(R.id.sort_favorites).setChecked(true);
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.sort_popular:
                if (!item.isChecked()){
                    item.setChecked(true);
                    sort_order = "popularity.desc";
                    //moviePopularityArrayList = null;
                    //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
                   // SharedPreferences.Editor editor = prefs.edit();
                    //editor.putString(getString(R.string.sort_criteria_key), sort_order);
                    //editor.apply();
                    updateMovies("");
                }
                return true;
            case R.id.sort_rating:
                if (!item.isChecked()) {
                    item.setChecked(true);
                    //moviePopularityArrayList = null;
                    sort_order = "vote_average.desc";
                    //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
                    //SharedPreferences.Editor editor = prefs.edit();
                    //editor.putString(getString(R.string.sort_criteria_key), sort_order);
                    //editor.apply();
                    updateMovies("");
                }
                return true;
            case R.id.sort_favorites:
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
                String movieFavoriteString = prefs.getString(saveInstanceFavoriteState_key, null);
                if (movieFavoriteString != null){
                    if (!item.isChecked()) {
                        item.setChecked(true);
                        sort_order = "";
                        updateMovies(movieFavoriteString);
                    }
                    Toast.makeText(getActivity(), movieFavoriteString, Toast.LENGTH_SHORT).show();
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.i(LOG_TAG, "onPause");
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        Log.i(LOG_TAG, "onSaveInstanceState");
        outState.putParcelableArrayList(saveInstancePopularityState_key, (ArrayList<? extends Parcelable>) moviePopularityArrayList);
        List<MovieNode> testArrayList = (List<MovieNode>)outState.get(saveInstancePopularityState_key);
        if (testArrayList != null) Log.i(LOG_TAG, "onSaveInstanceState moviePopularityArrayList.size()=" + testArrayList.size());
        outState.putParcelableArrayList(saveInstanceRatingState_key, (ArrayList<? extends Parcelable>) movieRatingArrayList);
        List<MovieNode> test2ArrayList = (List<MovieNode>)outState.get(saveInstanceRatingState_key);
        if (test2ArrayList != null)
            Log.i(LOG_TAG, "onSaveInstanceState movieRatingArrayList.size()=" + test2ArrayList.size());
        outState.putParcelableArrayList(saveInstanceFavoriteState_key, (ArrayList<? extends Parcelable>) movieFavoriteArrayList);
        List<MovieNode> test3ArrayList = (List<MovieNode>)outState.get(saveInstanceFavoriteState_key);
        if (test3ArrayList != null)
            Log.i(LOG_TAG, "onSaveInstanceState movieFavoriteArrayList.size()=" + test3ArrayList.size());
        outState.putString(saveInstanceSortOrder_key, sort_order);
        String testSortOrder = outState.getString(saveInstanceSortOrder_key);
        Log.i(LOG_TAG, "onSaveInstanceState sort_order=" + testSortOrder + "----");

    }

    @Override
    public void onStop() {
        super.onStop();
        Log.i(LOG_TAG, "onStop");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.i(LOG_TAG, "onDestroyView");
    }


    @Override
    public void onDestroy() {
        Log.v(LOG_TAG, "In onDestroy of MainFragment");
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.i(LOG_TAG, "MainActivityFragment onDetach");
    }


    private void updateMovies(String listOfIds) {

        Log.v(LOG_TAG, "In updateMovies()");
        FetchMoviesTask movieTask = new FetchMoviesTask();
        if (sort_order.equals("popularity.desc")) {
            if (moviePopularityArrayList != null) {
                movieTask.drawPosters(moviePopularityArrayList);
            } else {
                movieTask.execute();
            }
        } else if (sort_order.equals("vote_average.desc")) {
            if (movieRatingArrayList != null) {
                movieTask.drawPosters(movieRatingArrayList);
            } else {
                movieTask.execute();
            }
        } else if (sort_order.equals("")) {
            if (movieFavoriteArrayList != null) {
                movieTask.drawPosters(movieFavoriteArrayList);
            } else {
                movieTask.execute(listOfIds);
            }
        }
    }
    public class FetchMoviesTask extends AsyncTask<String, Void, List<MovieNode>> {

        private final String LOG_TAG = FetchMoviesTask.class.getSimpleName();
        final static String BASE_URL = "https://api.themoviedb.org/3/";
        final String MOVIES_BASE_URL = BASE_URL + "discover/movie?";
        final String SORT_PARAM = "sort_by";
        final String API_KEY_PARAM = "api_key";
        final String ID_MOVIE_BASE_URL = BASE_URL + "movie?";

        @Override
        protected List<MovieNode> doInBackground(String... params) {

            // Will contain the raw JSON response as a string.
            String moviesJsonStr = null;
            Log.i(LOG_TAG, "params.length = " + params.length);
            if (params.length == 0) {
                Uri builtUri = Uri.parse(MOVIES_BASE_URL).buildUpon()
                        .appendQueryParameter(SORT_PARAM, sort_order)
                        .appendQueryParameter(API_KEY_PARAM, api_key)
                        .build();
                String uriString = builtUri.toString();
                moviesJsonStr = getJsonMovies(uriString);
            } else {
                String favoriteMoviesIds = params[0];
                //updateMovies(movieFavoriteString);
                String[] movieIds = favoriteMoviesIds.split("_");
                moviesJsonStr = "{\"results\":[";
                for (String movieId: movieIds) {
                    Uri builtUri = Uri.parse(ID_MOVIE_BASE_URL).buildUpon().appendPath(movieId)
                            .appendQueryParameter(SORT_PARAM, sort_order)
                            .appendQueryParameter(API_KEY_PARAM, api_key)
                            .build();
                    String uriString = builtUri.toString();
                    Log.i(LOG_TAG, "Favorite Id URL = " + uriString);
                    moviesJsonStr += getJsonMovies(uriString) + ",";
                }
                moviesJsonStr = moviesJsonStr.substring(0,moviesJsonStr.length() -1) + "]}";
                Log.i(LOG_TAG, "Total of Favorite JSON = " + moviesJsonStr);
            }

            try {
                return getMoviesDataFromJson(moviesJsonStr);
            }catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }
            return null;
        }

        private String getJsonMovies(String uriString){
            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            String moviesJsonStr = null;

            try {
                // Construct the URL for the OpenWeatherMap query
                // Possible parameters are avaiable at OWM's forecast API page, at
                // http://openweathermap.org/API#forecast

                    //URL url = new URL("http://api.openweathermap.org/data/2.5/forecast/daily?q=94043&mode=json&units=metric&cnt=7");
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
                Log.e("...MainFragment", "Error ", e);
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

        //private final int NUM_RESULTS_PER_CALL = 20;
        @Override
        protected void onPostExecute(final List<MovieNode> result) {

            drawPosters(result);
        }
        public void drawPosters(final List<MovieNode> result) {
            //String[] posterUrls = new String[NUM_RESULTS_PER_CALL];
            MovieNode firstNode = null;
            String[] posterUrls = new String[result.size()];
            if (sort_order.equals("popularity.desc")) {
                moviePopularityArrayList = result;
            } else if (sort_order.equals("vote_average.desc")) {
                movieRatingArrayList = result;
            } else if (sort_order.equals("")) {
                movieFavoriteArrayList = result;
            }
            if (result != null) {
                Log.v(LOG_TAG, Integer.toString(result.size()));
                for (int i = 0; i < result.size(); i++) {
                    MovieNode node = result.get(i);
                    if (i == 0) {
                        firstNode = node;
                    }
                    posterUrls[i] = node.getPosterPath();
                    Log.v(LOG_TAG, posterUrls[i]);
                }
                final ImageAdapter movieImageAdapter = new ImageAdapter(getActivity(), posterUrls);
                gridview.setAdapter(movieImageAdapter);

                gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        MovieNode movieNode = result.get(position);
                        Callback callback = (Callback) getActivity();
                        callback.onItemSelected(movieNode, false);

                        //Intent detailIntent = new Intent(getActivity(), PopularMoviesDetailActivity.class);
                        //detailIntent.putExtra("MovieNode", movieNode);
                        //startActivity(detailIntent);
                    }
                });
            }
            Callback callback = (Callback) getActivity();
            callback.onItemSelected(firstNode, true);
        }
        /**
         * Take the String representing the complete forecast in JSON Format and
         * pull out the data we need to construct the Strings needed for the wireframes.
         *
         * Fortunately parsing is easy:  constructor takes the JSON string and converts it
         * into an Object hierarchy for us.
         */
        private List<MovieNode> getMoviesDataFromJson(String moviesJsonStr)
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

            //String[] resultStrs = new String[numFields2Extract];
            List<MovieNode> movieArrayList = new ArrayList<MovieNode>();

            String BASE_POSTER_URL = "http://image.tmdb.org/t/p/";
            String POSTER_SIZE = "w185";

            for(int i = 0; i < movieArray.length(); i++) {

                // Get the JSON object representing the day
                JSONObject movie = movieArray.getJSONObject(i);

                String id = movie.getString(MOVIE_ID);
                String title = movie.getString(MOVIE_TITLE);
                String summary = movie.getString(MOVIE_SUMMARY);
                String release_date = movie.getString(MOVIE_RELEASE_DATE);
                String poster_path = BASE_POSTER_URL + POSTER_SIZE +  movie.getString(MOVIE_POSTER_PATH);
                String vote_average = movie.getString(MOVIE_VOTE_AVERAGE);

                MovieNode node = new MovieNode(id, title, summary, release_date, poster_path, vote_average);

                movieArrayList.add(node);
            }

            //for (int i = 0; i<moviePopularityArrayList.size(); i++) {
            //    MovieNode node = moviePopularityArrayList.get(i);

            //    Log.v(LOG_TAG, node.getPosterPath());
            //}

            return movieArrayList;

        }

    }


}
