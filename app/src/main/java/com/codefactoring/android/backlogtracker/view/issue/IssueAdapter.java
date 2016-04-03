package com.codefactoring.android.backlogtracker.view.issue;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.codefactoring.android.backlogtracker.R;

import static com.codefactoring.android.backlogtracker.provider.BacklogContract.IssueEntry;
import static com.codefactoring.android.backlogtracker.provider.BacklogContract.IssuePreviewEntry;

public class IssueAdapter extends CursorAdapter {

    public IssueAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        final View view = LayoutInflater.from(context).inflate(R.layout.item_issue, parent, false);
        final ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        final ViewHolder viewHolder = (ViewHolder) view.getTag();

        final String issueKey = cursor.getString(cursor.getColumnIndex(IssueEntry.ISSUE_KEY));
        viewHolder.issueKeyView.setText(issueKey);

        final String issueSummary = cursor.getString(cursor.getColumnIndex(IssueEntry.SUMMARY));
        viewHolder.issueSummaryView.setText(issueSummary);

        final String issuePriority = cursor.getString(cursor.getColumnIndex(IssueEntry.PRIORITY));
        viewHolder.issuePriorityView.setText(issuePriority);

        final String assignee = cursor.getString(cursor.getColumnIndex(IssuePreviewEntry.ASSIGNEE_NAME_ALIAS));
        viewHolder.assigneeView.setContentDescription(assignee);

        final String assigneeThumbnailPath = cursor.getString(cursor.getColumnIndex(IssuePreviewEntry.ASSIGNEE_THUMBNAIL_URL_ALIAS));
        Glide.with(view.getContext())
                .load(assigneeThumbnailPath)
                .asBitmap()
                .centerCrop()
                .placeholder(R.drawable.person_image_empty)
                .into(new SimpleTarget<Bitmap>(100, 100) {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation glideAnimation) {
                        viewHolder.assigneeView.setCompoundDrawablesWithIntrinsicBounds(
                                new BitmapDrawable(viewHolder.assigneeView.getResources(), resource),
                                null,
                                null,
                                null);
                        viewHolder.assigneeView.setCompoundDrawablePadding(16);
                    }
                });
        viewHolder.assigneeView.setContentDescription(assignee);
    }

    private static final class ViewHolder {
        public final TextView issueKeyView;
        public final TextView issuePriorityView;
        public final TextView issueSummaryView;
        public final TextView assigneeView;

        public ViewHolder(View view) {
            issueKeyView = (TextView) view.findViewById(R.id.text_issue_key);
            issuePriorityView = (TextView) view.findViewById(R.id.text_issue_priority);
            issueSummaryView = (TextView) view.findViewById(R.id.text_issue_summary);
            assigneeView = (TextView) view.findViewById(R.id.text_issue_assignee);
        }
    }
}