package com.codefactoring.android.backlogtracker.sync.handlers;

import android.content.ContentProviderOperation;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.codefactoring.android.backlogtracker.sync.models.IssueDto;
import com.codefactoring.android.backlogtracker.sync.utils.SyncUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import static com.codefactoring.android.backlogtracker.provider.BacklogContract.IssueEntry;

public class IssueDataHandler {

    public static final String LOG_TAG = IssueDataHandler.class.getSimpleName();

    private static String[] ISSUES_COLUMNS = {
            IssueEntry._ID,
            IssueEntry.FINGERPRINT
    };

    protected static final int COL_ID = 0;
    protected static final int COL_FINGERPRINT = 1;

    private final Context mContext;

    public IssueDataHandler(Context context) {
        mContext = context;
    }

    public ArrayList<ContentProviderOperation> makeContentProviderOperations(List<IssueDto> issues) {

        final ArrayList<ContentProviderOperation> operations = new ArrayList<>();

        final HashMap<Long, String> fingerprints = loadFingerprints();
        final HashSet<Long> dataToKeep = new HashSet<>();

        for (IssueDto issue : issues) {

            if (fingerprints.containsKey(issue.getId())) {

                final String currentFingerprint = SyncUtils.computeWeakHash(issue.toString());
                final String recordedFingerPrint = fingerprints.get(issue.getId());

                if (!recordedFingerPrint.equals(currentFingerprint)) {
                    operations.add(buildOperation(issue, false));
                }
            } else {
                operations.add(buildOperation(issue, true));
            }

            dataToKeep.add(issue.getId());
        }

        for (Long id : fingerprints.keySet()) {
            if (!dataToKeep.contains(id)) {
                operations.add(ContentProviderOperation
                        .newDelete(IssueEntry.CONTENT_URI)
                        .withSelection(IssueEntry._ID + "=?", new String[]{String.valueOf(id)})
                        .build());
            }
        }

        return operations;
    }

    private ContentProviderOperation buildOperation(IssueDto issueDto, boolean isNew) {
        final ContentProviderOperation.Builder builder;

        if (isNew) {
            builder = ContentProviderOperation
                    .newInsert(IssueEntry.CONTENT_URI);
        } else {
            builder = ContentProviderOperation
                    .newUpdate(IssueEntry.CONTENT_URI)
                    .withSelection(IssueEntry._ID + "=?", new String[]{String.valueOf(issueDto.getId())});
        }

        builder.withValue(IssueEntry._ID, issueDto.getId())
                .withValue(IssueEntry.ISSUE_KEY, issueDto.getIssueKey())
                .withValue(IssueEntry.PROJECT_ID, issueDto.getProjectId())
                .withValue(IssueEntry.TYPE_ID, issueDto.getIssueType().getId())
                .withValue(IssueEntry.SUMMARY, issueDto.getSummary())
                .withValue(IssueEntry.DESCRIPTION, issueDto.getDescription())
                .withValue(IssueEntry.PRIORITY, issueDto.getPriority())
                .withValue(IssueEntry.STATUS, issueDto.getStatus())
                .withValue(IssueEntry.MILESTONES, issueDto.getMilestones())
                .withValue(IssueEntry.URL, issueDto.getUrl())
                .withValue(IssueEntry.ASSIGNEE_ID, issueDto.getAssigneeId())
                .withValue(IssueEntry.CREATED_USER_ID, issueDto.getCreatedUserId())
                .withValue(IssueEntry.CREATED_DATE, issueDto.getCreatedDate())
                .withValue(IssueEntry.UPDATED_USER_ID, issueDto.getUpdatedUserId())
                .withValue(IssueEntry.UPDATED_DATE, issueDto.getUpdatedDate())
                .withValue(IssueEntry.FINGERPRINT, SyncUtils.computeWeakHash(issueDto.toString()));

        return builder.build();
    }


    private HashMap<Long, String> loadFingerprints() {
        Cursor cursor = null;

        try {
            cursor = query();
            if (cursor == null) {
                Log.e(LOG_TAG, "Error querying fingerprints (got null cursor)");
                return new HashMap<>();
            }
            if (cursor.getCount() < 1) {
                Log.e(LOG_TAG, "Error querying fingerprints (no records returned)");
                return new HashMap<>();
            }

            final HashMap<Long, String> result = new HashMap<>();

            if (cursor.moveToFirst()) {
                do {
                    final Long id = cursor.getLong(COL_ID);
                    final String fingerprint = cursor.getString(COL_FINGERPRINT);
                    result.put(id, fingerprint == null ? "" : fingerprint);
                } while (cursor.moveToNext());
            }
            return result;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    protected Cursor query() {
        return mContext.getContentResolver().query(IssueEntry.CONTENT_URI, ISSUES_COLUMNS,
                null, null, null);
    }
}
