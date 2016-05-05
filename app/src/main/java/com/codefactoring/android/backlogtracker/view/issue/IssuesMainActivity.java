package com.codefactoring.android.backlogtracker.view.issue;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.codefactoring.android.backlogtracker.BacklogTrackerApplication;
import com.codefactoring.android.backlogtracker.Config;
import com.codefactoring.android.backlogtracker.R;
import com.codefactoring.android.backlogtracker.view.settings.SettingsActivity;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;

public class IssuesMainActivity extends AppCompatActivity implements IssueListFragment.OnFragmentInteractionListener {

    @Nullable
    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Inject
    Tracker mTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_issues_main);
        setTitle(R.string.title_activity_issues_main);
        initializeDependencyInjector();
        initializeAnalytics();
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        final String projectKey = getIntent().getStringExtra(Config.KEY_PROJECT_KEY);

        final ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setHomeButtonEnabled(true);
            supportActionBar.setDisplayHomeAsUpEnabled(true);
            supportActionBar.setTitle(projectKey);
        }

        final Uri mUri = getIntent().getData();

        if (savedInstanceState == null) {

            final IssuesMainFragment issuesMainFragment = IssuesMainFragment
                    .newInstance(mUri, getIntent().getStringExtra(Config.KEY_PROJECT_KEY));

            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.issues_main_container, issuesMainFragment)
                    .commit();

        }
    }

    private void initializeAnalytics() {
        mTracker.setScreenName(IssuesMainActivity.class.getSimpleName());
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    private void initializeDependencyInjector() {
        ((BacklogTrackerApplication) getApplication())
                .getApplicationComponent()
                .inject(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                supportFinishAfterTransition();
                return true;
            case R.id.action_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onIssueSelected(Uri uri, String issueKey, String issueUrl) {
        final Intent intent = new Intent(this, IssueDetailActivity.class).setData(uri);
        intent.putExtra(Config.KEY_ISSUE_KEY, issueKey);
        intent.putExtra(Config.KEY_ISSUE_URL, issueUrl);
        startActivity(intent);
    }
}