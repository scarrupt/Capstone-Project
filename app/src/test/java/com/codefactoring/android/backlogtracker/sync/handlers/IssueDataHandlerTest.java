package com.codefactoring.android.backlogtracker.sync.handlers;

import android.content.ContentProviderOperation;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.codefactoring.android.backlogtracker.BuildConfig;
import com.codefactoring.android.backlogtracker.provider.BacklogContract;
import com.codefactoring.android.backlogtracker.sync.models.IssueDto;
import com.codefactoring.android.backlogtracker.sync.models.IssueTypeDto;
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

import static com.codefactoring.android.backlogtracker.provider.BacklogContract.IssueEntry;
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
public class IssueDataHandlerTest {

    private static final long ISSUE_ID = 1L;

    private Context mContext;

    @Before
    public void setUp() {
        mContext = RuntimeEnvironment.application;
    }

    @Test
    public void createsOperationDeleteRemovedIssue() {
        final IssueDataHandler issueDataHandler = new IssueDataHandler(mContext) {
            @Override
            protected Cursor query() {
                final Cursor cursor = mock(Cursor.class);
                when(cursor.getCount()).thenReturn(1);
                when(cursor.moveToFirst()).thenReturn(true);
                when(cursor.getLong(eq(COL_ID))).thenReturn(1L);
                return cursor;
            }
        };

        final ArrayList<ContentProviderOperation> operations = issueDataHandler
                .makeContentProviderOperations(new ArrayList<IssueDto>());

        final ContentProviderOperation operation = operations.get(0);

        assertThat(operation.getUri(), equalTo(IssueEntry.CONTENT_URI));
        final ShadowContentProviderOperation shadowOperation = Shadows.shadowOf(operation);
        assertThat(shadowOperation.getType(), equalTo(TYPE_DELETE));
    }

    @Test
    public void createsOperationUpdateIssue(){

        final IssueDataHandler issueDataHandler = new IssueDataHandler(mContext) {
            @Override
            protected Cursor query() {
                final Cursor cursor = mock(Cursor.class);
                when(cursor.getCount()).thenReturn(1);
                when(cursor.moveToFirst()).thenReturn(true);
                when(cursor.getLong(eq(COL_ID))).thenReturn(ISSUE_ID);
                return cursor;
            }
        };

        final IssueDto issueDto = new IssueDto();
        issueDto.setId(ISSUE_ID);
        issueDto.setProjectId(1L);
        final IssueTypeDto issueTypeDto = new IssueTypeDto();
        issueTypeDto.setId(1L);
        issueDto.setIssueType(issueTypeDto);
        issueDto.setSummary("first issue");
        issueDto.setDescription("");
        issueDto.setPriority("Normal");
        issueDto.setStatus("Open");
        issueDto.setMilestones("wait for release");
        issueDto.setAssigneeId(1L);
        issueDto.setCreatedUserId(1L);
        issueDto.setCreatedDate("2012-07-23T06:10:15Z");
        issueDto.setUpdatedUserId(1L);
        issueDto.setUpdatedDate("2013-02-07T08:09:49Z");

        final ArrayList<ContentProviderOperation> operations = issueDataHandler
                .makeContentProviderOperations(Lists.newArrayList(issueDto));

        final ContentProviderOperation operation = operations.get(0);

        assertThat(operation.getUri(), equalTo(BacklogContract.IssueEntry.CONTENT_URI));
        final ShadowContentProviderOperation shadowOperation = Shadows.shadowOf(operation);
        assertThat(shadowOperation.getType(), equalTo(TYPE_UPDATE));
    }

    @Test
    public void createsOperationInsertIssue() {
        final List<IssueDto> issues = new ArrayList<>();
        final IssueDto issueDto = new IssueDto();
        issueDto.setId(ISSUE_ID);
        issueDto.setProjectId(1L);
        final IssueTypeDto issueTypeDto = new IssueTypeDto();
        issueTypeDto.setId(1L);
        issueDto.setIssueType(issueTypeDto);
        issueDto.setSummary("first issue");
        issueDto.setDescription("");
        issueDto.setPriority("Normal");
        issueDto.setStatus("Open");
        issueDto.setMilestones("wait for release");
        issueDto.setAssigneeId(1L);
        issueDto.setCreatedUserId(1L);
        issueDto.setCreatedDate("2012-07-23T06:10:15Z");
        issueDto.setUpdatedUserId(1L);
        issueDto.setUpdatedDate("2013-02-07T08:09:49Z");
        issues.add(issueDto);

        final ArrayList<ContentProviderOperation> operations = new IssueDataHandler(mContext)
                .makeContentProviderOperations(issues);

        final ContentProviderOperation operation = operations.get(0);
        assertThat(operation.getUri(), equalTo(IssueEntry.CONTENT_URI  ));

        final ShadowContentProviderOperation shadowOperation = Shadows.shadowOf(operation);
        assertThat(shadowOperation.getType(), equalTo(TYPE_INSERT));

        final ContentValues contentValues = shadowOperation.getContentValues();
        assertThat(contentValues.getAsLong(IssueEntry._ID), equalTo(issueDto.getId()));
        assertThat(contentValues.getAsLong(IssueEntry.PROJECT_ID), equalTo(issueDto.getProjectId()));
        assertThat(contentValues.getAsLong(IssueEntry.TYPE_ID), equalTo(issueDto.getIssueType().getId()));
        assertThat(contentValues.getAsString(IssueEntry.SUMMARY), equalTo(issueDto.getSummary()));
        assertThat(contentValues.getAsString(IssueEntry.DESCRIPTION), equalTo(issueDto.getDescription()));
        assertThat(contentValues.getAsString(IssueEntry.PRIORITY), equalTo(issueDto.getPriority()));
        assertThat(contentValues.getAsString(IssueEntry.STATUS), equalTo(issueDto.getStatus()));
        assertThat(contentValues.getAsString(IssueEntry.MILESTONES), equalTo(issueDto.getMilestones()));
        assertThat(contentValues.getAsLong(IssueEntry.ASSIGNEE_ID), equalTo(issueDto.getAssigneeId()));
        assertThat(contentValues.getAsLong(IssueEntry.CREATED_USER_ID), equalTo(issueDto.getCreatedUserId()));
        assertThat(contentValues.getAsString(IssueEntry.CREATED_DATE), equalTo(issueDto.getCreatedDate()));
        assertThat(contentValues.getAsLong(IssueEntry.UPDATED_USER_ID), equalTo(issueDto.getUpdatedUserId()));
        assertThat(contentValues.getAsString(IssueEntry.UPDATED_DATE), equalTo(issueDto.getUpdatedDate()));
    }

}