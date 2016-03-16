package com.codefactoring.android.backlogtracker.sync.handlers;

import android.content.ContentProviderOperation;
import android.content.ContentValues;

import com.codefactoring.android.backlogtracker.BuildConfig;
import com.codefactoring.android.backlogtracker.sync.models.IssueDto;
import com.codefactoring.android.backlogtracker.sync.models.IssueTypeDto;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.Shadows;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowContentProviderOperation;

import java.util.ArrayList;
import java.util.List;

import static com.codefactoring.android.backlogtracker.provider.BacklogContract.IssueEntry;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.robolectric.shadows.ShadowContentProviderOperation.TYPE_DELETE;
import static org.robolectric.shadows.ShadowContentProviderOperation.TYPE_INSERT;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class IssueDataHandlerTest {

    private static final int INDEX_TYPE_DELETE = 0;
    private static final int INDEX_TYPE_INSERT = 1;

    @Test
    public void createsOperationDeleteAllIssuesAtFirst() {
        final ArrayList<ContentProviderOperation> operations = new IssueDataHandler()
                .makeContentProviderOperations(new ArrayList<IssueDto>());

        final ContentProviderOperation operation = operations.get(INDEX_TYPE_DELETE);

        assertThat(operation.getUri(), equalTo(IssueEntry.CONTENT_URI));
        final ShadowContentProviderOperation shadowOperation = Shadows.shadowOf(operation);
        assertThat(shadowOperation.getType(), equalTo(TYPE_DELETE));
    }

    @Test
    public void createsOperationInsertIssue() {
        final List<IssueDto> issues = new ArrayList<>();
        final IssueDto issueDto = new IssueDto();
        issueDto.setId(1L);
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

        final ArrayList<ContentProviderOperation> operations = new IssueDataHandler()
                .makeContentProviderOperations(issues);

        final ContentProviderOperation operation = operations.get(INDEX_TYPE_INSERT);
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