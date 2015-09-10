package com.example.mykongee.popularmovies.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

/**
 * Created by Mykongee on 9/4/15.
 */
public class MovieProvider extends ContentProvider {

    static final int MOVIE = 100;

    // Uri Matcher used by this ContentProvider
    // Method buildUriMatcher() is written below
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private MovieDbHelper mOpenHelper;


    static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MovieContract.CONTENT_AUTHORITY;

        // Arguments are: authority, path, code to return when uri is matched
        matcher.addURI(authority, "movie", MOVIE);

        // Return the UriMatcher
        return matcher;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new MovieDbHelper(getContext());
        return false;
    }

    @Override
    public String getType(Uri uri) {
        // Utilize the UriMatcher built with the buildUriMatcher method to
        // determine the type of URI this is
        // Use UriMatcher object's match() method.
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case MOVIE:
                return MovieContract.MovieEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {
            case MOVIE:
                // Use the SQLiteOpenHelper object to obtain a readable database
                // Which you can query for data to obtain a cursor
                retCursor = mOpenHelper.getReadableDatabase().query(
                        MovieContract.MovieEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        // First get a writable database because you're inserting
        // Then match the uri
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case MOVIE:
                // the SQLiteDatabase.insert() method returns either the
                // Row id of the inserted row or -1 if an error occurred
                // Arguments: String (name of table), columnHack, ContentValues to put in
                long _id = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, values);
                if (_id > 0) {
                    returnUri = MovieContract.MovieEntry.buildMovieUri(_id);
                }
                else {
                    throw new android.database.SQLException("Failed to insert a row into " + uri);
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        // Notify the ContentResolver of changes to the database pointed at by the uri
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case MOVIE:
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            default:
                return super.bulkInsert(uri, values);
        }
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // First, obtain a writable database
        // Next, match the Uri
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int deletedRows = 0;

        switch (match) {
            case MOVIE:
                deletedRows = db.delete(MovieContract.MovieEntry.TABLE_NAME, selection,
                        selectionArgs);
                break;
            default:
                 throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        // If the selection argument is null, it deletes all rows
        // If deletedRows == 0, nothing was deleted
        // Based on those statements, notify the ContentResolver of a change
        if (selection == null || deletedRows != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return deletedRows;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int updatedRows = 0;

        switch (match) {
            case MOVIE:
                updatedRows = db.update(MovieContract.MovieEntry.TABLE_NAME, values,
                        selection, selectionArgs);
                break;
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return updatedRows;
    }
}
