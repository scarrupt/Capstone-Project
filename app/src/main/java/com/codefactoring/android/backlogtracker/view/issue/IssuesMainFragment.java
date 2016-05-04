package com.codefactoring.android.backlogtracker.view.issue;


import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codefactoring.android.backlogtracker.R;
import com.codefactoring.android.backlogtracker.provider.BacklogContract;

import butterknife.Bind;
import butterknife.ButterKnife;

import static com.codefactoring.android.backlogapi.BacklogApiConstants.STATUS_ISSUE_IN_PROGRESS;
import static com.codefactoring.android.backlogapi.BacklogApiConstants.STATUS_ISSUE_OPEN;
import static com.codefactoring.android.backlogapi.BacklogApiConstants.STATUS_ISSUE_RESOLVED;

public class IssuesMainFragment extends Fragment {

    private static final String ARG_URI = "ARG_URI";

    private static final String ARG_PROJECT_KEY = "ARG_PROJECT_KEY";

    @Bind(R.id.pager_issues)
    ViewPager viewPager;

    @Bind(R.id.sliding_tabs)
    TabLayout tabLayout;

    public static IssuesMainFragment newInstance(Uri uri, String projectKey) {
        final IssuesMainFragment fragment = new IssuesMainFragment();
        final Bundle args = new Bundle();
        args.putParcelable(ARG_URI, uri);
        args.putString(ARG_PROJECT_KEY, projectKey);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.fragment_issues_main, container, false);
        ButterKnife.bind(this, rootView);

        viewPager.setAdapter(new IssuesMainPageAdapter(getChildFragmentManager()));
        tabLayout.setupWithViewPager(viewPager);

        return rootView;
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

            if (getArguments() != null) {

                final Uri uri = getArguments().getParcelable(ARG_URI);

                if (uri != null) {
                    switch (position) {
                        case TAB_OPEN_ISSUES: {
                            final Uri filteredUri = BacklogContract.IssuePreviewEntry.addStatusQueryParameterToUri(uri,
                                    STATUS_ISSUE_OPEN);
                            return IssueListFragment.newInstance(filteredUri);
                        }
                        case TAB_IN_PROGRESS_ISSUES: {
                            final Uri filteredUri = BacklogContract.IssuePreviewEntry.addStatusQueryParameterToUri(uri,
                                    STATUS_ISSUE_IN_PROGRESS);
                            return IssueListFragment.newInstance(filteredUri);
                        }
                        case TAB_RESOLVED_ISSUES: {
                            final Uri filteredUri = BacklogContract.IssuePreviewEntry.addStatusQueryParameterToUri(uri,
                                    STATUS_ISSUE_RESOLVED);
                            return IssueListFragment.newInstance(filteredUri);
                        }
                        case TAB_ISSUE_STATS:
                            final String projectId = uri.getQueryParameter(BacklogContract.IssueStatsEntry.QUERY_PARAMETER_PROJECT_ID);
                            return IssueStatsFragment.newInstance(BacklogContract.IssueStatsEntry.buildIssueStatsUriWithProjectId(projectId));
                        default:
                            throw new IllegalArgumentException();
                    }
                }
            }

            return new IssueListFragment();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case TAB_OPEN_ISSUES:
                    return IssuesMainFragment.this.getString(R.string.title_open_issues);
                case TAB_IN_PROGRESS_ISSUES:
                    return IssuesMainFragment.this.getString(R.string.title_in_progress_issues);
                case TAB_RESOLVED_ISSUES:
                    return IssuesMainFragment.this.getString(R.string.title_resolved_issues);
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
