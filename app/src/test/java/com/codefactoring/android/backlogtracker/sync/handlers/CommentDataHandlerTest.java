package com.codefactoring.android.backlogtracker.sync.handlers;

import android.content.ContentProviderOperation;
import android.content.ContentValues;

import com.codefactoring.android.backlogtracker.BuildConfig;
import com.codefactoring.android.backlogtracker.provider.BacklogContract;
import com.codefactoring.android.backlogtracker.sync.models.CommentDto;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.Shadows;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowContentProviderOperation;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.robolectric.shadows.ShadowContentProviderOperation.TYPE_DELETE;
import static org.robolectric.shadows.ShadowContentProviderOperation.TYPE_INSERT;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class CommentDataHandlerTest {

    private static final int INDEX_TYPE_DELETE = 0;
    private static final int INDEX_TYPE_INSERT = 1;
    private static final long COMMENT_ID = 1L;
    private static final long ISSUE_ID = 1L;
    private static final long USER_ID = 1L;

    @Test
    public void createsOperationDeleteAllCommentsTypesAtFirst() {
        final ArrayList<ContentProviderOperation> operations = new CommentDataHandler()
                .makeContentProviderOperations(new HashSet<CommentDto>());

        final ContentProviderOperation operation = operations.get(INDEX_TYPE_DELETE);

        assertThat(operation.getUri(), equalTo(BacklogContract.CommentEntry.CONTENT_URI));
        final ShadowContentProviderOperation shadowOperation = Shadows.shadowOf(operation);
        assertThat(shadowOperation.getType(), equalTo(TYPE_DELETE));
    }

    @Test
    public void createsOperationInsertCommentType() {
        final Set<CommentDto> comments = new HashSet<>();
        final CommentDto commentDto = new CommentDto();
        commentDto.setId(COMMENT_ID);
        commentDto.setCreatedUserId(USER_ID);
        commentDto.setIssueId(ISSUE_ID);
        commentDto.setContent("test");
        commentDto.setCreated("2013-08-05T06:15:06Z");
        commentDto.setUpdated("2013-08-05T06:15:06Z");
        comments.add(commentDto);

        final ArrayList<ContentProviderOperation> operations = new CommentDataHandler()
                .makeContentProviderOperations(comments);

        final ContentProviderOperation operation = operations.get(INDEX_TYPE_INSERT);
        assertThat(operation.getUri(), equalTo(BacklogContract.CommentEntry.buildCommentUriFromIssueIdUri(ISSUE_ID)));

        final ShadowContentProviderOperation shadowOperation = Shadows.shadowOf(operation);
        assertThat(shadowOperation.getType(), equalTo(TYPE_INSERT));

        final ContentValues contentValues = shadowOperation.getContentValues();
        assertThat(contentValues.getAsLong(BacklogContract.CommentEntry._ID), equalTo(commentDto.getId()));
        assertThat(contentValues.getAsLong(BacklogContract.CommentEntry.ISSUE_ID), equalTo(commentDto.getIssueId()));
        assertThat(contentValues.getAsLong(BacklogContract.CommentEntry.CREATED_USER_ID), equalTo(commentDto.getCreatedUserId()));
        assertThat(contentValues.getAsString(BacklogContract.CommentEntry.CONTENT), equalTo(commentDto.getContent()));
        assertThat(contentValues.getAsString(BacklogContract.CommentEntry.CREATED), equalTo(commentDto.getCreated()));
        assertThat(contentValues.getAsString(BacklogContract.CommentEntry.UPDATED), equalTo(commentDto.getUpdated()));
    }
}