package com.hereticpurge.popularmovies;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.hereticpurge.popularmovies.database.FavoritesContract;
import com.hereticpurge.popularmovies.model.Movie;
import com.hereticpurge.popularmovies.utilities.JsonUtils;
import com.hereticpurge.popularmovies.utilities.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Objects;

public class DetailActivity extends VolleyAppCompatActivity {

    private static final String MOVIE_ID_TAG = "movieId";
    private static final String POSTER_PATH_TAG = "posterPath";
    private static final String VOLLEY_REVIEW_TAG = "reviews";
    private static final String VOLLEY_TRAILER_TAG = "trailers";

    // intent tag for review activity to get the movie object out of extras
    private static final String MOVIE_OBJECT_TAG = "movieObject";

    private String mMovieId;
    private String mPosterPath;

    private Menu mMenu;

    private boolean displayReady = false;

    private String mMovieJson;
    private String mReviewJson;
    private String mTrailerJson;

    private Movie mMovie;

    // private SQLiteDatabase mDb;

    private DetailRecyclerViewAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        // FavoritesDbHelper dbHelper = new FavoritesDbHelper(this);
        // mDb = dbHelper.getWritableDatabase();

        RecyclerView trailerRecycler = findViewById(R.id.rv_detail_trailers);
        adapter = new DetailRecyclerViewAdapter(this);
        trailerRecycler.setAdapter(adapter);
        trailerRecycler.setLayoutManager(new LinearLayoutManager(this));

        // binding intent extras to usable variables for later
        try {
            mMovieId = getIntent()
                    .getExtras()
                    .getString(MOVIE_ID_TAG);

            mPosterPath = getIntent()
                    .getExtras()
                    .getString(POSTER_PATH_TAG);
        } catch (NullPointerException e) {
            Toast.makeText(this, R.string.generic_error_toast, Toast.LENGTH_LONG).show();
        }

        // Volley queries run the app.  App will populate after a volley query responds.
        // See VolleyAppCompatClass for volley implementation.
        volleyQuery(NetworkUtils.getMovieDbUrlString(mMovieId), this);
    }

    // volley listener that populates the app from the returned volley json string.
    @Override
    public void onResponse(String jsonString, String requestTag) {

        if (requestTag == null) {
            mMovieJson = jsonString;
            volleyQuery(NetworkUtils.getMovieReviewUrlString(mMovieId), VOLLEY_REVIEW_TAG, this);
        }

        if (Objects.equals(requestTag, VOLLEY_REVIEW_TAG)) {
            mReviewJson = jsonString;
            volleyQuery(NetworkUtils.getMovieTrailerUrlString(mMovieId), VOLLEY_TRAILER_TAG, this);
        }

        if (Objects.equals(requestTag, VOLLEY_TRAILER_TAG)) {
            mTrailerJson = jsonString;
            displayReady = true;
        }

        updateUI();

    }

    // volley error listener.  Pops a toast if there is an internet error but allows the app to
    // continue running.
    @Override
    public void onErrorResponse(VolleyError error, String requestTag) {
        Toast.makeText(this, R.string.generic_error_toast, Toast.LENGTH_LONG).show();
    }

    private void updateUI() {

        if (displayReady) {

            // getting a populated mMovie object
            // mMovie objects are built in the JsonUtils class see there for specifics
            mMovie = JsonUtils.getMovieObject(mMovieJson, mReviewJson, mTrailerJson);

            // binding the views from activity_detail.xml
            TextView title = findViewById(R.id.detail_title_text);
            TextView rating = findViewById(R.id.detail_rating_text);
            TextView releaseDate = findViewById(R.id.detail_release_text);
            TextView description = findViewById(R.id.detail_description_text);
            ImageView imageView = findViewById(R.id.detail_image);

            // setting the data for the views
            title.setText(mMovie.getTitle());
            rating.setText(mMovie.getVoteAverage());
            releaseDate.setText(mMovie.getReleaseDate());
            description.setText(mMovie.getDescription());

            // picasso implementation to load the poster image into the image view
            Picasso.with(this)
                    .load(mPosterPath)
                    .into(imageView);

            ArrayList<String> trailerUrls = new ArrayList<>();
            trailerUrls.addAll(mMovie.getTrailers().values());
            adapter.setUrls(trailerUrls);

            ArrayList<String> trailerTitles = new ArrayList<>();
            trailerTitles.addAll(mMovie.getTrailers().keySet());
            adapter.setTitles(trailerTitles);

            adapter.notifyDataSetChanged();

            /*
            Cursor cursor = mDb.query(FavoritesContract.FavoritesTable.TABLE_NAME,
                    null,
                    null,
                    null,
                    null,
                    null,
                    FavoritesContract.FavoritesTable.COLUMN_MOVIE_TIMESTAMP);
            */

            Cursor cursor = getContentResolver().query(FavoritesContract.FavoritesTable.CONTENT_URI,
                    null,
                    null,
                    null,
                    FavoritesContract.FavoritesTable.COLUMN_MOVIE_TIMESTAMP);

            // Checks to see if the db contains the favorited item.
            if (cursor.moveToFirst()) {
                do {
                    String id = cursor.getString(cursor.getColumnIndex(FavoritesContract.FavoritesTable.COLUMN_MOVIE_ID));
                    if (id.equals(mMovieId)) {
                        swapFavoriteButton();
                    }
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detail_menu, menu);
        this.mMenu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.detail_menu_reviews:
                // starts the review activity for the selected movie
                Intent intent = new Intent(this, ReviewActivity.class);
                intent.putExtra(MOVIE_OBJECT_TAG, mMovie);
                startActivity(intent);

                break;
            case R.id.detail_menu_favorite:
                // adds a favorite and swaps the favorite button display if
                // adding the favorite is successful
                if (addFavorite()) {
                    swapFavoriteButton();
                }

                break;
            case R.id.detail_menu_unfavorite:
                // removes a favorite and swaps the favorite button display if
                // removing the favorite is successful
                if (removeFavorite()) {
                    swapFavoriteButton();
                }

                break;
        }
        return super.onOptionsItemSelected(item);
    }

    // Swap the visibility of the favorite / unfavorite button
    private void swapFavoriteButton() {
        MenuItem markFavorite = mMenu.findItem(R.id.detail_menu_favorite);
        MenuItem markUnfavorite = mMenu.findItem(R.id.detail_menu_unfavorite);

        markFavorite.setVisible(!markFavorite.isVisible());
        markUnfavorite.setVisible(!markFavorite.isVisible());
    }

    private boolean addFavorite() {
        if (mMovie.getId() != null) {
            ContentValues cv = new ContentValues();
            cv.put(FavoritesContract.FavoritesTable.COLUMN_MOVIE_ID, mMovie.getId());
            cv.put(FavoritesContract.FavoritesTable.COLUMN_MOVIE_TITLE, mMovie.getTitle());
            cv.put(FavoritesContract.FavoritesTable.COLUMN_MOVIE_POSTER_PATH, mMovie.getPosterPath());
            getContentResolver().insert(FavoritesContract.FavoritesTable.CONTENT_URI, cv);
            return true;
        } else {
            return false;
        }
        // mDb.insert(FavoritesContract.FavoritesTable.TABLE_NAME, null, cv);

    }

    private boolean removeFavorite() {
        // mDb.delete(FavoritesContract.FavoritesTable.TABLE_NAME, FavoritesContract.FavoritesTable.COLUMN_MOVIE_ID + "=" + mMovie.getId(), null);
        if (mMovie.getId() != null) {
            getContentResolver().delete(FavoritesContract.FavoritesTable.CONTENT_URI,
                    FavoritesContract.FavoritesTable.COLUMN_MOVIE_ID + "=" + mMovie.getId(),
                    null);
            return true;
        } else {
            return false;
        }
    }
}
