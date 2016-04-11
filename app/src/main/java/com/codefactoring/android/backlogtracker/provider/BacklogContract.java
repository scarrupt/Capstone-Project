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

    public static final String PATH_ISSUES_PREVIEWS = "issues/previews";

    public static final String PATH_ISSUES_STATS = "issues/stats";

    public static final String PATH_ISSUE = "issues/#";

    public static final String PATH_ISSUE_COMMENTS = "issues/#/comments";

    public static final String PATH_COMMENTS = "comments";

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

        public static final String USER_PREFIX = TABLE_NAME + "_";

        public static final String ASSIGNEE_ALIAS = "ASSIGNEE";

        public static final String ASSIGNEE_PREFIX = ASSIGNEE_ALIAS + "_";

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

        public static final String PREFIX = TABLE_NAME + "_";

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_ISSUE_TYPES).build();

        public static final String CONTENT_TYPE =
                ProjectEntry.CONTENT_TYPE + "/" + PATH_ISSUE_TYPES;

        public static Uri buildIssueTypeFromProjectIdUri(long projectId) {
            return ProjectEntry.CONTENT_URI.buildUpon().appendPath(String.valueOf(projectId))
                    .appendPath(PATH_ISSUE_TYPES).build();
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

        public static Uri buildIssueUri(long issueId) {
            return IssueEntry.CONTENT_URI.buildUpon().appendPath(String.valueOf(issueId))
                    .build();
        }

        public static Uri buildIssuesWithProjectKey(String projectKey) {
            return CONTENT_URI
                    .buildUpon()
                    .appendQueryParameter(QUERY_PARAMETER_PROJECT_KEY, projectKey)
                    .build();
        }
    }

    public static final class IssuePreviewEntry {

        public static final String VIEW_NAME = "issue_preview";

        public static final String ASSIGNEE_NAME_ALIAS = "assignee_name";

        public static final String ASSIGNEE_THUMBNAIL_URL_ALIAS = "assignee_thumbnail_url";

        public static final String DEFAULT_SORT = IssueEntry.CREATED_DATE + " COLLATE NOCASE DESC";

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendEncodedPath(PATH_ISSUES_PREVIEWS).build();

        public static final String CONTENT_TYPE =
                CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_ISSUES_PREVIEWS;

        public static final String QUERY_PARAMETER_PROJECT_ID = "projectId";

        public static final String QUERY_PARAMETER_STATUS = "status";

        public static Uri buildIssuePreviewsWithProjectId(long projectId) {
            return CONTENT_URI
                    .buildUpon()
                    .appendQueryParameter(QUERY_PARAMETER_PROJECT_ID, String.valueOf(projectId))
                    .build();
        }

        public static Uri addStatusQueryParameterToUri(Uri uri, String status) {
            return uri
                    .buildUpon()
                    .appendQueryParameter(QUERY_PARAMETER_STATUS, status)
                    .build();
        }
    }

    public static final class IssueStatsEntry {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendEncodedPath(PATH_ISSUES_STATS).build();

        public static final String QUERY_PARAMETER_PROJECT_ID = "projectId";

        public static Uri buildIssueStatsUriWithProjectId(String projectId) {
            return CONTENT_URI
                    .buildUpon()
                    .appendQueryParameter(QUERY_PARAMETER_PROJECT_ID, projectId)
                    .build();
        }
    }

    interface CommentColumns extends BaseColumns {
        String ISSUE_ID = "issue_id";
        String CONTENT = "content";
        String CREATED_USER_ID = "created_user_id";
        String CREATED = "created";
        String UPDATED = "updated";
    }

    public static final class CommentEntry implements CommentColumns {

        public static final String TABLE_NAME = "comment";

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendEncodedPath(PATH_COMMENTS).build();

        public static final String CONTENT_TYPE =
                ProjectEntry.CONTENT_TYPE + "/" + PATH_COMMENTS;

        public static Uri buildCommentUriFromIssueIdUri(long issueId) {
            return IssueEntry.CONTENT_URI.buildUpon().appendPath(String.valueOf(issueId))
                    .appendPath(PATH_COMMENTS).build();
        }
    }
}