package com.codefactoring.android.backlogapi;

import com.codefactoring.android.backlogapi.models.BacklogError;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class BacklogApiException extends RuntimeException {

    public static final int DEFAULT_ERROR_CODE = 0;

    private final BacklogError mBacklogError;

    public BacklogApiException(String responseBody) {
        mBacklogError = readHttpException(responseBody);
    }

    private BacklogError readHttpException(String responseBody) {
        return parseBacklogError(responseBody);
    }

    public int getErrorCode() {
        return mBacklogError == null ? DEFAULT_ERROR_CODE : mBacklogError.getCode();
    }

    private BacklogError parseBacklogError(String response) {
        final Gson gson = new Gson();

        try {
            final JsonObject responseObj = new JsonParser().parse(response).getAsJsonObject();
            final BacklogError[] backlogErrors = gson.fromJson(
                    responseObj.get(BacklogApiConstants.ERRORS), BacklogError[].class);

            if (backlogErrors.length == 0) {
                return null;
            } else {
                return backlogErrors[0];
            }
        } catch (Exception exception) {
            return null;
        }
    }

    public BacklogError getBacklogError() {
        return mBacklogError;
    }
}