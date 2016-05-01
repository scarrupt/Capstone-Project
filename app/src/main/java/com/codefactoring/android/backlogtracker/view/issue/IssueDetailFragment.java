package com.codefactoring.android.backlogtracker.view.issue;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.codefactoring.android.backlogtracker.R;
import com.codefactoring.android.backlogtracker.provider.BacklogContract;

import butterknife.Bind;
import butterknife.ButterKnife;

public class IssueDetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int LOADER = 0;

    private static final int COL_AUTHOR_THUMBNAIL = 0;
    private static final int COL_AUTHOR_NAME = 1;
    private static final int COL_CREATED_DATE = 2;
    private static final int COL_DESCRIPTION = 3;
    private static final int COL_ASSIGNEE_THUMBNAIL = 4;
    private static final int COL_ASSIGNEE_NAME = 5;
    private static final int COL_STATUS = 6;
    private static final int COL_PRIORITY = 7;
    private static final int COL_TYPE = 8;
    private static final int COL_MILESTONES = 9;
    private static final int COL_URL = 10;

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

    private ShareActionProvider mShareActionProvider;

    private String mIssueUrl;

    public IssueDetailFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getActivity().getIntent() != null) {
            mUri = getActivity().getIntent().getData();
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
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_issue_detail, menu);

        MenuItem menuItem = menu.findItem(R.id.action_share);

        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);

        if (mIssueUrl != null) {
            mShareActionProvider.setShareIntent(createShareIntent());
        }
    }

    private Intent createShareIntent() {
        final Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, mIssueUrl);
        return shareIntent;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getActivity(),
                mUri,
                null,
                null,
                null,
                BacklogContract.IssueEntry.DEFAULT_SORT);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null && data.moveToFirst()) {

            final String authorName = data.getString(COL_AUTHOR_NAME);
            final String createdDate = data.getString(COL_CREATED_DATE);
            mAuthorView.setText(getString(R.string.format_author, authorName, "opened", createdDate));

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

            final String assigneeThumbnailPath = data.getString(COL_ASSIGNEE_THUMBNAIL);
            Glide.with(getContext())
                    .load(assigneeThumbnailPath)
                    .asBitmap()
                    .centerCrop()
                    .placeholder(R.drawable.person_image_empty)
                    .into(mAssigneeThumbnail);
            mAssigneeThumbnail.setContentDescription(assigneeName);

            final String status = data.getString(COL_STATUS);
            mStatusView.setText(status);

            final String priority = data.getString(COL_PRIORITY);
            mPriorityView.setText(priority);

            final String type = data.getString(COL_TYPE);
            mTypeView.setText(type);

            final String milestones = data.getString(COL_MILESTONES);
            mMilestonesView.setText(milestones);

            mIssueUrl = data.getString(COL_URL);

            if (mShareActionProvider != null) {
                mShareActionProvider.setShareIntent(createShareIntent());
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
