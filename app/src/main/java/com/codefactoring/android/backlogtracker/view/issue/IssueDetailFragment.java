package com.codefactoring.android.backlogtracker.view.issue;

import android.app.Dialog;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.codefactoring.android.backlogtracker.R;
import com.codefactoring.android.backlogtracker.provider.BacklogContract;
import com.codefactoring.android.backlogtracker.view.util.DateTimeUtils;

import butterknife.Bind;
import butterknife.ButterKnife;

public class IssueDetailFragment extends DialogFragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private final String LOG_TAG = IssueDetailFragment.class.getSimpleName();

    private static final int LOADER = 0;

    private static final String[] COLUMNS = new String[] {
            BacklogContract.UserEntry.USER_PREFIX + BacklogContract.UserEntry.THUMBNAIL_URL,
            BacklogContract.UserEntry.USER_PREFIX + BacklogContract.UserEntry.NAME,
            BacklogContract.IssueEntry.CREATED_DATE,
            BacklogContract.IssueEntry.DESCRIPTION,
            BacklogContract.UserEntry.ASSIGNEE_PREFIX + BacklogContract.UserEntry.NAME,
            BacklogContract.IssueEntry.STATUS,
            BacklogContract.IssueEntry.PRIORITY,
            BacklogContract.IssueTypeEntry.PREFIX + BacklogContract.IssueTypeEntry.NAME,
            BacklogContract.IssueEntry.MILESTONES,
            BacklogContract.IssueEntry.SUMMARY
    };

    private static final int COL_AUTHOR_THUMBNAIL = 0;
    private static final int COL_AUTHOR_NAME = 1;
    private static final int COL_CREATED_DATE = 2;
    private static final int COL_DESCRIPTION = 3;
    private static final int COL_ASSIGNEE_NAME = 4;
    private static final int COL_STATUS = 5;
    private static final int COL_PRIORITY = 6;
    private static final int COL_TYPE = 7;
    private static final int COL_MILESTONES = 8;
    private static final int COL_SUMMARY = 9;

    private static final String ARG_URI = "ARG_URI";

    private static final String ARG_ISSUE_KEY = "ARG_ISSUE_KEY";

    @Bind(R.id.text_title)
    TextView mTitle;

    @Bind(R.id.text_author)
    TextView mAuthorView;

    @Bind(R.id.text_description)
    TextView mDescriptionView;

    @Bind(R.id.img_assignee)
    ImageView mAssigneeThumbnail;

    @Bind(R.id.text_assignee)
    TextView mAssigneeView;

    @Bind(R.id.text_status)
    TextView mStatusView;

    @Bind(R.id.text_priority)
    TextView mPriorityView;

    @Bind(R.id.text_type)
    TextView mTypeView;

    @Bind(R.id.text_milestones)
    TextView mMilestonesView;

    private Uri mUri;

    public IssueDetailFragment() {
        setHasOptionsMenu(true);
    }

    public static IssueDetailFragment newInstance(Uri uri, String issueKey) {
        final IssueDetailFragment fragment = new IssueDetailFragment();
        final Bundle args = new Bundle();
        args.putParcelable(ARG_URI, uri);
        args.putString(ARG_ISSUE_KEY, issueKey);
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.setTitle(getArguments().getString(ARG_ISSUE_KEY));
        return dialog;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mUri = getArguments().getParcelable(ARG_URI);
            Log.i(LOG_TAG, "URI: " + mUri);
        }

        if (savedInstanceState == null) {
            final CommentListFragment commentListFragment = CommentListFragment.newInstance(mUri);
            getChildFragmentManager()
                    .beginTransaction()
                    .add(R.id.comment_list_container, commentListFragment)
                    .commit();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_issue_detail, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (mUri != null) {
            return new CursorLoader(getActivity(),
                    mUri,
                    COLUMNS,
                    null,
                    null,
                    BacklogContract.IssueEntry.DEFAULT_SORT);
        }

        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null && data.moveToFirst()) {

            final String title = data.getString(COL_SUMMARY);
            mTitle.setText(title);

            final String authorName = data.getString(COL_AUTHOR_NAME);
            final String createdDate = data.getString(COL_CREATED_DATE);
            final String formattedDate = DateTimeUtils.convertUTCDateTimeToLocalDateTime(
                    getContext(), createdDate);

            mAuthorView.setText(getString(R.string.format_author_issue, authorName, formattedDate));

            final String authorThumbnailPath = data.getString(COL_AUTHOR_THUMBNAIL);
            Glide.with(getContext())
                    .load(authorThumbnailPath)
                    .asBitmap()
                    .centerCrop()
                    .placeholder(R.drawable.person_image_empty)
                    .into(new SimpleTarget<Bitmap>(200, 200) {
                        @Override
                        public void onResourceReady(Bitmap resource, GlideAnimation glideAnimation) {
                            mAuthorView.setCompoundDrawablesWithIntrinsicBounds(
                                    new BitmapDrawable(mAuthorView.getResources(), resource),
                                    null,
                                    null,
                                    null);
                            mAuthorView.setCompoundDrawablePadding(16);
                        }
                    });
            mAuthorView.setContentDescription(authorName);

            final String description = data.getString(COL_DESCRIPTION);
            mDescriptionView.setText(description);

            final String assigneeName = data.getString(COL_ASSIGNEE_NAME);
            mAssigneeView.setText(assigneeName);

            final String status = data.getString(COL_STATUS);
            mStatusView.setText(status);

            final String priority = data.getString(COL_PRIORITY);
            mPriorityView.setText(priority);

            final String type = data.getString(COL_TYPE);
            mTypeView.setText(type);

            final String milestones = data.getString(COL_MILESTONES);
            mMilestonesView.setText(milestones);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }

    /*
    Workaround for issue 17423
    https://code.google.com/p/android/issues/detail?id=17423&q=setRetaininstance&colspec=ID%20Type%20Status%20Owner%20Summary%20Stars
     */
    @Override
    public void onDestroyView() {
        if (getDialog() != null && getRetainInstance())
            getDialog().setOnDismissListener(null);
        super.onDestroyView();
    }
}