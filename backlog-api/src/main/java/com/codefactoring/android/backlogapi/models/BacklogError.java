package com.codefactoring.android.backlogapi.models;

import com.google.gson.annotations.SerializedName;

/**
 * Represents error returned from the Backlog API. For complete list of error codes, see
 * http://developer.nulab-inc.com/docs/backlog/error-response
 */
public class BacklogError {

    @SerializedName("message")
    private String message;

    @SerializedName("code")
    private int code;

    @SerializedName("moreInfo")
    private String moreInfo;

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