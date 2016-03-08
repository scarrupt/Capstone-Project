package com.codefactoring.android.backlogtracker.injector.modules;


import com.codefactoring.android.backlogapi.BacklogApiClient;
import com.codefactoring.android.backlogapi.BacklogToolConfig;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class BacklogModule {

    private final BacklogToolConfig mBacklogToolConfig;

    public BacklogModule(BacklogToolConfig backlogToolConfig) {
        mBacklogToolConfig = backlogToolConfig;
    }

    @Provides
    @Singleton
    public BacklogApiClient providesBacklogApiClient() {
        return new BacklogApiClient(mBacklogToolConfig);
    }
}
