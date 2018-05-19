package com.hereticpurge.popularmovies.database;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class FavoritesProvider extends ContentProvider {

    private FavoritesDbHelper mDbHelper;
    private final UriMatcher sUriMatcher = buildUriMatcher();

    private static final int FAVORITES = 100;

    private static UriMatcher buildUriMatcher() {
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(FavoritesContract.AUTHORITY, FavoritesContract.PATH_FAVORITES, FAVORITES);
        return uriMatcher;
    }

    @Override
    public boolean onCreate() {
        Context context = getContext();
        mDbHelper = new FavoritesDbHelper(context);
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        final SQLiteDatabase database = mDbHelper.getReadableDatabase();

        int match = sUriMatcher.match(uri);

        Cursor cursor;

        // Checks for matched uri.  In our case only 1 query is available since the whole table is
        // checked against for favorites.
        switch (match) {
            case FAVORITES:
                cursor = database.query(FavoritesContract.FavoritesTable.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                return cursor;

            default:
                // incase the provider method is called with an incorrect uri
                throw new UnsupportedOperationException("Couldn't query: " + uri);
        }
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        // Database is for internal use only.  If ever made public this could be implemented as necessary.
        throw new UnsupportedOperationException("Unimplemented.  Internal use only database.");
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        final SQLiteDatabase database = mDbHelper.getWritableDatabase();

        int match = sUriMatcher.match(uri);

        Uri returnUri;

        // check for a match.  Again in our case only 1 option since we only allow favorites inserts.
        switch (match) {
            case FAVORITES:
                // do the insert operation and get a return if successful.
                long id = database.insert(FavoritesContract.FavoritesTable.TABLE_NAME, null, values);
                if (id > 0) {
                    // if the above is successful the id should be > 0 since the operation worked and the
                    // uri is updated and returned.  We don't use the return in this program but it's returned
                    // anyways since the method requires a return.
                    returnUri = ContentUris.withAppendedId(FavoritesContract.FavoritesTable.CONTENT_URI, id);
                } else {
                    // if the id returns < 0 then the insert failed and an exception is thrown with
                    // the failed uri included.
                    throw new SQLException("Failed Insert into: " + uri);
                }
                break;
            default:
                // incase the provider method is called with an incorrect uri
                throw new UnsupportedOperationException("Unknown Uri: " + uri);

        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        final SQLiteDatabase database = mDbHelper.getWritableDatabase();

        int match = sUriMatcher.match(uri);

        switch (match) {
            // check for a match (in our case only favorites) and perform the delete operation and
            // return the new id incase it needs to be used...  We don't use the return but the method
            // requires it.
            case FAVORITES:
                return database.delete(FavoritesContract.FavoritesTable.TABLE_NAME, selection, selectionArgs);

            default:
                // incase the delete fails
                throw new UnsupportedOperationException("Failed to delete entry" + uri);

        }
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        // This database doesn't allow for individual items to be updated at this time.  Items may only be added or removed entirely.
        throw new UnsupportedOperationException("Database doesn't allow updates.  Only full entries may be added and deleted.");
    }
}
