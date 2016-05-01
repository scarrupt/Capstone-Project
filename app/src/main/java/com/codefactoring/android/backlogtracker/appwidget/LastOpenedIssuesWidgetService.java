package com.codefactoring.android.backlogtracker.appwidget;

import android.annotation.TargetApi;
import android.content.Intent;
import android.database.Cursor;
import android.os.Binder;
import android.os.Build;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.codefactoring.android.backlogtracker.R;
import com.codefactoring.android.backlogtracker.provider.BacklogContract;

public class LastOpenedIssuesWidgetService extends RemoteViewsService {

    public static final String[] LAST_ISSUES_COLUMNS =
            new String[]{
                    BacklogContract.IssueEntry._ID,
                    BacklogContract.IssueEntry.ISSUE_KEY,
                    BacklogContract.IssueEntry.SUMMARY
            };

    private static final int INDEX_ISSUE_ID = 0;
    private static final int INDEX_ISSUE_KEY = 1;
    private static final int INDEX_ISSUE_SUMMARY = 2;

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new RemoteViewsFactory() {

            private Cursor cursor;

            @Override
            public void onCreate() {
                // Nothing to do
            }

            @Override
            public void onDataSetChanged() {

                if (cursor != null) {
                    cursor.close();
                }

                final long identityToken = Binder.clearCallingIdentity();
                cursor = getContentResolver().query(BacklogContract.IssueEntry.buildLast10OpenedIssueUri(),
                        LAST_ISSUES_COLUMNS,
                        null,
                        null,
                        null);
                Binder.restoreCallingIdentity(identityToken);
            }

            @Override
            public void onDestroy() {
                if (cursor != null) {
                    cursor.close();
                    cursor = null;
                }
            }

            @Override
            public int getCount() {
                return cursor == null ? 0 : cursor.getCount();
            }

            @Override
            public RemoteViews getViewAt(int position) {
                if (position == AdapterView.INVALID_POSITION ||
                        cursor == null || !cursor.moveToPosition(position)) {
                    return null;
                }
                final RemoteViews views = new RemoteViews(getPackageName(),
                        R.layout.item_issue_widget);

                final String issueId = cursor.getString(INDEX_ISSUE_ID);

                final String issueKey = cursor.getString(INDEX_ISSUE_KEY);
                views.setTextViewText(R.id.text_issue_key_widget, issueKey);

                final String issueSummary = cursor.getString(INDEX_ISSUE_SUMMARY);
                views.setTextViewText(R.id.text_issue_summary_widget, issueSummary);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
                    setRemoteContentDescription(views, R.id.text_issue_key_widget,
                            getResources().getString(R.string.a11y_issue_key));
                    setRemoteContentDescription(views, R.id.text_issue_summary_widget,
                            getResources().getString(R.string.a11y_issue_summary));
                }

                final Intent fillInIntent = new Intent();
                fillInIntent.setData(BacklogContract.IssueEntry.buildIssueUriFromIssueId(issueId));
                views.setOnClickFillInIntent(R.id.item_issue_widget, fillInIntent);
                return views;
            }

            @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
            private void setRemoteContentDescription(RemoteViews views, int viewId, String description) {
                views.setContentDescription(viewId, description);
            }

            @Override
            public RemoteViews getLoadingView() {
                return new RemoteViews(getPackageName(), R.layout.item_issue_widget);
            }

            @Override
            public int getViewTypeCount() {
                return 1;
            }

            @Override
            public long getItemId(int position) {

                if (cursor.moveToPosition(position)) {
                    return cursor.getLong(INDEX_ISSUE_ID);
                }

                return position;
            }

            @Override
            public boolean hasStableIds() {
                return true;
            }
        };
    }
}
