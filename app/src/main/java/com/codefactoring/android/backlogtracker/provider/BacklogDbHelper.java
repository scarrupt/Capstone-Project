package com.codefactoring.android.backlogtracker.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.codefactoring.android.backlogtracker.provider.BacklogContract.CommentEntry;
import static com.codefactoring.android.backlogtracker.provider.BacklogContract.IssueEntry;
import static com.codefactoring.android.backlogtracker.provider.BacklogContract.IssuePreviewEntry;
import static com.codefactoring.android.backlogtracker.provider.BacklogContract.IssueTypeEntry;
import static com.codefactoring.android.backlogtracker.provider.BacklogContract.ProjectEntry;
import static com.codefactoring.android.backlogtracker.provider.BacklogContract.UserEntry;

public class BacklogDbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 3;
    private static final String DROP_TABLE_IF_EXISTS = "DROP TABLE IF EXISTS ";

    static final String DATABASE_NAME = "backlog.db";

    public BacklogDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_PROJECT_TABLE = "CREATE TABLE " + ProjectEntry.TABLE_NAME + " (" +
                ProjectEntry._ID + " INTEGER PRIMARY KEY," +
                ProjectEntry.PROJECT_KEY + " TEXT NOT NULL, " +
                ProjectEntry.NAME + " TEXT NOT NULL, " +
                ProjectEntry.THUMBNAIL_URL + " TEXT NULL " +
                " );";

        final String SQL_CREATE_USER_TABLE = "CREATE TABLE " + UserEntry.TABLE_NAME + " (" +
                UserEntry._ID + " INTEGER PRIMARY KEY," +
                UserEntry.USER_ID + " TEXT NOT NULL, " +
                UserEntry.NAME + " TEXT NOT NULL, " +
                UserEntry.THUMBNAIL_URL + " TEXT NULL " +
                " );";

        final String SQL_CREATE_ISSUE_TYPE_TABLE = "CREATE TABLE " + IssueTypeEntry.TABLE_NAME + " (" +
                IssueTypeEntry._ID + " INTEGER PRIMARY KEY," +
                IssueTypeEntry.PROJECT_ID + " INTEGER NOT NULL, " +
                IssueTypeEntry.NAME + " TEXT NOT NULL, " +
                IssueTypeEntry.COLOR + " TEXT NOT NULL, " +
                " FOREIGN KEY (" + IssueTypeEntry.PROJECT_ID + ") REFERENCES " +
                ProjectEntry.TABLE_NAME + " (" + ProjectEntry._ID + ")" +
                " );";


        final String SQL_CREATE_ISSUE_TABLE = "CREATE TABLE " + IssueEntry.TABLE_NAME + " (" +
                IssueEntry._ID + " INTEGER PRIMARY KEY," +
                IssueEntry.PROJECT_ID + " INTEGER NOT NULL, " +
                IssueEntry.TYPE_ID + " INTEGER NOT NULL, " +
                IssueEntry.ISSUE_KEY + " TEXT NOT NULL, " +
                IssueEntry.SUMMARY + " TEXT NOT NULL, " +
                IssueEntry.DESCRIPTION + " TEXT, " +
                IssueEntry.PRIORITY + " TEXT NOT NULL, " +
                IssueEntry.STATUS + " TEXT NOT NULL, " +
                IssueEntry.MILESTONES + " TEXT NULL, " +
                IssueEntry.URL + " TEXT NULL, " +
                IssueEntry.ASSIGNEE_ID + " INTEGER, " +
                IssueEntry.CREATED_USER_ID + " INTEGER NOT NULL, " +
                IssueEntry.CREATED_DATE + " TEXT NOT NULL, " +
                IssueEntry.UPDATED_USER_ID + " INTEGER, " +
                IssueEntry.UPDATED_DATE + " TEXT, " +
                " FOREIGN KEY (" + IssueEntry.PROJECT_ID + ") REFERENCES " +
                ProjectEntry.TABLE_NAME + " (" + ProjectEntry._ID + "), " +
                " FOREIGN KEY (" + IssueEntry.TYPE_ID + ") REFERENCES " +
                IssueTypeEntry.TABLE_NAME + " (" + IssueTypeEntry._ID + "), " +
                " FOREIGN KEY (" + IssueEntry.ASSIGNEE_ID + ") REFERENCES " +
                UserEntry.TABLE_NAME + " (" + UserEntry._ID + "), " +
                " FOREIGN KEY (" + IssueEntry.CREATED_USER_ID + ") REFERENCES " +
                UserEntry.TABLE_NAME + " (" + UserEntry._ID + "), " +
                " FOREIGN KEY (" + IssueEntry.UPDATED_USER_ID + ") REFERENCES " +
                UserEntry.TABLE_NAME + " (" + UserEntry._ID + ")" +
                " );";

        final String SQL_CREATE_COMMENT_TABLE = "CREATE TABLE " + CommentEntry.TABLE_NAME + " (" +
                CommentEntry._ID + " INTEGER PRIMARY KEY," +
                CommentEntry.ISSUE_ID + " INTEGER NOT NULL, " +
                CommentEntry.CONTENT + " TEXT NOT NULL, " +
                CommentEntry.CREATED_USER_ID + " INTEGER NOT NULL, " +
                CommentEntry.CREATED + " TEXT NOT NULL, " +
                CommentEntry.UPDATED + " TEXT NOT NULL, " +
                " FOREIGN KEY (" + CommentEntry.ISSUE_ID + ") REFERENCES " +
                IssueEntry.TABLE_NAME + " (" + IssueEntry._ID + "), " +
                " FOREIGN KEY (" + IssueEntry.CREATED_USER_ID + ") REFERENCES " +
                UserEntry.TABLE_NAME + " (" + UserEntry._ID + ")" +
                " );";

        final String SQL_CREATE_ISSUE_SUMMARY_VIEW = "CREATE VIEW " + IssuePreviewEntry.VIEW_NAME +
                " AS SELECT " +
                IssueEntry.TABLE_NAME + "." + IssueEntry._ID + ", " +
                IssueEntry.ISSUE_KEY + ", " +
                IssueEntry.PROJECT_ID + ", " +
                IssueEntry.SUMMARY + ", " +
                IssueEntry.PRIORITY + ", " +
                IssueEntry.STATUS + ", " +
                IssueEntry.CREATED_DATE + ", " +
                UserEntry.NAME + " AS " + IssuePreviewEntry.ASSIGNEE_NAME_ALIAS + ", " +
                UserEntry.THUMBNAIL_URL + " AS " + IssuePreviewEntry.ASSIGNEE_THUMBNAIL_URL_ALIAS +
                " FROM " + IssueEntry.TABLE_NAME + " LEFT JOIN " + UserEntry.TABLE_NAME +
                " ON " +
                IssueEntry.TABLE_NAME + "." + IssueEntry.ASSIGNEE_ID +
                " = " +
                UserEntry.TABLE_NAME + "." + UserEntry._ID;

        db.execSQL(SQL_CREATE_PROJECT_TABLE);
        db.execSQL(SQL_CREATE_USER_TABLE);
        db.execSQL(SQL_CREATE_ISSUE_TYPE_TABLE);
        db.execSQL(SQL_CREATE_ISSUE_TABLE);
        db.execSQL(SQL_CREATE_COMMENT_TABLE);
        db.execSQL(SQL_CREATE_ISSUE_SUMMARY_VIEW);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DROP_TABLE_IF_EXISTS + ProjectEntry.TABLE_NAME);
        db.execSQL(DROP_TABLE_IF_EXISTS + UserEntry.TABLE_NAME);
        db.execSQL(DROP_TABLE_IF_EXISTS + IssueTypeEntry.TABLE_NAME);
        db.execSQL(DROP_TABLE_IF_EXISTS + IssueEntry.TABLE_NAME);
        db.execSQL(DROP_TABLE_IF_EXISTS + CommentEntry.TABLE_NAME);
        db.execSQL("DROP VIEW IF EXISTS " + IssuePreviewEntry.VIEW_NAME);
        onCreate(db);
    }
}