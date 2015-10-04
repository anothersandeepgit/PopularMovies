package com.end2end.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class PopularMoviesMainActivity extends ActionBarActivity implements PopularMoviesMainActivityFragment.Callback{

    private final String LOG_TAG = PopularMoviesMainActivity.class.getSimpleName();
    private static final String DETAILFRAGMENT_TAG = "DFTAG";

    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_popular_movies_main);

            Log.i(LOG_TAG, "MainActivity onCreate");

            //?????uncommenting the following lines puts icon in centere of ActionBar and moves Name to right
            //getSupportActionBar().setIcon(R.drawable.filmreviewarchive);
            //getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME|ActionBar.DISPLAY_SHOW_TITLE);

        if (findViewById(R.id.detail_container) != null) {
            // The detail container view will be present only in the large-screen layouts
            // (res/layout-sw600dp). If this view is present, then the activity should be
            // in two-pane mode.
            mTwoPane = true;
        } else {
            mTwoPane = false;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(LOG_TAG, "MainActivity onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(LOG_TAG, "MainActivity onResume");
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        Log.i(LOG_TAG, "MainActivity onCreateOptionsMenu");
        getMenuInflater().inflate(R.menu.menu_popular_movies_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        Log.i(LOG_TAG, "MainActivity onOptionsItemSelected");
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        //if (id == R.id.action_settings) {
        //    return true;
        //}

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(LOG_TAG, "MainActivity onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(LOG_TAG, "MainActivity onStop");
    }

    @Override
    public void onDestroy() {
        Log.v(LOG_TAG, "MainActivity onDestroy");
        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        Log.i(LOG_TAG, "MainActivity onSaveInstanceState");
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {

        Log.i(LOG_TAG, "MainActivity onRestoreInstanceState");
        if (savedInstanceState != null) {
            Log.i(LOG_TAG, "MainActivity onRestoreInstanceState savedInstanceState!=null");
        }
        super.onRestoreInstanceState(savedInstanceState);
    }

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
