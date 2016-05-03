package com.codefactoring.android.backlogtracker.view.issue;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.codefactoring.android.backlogtracker.BacklogTrackerApplication;
import com.codefactoring.android.backlogtracker.R;
import com.codefactoring.android.backlogtracker.view.settings.SettingsActivity;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import javax.inject.Inject;

public class IssuesMainActivity extends AppCompatActivity implements IssueListFragment.OnFragmentInteractionListener {

    @Inject
    Tracker mTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_issues_main);
        setTitle(R.string.title_activity_issues_main);
        initializeDependencyInjector();

        if (getIntent() != null && getIntent().hasExtra(Intent.EXTRA_TEXT)) {
            final String projectKey = getIntent().getStringExtra(Intent.EXTRA_TEXT);
            final ActionBar supportActionBar = getSupportActionBar();
            if (supportActionBar != null) {
                supportActionBar.setSubtitle(projectKey);
            }
        }

        if (savedInstanceState == null && getIntent() != null) {
            final IssuesMainFragment issuesMainFragment = IssuesMainFragment.newInstance(getIntent().getData());
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.issues_main_container, issuesMainFragment)
                    .commit();
        }

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
        final int itemId = item.getItemId();

        if (itemId == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFragmentInteraction(Uri uri, String issueKey) {
        final Intent intent = new Intent(this, IssueDetailActivity.class).setData(uri);
        intent.putExtra(Intent.EXTRA_TEXT, issueKey);
        startActivity(intent);
    }
}