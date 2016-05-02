package com.codefactoring.android.backlogtracker.view.issue;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
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

import butterknife.ButterKnife;

import static com.codefactoring.android.backlogapi.BacklogApiConstants.STATUS_ISSUE_IN_PROGRESS;
import static com.codefactoring.android.backlogapi.BacklogApiConstants.STATUS_ISSUE_OPEN;
import static com.codefactoring.android.backlogapi.BacklogApiConstants.STATUS_ISSUE_RESOLVED;
import static com.codefactoring.android.backlogtracker.provider.BacklogContract.IssuePreviewEntry;
import static com.codefactoring.android.backlogtracker.provider.BacklogContract.IssueStatsEntry;

public class IssuesMainActivity extends AppCompatActivity implements IssueListFragment.OnFragmentInteractionListener {

    @Inject
    Tracker mTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_issues_main);
        setTitle(R.string.title_activity_issues_main);

        if (getIntent() != null && getIntent().hasExtra(Intent.EXTRA_TEXT)) {
            final String projectKey = getIntent().getStringExtra(Intent.EXTRA_TEXT);
            final ActionBar supportActionBar = getSupportActionBar();
            if (supportActionBar != null) {
                supportActionBar.setSubtitle(projectKey);
            }
        }

        initializeDependencyInjector();

        final ViewPager viewPager = ButterKnife.findById(this, R.id.pager_issues);
        final FragmentPagerAdapter pageAdapter = new IssuesMainPageAdapter(getSupportFragmentManager());
        viewPager.setAdapter(pageAdapter);
        final TabLayout tabLayout = ButterKnife.findById(this, R.id.sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);

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

    private class IssuesMainPageAdapter extends FragmentPagerAdapter {

        private static final int TAB_COUNT = 4;

        private static final int TAB_OPEN_ISSUES = 0;

        private static final int TAB_IN_PROGRESS_ISSUES = 1;

        private static final int TAB_RESOLVED_ISSUES = 2;

        private static final int TAB_ISSUE_STATS = 3;

        public IssuesMainPageAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case TAB_OPEN_ISSUES: {
                    final Uri filteredUri = IssuePreviewEntry.addStatusQueryParameterToUri(getIntent().getData(),
                            STATUS_ISSUE_OPEN);
                    return IssueListFragment.newInstance(filteredUri);
                }
                case TAB_IN_PROGRESS_ISSUES: {
                    final Uri filteredUri = IssuePreviewEntry.addStatusQueryParameterToUri(getIntent().getData(),
                            STATUS_ISSUE_IN_PROGRESS);
                    return IssueListFragment.newInstance(filteredUri);
                }
                case TAB_RESOLVED_ISSUES: {
                    final Uri filteredUri = IssuePreviewEntry.addStatusQueryParameterToUri(getIntent().getData(),
                            STATUS_ISSUE_RESOLVED);
                    return IssueListFragment.newInstance(filteredUri);
                }
                case TAB_ISSUE_STATS:
                    final String projectId = getIntent().getData().getQueryParameter(IssueStatsEntry.QUERY_PARAMETER_PROJECT_ID);
                    return IssueStatsFragment.newInstance(IssueStatsEntry.buildIssueStatsUriWithProjectId(projectId));
                default:
                    throw new IllegalArgumentException();
            }
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case TAB_OPEN_ISSUES:
                    return IssuesMainActivity.this.getString(R.string.title_open_issues);
                case TAB_IN_PROGRESS_ISSUES:
                    return IssuesMainActivity.this.getString(R.string.title_in_progress_issues);
                case TAB_RESOLVED_ISSUES:
                    return IssuesMainActivity.this.getString(R.string.title_resolved_issues);
                case TAB_ISSUE_STATS:
                    return getString(R.string.title_issue_stats);
                default:
                    throw new IllegalArgumentException();
            }
        }

        @Override
        public int getCount() {
            return TAB_COUNT;
        }
    }
}