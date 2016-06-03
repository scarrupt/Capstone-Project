package com.codefactoring.android.backlogtracker.sync.handlers;

import android.content.ContentProviderOperation;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.codefactoring.android.backlogtracker.sync.models.BacklogImage;
import com.codefactoring.android.backlogtracker.sync.models.UserDto;
import com.codefactoring.android.backlogtracker.sync.utils.BacklogImageUtils;
import com.codefactoring.android.backlogtracker.sync.utils.SyncUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import javax.inject.Inject;

import static com.codefactoring.android.backlogtracker.provider.BacklogContract.UserEntry;
import static com.codefactoring.android.backlogtracker.provider.BacklogContract.UserEntry.CONTENT_URI;

public class UserDataHandler {

    public static final String LOG_TAG = UserDataHandler.class.getSimpleName();

    private static String[] USER_COLUMNS = {
            UserEntry.USER_ID,
            UserEntry.FINGERPRINT
    };

    protected static final int COL_USER_ID = 0;
    protected static final int COL_FINGERPRINT = 1;

    private final File mThumbnailDir;
    private final Context mContext;

    @Inject
    public UserDataHandler(Context context) {
        this.mContext = context;
        this.mThumbnailDir = context.getDir("user_thumbnails", Context.MODE_PRIVATE);
    }

    public ArrayList<ContentProviderOperation> makeContentProviderOperations(List<UserDto> users) {
        final ArrayList<ContentProviderOperation> operations = new ArrayList<>();

        final HashMap<String, String> fingerprints = loadFingerprints();
        final HashSet<String> usersToKeep = new HashSet<>();

        for (UserDto userDto : users) {

            if (fingerprints.containsKey(userDto.getUserId())) {

                final String currentFingerprint = SyncUtils.computeWeakHash(userDto.toString());
                final String recordedFingerPrint = fingerprints.get(userDto.getUserId());

                if (!recordedFingerPrint.equals(currentFingerprint)) {
                    operations.add(buildUserOperation(userDto, false));
                }
            } else {
                operations.add(buildUserOperation(userDto, true));
            }

            usersToKeep.add(userDto.getUserId());
        }

        for (String userId : fingerprints.keySet()) {
            if (!usersToKeep.contains(userId)) {
                operations.add(ContentProviderOperation
                        .newDelete(UserEntry.CONTENT_URI)
                        .withSelection(UserEntry.USER_ID + "=?", new String[]{userId})
                        .build());
            }
        }

        return operations;
    }

    private ContentProviderOperation buildUserOperation(UserDto userDto, boolean isNew) {
        final ContentProviderOperation.Builder builder;

        if (isNew) {
            builder = ContentProviderOperation
                    .newInsert(CONTENT_URI);
        } else {
            builder = ContentProviderOperation
                    .newUpdate(CONTENT_URI)
                    .withSelection(UserEntry.USER_ID + "=?", new String[]{userDto.getUserId()});
        }

        builder.withValue(UserEntry._ID, userDto.getId())
                .withValue(UserEntry.USER_ID, userDto.getUserId())
                .withValue(UserEntry.NAME, userDto.getName())
                .withValue(UserEntry.FINGERPRINT, SyncUtils.computeWeakHash(userDto.toString()));

        final String imagePath = getImagePathOrNull(userDto.getImage());
        if (imagePath != null) {
            BacklogImageUtils.saveImage(imagePath, userDto.getImage().getData());
        }
        builder.withValue(UserEntry.THUMBNAIL_URL, imagePath);

        return builder.build();
    }

    private String getImagePathOrNull(BacklogImage image) {
        return image == null ? null : new File(mThumbnailDir, image.getFilename()).toString();
    }

    private HashMap<String, String> loadFingerprints() {
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

            final HashMap<String, String> result = new HashMap<>();

            if (cursor.moveToFirst()) {
                do {
                    final String userId = cursor.getString(COL_USER_ID);
                    final String fingerprint = cursor.getString(COL_FINGERPRINT);
                    result.put(userId, fingerprint == null ? "" : fingerprint);
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
        return mContext.getContentResolver().query(UserEntry.CONTENT_URI, USER_COLUMNS,
                null, null, null);
    }
}
