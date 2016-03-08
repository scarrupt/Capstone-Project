package com.codefactoring.android.backlogapi;

public class BacklogToolConfig {

    public String getBaseURL(String spaceKey) {
        return "https://" + spaceKey + ".backlogtool.com";
    }
}
