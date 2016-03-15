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

    public static final String PATH_ISSUE_TYPES = "issueTypes";

    public static final String PATH_PROJECT_ISSUE_TYPES = "projects/#/issueTypes";

    public static final String PATH_ISSUES = "issues";

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

    interface IssueTypeColumns extends BaseColumns {
        String PROJECT_ID = "project_id";
        String NAME = "name";
        String COLOR = "color";
    }

    public static final class IssueTypeEntry implements IssueTypeColumns {

        public static final String TABLE_NAME = "issue_type";

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_ISSUE_TYPES).build();

        public static final String CONTENT_TYPE =
                ProjectEntry.CONTENT_TYPE +  "/" + PATH_ISSUE_TYPES;

        public static Uri buildIssueTypeUri(String issueTypeId) {
            return ProjectEntry.CONTENT_URI.buildUpon().appendPath(issueTypeId).appendPath(PATH_ISSUE_TYPES).build();
        }
    }

    interface IssueColumns extends BaseColumns {
        String PROJECT_ID = "project_id";
        String TYPE_ID = "type_id";
        String ISSUE_KEY = "issue_key";
        String SUMMARY = "summary";
        String DESCRIPTION = "description";
        String PRIORITY = "priority";
        String STATUS = "status";
        String MILESTONES = "milestones";
        String ASSIGNEE_ID = "assignee_id";
        String CREATED_USER_ID = "created_user_id";
        String CREATED_DATE = "created_date";
        String UPDATED_USER_ID = "updated_user_id";
        String UPDATED_DATE = "updated_date";
    }

    public static final class IssueEntry implements IssueColumns {

        public static final String TABLE_NAME = "issue";

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_ISSUES).build();

        public static final String CONTENT_TYPE =
                CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_ISSUES;

        public static final String CONTENT_ITEM_TYPE =
                CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_ISSUES;

        public static final String QUERY_PARAMETER_PROJECT_KEY = "projectKey";

        public static final String QUERY_PARAMETER_STATUS = "status";

        public static final String DEFAULT_SORT = CREATED_DATE + " COLLATE NOCASE DESC";

        public static Uri buildIssuesWithProjectKey(String projectKey) {
            return CONTENT_URI
                    .buildUpon()
                    .appendQueryParameter(QUERY_PARAMETER_PROJECT_KEY, projectKey)
                    .build();
        }
    }
}