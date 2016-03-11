package com.codefactoring.android.backlogtracker.sync.handlers;


import android.content.ContentProviderOperation;
import android.content.Context;

import com.codefactoring.android.backlogtracker.sync.models.BacklogImage;
import com.codefactoring.android.backlogtracker.sync.models.ProjectDto;
import com.codefactoring.android.backlogtracker.sync.utils.BacklogImageUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import static com.codefactoring.android.backlogtracker.provider.BacklogContract.ProjectEntry;

public class ProjectDataHandler {

    private final File mThumbnailDir;

    @Inject
    public ProjectDataHandler(Context context) {
        this.mThumbnailDir = context.getDir("project_thumbnails", Context.MODE_PRIVATE);
    }

    public ArrayList<ContentProviderOperation> makeContentProviderOperations(List<ProjectDto> projects) {
        ArrayList<ContentProviderOperation> operations = new ArrayList<>();

        operations.add(ContentProviderOperation.newDelete(ProjectEntry.CONTENT_URI).build());

        for (ProjectDto project : projects) {
            final ContentProviderOperation.Builder builder = ContentProviderOperation
                    .newInsert(ProjectEntry.CONTENT_URI)
                    .withValue(ProjectEntry._ID, project.getId())
                    .withValue(ProjectEntry.PROJECT_KEY, project.getProjectKey())
                    .withValue(ProjectEntry.NAME, project.getName());

            final String imagePath = getImagePathOrNull(project.getImage());
            builder.withValue(ProjectEntry.THUMBNAIL_URL, imagePath);

            if (imagePath != null) {
                BacklogImageUtils.saveImage(imagePath, project.getImage().getData());
            }

            operations.add(builder.build());
        }

        return operations;
    }

    private String getImagePathOrNull(BacklogImage image) {
        return image == null ? null : new File(mThumbnailDir, image.getFilename()).toString();
    }
}
