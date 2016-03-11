package com.codefactoring.android.backlogapi;

public class BacklogTestConfig extends BacklogToolConfig {
    @Override
    public String getBaseURL(String spaceUrl) {
        return spaceUrl;
    }
}