package com.codefactoring.android.backlogtracker.view.issue;

import android.net.Uri;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.codefactoring.android.backlogtracker.R;

import butterknife.ButterKnife;

import static com.codefactoring.android.backlogapi.BacklogApiConstants.STATUS_ISSUE_IN_PROGRESS;
import static com.codefactoring.android.backlogapi.BacklogApiConstants.STATUS_ISSUE_OPEN;
import static com.codefactoring.android.backlogapi.BacklogApiConstants.STATUS_ISSUE_RESOLVED;
import static com.codefactoring.android.backlogtracker.provider.BacklogContract.*;

public class IssuesMainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_issues_main);
        setTitle(R.string.title_activity_issues_main);

        final ViewPager viewPager = ButterKnife.findById(this, R.id.pager_issues);
        final FragmentPagerAdapter pageAdapter = new IssuesMainPageAdapter(getSupportFragmentManager());
        viewPager.setAdapter(pageAdapter);
        final TabLayout tabLayout = ButterKnife.findById(this, R.id.sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);
    }

    private class IssuesMainPageAdapter extends FragmentPagerAdapter {

        private static final int TAB_COUNT = 3;

        private static final int TAB_OPEN_ISSUES = 0;

        private static final int TAB_IN_PROGRESS_ISSUES = 1;

        private static final int TAB_RESOLVED_ISSUES = 2;

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