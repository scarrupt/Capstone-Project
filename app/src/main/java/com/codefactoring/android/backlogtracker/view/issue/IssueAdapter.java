package com.codefactoring.android.backlogtracker.view.issue;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.codefactoring.android.backlogtracker.R;
import com.codefactoring.android.backlogtracker.provider.BacklogContract;

import butterknife.Bind;
import butterknife.ButterKnife;

public class IssueAdapter extends RecyclerView.Adapter<IssueAdapter.IssuerAdapterViewHolder> {

    private final String LOG_TAG = CommentAdapter.class.getSimpleName();

    private Cursor mCursor;

    private final Context mContext;

    private final View mEmptyView;

    private final IssueAdapterOnClickHandler mClickHandler;

    public IssueAdapter(Context context, View emptyView, IssueAdapterOnClickHandler clickHandler) {
        mContext = context;
        mEmptyView = emptyView;
        mClickHandler = clickHandler;
    }

    public interface IssueAdapterOnClickHandler {
        void onClick(String issueId, String issueKey, String issueUrl, IssuerAdapterViewHolder viewHolder);
    }

    public class IssuerAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @Bind(R.id.text_issue_key)
        TextView issueKeyView;
        @Bind(R.id.text_issue_priority)
        TextView issuePriorityView;
        @Bind(R.id.text_issue_summary)
        TextView issueSummaryView;
        @Bind(R.id.text_issue_assignee)
        TextView assigneeView;

        public IssuerAdapterViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            mCursor.moveToPosition(adapterPosition);
            final String issueId = mCursor.getString(IssueListFragment.COL_ISSUE_ID);
            final String issueKey = mCursor.getString(IssueListFragment.COL_ISSUE_KEY);
            final String issueUrl = mCursor.getString(IssueListFragment.COL_URL);
            mClickHandler.onClick(issueId, issueKey, issueUrl, this);
        }
    }

    @Override
    public IssuerAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        if (viewGroup instanceof RecyclerView) {
            final View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_issue, viewGroup, false);
            view.setFocusable(true);
            return new IssuerAdapterViewHolder(view);
        } else {
            Log.e(LOG_TAG, "Not an instance of RecyclerView");
            throw new RuntimeException("Not bound to a RecyclerView");
        }
    }

    @Override
    public void onBindViewHolder(final IssuerAdapterViewHolder holder, int position) {
        mCursor.moveToPosition(position);

        final String issueKey = mCursor.getString(mCursor.getColumnIndex(BacklogContract.IssueEntry.ISSUE_KEY));
        holder.issueKeyView.setText(issueKey);

        final String issueSummary = mCursor.getString(mCursor.getColumnIndex(BacklogContract.IssueEntry.SUMMARY));
        holder.issueSummaryView.setText(issueSummary);

        final String issuePriority = mCursor.getString(mCursor.getColumnIndex(BacklogContract.IssueEntry.PRIORITY));
        holder.issuePriorityView.setText(issuePriority);

        final String assignee = mCursor.getString(mCursor.getColumnIndex(BacklogContract.IssuePreviewEntry.ASSIGNEE_NAME_ALIAS));
        holder.assigneeView.setContentDescription(assignee);

        final String assigneeThumbnailPath = mCursor.getString(mCursor.getColumnIndex(BacklogContract.IssuePreviewEntry.ASSIGNEE_THUMBNAIL_URL_ALIAS));
        Glide.with(mContext)
                .load(assigneeThumbnailPath)
                .asBitmap()
                .centerCrop()
                .placeholder(R.drawable.person_image_empty)
                .into(new SimpleTarget<Bitmap>(100, 100) {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation glideAnimation) {
                        holder.assigneeView.setCompoundDrawablesWithIntrinsicBounds(
                                new BitmapDrawable(holder.assigneeView.getResources(), resource),
                                null,
                                null,
                                null);
                        holder.assigneeView.setCompoundDrawablePadding(16);
                    }
                });
        holder.assigneeView.setContentDescription(assignee);
    }

    @Override
    public int getItemCount() {
        return mCursor == null ? 0 : mCursor.getCount();
    }

    public void swapCursor(Cursor newCursor) {
        mCursor = newCursor;
        notifyDataSetChanged();
        mEmptyView.setVisibility(getItemCount() == 0 ? View.VISIBLE : View.GONE);
    }
}
