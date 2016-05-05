package com.codefactoring.android.backlogtracker.view.project;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.codefactoring.android.backlogapi.BacklogToolConfig;
import com.codefactoring.android.backlogtracker.Config;
import com.codefactoring.android.backlogtracker.R;
import com.codefactoring.android.backlogtracker.gcm.RegistrationIntentService;
import com.codefactoring.android.backlogtracker.injector.components.DaggerApplicationComponent;
import com.codefactoring.android.backlogtracker.injector.modules.ApplicationModule;
import com.codefactoring.android.backlogtracker.injector.modules.BacklogModule;
import com.codefactoring.android.backlogtracker.sync.BacklogSyncAdapter;
import com.codefactoring.android.backlogtracker.view.account.AccountActivity;
import com.codefactoring.android.backlogtracker.view.issue.IssueDetailFragment;
import com.codefactoring.android.backlogtracker.view.issue.IssueListFragment;
import com.codefactoring.android.backlogtracker.view.issue.IssuesMainActivity;
import com.codefactoring.android.backlogtracker.view.issue.IssuesMainFragment;
import com.codefactoring.android.backlogtracker.view.settings.SettingsActivity;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import javax.inject.Inject;

public class ProjectListActivity extends AppCompatActivity implements
        ProjectListFragment.OnFragmentInteractionListener,
        IssueListFragment.OnFragmentInteractionListener {

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    private static final String TAG_NAME = ProjectListActivity.class.getSimpleName();

    private static final String ISSUES_MAIN_FRAGMENT_TAG = "IssuesMainFragmentTag";

    private static final String ISSUE_DETAIL_FRAGMENT_TAG = "IssueDetailFragmentTag";

    private boolean mTwoPane;

    @Inject
    BacklogSyncAdapter mBacklogSyncAdapter;

    @Inject
    AccountManager mAccountManager;

    @Inject
    Tracker mTracker;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_list);
        setTitle(R.string.title_activity_project_list);
        initializeDependencyInjector();

        if (findViewById(R.id.issues_main_container) != null) {
            mTwoPane = true;

            if (savedInstanceState == null) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.issues_main_container,
                                new IssuesMainFragment(),
                                ISSUES_MAIN_FRAGMENT_TAG)
                        .commit();
            }

        } else {
            mTwoPane = false;
        }

        mTracker.setScreenName(ProjectListActivity.class.getSimpleName());
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());

        if (isAuthenticated() && checkPlayServices()) {
            mBacklogSyncAdapter.startDataBootstrap();
            final Intent intent = new Intent(this, RegistrationIntentService.class);
            startService(intent);
        }
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
    public void onFragmentInteraction(Uri uri, String projectKey) {
        if (mTwoPane) {
            final IssuesMainFragment fragment = IssuesMainFragment.newInstance(uri, projectKey);

            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.issues_main_container, fragment, ISSUES_MAIN_FRAGMENT_TAG)
                    .commit();
        } else {
            final Intent intent = new Intent(this, IssuesMainActivity.class).setData(uri);
            intent.putExtra(Config.KEY_PROJECT_KEY, projectKey);
            startActivity(intent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final int itemId = item.getItemId();

        if (itemId == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
        }

        return super.onOptionsItemSelected(item);
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

    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Log.i(TAG_NAME, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

    @Override
    public void onIssueSelected(Uri uri, String issueKey, String issueUrl, View issueSummary) {
        final IssueDetailFragment fragment = IssueDetailFragment.newInstance(uri, issueKey);
        fragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.AppDialogTheme);
        fragment.show(getSupportFragmentManager(), ISSUE_DETAIL_FRAGMENT_TAG);
    }
}