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

import java.util.HashMap;
import java.util.Map;

import static com.codefactoring.android.backlogapi.BacklogApiConstants.STATUS_ISSUE_OPEN;
import static com.codefactoring.android.backlogtracker.provider.BacklogContract.CONTENT_AUTHORITY;
import static com.codefactoring.android.backlogtracker.provider.BacklogContract.CommentEntry;
import static com.codefactoring.android.backlogtracker.provider.BacklogContract.IssueEntry;
import static com.codefactoring.android.backlogtracker.provider.BacklogContract.IssuePreviewEntry;
import static com.codefactoring.android.backlogtracker.provider.BacklogContract.IssueTypeEntry;
import static com.codefactoring.android.backlogtracker.provider.BacklogContract.PATH_COMMENTS;
import static com.codefactoring.android.backlogtracker.provider.BacklogContract.PATH_ISSUES;
import static com.codefactoring.android.backlogtracker.provider.BacklogContract.PATH_ISSUES_LAST_TEN;
import static com.codefactoring.android.backlogtracker.provider.BacklogContract.PATH_ISSUES_PREVIEWS;
import static com.codefactoring.android.backlogtracker.provider.BacklogContract.PATH_ISSUES_STATS;
import static com.codefactoring.android.backlogtracker.provider.BacklogContract.PATH_ISSUE_COMMENTS;
import static com.codefactoring.android.backlogtracker.provider.BacklogContract.PATH_ISSUE_ID;
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
    static final int ISSUE = 410;
    static final int ISSUE_COMMENTS = 411;
    static final int ISSUES_LAST_TEN = 403;
    static final int COMMENTS = 500;

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
        matcher.addURI(authority, PATH_ISSUE_ID, ISSUE);
        matcher.addURI(authority, PATH_ISSUE_COMMENTS, ISSUE_COMMENTS);
//        matcher.addURI(authority, PATH_ISSUE_KEY_COMMENTS, ISSUE_COMMENTS);
        matcher.addURI(authority, PATH_ISSUES_LAST_TEN, ISSUES_LAST_TEN);
        matcher.addURI(authority, PATH_COMMENTS, COMMENTS);

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
            case ISSUE: {
                retCursor = findIssueById(uri);
                break;
            }
            case ISSUE_COMMENTS: {
                retCursor = findCommentsByIssueId(uri, projection, sortOrder);
                break;
            }
            case ISSUES_LAST_TEN: {
                retCursor = findLastTenOpenedIssues(projection);
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
            case ISSUE_COMMENTS: {
                final long _id = db.insert(CommentEntry.TABLE_NAME, null, values);
                if (_id > 0)
                    returnUri = ContentUris.withAppendedId(CommentEntry.CONTENT_URI, _id);
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
            case COMMENTS:
                rowsDeleted = db.delete(
                        CommentEntry.TABLE_NAME, selection, selectionArgs);
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

    private Cursor findIssueById(Uri uri) {
        final String issueId = uri.getLastPathSegment();

        final SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(IssueEntry.TABLE_NAME
                + " INNER JOIN " + UserEntry.TABLE_NAME
                + " ON " + IssueEntry.TABLE_NAME + ".CREATED_USER_ID = " + UserEntry.TABLE_NAME + "._ID " +
                "LEFT OUTER JOIN " + UserEntry.TABLE_NAME + " AS ASSIGNEE ON "
                + IssueEntry.TABLE_NAME + ".ASSIGNEE_ID = ASSIGNEE._ID " +
                "INNER JOIN " + IssueTypeEntry.TABLE_NAME + " ON " + IssueEntry.TABLE_NAME +
                ".TYPE_ID = " + IssueTypeEntry.TABLE_NAME + "._ID");

        final Map<String, String> columnMap = new HashMap<>();
        columnMap.put(UserEntry.USER_PREFIX + UserEntry.THUMBNAIL_URL, UserEntry.TABLE_NAME + "." + UserEntry.THUMBNAIL_URL);
        columnMap.put(UserEntry.USER_PREFIX + UserEntry.NAME, UserEntry.TABLE_NAME + "." + UserEntry.NAME);
        columnMap.put(IssueEntry.CREATED_DATE, IssueEntry.CREATED_DATE);
        columnMap.put(IssueEntry.DESCRIPTION, IssueEntry.DESCRIPTION);
        columnMap.put(UserEntry.ASSIGNEE_PREFIX + UserEntry.THUMBNAIL_URL, UserEntry.ASSIGNEE_ALIAS + "." + UserEntry.THUMBNAIL_URL);
        columnMap.put(UserEntry.ASSIGNEE_PREFIX + UserEntry.NAME, UserEntry.ASSIGNEE_ALIAS + "." + UserEntry.NAME);
        columnMap.put(IssueEntry.STATUS, IssueEntry.STATUS);
        columnMap.put(IssueEntry.PRIORITY, IssueEntry.PRIORITY);
        columnMap.put(IssueTypeEntry.PREFIX + IssueTypeEntry.NAME, IssueTypeEntry.TABLE_NAME + "." + IssueTypeEntry.NAME);
        columnMap.put(IssueEntry.MILESTONES, IssueEntry.MILESTONES);
        queryBuilder.setProjectionMap(columnMap);
        queryBuilder.appendWhere(IssueEntry.TABLE_NAME + "." + IssueEntry._ID + "=");
        queryBuilder.appendWhere("'" + issueId + "'");

        final SQLiteDatabase db = mOpenHelper.getReadableDatabase();

        return queryBuilder.query(
                db,
                new String[]{
                        UserEntry.USER_PREFIX + UserEntry.THUMBNAIL_URL,
                        UserEntry.USER_PREFIX + UserEntry.NAME,
                        IssueEntry.CREATED_DATE,
                        IssueEntry.DESCRIPTION,
                        UserEntry.ASSIGNEE_PREFIX + UserEntry.THUMBNAIL_URL,
                        UserEntry.ASSIGNEE_PREFIX + UserEntry.NAME, IssueEntry.STATUS,
                        IssueEntry.PRIORITY,
                        IssueTypeEntry.PREFIX + IssueTypeEntry.NAME,
                        IssueEntry.MILESTONES},
                null,
                null,
                null,
                null,
                null);
    }

    public Cursor findCommentsByIssueId(Uri uri, String[] projections, String sortOrder) {
        final String issueId = uri.getPathSegments().get(uri.getPathSegments().size() - 2);

        final SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(CommentEntry.TABLE_NAME
                        + " INNER JOIN " + UserEntry.TABLE_NAME
                        + " ON " + CommentEntry.TABLE_NAME + ".CREATED_USER_ID = " + UserEntry.TABLE_NAME + "._ID "
        );

        final SQLiteDatabase db = mOpenHelper.getReadableDatabase();

        final String selection = CommentEntry.ISSUE_ID + " = ?";

        return queryBuilder.query(
                db,
                projections,
                selection,
                new String[]{issueId},
                null,
                null,
                sortOrder);
    }

    public Cursor findLastTenOpenedIssues(String[] projections) {

        final SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(IssueEntry.TABLE_NAME);
        queryBuilder.appendWhere(IssueEntry.STATUS + " = ? ");

        final SQLiteDatabase db = mOpenHelper.getReadableDatabase();

        return queryBuilder.query(
                db,
                projections,
                null,
                new String[]{ STATUS_ISSUE_OPEN },
                null,
                null,
                IssueEntry.DEFAULT_SORT,
                "10",
                null);
    }
}