package com.codefactoring.android.backlogtracker.view.issue;


import android.content.Context;
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
import android.widget.AdapterView;
import android.widget.ListView;

import com.codefactoring.android.backlogtracker.R;
import com.codefactoring.android.backlogtracker.provider.BacklogContract;

public class IssueListFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String ARG_ISSUE_LIST_URI = "projectId";
    private static final int ISSUE_LIST_LOADER = 0;

    private static final String[] ISSUE_COLUMNS = {
            BacklogContract.IssueEntry._ID,
            BacklogContract.IssueEntry.ISSUE_KEY,
            BacklogContract.IssueEntry.SUMMARY,
            BacklogContract.IssueEntry.PRIORITY,
            BacklogContract.IssuePreviewEntry.ASSIGNEE_NAME_ALIAS,
            BacklogContract.IssuePreviewEntry.ASSIGNEE_THUMBNAIL_URL_ALIAS,
    };

    private Uri mIssueListUri;
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
            mIssueListUri = getArguments().getParcelable(ARG_ISSUE_LIST_URI);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mIssueAdapter = new IssueAdapter(getActivity(), null, 0);

        final View rootView = inflater.inflate(R.layout.fragment_issue_list, container, false);

        final ListView listView = (ListView) rootView.findViewById(R.id.listview_issue);
        listView.setAdapter(mIssueAdapter);
        listView.setEmptyView(rootView.findViewById(R.id.text_empty_issues));

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final Cursor cursor = (Cursor) parent.getItemAtPosition(position);
                if (cursor != null && mListener != null) {
                    mListener.onFragmentInteraction(BacklogContract.IssueEntry
                            .buildIssueUriFromIssueId(cursor.getString(
                                    cursor.getColumnIndex(BacklogContract.IssueEntry._ID))));
                }
            }
        });

        return rootView;
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
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getActivity(),
                mIssueListUri,
                ISSUE_COLUMNS,
                null,
                null,
                BacklogContract.IssueEntry.DEFAULT_SORT);
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