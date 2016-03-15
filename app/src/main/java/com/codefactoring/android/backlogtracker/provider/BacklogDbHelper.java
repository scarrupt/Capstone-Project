package com.codefactoring.android.backlogtracker.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.codefactoring.android.backlogtracker.provider.BacklogContract.*;
import static com.codefactoring.android.backlogtracker.provider.BacklogContract.ProjectEntry;

public class BacklogDbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
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

        db.execSQL(SQL_CREATE_PROJECT_TABLE);
        db.execSQL(SQL_CREATE_USER_TABLE);
        db.execSQL(SQL_CREATE_ISSUE_TYPE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DROP_TABLE_IF_EXISTS + ProjectEntry.TABLE_NAME);
        db.execSQL(DROP_TABLE_IF_EXISTS + UserEntry.TABLE_NAME);
        db.execSQL(DROP_TABLE_IF_EXISTS + IssueTypeEntry.TABLE_NAME);
        onCreate(db);
    }
}
