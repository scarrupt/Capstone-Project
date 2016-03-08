package com.codefactoring.android.backlogtracker;

import com.codefactoring.android.backlogtracker.injector.components.DaggerTestApplicationComponent;
import com.codefactoring.android.backlogtracker.injector.components.TestApplicationComponent;
import com.codefactoring.android.backlogtracker.injector.modules.ApplicationModule;
import com.codefactoring.android.backlogtracker.injector.modules.BacklogModule;

public class TestApplication extends BacklogTrackerApplication {

    private TestApplicationComponent mTestApplicationComponent;

    private BacklogTestConfig mBacklogToolConfig;

    @Override
    public void onCreate() {
        super.onCreate();
        mBacklogToolConfig = new BacklogTestConfig();
        mTestApplicationComponent = DaggerTestApplicationComponent
                .builder()
                .applicationModule(new ApplicationModule(this))
                .backlogModule(new BacklogModule(mBacklogToolConfig))
                .build();
    }

    @Override
    public TestApplicationComponent getApplicationComponent() {
        return mTestApplicationComponent;
    }

    public BacklogTestConfig getBacklogTestConfig() {
        return mBacklogToolConfig;
    }
}