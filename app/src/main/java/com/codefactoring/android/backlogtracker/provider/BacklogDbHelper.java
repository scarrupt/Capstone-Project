package com.codefactoring.android.backlogtracker.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.codefactoring.android.backlogtracker.provider.BacklogContract.ProjectEntry;

public class BacklogDbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;

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

        db.execSQL(SQL_CREATE_PROJECT_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + ProjectEntry.TABLE_NAME);
        onCreate(db);
    }
}
