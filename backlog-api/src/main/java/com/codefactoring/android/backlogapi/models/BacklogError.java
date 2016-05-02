package com.codefactoring.android.backlogapi.models;

import com.google.gson.annotations.SerializedName;

/**
 * Represents error returned from the Backlog API. For complete list of error codes, see
 * http://developer.nulab-inc.com/docs/backlog/error-response
 */
public class BacklogError {

    @SerializedName("message")
    private final String message;

    @SerializedName("code")
    private final int code;

    @SerializedName("moreInfo")
    private final String moreInfo;

    public BacklogError(String message, int code, String moreInfo) {
        this.message = message;
        this.code = code;
        this.moreInfo = moreInfo;
    }

    public String getMessage() {
        return message;
    }

    public int getCode() {
        return code;
    }

    public String getMoreInfo() {
        return moreInfo;
    }
}