package com.codefactoring.android.backlogtracker.sync.handlers;


import android.content.ContentProviderOperation;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.codefactoring.android.backlogtracker.sync.models.BacklogImage;
import com.codefactoring.android.backlogtracker.sync.models.ProjectDto;
import com.codefactoring.android.backlogtracker.sync.utils.BacklogImageUtils;
import com.codefactoring.android.backlogtracker.sync.utils.SyncUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import javax.inject.Inject;

import static com.codefactoring.android.backlogtracker.provider.BacklogContract.ProjectEntry;

public class ProjectDataHandler {

    public static final String LOG_TAG = ProjectDataHandler.class.getSimpleName();

    private static String[] PROJECT_COLUMNS = {
            ProjectEntry.PROJECT_KEY,
            ProjectEntry.FINGERPRINT
    };

    protected static final int COL_PROJECT_KEY = 0;
    protected static final int COL_FINGERPRINT = 1;

    private final File mThumbnailDir;
    private final Context mContext;

    @Inject
    public ProjectDataHandler(Context context) {
        mContext = context;
        this.mThumbnailDir = context.getDir("project_thumbnails", Context.MODE_PRIVATE);
    }

    public ArrayList<ContentProviderOperation> makeContentProviderOperations(List<ProjectDto> projects) {
        ArrayList<ContentProviderOperation> operations = new ArrayList<>();

        final HashMap<String, String> fingerprints = loadFingerprints();
        final HashSet<String> dataToKeep = new HashSet<>();

        for (ProjectDto project : projects) {

            if (fingerprints.containsKey(project.getProjectKey())) {

                final String currentFingerprint = SyncUtils.computeWeakHash(project.toString());
                final String recordedFingerPrint = fingerprints.get(project.getProjectKey());

                if (!recordedFingerPrint.equals(currentFingerprint)) {
                    operations.add(buildOperation(project, false));
                }
            } else {
                operations.add(buildOperation(project, true));
            }

            dataToKeep.add(project.getProjectKey());
        }

        for (String projectKey : fingerprints.keySet()) {
            if (!dataToKeep.contains(projectKey)) {
                operations.add(ContentProviderOperation
                        .newDelete(ProjectEntry.CONTENT_URI)
                        .withSelection(ProjectEntry.PROJECT_KEY + "=?", new String[]{projectKey})
                        .build());
            }
        }

        return operations;
    }

    private ContentProviderOperation buildOperation(ProjectDto projectDto, boolean isNew) {
        final ContentProviderOperation.Builder builder;

        if (isNew) {
            builder = ContentProviderOperation
                    .newInsert(ProjectEntry.CONTENT_URI);
        } else {
            builder = ContentProviderOperation
                    .newUpdate(ProjectEntry.CONTENT_URI)
                    .withSelection(ProjectEntry.PROJECT_KEY + "=?", new String[]{projectDto.getProjectKey()});
        }

        builder.withValue(ProjectEntry._ID, projectDto.getId())
                .withValue(ProjectEntry.PROJECT_KEY, projectDto.getProjectKey())
                .withValue(ProjectEntry.NAME, projectDto.getName())
                .withValue(ProjectEntry.FINGERPRINT, SyncUtils.computeWeakHash(projectDto.toString()));

        final String imagePath = getImagePathOrNull(projectDto.getImage());
        if (imagePath != null) {
            BacklogImageUtils.saveImage(imagePath, projectDto.getImage().getData());
        }
        builder.withValue(ProjectEntry.THUMBNAIL_URL, imagePath);

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
                    final String projectKey = cursor.getString(COL_PROJECT_KEY);
                    final String fingerprint = cursor.getString(COL_FINGERPRINT);
                    result.put(projectKey, fingerprint == null ? "" : fingerprint);
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
        return mContext.getContentResolver().query(ProjectEntry.CONTENT_URI, PROJECT_COLUMNS,
                null, null, null);
    }
}
