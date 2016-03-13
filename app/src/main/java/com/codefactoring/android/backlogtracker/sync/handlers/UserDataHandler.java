package com.codefactoring.android.backlogtracker.sync.handlers;

import android.content.ContentProviderOperation;
import android.content.Context;

import com.codefactoring.android.backlogtracker.sync.models.BacklogImage;
import com.codefactoring.android.backlogtracker.sync.models.UserDto;
import com.codefactoring.android.backlogtracker.sync.utils.BacklogImageUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import static com.codefactoring.android.backlogtracker.provider.BacklogContract.UserEntry;
import static com.codefactoring.android.backlogtracker.provider.BacklogContract.UserEntry.CONTENT_URI;

public class UserDataHandler {

    private final File mThumbnailDir;

    @Inject
    public UserDataHandler(Context context) {
        this.mThumbnailDir = context.getDir("user_thumbnails", Context.MODE_PRIVATE);
    }

    public ArrayList<ContentProviderOperation> makeContentProviderOperations(List<UserDto> users) {
        final ArrayList<ContentProviderOperation> operations = new ArrayList<>();

        operations.add(ContentProviderOperation.newDelete(UserEntry.CONTENT_URI).build());

        for (UserDto userDto : users) {
            final ContentProviderOperation.Builder builder = ContentProviderOperation
                    .newInsert(CONTENT_URI)
                    .withValue(UserEntry._ID, userDto.getId())
                    .withValue(UserEntry.USER_ID, userDto.getUserId())
                    .withValue(UserEntry.NAME, userDto.getName());


            final String imagePath = getImagePathOrNull(userDto.getImage());
            builder.withValue(UserEntry.THUMBNAIL_URL, imagePath);

            if (imagePath != null) {
                BacklogImageUtils.saveImage(imagePath, userDto.getImage().getData());
            }

            operations.add(builder.build());
        }

        return operations;
    }

    private String getImagePathOrNull(BacklogImage image) {
        return image == null ? null : new File(mThumbnailDir, image.getFilename()).toString();
    }
}
