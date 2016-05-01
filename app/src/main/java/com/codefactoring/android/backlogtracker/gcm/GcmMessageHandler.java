package com.codefactoring.android.backlogtracker.gcm;

import android.os.Bundle;

import com.codefactoring.android.backlogapi.BacklogToolConfig;
import com.codefactoring.android.backlogtracker.injector.components.DaggerApplicationComponent;
import com.codefactoring.android.backlogtracker.injector.modules.ApplicationModule;
import com.codefactoring.android.backlogtracker.injector.modules.BacklogModule;
import com.codefactoring.android.backlogtracker.sync.BacklogSyncAdapter;
import com.google.android.gms.gcm.GcmListenerService;

import javax.inject.Inject;

public class GcmMessageHandler extends GcmListenerService {

    @Inject
    BacklogSyncAdapter mBacklogSyncAdapter;

    @Override
    public void onCreate() {
        super.onCreate();
        initializeDependencyInjector();

    }

    private void initializeDependencyInjector() {
        DaggerApplicationComponent
                .builder()
                .applicationModule(new ApplicationModule(getApplication()))
                .backlogModule(new BacklogModule(new BacklogToolConfig()))
                .build()
                .inject(this);
    }

    @Override
    public void onMessageReceived(String from, Bundle data) {
        mBacklogSyncAdapter.syncImmediately();
    }
}
