package com.codefactoring.android.backlogtracker.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.Context;
import android.content.OperationApplicationException;
import android.content.SyncResult;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;

import com.codefactoring.android.backlogapi.BacklogApiClient;
import com.codefactoring.android.backlogtracker.Config;
import com.codefactoring.android.backlogtracker.R;
import com.codefactoring.android.backlogtracker.provider.BacklogContract;
import com.codefactoring.android.backlogtracker.sync.fetchers.IssueDataFetcher;
import com.codefactoring.android.backlogtracker.sync.fetchers.ProjectDataFetcher;
import com.codefactoring.android.backlogtracker.sync.fetchers.UserDataFetcher;
import com.codefactoring.android.backlogtracker.sync.handlers.IssueDataHandler;
import com.codefactoring.android.backlogtracker.sync.handlers.IssueTypeDataHandler;
import com.codefactoring.android.backlogtracker.sync.handlers.ProjectDataHandler;
import com.codefactoring.android.backlogtracker.sync.handlers.UserDataHandler;
import com.codefactoring.android.backlogtracker.sync.models.IssueDto;
import com.codefactoring.android.backlogtracker.sync.models.IssueTypeDto;
import com.codefactoring.android.backlogtracker.sync.models.ProjectDto;
import com.codefactoring.android.backlogtracker.sync.models.UserDto;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BacklogSyncAdapter extends AbstractThreadedSyncAdapter {

    public final String LOG_TAG = BacklogSyncAdapter.class.getSimpleName();

    private final BacklogApiClient mBacklogApiClient;

    private final AccountManager mAccountManager;

    private final Context mContext;

    public BacklogSyncAdapter(Context context, boolean autoInitialize, AccountManager accountManager,
                              BacklogApiClient backlogApiClient) {
        super(context, autoInitialize);
        mContext = context;
        mAccountManager = accountManager;
        mBacklogApiClient = backlogApiClient;
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        Log.d(LOG_TAG, "Starting sync");

        final String spaceKey = mAccountManager.getUserData(account, Config.KEY_SPACE_KEY);
        final String apiKey = mAccountManager.getUserData(account, Config.KEY_API_KEY);

        mBacklogApiClient.connectWith(spaceKey, apiKey);

        final ArrayList<ContentProviderOperation> operations = new ArrayList<>();
        operations.addAll(syncUserData());
        operations.addAll(syncProjectData());

        if (operations.size() > 0) {
            try {
                getContext().getContentResolver().applyBatch(BacklogContract.CONTENT_AUTHORITY, operations);
            } catch (RemoteException ex) {
                Log.e(LOG_TAG, "RemoteException while applying content provider operations.", ex);
            } catch (OperationApplicationException ex) {
                Log.e(LOG_TAG, "OperationApplicationException while applying content provider operations.", ex);
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
        for (IssueDto issue: issues) {
            issueTypes.add(issue.getIssueType());
        }

        final IssueTypeDataHandler issueTypeDataHandler = new IssueTypeDataHandler();
        operations.addAll(issueTypeDataHandler.makeContentProviderOperations(issueTypes));

        final IssueDataHandler issueDataHandler = new IssueDataHandler();
        operations.addAll(issueDataHandler.makeContentProviderOperations(issues));

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
            final Bundle bundle = new Bundle();
            bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
            bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
            ContentResolver.requestSync(account,
                    mContext.getString(R.string.content_authority), bundle);

        }
    }

    private Account[] getSyncAccounts(Context context) {
        return mAccountManager.getAccountsByType(context.getString(R.string.account_type));
    }
}