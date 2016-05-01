package com.codefactoring.android.backlogtracker.sync.handlers;

import android.content.ContentProviderOperation;

import com.codefactoring.android.backlogtracker.provider.BacklogContract;
import com.codefactoring.android.backlogtracker.sync.models.CommentDto;

import java.util.ArrayList;
import java.util.List;

public class CommentDataHandler {

    public ArrayList<ContentProviderOperation> makeContentProviderOperations(List<CommentDto> comments) {

        final ArrayList<ContentProviderOperation> operations = new ArrayList<>();

        operations.add(ContentProviderOperation.newDelete(BacklogContract.CommentEntry.CONTENT_URI).build());

        for (CommentDto comment : comments) {
            final ContentProviderOperation.Builder builder = ContentProviderOperation
                    .newInsert(BacklogContract.CommentEntry.buildCommentUriFromIssueIdUri(comment.getIssueId()))
                    .withValue(BacklogContract.CommentEntry._ID, comment.getId())
                    .withValue(BacklogContract.CommentEntry.ISSUE_ID, comment.getIssueId())
                    .withValue(BacklogContract.CommentEntry.CREATED_USER_ID, comment.getCreatedUserId())
                    .withValue(BacklogContract.CommentEntry.CONTENT, comment.getContent())
                    .withValue(BacklogContract.CommentEntry.CREATED, comment.getCreated())
                    .withValue(BacklogContract.CommentEntry.UPDATED, comment.getUpdated());

            operations.add(builder.build());
        }

        return operations;
    }
}
