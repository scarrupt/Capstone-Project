package com.codefactoring.android.backlogtracker.view.issue;


import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codefactoring.android.backlogtracker.R;
import com.codefactoring.android.backlogtracker.provider.BacklogContract;

import butterknife.Bind;
import butterknife.ButterKnife;

public class IssueListFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String ARG_ISSUE_LIST_URI = "projectId";

    private static final int ISSUE_LIST_LOADER = 200;

    private static final String[] ISSUE_COLUMNS = {
            BacklogContract.IssueEntry._ID,
            BacklogContract.IssueEntry.ISSUE_KEY,
            BacklogContract.IssueEntry.SUMMARY,
            BacklogContract.IssueEntry.PRIORITY,
            BacklogContract.IssueEntry.URL,
            BacklogContract.IssuePreviewEntry.ASSIGNEE_NAME_ALIAS,
            BacklogContract.IssuePreviewEntry.ASSIGNEE_THUMBNAIL_URL_ALIAS,
    };

    public static final int COL_ISSUE_ID = 0;
    public static final int COL_ISSUE_KEY = 1;
    public static final int COL_URL = 4;

    @Bind(R.id.recycler_view_issues)
    RecyclerView mRecyclerView;

    @Bind(R.id.text_empty_issues)
    View mEmptyView;

    private Uri mUri;

    private IssueAdapter mIssueAdapter;

    private OnFragmentInteractionListener mListener;

    public static IssueListFragment newInstance(Uri issueListUri) {
        final IssueListFragment fragment = new IssueListFragment();
        final Bundle args = new Bundle();
        args.putParcelable(ARG_ISSUE_LIST_URI, issueListUri);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mUri = getArguments().getParcelable(ARG_ISSUE_LIST_URI);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_issue_list, container, false);
        ButterKnife.bind(this, view);

        mIssueAdapter = new IssueAdapter(getActivity(), mEmptyView, new IssueAdapter.IssueAdapterOnClickHandler() {

            @Override
            public void onClick(String issueId, String issueKey, String issueUrl, IssueAdapter.IssuerAdapterViewHolder viewHolder) {
                mListener.onIssueSelected(
                        BacklogContract.IssueEntry.buildIssueUriFromIssueId(issueId),
                        issueKey,
                        issueUrl,
                        viewHolder.issueSummaryView);
            }
        });

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(mIssueAdapter);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(ISSUE_LIST_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void onIssueSelected(Uri uri, String issueKey, String issueUrl, View issueSummary);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (mUri != null) {
            return new CursorLoader(getActivity(),
                    mUri,
                    ISSUE_COLUMNS,
                    null,
                    null,
                    BacklogContract.IssueEntry.DEFAULT_SORT);
        }

        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mIssueAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mIssueAdapter.swapCursor(null);
    }
}