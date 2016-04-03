package com.codefactoring.android.backlogtracker.view.project;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.codefactoring.android.backlogapi.BacklogToolConfig;
import com.codefactoring.android.backlogtracker.R;
import com.codefactoring.android.backlogtracker.injector.components.DaggerApplicationComponent;
import com.codefactoring.android.backlogtracker.injector.modules.ApplicationModule;
import com.codefactoring.android.backlogtracker.injector.modules.BacklogModule;
import com.codefactoring.android.backlogtracker.sync.BacklogSyncAdapter;
import com.codefactoring.android.backlogtracker.view.account.AccountActivity;
import com.codefactoring.android.backlogtracker.view.issue.IssuesMainActivity;

import javax.inject.Inject;

public class ProjectListActivity extends AppCompatActivity implements ProjectListFragment.OnFragmentInteractionListener {

    @Inject
    BacklogSyncAdapter mBacklogSyncAdapter;

    @Inject
    AccountManager mAccountManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_list);
        setTitle(R.string.title_activity_project_list);

        DaggerApplicationComponent
                .builder()
                .applicationModule(new ApplicationModule(getApplication()))
                .backlogModule(new BacklogModule(new BacklogToolConfig()))
                .build()
                .inject(this);

        if (isAuthenticated()) {
            mBacklogSyncAdapter.syncImmediately();
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
        final Intent intent = new Intent(this, IssuesMainActivity.class).setData(uri);
        startActivity(intent);
    }

    public boolean isAuthenticated() {
        final Account[] accounts = mAccountManager.getAccountsByType(getApplicationContext()
                .getString(R.string.account_type));

        if (accounts.length == 0) {
            final Intent intent = AccountActivity.makeIntent(getApplicationContext());
            startActivity(intent);
            finish();
            return false;
        }

        return true;
    }

    public static Intent makeIntent(Context context) {
        return new Intent(context, ProjectListActivity.class);
    }
}
