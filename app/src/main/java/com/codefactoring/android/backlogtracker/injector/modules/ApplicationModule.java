package com.codefactoring.android.backlogtracker.injector.modules;

import android.accounts.AccountManager;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.codefactoring.android.backlogapi.BacklogApiClient;
import com.codefactoring.android.backlogtracker.R;
import com.codefactoring.android.backlogtracker.authenticator.BacklogAuthenticator;
import com.codefactoring.android.backlogtracker.sync.BacklogSyncAdapter;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class ApplicationModule {

    private Application mApplication;

    public ApplicationModule(Application application) {
        mApplication = application;
    }

    @Provides
    @Singleton
    public Context providesContext() {
        return mApplication;
    }

    @Provides
    @Singleton
    public AccountManager accountManager(Context context) {
        return AccountManager.get(context);
    }

    @Provides
    @Singleton
    public BacklogAuthenticator backlogAuthenticator(Context context) {
        return new BacklogAuthenticator(context);
    }

    @Provides
    @Singleton
    public SharedPreferences sharedPreferences(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    @Provides
    @Singleton
    public BacklogSyncAdapter backlogSyncAdapter(Context context, AccountManager accountManager,
                                                 BacklogApiClient backlogApiClient, SharedPreferences sharedPreferences) {
        return new BacklogSyncAdapter(context, true, accountManager, backlogApiClient, sharedPreferences);
    }

    @Provides
    @Singleton
    public Tracker tracker(Context context) {
        return GoogleAnalytics.getInstance(context).newTracker(R.xml.global_tracker);
    }
}
