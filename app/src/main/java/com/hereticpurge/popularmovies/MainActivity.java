package com.hereticpurge.popularmovies;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.hereticpurge.popularmovies.database.FavoritesContract;
import com.hereticpurge.popularmovies.utilities.JsonUtils;
import com.hereticpurge.popularmovies.utilities.NetworkUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Objects;

public class MainActivity extends VolleyAppCompatActivity {

    // NUM_OF_COLUMNS is the number of columns to displace in the recycler view gridlayout.
    private static final int NUM_OF_COLUMNS = 2;

    private static final int THREAD_SLEEP = 200;

    private ArrayList<String> mRecyclerUrls = new ArrayList<>();
    private ArrayList<String> mRecyclerIds = new ArrayList<>();
    private MainRecyclerViewAdapter mMainRecyclerViewAdapter;

    // constants for saving and restoring state.
    private static final String MOVIE_ID_TAG = "movieId";
    private static final String POSTER_PATH_TAG = "posterPath";

    // Default number of movie posters to show in the recycler view.  each page is 20 movies
    private static final int PAGES_TO_FETCH = 5;

    // Tag for querying the favorites list instead of doing a network query.
    private static final String FAVORITES = "favorites";

    // private SQLiteDatabase mDb;

    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    private final ArrayList<String> mResponseTags = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // FavoritesDbHelper dbHelper = new FavoritesDbHelper(this);
        // mDb = dbHelper.getReadableDatabase();

        // Setting up the recycler view
        RecyclerView recyclerView = findViewById(R.id.rv_main);
        mMainRecyclerViewAdapter = new MainRecyclerViewAdapter(mRecyclerUrls, mRecyclerIds, this);
        recyclerView.setAdapter(mMainRecyclerViewAdapter);

        // Grid layout manager for the recycler view so the movie posters show 2 per row.
        recyclerView.setLayoutManager(new GridLayoutManager(this, NUM_OF_COLUMNS));


        if (savedInstanceState != null &&
                savedInstanceState.containsKey(MOVIE_ID_TAG) &&
                savedInstanceState.containsKey(POSTER_PATH_TAG)) {

            mRecyclerIds = savedInstanceState.getStringArrayList(MOVIE_ID_TAG);
            mRecyclerUrls = savedInstanceState.getStringArrayList(POSTER_PATH_TAG);
            // Update the recyclerview with the saved instance state values
            // otherwise request new default values below.
            updateRecycler();

        } else {
            // initial call with defaults to start the app running.  Nothing really loads until
            // the call to the Volley networking implementation responds to being called from
            // populateRecyclerView
            populateRecyclerView(NetworkUtils.MOVIEDB_MOST_POPULAR);
        }

    }

    // Method calls volley to get json strings from moviedb then volley responds and does the work
    // of actually repopulating the recycler view in onResponse
    private void populateRecyclerView(String query) {

        if (Objects.equals(query, FAVORITES)) {
            // get favorites from local database instead of querying the internet
            // Cursor cursor = getFavorites();
            Cursor cursor = getContentResolver().query(FavoritesContract.FavoritesTable.CONTENT_URI,
                    null,
                    null,
                    null,
                    FavoritesContract.FavoritesTable.COLUMN_MOVIE_TIMESTAMP);
            if (cursor.moveToFirst()) {
                do {
                    String url = cursor.getString(cursor.getColumnIndex(FavoritesContract.FavoritesTable.COLUMN_MOVIE_POSTER_PATH));
                    String id = cursor.getString(cursor.getColumnIndex(FavoritesContract.FavoritesTable.COLUMN_MOVIE_ID));
                    mRecyclerUrls.add(NetworkUtils.getPosterPath(url));
                    mRecyclerIds.add(id);
                } while (cursor.moveToNext());
            }
            cursor.close();
            updateRecycler();
        } else {
            // go through the normal loading process if favorites wasn't clicked.
            for (int i = 1; i < PAGES_TO_FETCH + 1; i++) {
                mResponseTags.add(Integer.toString(i));
                String url = NetworkUtils.getMovieDbUrlString(query, i);
                volleyQuery(url, Integer.toString(i), this);

                /*
                 * This is a bug fix.  When the program sends out a volley request the responses
                 * don't always come back in order if they're fired off too quickly due to latency on
                 * some packets.  Because of this the movie lists were sometimes out of order.
                 * So this is my bandaid until I can learn more about volley and do Synchronous requests
                 * instead.  The delay shouldn't be noticeable.
                 */
                try {
                    Thread.sleep(THREAD_SLEEP);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // volley response listener repopulates the recyclerview when it receives a json
    // string from the volley implementation.
    @Override
    public void onResponse(String jsonString, String tag) {

        // gets a hashmap with <ids, urls> from the JsonUtils and splits them in order then updates
        // the recycler view lists with the new ones and invalidates the info so the recycler view
        // refreshes.
        LinkedHashMap<String, String> idUrlMap;
        idUrlMap = JsonUtils.getPosterUrls(jsonString);
        mRecyclerIds.addAll(idUrlMap.keySet());
        mRecyclerUrls.addAll(idUrlMap.values());
        updateRecycler();

    }

    private void updateRecycler() {
        // replaces the lists in the recyclerview with the new lists and tells the recyclerview
        // to update its information
        mMainRecyclerViewAdapter.updateLists(mRecyclerIds, mRecyclerUrls);
        mMainRecyclerViewAdapter.notifyDataSetChanged();
    }

    // volley error listener.  Pops a toast if there is an internet error but allows the app to
    // continue running.
    @Override
    public void onErrorResponse(VolleyError e, String tag) {
        Toast.makeText(this, R.string.generic_error_toast, Toast.LENGTH_LONG).show();
    }

    // overrides and inflates the options menu so users can toggle between popular and
    // highest rated movies.
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    // handles user interaction with the options menu and calls populateRecyclerView which then
    // calls volley for new information and the volley response repopulates the recyclerview.
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_highest_rated:
                clearRecyclerLists();
                populateRecyclerView(NetworkUtils.MOVIEDB_TOP_RATED);
                return true;

            case R.id.menu_most_popular:
                clearRecyclerLists();
                populateRecyclerView(NetworkUtils.MOVIEDB_MOST_POPULAR);
                return true;

            case R.id.menu_favorites:
                clearRecyclerLists();
                populateRecyclerView(FAVORITES);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putStringArrayList(MOVIE_ID_TAG, mRecyclerIds);
        outState.putStringArrayList(POSTER_PATH_TAG, mRecyclerUrls);
    }

    private void clearRecyclerLists() {
        mRecyclerIds.clear();
        mRecyclerUrls.clear();
    }

    /*  OLD METHOD
    private Cursor getFavorites(){
        return mDb.query(FavoritesContract.FavoritesTable.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                FavoritesContract.FavoritesTable.COLUMN_MOVIE_TIMESTAMP);
    }
    */
}
