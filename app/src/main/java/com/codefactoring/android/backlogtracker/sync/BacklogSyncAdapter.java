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
import com.codefactoring.android.backlogtracker.sync.fetchers.ProjectDataFetcher;
import com.codefactoring.android.backlogtracker.sync.handlers.ProjectDataHandler;
import com.codefactoring.android.backlogtracker.sync.models.ProjectDto;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class BacklogSyncAdapter extends AbstractThreadedSyncAdapter {

    public final String LOG_TAG = BacklogSyncAdapter.class.getSimpleName();

    @Inject
    BacklogApiClient mBacklogApiClient;

    @Inject
    AccountManager mAccountManager;

    public BacklogSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        Log.d(LOG_TAG, "Starting sync");

        final String spaceKey = mAccountManager.getUserData(account, Config.KEY_SPACE_KEY);
        final String apiKey = mAccountManager.getUserData(account, Config.KEY_API_KEY);

        mBacklogApiClient.connectWith(spaceKey, apiKey);

        final ArrayList<ContentProviderOperation> contentProviderOperations = new ArrayList<>();

        final ProjectDataFetcher projectDataFetcher = new ProjectDataFetcher(mBacklogApiClient);
        final List<ProjectDto> projects = projectDataFetcher.getProjectList();

        final ProjectDataHandler projectDataHandler = new ProjectDataHandler(getContext());
        projectDataHandler.makeContentProviderOperations(projects);

        if (contentProviderOperations.size() > 0) {
            try {
                getContext().getContentResolver().applyBatch(BacklogContract.CONTENT_AUTHORITY, contentProviderOperations);
            } catch (RemoteException ex) {
                Log.e(LOG_TAG, "RemoteException while applying content provider operations.", ex);
            } catch (OperationApplicationException ex) {
                Log.e(LOG_TAG, "OperationApplicationException while applying content provider operations.", ex);
            }
        }
    }

    public void syncImmediately(Context context) {
        final Account[] accounts = getSyncAccounts(context);
        for (Account account : accounts) {
            final Bundle bundle = new Bundle();
            bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
            bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
            ContentResolver.requestSync(account,
                    context.getString(R.string.content_authority), bundle);
        }
    }

    private Account[] getSyncAccounts(Context context) {
        return mAccountManager.getAccountsByType(context.getString(R.string.account_type));
    }
}