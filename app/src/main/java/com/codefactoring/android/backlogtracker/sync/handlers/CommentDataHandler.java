package com.codefactoring.android.backlogtracker.sync.handlers;

import android.content.ContentProviderOperation;

import com.codefactoring.android.backlogtracker.sync.models.CommentDto;
import com.codefactoring.android.backlogtracker.sync.utils.SyncUtils;

import java.util.ArrayList;
import java.util.List;

import static com.codefactoring.android.backlogtracker.provider.BacklogContract.CommentEntry;

public class CommentDataHandler {

    public ArrayList<ContentProviderOperation> makeContentProviderOperations(List<CommentDto> comments) {

        final ArrayList<ContentProviderOperation> operations = new ArrayList<>();

        operations.add(ContentProviderOperation.newDelete(CommentEntry.CONTENT_URI).build());

        for (CommentDto comment : comments) {
            final ContentProviderOperation.Builder builder = ContentProviderOperation
                    .newInsert(CommentEntry.buildCommentUriFromIssueIdUri(comment.getIssueId()))
                    .withValue(CommentEntry._ID, comment.getId())
                    .withValue(CommentEntry.ISSUE_ID, comment.getIssueId())
                    .withValue(CommentEntry.CREATED_USER_ID, comment.getCreatedUserId())
                    .withValue(CommentEntry.CONTENT, comment.getContent())
                    .withValue(CommentEntry.CREATED, comment.getCreated())
                    .withValue(CommentEntry.UPDATED, comment.getUpdated())
                    .withValue(CommentEntry.FINGERPRINT, SyncUtils.computeWeakHash(comment.toString()));

            operations.add(builder.build());
        }

        return operations;
    }
}
