package com.hereticpurge.popularmovies.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;

// generic info holding class
// implements Serializable so objects instantiated from it can be sent via Intent
public class Movie implements Serializable {

    private String mId;
    private String mTitle;
    private String mDescription;
    private LinkedHashMap<String, String> mTrailers;
    private LinkedHashMap<String, String> mReviews;

    private String mPosterPath;
    private String mReleaseDate;
    private String mLanguage;

    private double mPopularity;
    private int mVoteCount;
    private String mVoteAverage;

    private boolean mAdult;
    private boolean mFavorite;

    private ArrayList<Integer> mGenres;

    // Straight forward class.  Just a bunch of movie information for use by other classes via
    // getters and setters.

    // Lots of unused fields for future implementation

    public Movie() {

    }

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String description) {
        mDescription = description;
    }

    public String getPosterPath() {
        return mPosterPath;
    }

    public void setPosterPath(String posterPath) {
        mPosterPath = posterPath;
    }

    public String getReleaseDate() {
        return mReleaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        mReleaseDate = releaseDate;
    }

    public String getLanguage() {
        return mLanguage;
    }

    public void setLanguage(String language) {
        mLanguage = language;
    }

    public double getPopularity() {
        return mPopularity;
    }

    public void setPopularity(double popularity) {
        mPopularity = popularity;
    }

    public int getVoteCount() {
        return mVoteCount;
    }

    public void setVoteCount(int voteCount) {
        mVoteCount = voteCount;
    }

    public String getVoteAverage() {
        return mVoteAverage;
    }

    public void setVoteAverage(String voteAverage) {
        mVoteAverage = voteAverage;
    }

    public boolean isAdult() {
        return mAdult;
    }

    public void setAdult(boolean adult) {
        mAdult = adult;
    }

    public boolean isFavorite() {
        return mFavorite;
    }

    public void setFavorite(boolean favorite) {
        mFavorite = favorite;
    }

    public ArrayList<Integer> getGenres() {
        return mGenres;
    }

    public void setGenres(ArrayList<Integer> genres) {
        mGenres = genres;
    }

    public LinkedHashMap<String, String> getReviews() {
        return mReviews;
    }

    public void setReviews(LinkedHashMap<String, String> reviews) {
        mReviews = reviews;
    }

    public LinkedHashMap<String, String> getTrailers() {
        return mTrailers;
    }

    public void setTrailers(LinkedHashMap<String, String> trailers) {
        mTrailers = trailers;
    }
}
