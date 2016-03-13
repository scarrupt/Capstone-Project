package com.codefactoring.android.backlogtracker.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.codefactoring.android.backlogapi.BacklogToolConfig;
import com.codefactoring.android.backlogtracker.injector.components.DaggerApplicationComponent;
import com.codefactoring.android.backlogtracker.injector.modules.ApplicationModule;
import com.codefactoring.android.backlogtracker.injector.modules.BacklogModule;

import javax.inject.Inject;

public class BacklogSyncService extends Service {

    private static final Object sSyncAdapterLock = new Object();
    private static BacklogSyncAdapter sBacklogSyncAdapter = null;

    @Inject
    BacklogSyncAdapter mBacklogSyncAdapter;

    @Override
    public void onCreate() {
        super.onCreate();

        if (mBacklogSyncAdapter == null) {
            DaggerApplicationComponent
                    .builder()
                    .applicationModule(new ApplicationModule(getApplication()))
                    .backlogModule(new BacklogModule(new BacklogToolConfig()))
                    .build()
                    .inject(this);
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBacklogSyncAdapter.getSyncAdapterBinder();
    }
}