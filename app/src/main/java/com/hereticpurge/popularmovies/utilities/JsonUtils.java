package com.hereticpurge.popularmovies.utilities;

import android.database.sqlite.SQLiteDatabase;

import com.hereticpurge.popularmovies.model.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedHashMap;

// Static class for unpacking the various json objects
public final class JsonUtils {

    private static final String RESULTS = "results";
    private static final String POSTER_PATH = "poster_path";
    private static final String ID = "id";

    private static final String OVERVIEW = "overview";
    private static final String POSTER_PATH1 = "poster_path";
    private static final String ORIGINAL_TITLE = "original_title";
    private static final String RELEASE_DATE = "release_date";
    private static final String VOTE_AVERAGE = "vote_average";

    private static final String REVIEW_AUTHOR = "author";
    private static final String REVIEW_TEXT = "content";

    private static final String YOUTUBE_BASE = "https://www.youtube.com/watch?v=";
    private static final String TRAILER_KEY = "key";
    private static final String TRAILER_NAME = "name";

    private SQLiteDatabase mDb;

    // Method was extracted for future usage.
    private static JSONArray getResultsArray(String jsonString) throws JSONException {
        JSONObject jObject = new JSONObject(jsonString);

        return jObject.getJSONArray(RESULTS);
    }

    // Method for assembling URLs and Ids
    // takes a raw json string and returns a linked hash map with movie ids as the key and their
    // corresponding full url for the movie poster as the values.  Raw json is 1 full page at
    // a time from movie db.
    public static LinkedHashMap<String, String> getPosterUrls(String jsonString) {
        LinkedHashMap<String, String> movieMap = new LinkedHashMap<>();

        try {
            JSONArray jsonResultsArray = getResultsArray(jsonString);
            for (int i = 0; i < jsonResultsArray.length(); i++) {

                JSONObject movieJsonObject = jsonResultsArray.getJSONObject(i);
                String id = movieJsonObject.getString(ID);
                String url = NetworkUtils.getPosterPath(movieJsonObject.getString(POSTER_PATH));
                movieMap.put(id, url);

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return movieMap;
    }

    // assembles movie objects used in the detail view
    // takes a raw json strings of the movie, the review and the trailer pages and unpacks
    // them into a Movie object.
    public static Movie getMovieObject(String movieJson, String reviewJson, String trailerJson) {
        Movie movie = new Movie();

        try {
            JSONObject movieObject = new JSONObject(movieJson);
            movie.setId(movieObject.getString(ID));
            movie.setDescription(movieObject.getString(OVERVIEW));
            movie.setPosterPath(movieObject.getString(POSTER_PATH1));
            movie.setTitle(movieObject.getString(ORIGINAL_TITLE));
            movie.setReleaseDate(movieObject.getString(RELEASE_DATE));
            movie.setVoteAverage(movieObject.getString(VOTE_AVERAGE));

            LinkedHashMap<String, String> reviewMap = new LinkedHashMap<>();
            JSONArray reviewArray = getResultsArray(reviewJson);
            for (int i = 0; i < reviewArray.length(); i++) {
                JSONObject review = reviewArray.getJSONObject(i);
                String reviewAuthor = review.getString(REVIEW_AUTHOR);
                String reviewText = review.getString(REVIEW_TEXT);
                reviewMap.put(reviewAuthor, reviewText);
            }
            movie.setReviews(reviewMap);

            LinkedHashMap<String, String> trailers = new LinkedHashMap<>();
            JSONArray trailerArray = getResultsArray(trailerJson);
            for (int i = 0; i < trailerArray.length(); i++) {
                String youtubeUrl = YOUTUBE_BASE + trailerArray.
                        getJSONObject(i).
                        get(TRAILER_KEY);
                String trailerName = trailerArray.getJSONObject(i).getString(TRAILER_NAME);
                trailers.put(trailerName, youtubeUrl);
            }
            movie.setTrailers(trailers);

            movie.setFavorite(false);


        } catch (JSONException e) {
            e.printStackTrace();
        }

        return movie;
    }

}
