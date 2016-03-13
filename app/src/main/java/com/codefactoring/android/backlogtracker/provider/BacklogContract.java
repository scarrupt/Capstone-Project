package com.codefactoring.android.backlogtracker.provider;

import android.net.Uri;
import android.provider.BaseColumns;

import static android.content.ContentResolver.CURSOR_DIR_BASE_TYPE;
import static android.content.ContentResolver.CURSOR_ITEM_BASE_TYPE;

public class BacklogContract {

    public static final String CONTENT_AUTHORITY = "com.codefactoring.android.backlog.app";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_PROJECTS = "projects";

    public static final String PATH_USERS = "users";

    interface ProjectColumns extends BaseColumns {
        String PROJECT_KEY = "project_key";
        String NAME = "name";
        String THUMBNAIL_URL = "thumbnail_url";
    }

    public static final class ProjectEntry implements ProjectColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_PROJECTS).build();

        public static final String CONTENT_TYPE =
                CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PROJECTS;

        public static final String TABLE_NAME = "project";

        public static final String DEFAULT_SORT = NAME + " COLLATE NOCASE ASC";

        public static Uri buildProjectUri(long projectId) {
            return CONTENT_URI.buildUpon().appendPath(String.valueOf(projectId)).build();
        }
    }

    interface UserColumns extends BaseColumns {
        String USER_ID = "user_id";
        String NAME = "name";
        String THUMBNAIL_URL = "thumbnail_url";
    }

    public static final class UserEntry implements UserColumns {

        public static final String TABLE_NAME = "user";

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_USERS).build();

        public static final String CONTENT_TYPE =
                CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_USERS;

        public static final String CONTENT_ITEM_TYPE =
                CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_USERS;
    }
}