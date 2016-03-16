package com.codefactoring.android.backlogtracker.sync.handlers;

import android.content.ContentProviderOperation;
import android.content.ContentValues;

import com.codefactoring.android.backlogtracker.BuildConfig;
import com.codefactoring.android.backlogtracker.sync.models.IssueTypeDto;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.Shadows;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowContentProviderOperation;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import static com.codefactoring.android.backlogtracker.provider.BacklogContract.IssueTypeEntry;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.robolectric.shadows.ShadowContentProviderOperation.TYPE_DELETE;
import static org.robolectric.shadows.ShadowContentProviderOperation.TYPE_INSERT;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class IssueTypeDataHandlerTest {

    private static final int INDEX_TYPE_DELETE = 0;
    private static final int INDEX_TYPE_INSERT = 1;
    private static final long PROJECT_ID = 1L;
    private static final long ISSUE_TYPE_ID = 1L;

    @Test
    public void createsOperationDeleteAllIssueTypesAtFirst() {
        final ArrayList<ContentProviderOperation> operations = new IssueTypeDataHandler()
                .makeContentProviderOperations(new HashSet<IssueTypeDto>());

        final ContentProviderOperation operation = operations.get(INDEX_TYPE_DELETE);

        assertThat(operation.getUri(), equalTo(IssueTypeEntry.CONTENT_URI));
        final ShadowContentProviderOperation shadowOperation = Shadows.shadowOf(operation);
        assertThat(shadowOperation.getType(), equalTo(TYPE_DELETE));
    }

    @Test
    public void createsOperationInsertIssueType() {
        final Set<IssueTypeDto> issueTypes = new HashSet<>();
        final IssueTypeDto issueTypeDto = new IssueTypeDto();
        issueTypeDto.setId(ISSUE_TYPE_ID);
        issueTypeDto.setProjectId(PROJECT_ID);
        issueTypeDto.setName("Bug");
        issueTypeDto.setColor("#990000");
        issueTypes.add(issueTypeDto);

        final ArrayList<ContentProviderOperation> operations = new IssueTypeDataHandler()
                .makeContentProviderOperations(issueTypes);

        final ContentProviderOperation operation = operations.get(INDEX_TYPE_INSERT);
        assertThat(operation.getUri(), equalTo(IssueTypeEntry.buildIssueTypeFromProjectIdUri(PROJECT_ID)));

        final ShadowContentProviderOperation shadowOperation = Shadows.shadowOf(operation);
        assertThat(shadowOperation.getType(), equalTo(TYPE_INSERT));

        final ContentValues contentValues = shadowOperation.getContentValues();
        assertThat(contentValues.getAsLong(IssueTypeEntry._ID), equalTo(issueTypeDto.getId()));
        assertThat(contentValues.getAsLong(IssueTypeEntry.PROJECT_ID), equalTo(issueTypeDto.getProjectId()));
        assertThat(contentValues.getAsString(IssueTypeEntry.NAME), equalTo(issueTypeDto.getName()));
        assertThat(contentValues.getAsString(IssueTypeEntry.COLOR), equalTo(issueTypeDto.getColor()));
    }
}