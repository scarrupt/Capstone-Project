package com.codefactoring.android.backlogtracker.view.issue;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ShareCompat;
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
import butterknife.OnClick;

public class IssueDetailActivity extends AppCompatActivity {

    private static final String ISSUE_DETAIL_FRAGMENT_TAG = "IssueDetailFragmentTag";

    @Nullable
    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Nullable
    @Bind(R.id.collapsing_toolbar)
    CollapsingToolbarLayout collapsingToolbar;

    @Bind(R.id.share_fab)
    FloatingActionButton fab;

    @Inject
    Tracker mTracker;

    private String mUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_issue_detail);
        initializeDependencyInjector();
        initializeAnalytics();
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        final ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setDisplayHomeAsUpEnabled(true);
        }

        final String issueKey = getIntent().getStringExtra(Config.KEY_ISSUE_KEY);
        mUrl = getIntent().getStringExtra(Config.KEY_ISSUE_URL);

        if (collapsingToolbar != null) {
            collapsingToolbar.setTitle(issueKey);
        }

        if (savedInstanceState == null) {
            final Uri data = getIntent().getData();
            final IssueDetailFragment issueDetailFragment = IssueDetailFragment.newInstance(data, issueKey);

            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.issue_detail_container, issueDetailFragment, ISSUE_DETAIL_FRAGMENT_TAG)
                    .commit();
        }
    }

    private void initializeAnalytics() {
        mTracker.setScreenName(IssueDetailActivity.class.getSimpleName());
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    private void initializeDependencyInjector() {
        ((BacklogTrackerApplication) getApplication())
                .getApplicationComponent()
                .inject(this);
    }

    @OnClick(R.id.share_fab)
    void onFabClick() {
        startActivity(Intent.createChooser(ShareCompat
                .IntentBuilder
                .from(this)
                .setType("text/plain")
                .setText(mUrl)
                .getIntent(), getString(R.string.action_share)));
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
}