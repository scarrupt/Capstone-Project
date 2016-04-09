package com.codefactoring.android.backlogtracker.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.common.collect.Sets;

import static com.codefactoring.android.backlogtracker.provider.BacklogContract.CONTENT_AUTHORITY;
import static com.codefactoring.android.backlogtracker.provider.BacklogContract.IssueEntry;
import static com.codefactoring.android.backlogtracker.provider.BacklogContract.IssuePreviewEntry;
import static com.codefactoring.android.backlogtracker.provider.BacklogContract.IssueTypeEntry;
import static com.codefactoring.android.backlogtracker.provider.BacklogContract.PATH_ISSUES;
import static com.codefactoring.android.backlogtracker.provider.BacklogContract.PATH_ISSUES_PREVIEWS;
import static com.codefactoring.android.backlogtracker.provider.BacklogContract.PATH_ISSUES_STATS;
import static com.codefactoring.android.backlogtracker.provider.BacklogContract.PATH_ISSUE_TYPES;
import static com.codefactoring.android.backlogtracker.provider.BacklogContract.PATH_PROJECTS;
import static com.codefactoring.android.backlogtracker.provider.BacklogContract.PATH_PROJECT_ISSUE_TYPES;
import static com.codefactoring.android.backlogtracker.provider.BacklogContract.PATH_USERS;
import static com.codefactoring.android.backlogtracker.provider.BacklogContract.ProjectEntry;
import static com.codefactoring.android.backlogtracker.provider.BacklogContract.UserEntry;

public class BacklogProvider extends ContentProvider {

    private BacklogDbHelper mOpenHelper;

    static final int PROJECTS = 100;
    static final int PROJECT_ISSUE_TYPES = 101;
    static final int USERS = 200;
    static final int ISSUE_TYPES = 300;
    static final int ISSUES = 400;
    static final int ISSUES_PREVIEWS = 401;
    static final int ISSUES_STATS = 402;

    private static final UriMatcher sURI_MATCHER = buildUriMatcher();

    static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = CONTENT_AUTHORITY;

        matcher.addURI(authority, PATH_PROJECTS, PROJECTS);
        matcher.addURI(authority, PATH_USERS, USERS);
        matcher.addURI(authority, PATH_PROJECT_ISSUE_TYPES, PROJECT_ISSUE_TYPES);
        matcher.addURI(authority, PATH_ISSUE_TYPES, ISSUE_TYPES);
        matcher.addURI(authority, PATH_ISSUES, ISSUES);
        matcher.addURI(authority, PATH_ISSUES_PREVIEWS, ISSUES_PREVIEWS);
        matcher.addURI(authority, PATH_ISSUES_STATS, ISSUES_STATS);

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
                return ProjectEntry.CONTENT_TYPE;
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
                        ProjectEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case ISSUES_PREVIEWS: {
                retCursor = findIssuesPreviewsByProjectId(uri, projection, sortOrder);
                break;
            }
            case ISSUES_STATS: {
                retCursor = findIssuesStatsByProjectId(uri);
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
                final long _id = db.insert(ProjectEntry.TABLE_NAME, null, values);
                if (_id > 0)
                    returnUri = ContentUris.withAppendedId(ProjectEntry.CONTENT_URI, _id);
                else
                    throw new SQLException("Failed to insert row into " + uri);
                break;
            }
            case USERS: {
                final long _id = db.insert(UserEntry.TABLE_NAME, null, values);
                if (_id > 0)
                    returnUri = ContentUris.withAppendedId(UserEntry.CONTENT_URI, _id);
                else
                    throw new SQLException("Failed to insert row into " + uri);
                break;
            }
            case PROJECT_ISSUE_TYPES: {
                final long _id = db.insert(IssueTypeEntry.TABLE_NAME, null, values);
                if (_id > 0)
                    returnUri = ContentUris.withAppendedId(IssueTypeEntry.CONTENT_URI, _id);
                else
                    throw new SQLException("Failed to insert row into " + uri);
                break;
            }
            case ISSUES: {
                final long _id = db.insert(IssueEntry.TABLE_NAME, null, values);
                if (_id > 0)
                    returnUri = ContentUris.withAppendedId(IssueEntry.CONTENT_URI, _id);
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
                        ProjectEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case USERS:
                rowsDeleted = db.delete(
                        UserEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case ISSUE_TYPES:
                rowsDeleted = db.delete(
                        IssueTypeEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case ISSUES:
                rowsDeleted = db.delete(
                        IssueEntry.TABLE_NAME, selection, selectionArgs);
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
                rowsUpdated = db.update(ProjectEntry.TABLE_NAME, values, selection,
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
                        long _id = db.insert(ProjectEntry.TABLE_NAME, null, value);
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

    private Cursor findIssuesPreviewsByProjectId(Uri uri, String[] projection, String sortOrder) {
        final String selection = IssueEntry.PROJECT_ID + " = ? AND " + IssueEntry.STATUS + " = ? ";
        final String projectId = uri.getQueryParameter(IssuePreviewEntry.QUERY_PARAMETER_PROJECT_ID);
        final String statusFilter = uri.getQueryParameter(IssuePreviewEntry.QUERY_PARAMETER_STATUS);

        if (projectId == null) {
            throw new IllegalArgumentException("ProjectId should not be null");
        }

        final String[] selectionArgs = new String[]{projectId, statusFilter};

        return mOpenHelper.getReadableDatabase().query(
                IssuePreviewEntry.VIEW_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder);
    }

    private Cursor findIssuesStatsByProjectId(Uri uri) {
        final String projectId = uri.getQueryParameter(IssuePreviewEntry.QUERY_PARAMETER_PROJECT_ID);

        if (projectId == null) {
            throw new IllegalArgumentException("ProjectId should not be null");
        }

        final String subQueryOpenIssues = createIssueSubQueryWithStatus(projectId, "Open");

        final String subQueryInProgressIssues = createIssueSubQueryWithStatus(projectId, "In Progress");

        final String subQueryResolved = createIssueSubQueryWithStatus(projectId, "Resolved");

        final SQLiteQueryBuilder unionQueryBuilder = new SQLiteQueryBuilder();
        final String sql = unionQueryBuilder.buildUnionQuery(new String[]{
                        subQueryOpenIssues, subQueryInProgressIssues, subQueryResolved},
                null, null);

        return mOpenHelper.getReadableDatabase().rawQuery(sql, null);
    }

    private String createIssueSubQueryWithStatus(String projectId, String status) {
        final SQLiteQueryBuilder statusQueryBuilder = new SQLiteQueryBuilder();
        statusQueryBuilder.setTables(IssueEntry.TABLE_NAME);
        return statusQueryBuilder.buildUnionSubQuery(
                "Status", new String[]{"COUNT(*)", IssueEntry.STATUS},
                Sets.newHashSet(IssueEntry.STATUS), 1, status,
                IssueEntry.PROJECT_ID + " = " + projectId + " AND " + IssueEntry.STATUS + "= '"
                        + status + "'",
                null, null);
    }
}