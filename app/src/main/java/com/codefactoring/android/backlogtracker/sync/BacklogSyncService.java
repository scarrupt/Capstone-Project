package com.codefactoring.android.backlogtracker.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import javax.inject.Inject;

public class BacklogSyncService extends Service {

    @Inject
    BacklogSyncAdapter backlogSyncAdapter;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return backlogSyncAdapter.getSyncAdapterBinder();
    }
}