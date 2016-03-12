package com.codefactoring.android.backlogtracker.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class BacklogProvider extends ContentProvider {

    private BacklogDbHelper mOpenHelper;

    static final int PROJECTS = 100;

    private static final UriMatcher sURI_MATCHER = buildUriMatcher();

    static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = BacklogContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, BacklogContract.PATH_PROJECTS, PROJECTS);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new BacklogDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        final int match = sURI_MATCHER.match(uri);

        switch (match) {
            case PROJECTS:
                return BacklogContract.ProjectEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        final SQLiteDatabase db = mOpenHelper.getReadableDatabase();


        Cursor retCursor;
        switch (sURI_MATCHER.match(uri)) {
            case PROJECTS: {
                retCursor = db.query(
                        BacklogContract.ProjectEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (getContext() != null) {
            retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        }

        return retCursor;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sURI_MATCHER.match(uri);
        Uri returnUri;

        switch (match) {
            case PROJECTS: {
                final long _id = db.insert(BacklogContract.ProjectEntry.TABLE_NAME, null, values);
                if (_id > 0)
                    returnUri = ContentUris.withAppendedId(BacklogContract.ProjectEntry.CONTENT_URI, _id);
                else
                    throw new SQLException("Failed to insert row into " + uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (getContext() != null) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sURI_MATCHER.match(uri);
        int rowsDeleted;
        // this makes delete all rows return the number of rows deleted
        if (null == selection) selection = "1";
        switch (match) {
            case PROJECTS:
                rowsDeleted = db.delete(
                        BacklogContract.ProjectEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // Because a null deletes all rows
        if (rowsDeleted != 0 && getContext() != null) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sURI_MATCHER.match(uri);
        int rowsUpdated;

        switch (match) {
            case PROJECTS:
                rowsUpdated = db.update(BacklogContract.ProjectEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsUpdated != 0 && getContext() != null) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sURI_MATCHER.match(uri);
        switch (match) {
            case PROJECTS:
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(BacklogContract.ProjectEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                if (getContext() != null) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                return returnCount;
            default:
                return super.bulkInsert(uri, values);
        }
    }
}