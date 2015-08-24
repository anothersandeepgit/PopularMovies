package com.end2end.popularmovies;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

/**
 * Created by lendevsanadmin on 8/22/2015.
 */
public class PopularMoviesDetailActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_popular_movies_detail);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PopularMoviesDetailFragment())
                    .commit();
        }
    }
}
