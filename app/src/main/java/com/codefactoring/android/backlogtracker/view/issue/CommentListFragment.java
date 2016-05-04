package com.codefactoring.android.backlogtracker.view.issue;


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

public class CommentListFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int LOADER = 1;

    private static final String ARG_URI = "ARG_URI";

    private static final String[] COMMENT_COLUMNS = {
            BacklogContract.CommentEntry.TABLE_NAME + "." +
                    BacklogContract.CommentEntry._ID,
            BacklogContract.UserEntry.NAME,
            BacklogContract.UserEntry.THUMBNAIL_URL,
            BacklogContract.CommentEntry.CREATED,
            BacklogContract.CommentEntry.CONTENT
    };

    @Bind(R.id.recycler_view_comment)
    RecyclerView mRecyclerView;

    @Bind(R.id.text_empty_comments)
    View emptyView;

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
        if (getArguments() != null) {
            mUri = getArguments().getParcelable(ARG_URI);
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

        final View view = inflater.inflate(R.layout.fragment_comment_list, container, false);
        ButterKnife.bind(this, view);

        mCommentAdapter = new CommentAdapter(getActivity(), emptyView);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(mCommentAdapter);

        return view;
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