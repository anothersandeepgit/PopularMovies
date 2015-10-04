package com.end2end.popularmovies;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

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
import java.util.concurrent.CountDownLatch;

/**
 * Created by lendevsanadmin on 8/22/2015.
 */
public class PopularMoviesDetailFragment extends Fragment {

    public static final String MOVIE_PASSED = "movie_passed";

    private static final String LOG_TAG = PopularMoviesDetailFragment.class.getSimpleName();
    String jsonResult;
    String saveInstanceFavoriteState_key = "MOVIES_FAVORITE_LIST";
    MovieNode  movie;
    MovieNode movie2;
    MovieNode movie3;
    List<MovieNode> movieFavoriteArrayList;

    ImageView posterImageView;
    TextView titleTextView;
    TextView summaryTextView;
    TextView releaseDateTextView;
    TextView voteAverageTextView;
    LinearLayout trailersLL;
    LinearLayout reviewsLL;
    Button favoriteButton;

    View rootView;

    CountDownLatch latch = null;
    boolean dynamicViewsAdded = false;

    public PopularMoviesDetailFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Bundle arguments = getArguments();
        if (arguments != null) {
            movie = arguments.getParcelable(PopularMoviesDetailFragment.MOVIE_PASSED);
        }

        rootView = inflater.inflate(R.layout.fragment_popular_movies_detail, container, false);
        posterImageView = (ImageView) rootView.findViewById(R.id.posterImageView);
        titleTextView = (TextView) rootView.findViewById(R.id.titleTextView);
        summaryTextView = (TextView) rootView.findViewById(R.id.summaryTextView);
        releaseDateTextView = (TextView) rootView.findViewById(R.id.releaseDateTextView);
        voteAverageTextView = (TextView) rootView.findViewById(R.id.voteAverageTextView);
        trailersLL = (LinearLayout) rootView.findViewById(R.id.layout_trailers);
        reviewsLL = (LinearLayout) rootView.findViewById(R.id.layout_reviews);
        favoriteButton = (Button) rootView.findViewById(R.id.favoriteButton);
        favoriteButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
                String movieFavoriteString = prefs.getString(saveInstanceFavoriteState_key, null);


                SharedPreferences.Editor editor = prefs.edit();
                String movieIds = prefs.getString(saveInstanceFavoriteState_key, "");
                String toAdd = movie.getId();
                Log.d(LOG_TAG, "Favorite ID = " + toAdd);
                if (!movieIds.contains(movie.getId())) {
                    if (!movieIds.equals("")){
                        toAdd = "_" + toAdd;
                    }
                    editor.putString(saveInstanceFavoriteState_key, movieIds + toAdd);
                    Toast.makeText(getActivity(), movieIds + toAdd, Toast.LENGTH_SHORT).show();
                    editor.apply();
                }

            }
        });

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (movie == null) {
            Intent intent = getActivity().getIntent();
            //String forecastStr = intent.getStringExtra(Intent.EXTRA_TEXT);
            movie = intent.getParcelableExtra("MovieNode");
        }
        String title = movie.getTitle();
        titleTextView.setText("Title: " + title);
        Log.v(LOG_TAG, title);

        String releaseDate = movie.getReleaseDate();
        releaseDateTextView.setText("Release Date: " + releaseDate);
        Log.v(LOG_TAG, releaseDate);

        String posterPath = movie.getPosterPath();
        Log.v(LOG_TAG, posterPath);
        Picasso.with(getActivity()).load(posterPath).into(posterImageView);

        String voteAverage = movie.getVoteAverage();
        voteAverageTextView.setText("Vote Average: " + voteAverage);
        Log.v(LOG_TAG, voteAverage);

        String summary = movie.getSummary();
        summaryTextView.setText(summary);
        Log.v(LOG_TAG, summary);

        String id = movie.getId();
        Log.v(LOG_TAG, id);

        if (!dynamicViewsAdded) {
            try {
                addTrailers(id);
                addReviews(id);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            dynamicViewsAdded = true;
        }

    }
    private void addTrailers(String id) throws InterruptedException {

        String urlTrailers = PopularMoviesMainActivityFragment.FetchMoviesTask.BASE_URL +
                "movie/" + id + "/videos?api_key=" + PopularMoviesMainActivityFragment.api_key;

        latch = new CountDownLatch(1);
        jsonResult = null;
        FetchJSONTask trailerTask = new FetchJSONTask();
        trailerTask.execute(urlTrailers);
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        JSONObject moviesJson = null;
        try {
            Log.v(LOG_TAG, jsonResult);
            moviesJson = new JSONObject(jsonResult);
            JSONArray movieArray = moviesJson.getJSONArray("results");

            for (int i=0; i<movieArray.length(); i++) {
                String title = movieArray.getJSONObject(i).getString("name");
                String youTubePath = movieArray.getJSONObject(i).getString("key");
                final String youTubeURLPath = "https://www.youtube.com/watch?v=" + youTubePath;

                LinearLayout ll = new LinearLayout(getActivity());
                ImageButton playButton = new ImageButton(getActivity());
                playButton.setBackgroundResource(R.drawable.controls_play);
                playButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //********to be replaced by launching youtube video
                        //URL: https://www.youtube.com/watch?v={key}
                        //startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=cxLG2wtE7TM")));
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(youTubeURLPath)));
                        /*
                        Context context = getActivity();
                        CharSequence text = youTubeURLPath;
                        int duration = Toast.LENGTH_SHORT;
                        Toast toast = Toast.makeText(context, text, duration);
                        toast.show();
                        */
                    }
                });
                ll.addView(playButton);

                TextView tv = new TextView(getActivity());
                tv.setText(title);
                ll.addView(tv);

                trailersLL.addView(ll);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    private void addReviews(String id) {
        String urlReviews = PopularMoviesMainActivityFragment.FetchMoviesTask.BASE_URL +
                "movie/" + id + "/reviews?api_key=" + PopularMoviesMainActivityFragment.api_key;

        latch = new CountDownLatch(1);
        jsonResult = null;
        FetchJSONTask reviewTask = new FetchJSONTask();
        reviewTask.execute(urlReviews);
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        JSONObject moviesJson = null;
        try {
            Log.v(LOG_TAG, jsonResult);
            moviesJson = new JSONObject(jsonResult);
            JSONArray movieArray = moviesJson.getJSONArray("results");

            for (int i=0; i<movieArray.length(); i++) {
                String author = movieArray.getJSONObject(i).getString("author");
                String content = movieArray.getJSONObject(i).getString("content");

                LinearLayout ll = new LinearLayout(getActivity());
                ll.setOrientation(LinearLayout.VERTICAL);

                TextView tvAuthor = new TextView(getActivity());
                tvAuthor.setText(author);
                ll.addView(tvAuthor);

                TextView tvContent = new TextView(getActivity());
                tvContent.setText(content);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                                                                                LinearLayout.LayoutParams.WRAP_CONTENT);
                params.setMargins(5, 0, 5, 10);
                tvContent.setLayoutParams(params);
                ll.addView(tvContent);
                reviewsLL.addView(ll);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
    public class FetchJSONTask extends AsyncTask<String, Void, Void> {

        private final String LOG_TAG = FetchJSONTask.class.getSimpleName();

        @Override
        protected Void doInBackground(String... params) {
            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String moviesJsonStr = null;

            try {
                // Construct the URL for the OpenWeatherMap query
                // Possible parameters are avaiable at OWM's forecast API page, at
                // http://openweathermap.org/API#forecast

                Uri builtUri = Uri.parse(params[0]);


                //URL url = new URL("http://api.openweathermap.org/data/2.5/forecast/daily?q=94043&mode=json&units=metric&cnt=7");
                URL url = new URL(builtUri.toString());
                Log.v(LOG_TAG, "Built URI: " + builtUri.toString());
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
                jsonResult = moviesJsonStr;

            //countdown latch so that data is produced and the fragment waits
            latch.countDown();
            return null;
        }
    }
}

