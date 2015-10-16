package com.end2end.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class PopularMoviesMainActivity extends AppCompatActivity implements PopularMoviesMainActivityFragment.Callback{

    private final String LOG_TAG = PopularMoviesMainActivity.class.getSimpleName();
    private static final String DETAILFRAGMENT_TAG = "DFTAG";

    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_popular_movies_main);

        // The detail container view will be present only in the large-screen layouts
        // (res/layout-sw600dp). If this view is present, then the activity should be
        // in two-pane mode.
        mTwoPane = findViewById(R.id.detail_container) == null ? false : true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_popular_movies_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return super.onOptionsItemSelected(item);
    }


    /**
     * Method implementing the interface declaration PopularMoviesMainActivityFragment.Callback
     * @param movieNode
     * @param ignore : if true then the DetailActivity is launched
     */
    public void onItemSelected(MovieNode movieNode, boolean ignore){
        if (mTwoPane) {
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            Bundle args = new Bundle();
            args.putParcelable(PopularMoviesDetailFragment.MOVIE_PASSED, movieNode);

            PopularMoviesDetailFragment detailFragment = new PopularMoviesDetailFragment();
            detailFragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.detail_container, detailFragment, DETAILFRAGMENT_TAG)
                    .commit();
        } else {
            if (!ignore) {
                Intent detailIntent = new Intent(this, PopularMoviesDetailActivity.class);
                detailIntent.putExtra("MovieNode", movieNode);
                startActivity(detailIntent);
            }
        }
    }
}
