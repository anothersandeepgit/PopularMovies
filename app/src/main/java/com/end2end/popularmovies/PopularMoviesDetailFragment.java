package com.end2end.popularmovies;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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

    private boolean movieNull = true;
    CountDownLatch latch = null;
    boolean dynamicViewsAdded = false;
    private String mshareName = null;
    private String mshareYouTubeURLPath = null;
    ShareActionProvider mShareActionProvider;

    public PopularMoviesDetailFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        Log.i(LOG_TAG, "DetailFragment onCreate");
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Log.i(LOG_TAG, "DetailFragment onCreateView");
        Bundle arguments = getArguments();
        if (arguments != null) {
            movieNull = false;
            movie = arguments.getParcelable(PopularMoviesDetailFragment.MOVIE_PASSED);
        } else {
            movieNull = true;
        }

        mshareName = null;
        mshareYouTubeURLPath = null;

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

                int movieIdInteger = Integer.valueOf(movie.getId());
                if (!DBUtilities.isMovieInDB(getActivity(), movie)) {
                    long movieId = DBUtilities.insertMovieInDB(getActivity(), movie);
                    Toast.makeText(getActivity(), "Added favorite movie: " + movie.getTitle(), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), "***movie already in favorite list***", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu,  MenuInflater inflater) {

        inflater.inflate(R.menu.menu_popular_movies_detail_fragment, menu);

        Log.d(LOG_TAG, "onCreateOptionsMenu");
        MenuItem menuItem = menu.findItem(R.id.action_share);
        mShareActionProvider =
                (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);
        if(mShareActionProvider != null) {
            Intent shareMovieTrailerIntent = createShareMovieTrailerIntent();
            if (shareMovieTrailerIntent != null) {
                Log.d(LOG_TAG, "shareMovieTrailerIntent set to share!");
                mShareActionProvider.setShareIntent(shareMovieTrailerIntent);
            }else {
                mShareActionProvider.setShareIntent(null);
                Log.d(LOG_TAG, "No trailer to share!");
            }
        } else{
            Log.d(LOG_TAG, "Share Action Provider is null?");
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id) {
            case R.id.action_share:

                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private Intent createShareMovieTrailerIntent() {
        if (mshareYouTubeURLPath != null) {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
            shareIntent.setType("text/plain");

            shareIntent.putExtra(Intent.EXTRA_TEXT, mshareName + " --TRAILER--: " + mshareYouTubeURLPath);
            return shareIntent;
        }else {
            return null;
        }
    }

    @Override
    public void onPause() {
        Log.i(LOG_TAG, "onPause");
        mshareYouTubeURLPath = null;
        mshareName = null;
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();

        mshareYouTubeURLPath = null;
        Log.i(LOG_TAG, "onResume");
        if (movie == null) {
            Intent intent = getActivity().getIntent();
            movie = intent.getParcelableExtra("MovieNode");
        }
        if (movie == null) {
            rootView.setVisibility(View.INVISIBLE);
        }else {
            String title = movie.getTitle();
            titleTextView.setText("Title: " + title);

            String releaseDate = movie.getReleaseDate();
            releaseDateTextView.setText("Release Date: " + releaseDate);

            String posterPath = movie.getPosterPath();
            Picasso.with(getActivity()).load(posterPath).into(posterImageView);

            String voteAverage = movie.getVoteAverage();
            voteAverageTextView.setText("Vote Average: " + voteAverage);

            String summary = movie.getSummary();
            summaryTextView.setText(summary);

            String id = movie.getId();

            if (!dynamicViewsAdded && Utilities.isNetworkAvailable(getActivity())) {
                try {
                    addTrailers(id);
                    addReviews(id);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                dynamicViewsAdded = true;
            }
        }
    }

    private void addTrailers(String id) throws InterruptedException {

        if (Utilities.isNetworkAvailable(getActivity())) {
            String urlTrailers = FetchMoviesTask.BASE_URL +
                    "movie/" + id + "/videos?api_key=" + FetchMoviesTask.api_key;

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

                for (int i = 0; i < movieArray.length(); i++) {
                    String title = movieArray.getJSONObject(i).getString("name");
                    String youTubePath = movieArray.getJSONObject(i).getString("key");
                    final String youTubeURLPath = "https://www.youtube.com/watch?v=" + youTubePath;
                    if (i == 0) {
                        mshareName =  movie.getTitle() + ": " + title;
                        mshareYouTubeURLPath = youTubeURLPath;
                    }

                    LinearLayout ll = new LinearLayout(getActivity());
                    ImageButton playButton = new ImageButton(getActivity());
                    playButton.setBackgroundResource(R.drawable.controls_play);
                    playButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(youTubeURLPath)));
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
    }

    private void addReviews(String id) {

        if (Utilities.isNetworkAvailable(getActivity())) {
            String urlReviews = FetchMoviesTask.BASE_URL +
                    "movie/" + id + "/reviews?api_key=" + FetchMoviesTask.api_key;

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

                for (int i = 0; i < movieArray.length(); i++) {
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
    }
    public class FetchJSONTask extends AsyncTask<String, Void, Void> {

        private final String LOG_TAG = FetchJSONTask.class.getSimpleName();

        @Override
        protected Void doInBackground(String... params) {

            Uri builtUri = Uri.parse(params[0]);
            jsonResult = Utilities.getJsonMovies(builtUri.toString());

            latch.countDown();
            return null;
        }
    }
}

