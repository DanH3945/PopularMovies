package com.hereticpurge.popularmovies.database;

import android.net.Uri;
import android.provider.BaseColumns;

// Standard contract class for storing database variables.
public final class FavoritesContract {

    private static final String SCHEME = "content://";
    public static final String AUTHORITY = "com.hereticpurge.popularmovies";
    private static final Uri BASE_CONTENT_URI = Uri.parse(SCHEME + AUTHORITY);

    public static final String PATH_FAVORITES = "favorites";

    private FavoritesContract() {
    }

    public static class FavoritesTable implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_FAVORITES).build();

        public static final String TABLE_NAME = "favorites";
        public static final String COLUMN_MOVIE_ID = "movieId";
        public static final String COLUMN_MOVIE_TITLE = "title";
        public static final String COLUMN_MOVIE_POSTER_PATH = "poster";
        public static final String COLUMN_MOVIE_TIMESTAMP = "timeStamp";

        /*
        * not yet implemented
        public static final String COLUMN_MOVIE_DESCRIPTION = "description";
        public static final String COLUMN_MOVIE_RATING = "rating";
        public static final String COLUMN_MOVIE_RELEASE_DATE = "releaseDate";
        */
    }
}
