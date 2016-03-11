package com.codefactoring.android.backlogtracker.sync.fetchers;

import com.codefactoring.android.backlogapi.BacklogToolConfig;

public class BacklogTestConfig extends BacklogToolConfig {
    @Override
    public String getBaseURL(String spaceUrl) {
        return spaceUrl;
    }
}