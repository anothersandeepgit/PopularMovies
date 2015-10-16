package com.end2end.popularmovies;

import android.app.Activity;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * A fragment containing a simple view to display list of movie obtained from
 * http://api.themoviedb.org/3/discover/movie?sort_by=popularity.desc&api_key=3f9ba3a7cf26793007032a520fbe0cff
 */
public class PopularMoviesMainActivityFragment extends Fragment {

    private static String sortOrder = "popularity.desc";

    private final String LOG_TAG = PopularMoviesMainActivityFragment.class.getSimpleName();

    private static GridView gridview;
    private static Activity context;
    private static List<MovieNode> moviePopularityArrayList;
    private static List<MovieNode> movieRatingArrayList;
    private static List<MovieNode> movieFavoriteArrayList;
    private final String saveInstanceFavoriteState_key = "MOVIES_FAVORITE_LIST";
    private final String saveInstancePopularityState_key = "MOVIES_POPULARITY_LIST";
    private final String saveInstanceRatingState_key = "MOVIES_Rating_LIST";
    private final String saveInstanceSortOrder_key = "SORT_ORDER";
    boolean isSaveInstanceState = false;

    //references to our initial image
    private Integer[] mThumbsIds = { R.drawable.loading };

    /**
     * interface with method to be implemented by parent Activity in this case to
     * pass on to the event when an item has been selected through Parent Activity
     * to another fragment, DetailFragment
     */
    public interface Callback {
        /**
         * DetailFragmentCallback for .
         */
        public void onItemSelected(MovieNode movieNode, boolean ignore);
    }

    public PopularMoviesMainActivityFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        context = getActivity();
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_popular_movies_main, container, false);
        gridview = (GridView) rootView.findViewById(R.id.gridview);

        if(savedInstanceState == null) {
            gridview.setAdapter(new ImageAdapter(getActivity(), mThumbsIds));
        } else {

            isSaveInstanceState = true;
            moviePopularityArrayList = (List<MovieNode>)savedInstanceState.get(saveInstancePopularityState_key);
            movieRatingArrayList = (List<MovieNode>)savedInstanceState.get(saveInstanceRatingState_key);
            movieFavoriteArrayList = (List<MovieNode>)savedInstanceState.get(saveInstanceFavoriteState_key);
            sortOrder = savedInstanceState.getString(saveInstanceSortOrder_key);
        }
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);

        updateMovies("");
    }

    @Override
    public void onCreateOptionsMenu(Menu menu,  MenuInflater inflater) {

        inflater.inflate(R.menu.menu_popular_movies_main_fragment, menu);
        if (sortOrder.equals("vote_average.desc")) {
            menu.findItem(R.id.sort_rating).setChecked(true);
        } else if (sortOrder.equals("favorite.local")){
            menu.findItem(R.id.sort_favorites).setChecked(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id) {
            case R.id.sort_popular:
                if (!item.isChecked()){
                    item.setChecked(true);
                    sortOrder = "popularity.desc";
                    isSaveInstanceState = false;
                    updateMovies("");
                }
                return true;
            case R.id.sort_rating:
                if (!item.isChecked()) {
                    item.setChecked(true);
                    sortOrder = "vote_average.desc";
                    isSaveInstanceState = false;
                    updateMovies("");
                }
                return true;
            case R.id.sort_favorites:
                movieFavoriteArrayList = new ArrayList<MovieNode>();
                sortOrder = "favorite.local";
                isSaveInstanceState = false;
                DBUtilities.fetchMoviesFromDB(getActivity(), (ArrayList<MovieNode>) movieFavoriteArrayList);
                if (movieFavoriteArrayList.size() != 0){
                    if (!item.isChecked()) {
                        item.setChecked(true);
                        Utilities.drawPosters(getActivity(), gridview, movieFavoriteArrayList, isSaveInstanceState);
                    }
                } else {
                    Toast.makeText(getActivity(), "No favorite movies found!", Toast.LENGTH_SHORT).show();
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        Log.i(LOG_TAG, "onSaveInstanceState");
        outState.putParcelableArrayList(saveInstancePopularityState_key, (ArrayList<? extends Parcelable>) moviePopularityArrayList);
        outState.putParcelableArrayList(saveInstanceRatingState_key, (ArrayList<? extends Parcelable>) movieRatingArrayList);
        outState.putParcelableArrayList(saveInstanceFavoriteState_key, (ArrayList<? extends Parcelable>) movieFavoriteArrayList);
        outState.putString(saveInstanceSortOrder_key, sortOrder);
    }

    private void updateMovies(String listOfIds) {

        if (!Utilities.isNetworkAvailable(context) && sortOrder!= "favorite.local") {
            Utilities.drawPosters(context, gridview, null, isSaveInstanceState);
        } else if (sortOrder.equals("popularity.desc")) {
            if (moviePopularityArrayList != null) {
                Utilities.drawPosters(context, gridview, moviePopularityArrayList, isSaveInstanceState);
            } else {
                moviePopularityArrayList = new ArrayList<MovieNode>();
                FetchMoviesTask movieTask = new FetchMoviesTask(moviePopularityArrayList, sortOrder, context, gridview);
                try {
                    movieTask.execute();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        } else if (sortOrder.equals("vote_average.desc")) {
            if (movieRatingArrayList != null) {
                Utilities.drawPosters(context, gridview, movieRatingArrayList, isSaveInstanceState);
            } else {
                movieRatingArrayList = new ArrayList<MovieNode>();
                FetchMoviesTask movieTask = new FetchMoviesTask(movieRatingArrayList, sortOrder, context, gridview);
                try {
                    movieTask.execute();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        } else if (sortOrder.equals("favorite.local")) {
            if (movieFavoriteArrayList != null) {
                Utilities.drawPosters(context, gridview, movieFavoriteArrayList, isSaveInstanceState);
            }
        }
    }
}
