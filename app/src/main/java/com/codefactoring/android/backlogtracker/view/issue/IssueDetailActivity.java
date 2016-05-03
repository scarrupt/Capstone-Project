package com.codefactoring.android.backlogtracker.view.issue;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

import com.codefactoring.android.backlogtracker.BacklogTrackerApplication;
import com.codefactoring.android.backlogtracker.R;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import javax.inject.Inject;

public class IssueDetailActivity extends AppCompatActivity {

    private static final String ISSUE_DETAIL_FRAGMENT_TAG = "IssueDetailFragmentTag";

    @Inject
    Tracker mTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_issue_detail);
        setTitle(R.string.title_activity_issue_detail);
        initializeDependencyInjector();
        mTracker.setScreenName(IssueDetailActivity.class.getSimpleName());
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());

        if (savedInstanceState == null) {
            if (getIntent() != null && getIntent().hasExtra(Intent.EXTRA_TEXT)) {
                final String issueKey = getIntent().getStringExtra(Intent.EXTRA_TEXT);
                final ActionBar supportActionBar = getSupportActionBar();
                if (supportActionBar != null) {
                    supportActionBar.setSubtitle(issueKey);
                }

                final IssueDetailFragment issueDetailFragment = IssueDetailFragment.newInstance(getIntent().getData(), issueKey);

                getSupportFragmentManager()
                        .beginTransaction()
                        .add(R.id.issue_detail_container, issueDetailFragment, ISSUE_DETAIL_FRAGMENT_TAG)
                        .commit();
            }
        }
    }

    private void initializeDependencyInjector() {
        ((BacklogTrackerApplication) getApplication())
                .getApplicationComponent()
                .inject(this);
    }
}
