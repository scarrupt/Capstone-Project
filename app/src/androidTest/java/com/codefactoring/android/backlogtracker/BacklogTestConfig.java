package com.codefactoring.android.backlogtracker;

import com.codefactoring.android.backlogapi.BacklogToolConfig;

public class BacklogTestConfig extends BacklogToolConfig {

    private String mUrl;

    @Override
    public String getBaseURL(String spaceKey) {
        return mUrl;
    }

    public void setUrl(String mUrl) {
        this.mUrl = mUrl;
    }
}
