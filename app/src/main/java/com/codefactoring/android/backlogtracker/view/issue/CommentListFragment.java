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
import android.widget.ListView;

import com.codefactoring.android.backlogtracker.R;
import com.codefactoring.android.backlogtracker.provider.BacklogContract;

public class CommentListFragment extends Fragment  implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int LOADER = 1;

    private static final String ARG_URI = "arg_uri";

    private static final String[] COMMENT_COLUMNS = {
            BacklogContract.CommentEntry.TABLE_NAME + "." +
            BacklogContract.CommentEntry._ID,
            BacklogContract.UserEntry.NAME,
            BacklogContract.UserEntry.THUMBNAIL_URL,
            BacklogContract.CommentEntry.CREATED,
            BacklogContract.CommentEntry.CONTENT
    };

    private CommentAdapter mCommentAdapter;
    private Uri mUri;

    public static CommentListFragment newInstance(Uri uri) {
        final CommentListFragment fragment = new CommentListFragment();
        final Bundle args = new Bundle();
        args.putParcelable(ARG_URI, uri);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getActivity().getIntent() != null) {
            mUri = getActivity().getIntent().getData();
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mCommentAdapter = new CommentAdapter(getActivity(), null, 0);

        final View rootView = inflater.inflate(R.layout.fragment_comment_list, container, false);

        final ListView listView = (ListView) rootView.findViewById(R.id.listview_comment);
        listView.setAdapter(mCommentAdapter);
        listView.setEmptyView(rootView.findViewById(R.id.text_empty_comments));

        return rootView;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (mUri != null) {
            return new CursorLoader(getActivity(),
                    BacklogContract.CommentEntry.buildCommentUriFromIssueUri(mUri),
                    COMMENT_COLUMNS,
                    null,
                    null,
                    BacklogContract.CommentEntry.DEFAULT_SORT);
        }

        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mCommentAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCommentAdapter.swapCursor(null);
    }
}