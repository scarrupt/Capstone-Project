package com.codefactoring.android.backlogtracker.view.issue;


import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codefactoring.android.backlogtracker.R;
import com.codefactoring.android.backlogtracker.provider.BacklogContract;
import com.codefactoring.android.backlogtracker.view.issue.widget.BarChartView;

import butterknife.Bind;
import butterknife.ButterKnife;

public class IssueStatsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String ARG_PROJECT_ID = "projectId";
    private static final int ISSUE_STATS_LOADER = 0;

    @Bind(R.id.chart_issues_stats)
    BarChartView barChartView;

    private Uri mUri;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mUri = getArguments().getParcelable(ARG_PROJECT_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_issue_stats, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(ISSUE_STATS_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (mUri == null) {
            return null;
        } else {
            return new CursorLoader(getActivity(),
                    mUri,
                    null,
                    null,
                    null,
                    BacklogContract.IssueEntry.DEFAULT_SORT);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null && data.moveToFirst()) {
            barChartView.setCountOpenIssues(data.getInt(0));
            data.moveToNext();
            barChartView.setCountInProgressIssues(data.getInt(0));
            data.moveToNext();
            barChartView.setCountResolvedIssues(data.getInt(0));
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    public static IssueStatsFragment newInstance(Uri uri) {
        final IssueStatsFragment fragment = new IssueStatsFragment();
        final Bundle args = new Bundle();
        args.putParcelable(ARG_PROJECT_ID, uri);
        fragment.setArguments(args);
        return fragment;
    }
}
