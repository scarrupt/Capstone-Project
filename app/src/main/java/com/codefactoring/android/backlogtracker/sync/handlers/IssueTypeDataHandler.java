package com.codefactoring.android.backlogtracker.sync.handlers;

import android.content.ContentProviderOperation;

import com.codefactoring.android.backlogtracker.sync.models.IssueTypeDto;

import java.util.ArrayList;
import java.util.Set;

import static com.codefactoring.android.backlogtracker.provider.BacklogContract.IssueTypeEntry;

public class IssueTypeDataHandler {

    public ArrayList<ContentProviderOperation>  makeContentProviderOperations(Set<IssueTypeDto> issueTypeSet) {

        final ArrayList<ContentProviderOperation> operations = new ArrayList<>();

        operations.add(ContentProviderOperation.newDelete(IssueTypeEntry.CONTENT_URI).build());

        for (IssueTypeDto issueType : issueTypeSet) {
            final ContentProviderOperation.Builder builder = ContentProviderOperation
                    .newInsert(IssueTypeEntry.buildIssueTypeFromProjectIdUri(issueType.getProjectId()))
                    .withValue(IssueTypeEntry._ID, issueType.getId())
                    .withValue(IssueTypeEntry.PROJECT_ID, issueType.getProjectId())
                    .withValue(IssueTypeEntry.NAME, issueType.getName())
                    .withValue(IssueTypeEntry.COLOR, issueType.getColor());

            operations.add(builder.build());
        }

        return operations;
    }
}
