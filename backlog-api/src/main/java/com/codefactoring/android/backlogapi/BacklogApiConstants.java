package com.codefactoring.android.backlogapi;

public interface BacklogApiConstants {
    String DATE_FORMAT_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    String BACKLOG_API_ENDPOINT = "/api/v2/";
    String API_KEY_PARAMETER = "apiKey";
    String ERRORS = "errors";
    String STATUS_ISSUE_OPEN = "Open";
    int STATUS_ISSUE_ID_OPEN = 1;
    String STATUS_ISSUE_IN_PROGRESS = "In Progress";
    int STATUS_ISSUE_ID_IN_PROGRESS = 2;
    String STATUS_ISSUE_RESOLVED = "Resolved";
    int STATUS_ISSUE_ID_RESOLVED = 3;
}