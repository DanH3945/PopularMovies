package com.hereticpurge.popularmovies.utilities;

// Static class for interacting with MovieDB.
public final class NetworkUtils {

    public static final String MOVIEDB_MOST_POPULAR = "popular";
    public static final String MOVIEDB_TOP_RATED = "top_rated";

    // DEVELOPER API KEY HERE!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    private static final String MOVIEDB_API_KEY = "";
    // DEVELOPER API KEY HERE!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

    private static final String MOVIEDB_API_URL = "http://api.themoviedb.org/3/movie/";
    private static final String MOVIEDB_API_KEY_PARAM = "?api_key=";

    private static final String POSTER_BASE_URL = "https://image.tmdb.org/t/p/";
    private static final String POSTER_SIZE = "w500";

    private static final int DEFAULT_PAGE_NUM = -1;
    private static final String DEFAULT_PAGE_PARAM = "&page=";

    private static final String REVIEW_PARAM = "/reviews";
    private static final String VIDEO_PARAM = "/videos";


    // Class is meant to be accessed in a static way.
    private NetworkUtils() {
    }


    public static String getMovieReviewUrlString(String query) {
        return getMovieDbUrlString(query, DEFAULT_PAGE_NUM, true, false);
    }

    public static String getMovieTrailerUrlString(String query) {
        return getMovieDbUrlString(query, DEFAULT_PAGE_NUM, false, true);
    }

    // method takes a string query and calls its overload.  This accepts only a query in the form
    // of a movie ID number and returns the url for its json object.
    public static String getMovieDbUrlString(String query) {
        return getMovieDbUrlString(query, DEFAULT_PAGE_NUM, false, false);
    }

    public static String getMovieDbUrlString(String query, int pageNum) {
        return getMovieDbUrlString(query, pageNum, false, false);
    }

    // Method to assemble Url string from a query.  Accepts one of the above
    // constants MOVIEDB_MOST_POPULAR or MOVIEDB_TOP_RATED as parameters for the query string
    // and a page number to add to the query and returns a json object for that page.  Optionally
    // you can use the constants as inputs for the above method and just get page 1 by default.
    private static String getMovieDbUrlString(String query, int pageNum, boolean getReviews, boolean getTrailers) {
        StringBuilder resultString = new StringBuilder(MOVIEDB_API_URL);

        switch (query) {
            case MOVIEDB_MOST_POPULAR:
                resultString.append(MOVIEDB_MOST_POPULAR);
                break;
            case MOVIEDB_TOP_RATED:
                resultString.append(MOVIEDB_TOP_RATED);
                break;
            default:
                resultString.append(query);
                break;
        }

        if (getReviews) {
            resultString.append(REVIEW_PARAM);
        }
        if (getTrailers) {
            resultString.append(VIDEO_PARAM);
        }

        resultString.append(MOVIEDB_API_KEY_PARAM)
                .append(MOVIEDB_API_KEY);

        if (pageNum != DEFAULT_PAGE_NUM) {
            resultString.append(DEFAULT_PAGE_PARAM)
                    .append(Integer.toString(pageNum));
        }

        return resultString.toString();
    }

    // takes a url string from a movie json and gives the full path to its image based on
    // the size constant above.
    public static String getPosterPath(String imgUrl) {
        return POSTER_BASE_URL + POSTER_SIZE + imgUrl;
    }

}
