package com.hereticpurge.popularmovies.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

class FavoritesDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "moviedb.db";
    private static final int VERSION_NUMBER = 4;

    public FavoritesDbHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION_NUMBER);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // Creating the database with the required columns.
        final String SQL_CREATE_FAVORITES_TABLE = "CREATE TABLE " +
                FavoritesContract.FavoritesTable.TABLE_NAME + " (" +
                FavoritesContract.FavoritesTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                FavoritesContract.FavoritesTable.COLUMN_MOVIE_ID + " TEXT NOT NULL, " +
                FavoritesContract.FavoritesTable.COLUMN_MOVIE_TITLE + " TEXT NOT NULL, " +
                FavoritesContract.FavoritesTable.COLUMN_MOVIE_POSTER_PATH + " TEXT NOT NULL, " +
                FavoritesContract.FavoritesTable.COLUMN_MOVIE_TIMESTAMP + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                ");";
        db.execSQL(SQL_CREATE_FAVORITES_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Just clears the DB for testing purposes.
        db.execSQL("DROP TABLE IF EXISTS " + FavoritesContract.FavoritesTable.TABLE_NAME);
        onCreate(db);
    }
}
