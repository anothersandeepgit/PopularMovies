package com.end2end.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * Created by lendevsanadmin on 8/22/2015.
 */
public class PopularMoviesDetailFragment extends Fragment {

    private static final String LOG_TAG = PopularMoviesDetailFragment.class.getSimpleName();

    ImageView posterImageView;
    TextView titleTextView;
    TextView summaryTextView;
    TextView releaseDateTextView;
    TextView voteAverageTextView;

    View rootView;

    public PopularMoviesDetailFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_popular_movies_detail, container, false);
        posterImageView = (ImageView) rootView.findViewById(R.id.posterImageView);
        titleTextView = (TextView) rootView.findViewById(R.id.titleTextView);
        summaryTextView = (TextView) rootView.findViewById(R.id.summaryTextView);
        releaseDateTextView = (TextView) rootView.findViewById(R.id.releaseDateTextView);
        voteAverageTextView = (TextView) rootView.findViewById(R.id.voteAverageTextView);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        Intent intent = getActivity().getIntent();
        //String forecastStr = intent.getStringExtra(Intent.EXTRA_TEXT);
        MovieNode  movie =intent.getParcelableExtra("MovieNode");

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
    }
}

