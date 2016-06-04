package com.codefactoring.android.backlogtracker.sync.handlers;

import android.content.ContentProviderOperation;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.codefactoring.android.backlogtracker.BuildConfig;
import com.codefactoring.android.backlogtracker.provider.BacklogContract;
import com.codefactoring.android.backlogtracker.sync.models.CommentDto;
import com.google.common.collect.Lists;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.Shadows;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowContentProviderOperation;

import java.util.ArrayList;
import java.util.List;

import static com.codefactoring.android.backlogtracker.provider.BacklogContract.CommentEntry.CONTENT_URI;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.robolectric.shadows.ShadowContentProviderOperation.TYPE_DELETE;
import static org.robolectric.shadows.ShadowContentProviderOperation.TYPE_INSERT;
import static org.robolectric.shadows.ShadowContentProviderOperation.TYPE_UPDATE;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class CommentDataHandlerTest {

    private static final long COMMENT_ID = 1L;
    private static final long ISSUE_ID = 1L;
    private static final long USER_ID = 1L;

    private Context mContext;

    @Before
    public void setUp() {
        mContext = RuntimeEnvironment.application;
    }

    @Test
    public void createsOperationDeleteRemovedComment() {
        final CommentDataHandler commentDataHandler = new CommentDataHandler(mContext) {
            @Override
            protected Cursor query() {
                final Cursor cursor = mock(Cursor.class);
                when(cursor.getCount()).thenReturn(1);
                when(cursor.moveToFirst()).thenReturn(true);
                when(cursor.getLong(eq(COL_ID))).thenReturn(1L);
                return cursor;
            }
        };

        final ArrayList<ContentProviderOperation> operations = commentDataHandler
                .makeContentProviderOperations(new ArrayList<CommentDto>());

        final ContentProviderOperation operation = operations.get(0);

        assertThat(operation.getUri(), equalTo(CONTENT_URI));
        final ShadowContentProviderOperation shadowOperation = Shadows.shadowOf(operation);
        assertThat(shadowOperation.getType(), equalTo(TYPE_DELETE));
    }

    @Test
    public void createsOperationUpdateComment(){

        final CommentDataHandler commentDataHandler = new CommentDataHandler(mContext) {
            @Override
            protected Cursor query() {
                final Cursor cursor = mock(Cursor.class);
                when(cursor.getCount()).thenReturn(1);
                when(cursor.moveToFirst()).thenReturn(true);
                when(cursor.getLong(eq(COL_ID))).thenReturn(COMMENT_ID);
                return cursor;
            }
        };

        final CommentDto commentDto = new CommentDto();
        commentDto.setId(COMMENT_ID);
        commentDto.setCreatedUserId(USER_ID);
        commentDto.setIssueId(ISSUE_ID);
        commentDto.setContent("test");
        commentDto.setCreated("2013-08-05T06:15:06Z");
        commentDto.setUpdated("2013-08-05T06:15:06Z");

        final ArrayList<ContentProviderOperation> operations = commentDataHandler
                .makeContentProviderOperations(Lists.newArrayList(commentDto));

        final ContentProviderOperation operation = operations.get(0);

        assertThat(operation.getUri(), equalTo(BacklogContract.CommentEntry.CONTENT_URI));
        final ShadowContentProviderOperation shadowOperation = Shadows.shadowOf(operation);
        assertThat(shadowOperation.getType(), equalTo(TYPE_UPDATE));
    }

    @Test
    public void createsOperationInsertCommentType() {
        final List<CommentDto> comments = new ArrayList<>();
        final CommentDto commentDto = new CommentDto();
        commentDto.setId(COMMENT_ID);
        commentDto.setCreatedUserId(USER_ID);
        commentDto.setIssueId(ISSUE_ID);
        commentDto.setContent("test");
        commentDto.setCreated("2013-08-05T06:15:06Z");
        commentDto.setUpdated("2013-08-05T06:15:06Z");
        comments.add(commentDto);

        final ArrayList<ContentProviderOperation> operations = new CommentDataHandler(mContext)
                .makeContentProviderOperations(comments);

        final ContentProviderOperation operation = operations.get(0);
        assertThat(operation.getUri(), equalTo(BacklogContract.CommentEntry.CONTENT_URI));

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