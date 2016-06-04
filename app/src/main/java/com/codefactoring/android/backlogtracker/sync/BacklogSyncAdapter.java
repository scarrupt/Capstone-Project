package com.codefactoring.android.backlogtracker.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SyncResult;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.codefactoring.android.backlogapi.BacklogApiClient;
import com.codefactoring.android.backlogtracker.Config;
import com.codefactoring.android.backlogtracker.R;
import com.codefactoring.android.backlogtracker.provider.BacklogContract;
import com.codefactoring.android.backlogtracker.sync.fetchers.CommentDataFetcher;
import com.codefactoring.android.backlogtracker.sync.fetchers.IssueDataFetcher;
import com.codefactoring.android.backlogtracker.sync.fetchers.ProjectDataFetcher;
import com.codefactoring.android.backlogtracker.sync.fetchers.UserDataFetcher;
import com.codefactoring.android.backlogtracker.sync.handlers.CommentDataHandler;
import com.codefactoring.android.backlogtracker.sync.handlers.IssueDataHandler;
import com.codefactoring.android.backlogtracker.sync.handlers.IssueTypeDataHandler;
import com.codefactoring.android.backlogtracker.sync.handlers.ProjectDataHandler;
import com.codefactoring.android.backlogtracker.sync.handlers.UserDataHandler;
import com.codefactoring.android.backlogtracker.sync.models.CommentDto;
import com.codefactoring.android.backlogtracker.sync.models.IssueDto;
import com.codefactoring.android.backlogtracker.sync.models.IssueTypeDto;
import com.codefactoring.android.backlogtracker.sync.models.ProjectDto;
import com.codefactoring.android.backlogtracker.sync.models.UserDto;
import com.codefactoring.android.backlogtracker.view.issue.IssueDetailActivity;
import com.codefactoring.android.backlogtracker.view.issue.IssuesMainActivity;
import com.google.common.base.Objects;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import static com.codefactoring.android.backlogapi.BacklogApiConstants.DATE_FORMAT_PATTERN;
import static com.codefactoring.android.backlogapi.BacklogApiConstants.STATUS_ISSUE_OPEN;
import static com.codefactoring.android.backlogtracker.provider.BacklogContract.CONTENT_AUTHORITY;
import static com.codefactoring.android.backlogtracker.provider.BacklogContract.IssueEntry;
import static com.codefactoring.android.backlogtracker.provider.BacklogContract.ProjectEntry;

public class BacklogSyncAdapter extends AbstractThreadedSyncAdapter {

    public static final String ACTION_DATA_UPDATED = "com.codefactoring.android.backlogtracker.sync.BacklogSyncAdapter.ACTION_DATA_UPDATED";

    public static final String LOG_TAG = BacklogSyncAdapter.class.getSimpleName();

    private static final String PREF_KEY_DATA_TIMESTAMP = "data_timestamp";

    private static final String PREF_DATA_BOOTSTRAP_DONE = "data_bootstrap_done";

    private static final String DEFAULT_TIMESTAMP = "2000-01-01T00:00:00Z";

    private final BacklogApiClient mBacklogApiClient;

    private final AccountManager mAccountManager;

    private final Context mContext;

    private final SharedPreferences mSharedPreferences;

    public static final String[] ISSUES_COLUMNS =
            new String[]{
                    IssueEntry.TABLE_NAME + "." + IssueEntry._ID,
                    IssueEntry.ISSUE_KEY,
                    IssueEntry.PROJECT_ID,
                    ProjectEntry.NAME,
                    ProjectEntry.PROJECT_KEY,
                    IssueEntry.URL,
                    IssueEntry.SUMMARY
            };

    private static final int INDEX_ISSUE_ID = 0;
    private static final int INDEX_ISSUE_KEY = 1;
    private static final int INDEX_PROJECT_ID = 2;
    private static final int INDEX_PROJECT_NAME = 3;
    private static final int INDEX_PROJECT_KEY = 4;
    private static final int INDEX_ISSUE_SUMMARY = 5;
    private static final int INDEX_ISSUE_URL = 6;


    public BacklogSyncAdapter(Context context, boolean autoInitialize, AccountManager accountManager,
                              BacklogApiClient backlogApiClient, SharedPreferences sharedPreferences) {
        super(context, autoInitialize);
        mContext = context;
        mAccountManager = accountManager;
        mBacklogApiClient = backlogApiClient;
        mSharedPreferences = sharedPreferences;
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        if (!isOnline()) {
            Log.d(LOG_TAG, "Data will be not syncing because device is offline");
            return;
        }

        Log.d(LOG_TAG, "Starting sync");
        LocalBroadcastManager.getInstance(mContext).sendBroadcast(new Intent(Config.ACTION_SYNC_STARTED));

        final String spaceKey = mAccountManager.getUserData(account, Config.KEY_SPACE_KEY);
        final String apiKey = mAccountManager.getUserData(account, Config.KEY_API_KEY);

        mBacklogApiClient.connectWith(spaceKey, apiKey);

        final ArrayList<ContentProviderOperation> operations = new ArrayList<>();
        operations.addAll(syncUserData());
        operations.addAll(syncProjectData());

        if (operations.size() > 0) {
            try {
                getContext().getContentResolver().applyBatch(CONTENT_AUTHORITY, operations);

                final Intent dataUpdatedIntent = new Intent(ACTION_DATA_UPDATED)
                        .setPackage(getContext().getPackageName());
                getContext().sendBroadcast(dataUpdatedIntent);

                final String lastDataTimestamp = getDataTimestamp();

                sendNotificationsIfEnabled(lastDataTimestamp);

                final Date now = GregorianCalendar.getInstance().getTime();
                setDataTimestamp(formatDate(now));
                syncResult.stats.numEntries += operations.size();
                syncResult.stats.numUpdates += operations.size();
            } catch (Throwable ex) {
                syncResult.stats.numAuthExceptions++;
                Log.e(LOG_TAG, "Exception while applying content provider operations.", ex);
            }
        }

        LocalBroadcastManager.getInstance(mContext).sendBroadcast(new Intent(Config.ACTION_SYNC_DONE));
        Log.d(LOG_TAG, "Done sync");
    }

    private String formatDate(Date date) {
        if (date == null) {
            return null;
        } else {
            final SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_FORMAT_PATTERN, Locale.getDefault());
            return simpleDateFormat.format(date);
        }
    }

    private boolean isOnline() {
        final ConnectivityManager connectivityManager = (ConnectivityManager)
                mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        return connectivityManager.getActiveNetworkInfo() != null &&
                connectivityManager.getActiveNetworkInfo().isConnectedOrConnecting();
    }

    private String getDataTimestamp() {
        return mSharedPreferences.getString(PREF_KEY_DATA_TIMESTAMP, DEFAULT_TIMESTAMP);
    }

    public void setDataTimestamp(String timestamp) {
        Log.d(LOG_TAG, "Setting data timestamp to: " + timestamp);
        mSharedPreferences.edit().putString(PREF_KEY_DATA_TIMESTAMP, timestamp).apply();
    }

    private void sendNotificationsIfEnabled(String lastDataTimestamp) {
        if (isNotificationEnabled()) {

            final Uri uri = IssueEntry.buildIssueUriWithStatusAndCreatedDate(STATUS_ISSUE_OPEN, lastDataTimestamp);

            final Cursor data = mContext.getContentResolver().query(uri,
                    ISSUES_COLUMNS, null, null, null);

            final NotificationManager notificationManager = (NotificationManager)
                    mContext.getSystemService(Context.NOTIFICATION_SERVICE);

            if (data != null) {
                final Map<String, ProjectData> projectDataMap = new HashMap<>();

                while (data.moveToNext()) {
                    final int issueId = data.getInt(INDEX_ISSUE_ID);
                    final String projectId = data.getString(INDEX_PROJECT_ID);
                    final String projectName = data.getString(INDEX_PROJECT_NAME);
                    final String projectKey = data.getString(INDEX_PROJECT_KEY);
                    final String issueKey = data.getString(INDEX_ISSUE_KEY);
                    final String summary = data.getString(INDEX_ISSUE_SUMMARY);
                    final String issueUrl = data.getString(INDEX_ISSUE_URL);

                    if (projectDataMap.containsKey(projectId)) {
                        final ProjectData projectData = projectDataMap.get(projectId);
                        projectData.setCount(projectData.getCount() + 1);
                    } else {
                        projectDataMap.put(projectId, new ProjectData(projectId, projectName, projectKey));
                    }

                    final Intent intent = new Intent(mContext, IssueDetailActivity.class);
                    intent.setData(IssueEntry.buildIssueUriFromIssueId(String.valueOf(issueId)));
                    intent.putExtra(Config.KEY_ISSUE_KEY, issueKey);
                    intent.putExtra(Config.KEY_ISSUE_URL, issueUrl);
                    final PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);

                    final NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext)
                            .setSmallIcon(R.mipmap.ic_launcher)
                            .setContentTitle(mContext.getString(R.string.notification_title))
                            .setContentText(mContext.getString(R.string.notification_message,
                                    issueKey, summary))
                            .setContentIntent(pendingIntent)
                            .setAutoCancel(true)
                            .setGroup(projectId);

                    notificationManager.notify(issueId, builder.build());
                }
                data.close();

                for (String project : projectDataMap.keySet()) {

                    final ProjectData projectData = projectDataMap.get(project);

                    if (projectData.getCount() > 1) {
                        final Intent intent = new Intent(mContext, IssuesMainActivity.class);
                        intent.setData(BacklogContract.IssuePreviewEntry.buildIssuePreviewsWithProjectId(projectData.getProjectId()));
                        intent.putExtra(Intent.EXTRA_TEXT, projectData.getProjectKey());
                        final PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);

                        final NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext)
                                .setSmallIcon(R.mipmap.ic_launcher)
                                .setContentTitle(mContext.getString(R.string.notification_multiple_title))
                                .setContentText(mContext.getString(R.string.notification_multiple_message,
                                        projectData.getProjectName()))
                                .setContentIntent(pendingIntent)
                                .setAutoCancel(true)
                                .setGroup(projectData.getProjectId())
                                .setGroupSummary(true);
                        notificationManager.notify(Integer.valueOf(projectData.getProjectId()), builder.build());
                    }
                }
            }
        }
    }

    protected ArrayList<ContentProviderOperation> syncProjectData() {
        final ArrayList<ContentProviderOperation> operations = new ArrayList<>();

        final ProjectDataFetcher projectDataFetcher = new ProjectDataFetcher(mBacklogApiClient);
        final List<ProjectDto> projects = projectDataFetcher.getProjectList();
        final ProjectDataHandler projectDataHandler = new ProjectDataHandler(getContext());
        operations.addAll(projectDataHandler.makeContentProviderOperations(projects));

        final IssueDataFetcher issueDataFetcher = new IssueDataFetcher(mBacklogApiClient);
        final List<IssueDto> issues = new ArrayList<>();
        for (ProjectDto projectDto : projects) {
            issues.addAll(issueDataFetcher.getIssueList(projectDto.getId()));
        }

        final Set<IssueTypeDto> issueTypes = new HashSet<>();
        for (IssueDto issue : issues) {
            issueTypes.add(issue.getIssueType());
        }

        final IssueTypeDataHandler issueTypeDataHandler = new IssueTypeDataHandler();
        operations.addAll(issueTypeDataHandler.makeContentProviderOperations(issueTypes));

        final IssueDataHandler issueDataHandler = new IssueDataHandler();
        operations.addAll(issueDataHandler.makeContentProviderOperations(issues));

        final CommentDataFetcher commentDataFetcher = new CommentDataFetcher(mBacklogApiClient);
        final List<CommentDto> comments = new ArrayList<>();
        for (IssueDto issue : issues) {
            comments.addAll(commentDataFetcher.getCommentList(issue.getId()));
        }

        final CommentDataHandler commentDataHandler = new CommentDataHandler(mContext);
        operations.addAll(commentDataHandler.makeContentProviderOperations(comments));

        return operations;
    }

    protected ArrayList<ContentProviderOperation> syncUserData() {
        final UserDataFetcher userDataFetcher = new UserDataFetcher(mBacklogApiClient);
        final List<UserDto> users = userDataFetcher.getUserList();

        final UserDataHandler userDataHandler = new UserDataHandler(getContext());
        return userDataHandler.makeContentProviderOperations(users);
    }

    public void syncImmediately() {
        final Account[] accounts = getSyncAccounts(mContext);
        for (Account account : accounts) {
            final String authority = mContext.getString(R.string.content_authority);

            final boolean pending = isSyncPending(account, authority);

            final boolean active = isSyncActive(account, authority);

            if (pending || active) {
                Log.d(LOG_TAG, "Cancelling previously pending/active sync.");
                ContentResolver.cancelSync(account, authority);
            }

            final Bundle bundle = new Bundle();
            bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
            bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);

            ContentResolver.requestSync(account, authority, bundle);
        }
    }

    private boolean isSyncActive(Account account, String authority) {
        final boolean active = ContentResolver.isSyncActive(account, authority);

        if (active) {
            Log.d(LOG_TAG, "Warning: sync is ACTIVE. Will cancel.");
        }
        return active;
    }

    private boolean isSyncPending(Account account, String authority) {
        final boolean pending = ContentResolver.isSyncPending(account, authority);
        if (pending) {
            Log.d(LOG_TAG, "Warning: sync is PENDING. Will cancel.");
        }
        return pending;
    }

    private Account[] getSyncAccounts(Context context) {
        return mAccountManager.getAccountsByType(context.getString(R.string.account_type));
    }

    private boolean isDataBootstrapDone() {
        return mSharedPreferences.getBoolean(PREF_DATA_BOOTSTRAP_DONE, false);
    }

    private void markDataBootstrapDone() {
        mSharedPreferences.edit().putBoolean(PREF_DATA_BOOTSTRAP_DONE, true).apply();
    }

    private boolean isNotificationEnabled() {
        return mSharedPreferences.getBoolean(mContext.getString(R.string.pref_notification_key),
                mContext.getResources().getBoolean(R.bool.pref_notification_default));
    }

    public void startDataBootstrap() {
        if (!isDataBootstrapDone()) {
            syncImmediately();
            markDataBootstrapDone();
        }
    }

    private static class ProjectData {
        private final String projectId;
        private final String projectName;
        private final String projectKey;
        private int count = 1;

        public ProjectData(String projectId, String projectName, String projectKey) {
            this.projectId = projectId;
            this.projectName = projectName;
            this.projectKey = projectKey;
        }

        public String getProjectId() {
            return projectId;
        }

        public String getProjectName() {
            return projectName;
        }

        public String getProjectKey() {
            return projectKey;
        }

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ProjectData that = (ProjectData) o;
            return Objects.equal(projectId, that.projectId) &&
                    Objects.equal(projectName, that.projectName);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(projectId, projectName);
        }
    }
}