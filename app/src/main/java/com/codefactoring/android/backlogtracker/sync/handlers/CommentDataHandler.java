package com.codefactoring.android.backlogtracker.sync.handlers;

import android.content.ContentProviderOperation;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.codefactoring.android.backlogtracker.provider.BacklogContract;
import com.codefactoring.android.backlogtracker.sync.models.CommentDto;
import com.codefactoring.android.backlogtracker.sync.utils.SyncUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import static com.codefactoring.android.backlogtracker.provider.BacklogContract.CommentEntry;

public class CommentDataHandler {

    public static final String LOG_TAG = CommentDataHandler.class.getSimpleName();

    private static String[] COMMENT_COLUMNS = {
            CommentEntry._ID,
            CommentEntry.FINGERPRINT
    };

    protected static final int COL_ID = 0;
    protected static final int COL_FINGERPRINT = 1;

    private final Context mContext;

    public CommentDataHandler(Context context) {
        this.mContext = context;
    }

    public ArrayList<ContentProviderOperation> makeContentProviderOperations(List<CommentDto> comments) {

        final ArrayList<ContentProviderOperation> operations = new ArrayList<>();

        final HashMap<Long, String> fingerprints = loadFingerprints();
        final HashSet<Long> dataToKeep = new HashSet<>();

        for (CommentDto comment : comments) {

            if (fingerprints.containsKey(comment.getId())) {

                final String currentFingerprint = SyncUtils.computeWeakHash(comment.toString());
                final String recordedFingerPrint = fingerprints.get(comment.getId());

                if (!recordedFingerPrint.equals(currentFingerprint)) {
                    operations.add(buildOperation(comment, false));
                }
            } else {
                operations.add(buildOperation(comment, true));
            }

            dataToKeep.add(comment.getId());
        }

        for (Long id : fingerprints.keySet()) {
            if (!dataToKeep.contains(id)) {
                operations.add(ContentProviderOperation
                        .newDelete(CommentEntry.CONTENT_URI)
                        .withSelection(CommentEntry._ID + "=?", new String[]{String.valueOf(id)})
                        .build());
            }
        }

        return operations;
    }

    private ContentProviderOperation buildOperation(CommentDto commentDto, boolean isNew) {
        final ContentProviderOperation.Builder builder;

        if (isNew) {
            builder = ContentProviderOperation
                    .newInsert(CommentEntry.CONTENT_URI);
        } else {
            builder = ContentProviderOperation
                    .newUpdate(CommentEntry.CONTENT_URI)
                    .withSelection(CommentEntry._ID + "=?", new String[]{String.valueOf(commentDto.getId())});
        }

        builder.withValue(CommentEntry._ID, commentDto.getId())
                .withValue(CommentEntry.ISSUE_ID, commentDto.getIssueId())
                .withValue(CommentEntry.CREATED_USER_ID, commentDto.getCreatedUserId())
                .withValue(CommentEntry.CONTENT, commentDto.getContent())
                .withValue(CommentEntry.CREATED, commentDto.getCreated())
                .withValue(CommentEntry.UPDATED, commentDto.getUpdated())
                .withValue(CommentEntry.FINGERPRINT, SyncUtils.computeWeakHash(commentDto.toString()));

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
        return mContext.getContentResolver().query(BacklogContract.CommentEntry.CONTENT_URI, COMMENT_COLUMNS,
                null, null, null);
    }
}
