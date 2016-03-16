package com.codefactoring.android.backlogtracker.sync.handlers;

import android.content.ContentProviderOperation;

import com.codefactoring.android.backlogtracker.sync.models.IssueDto;

import java.util.ArrayList;
import java.util.List;

import static com.codefactoring.android.backlogtracker.provider.BacklogContract.IssueEntry;

public class IssueDataHandler {

    public ArrayList<ContentProviderOperation> makeContentProviderOperations(List<IssueDto> issues) {
        final ArrayList<ContentProviderOperation> operations = new ArrayList<>();
        operations.add(ContentProviderOperation.newDelete(IssueEntry.CONTENT_URI).build());

        for (IssueDto issue : issues) {
            final ContentProviderOperation.Builder builder = ContentProviderOperation
                    .newInsert(IssueEntry.CONTENT_URI)
                    .withValue(IssueEntry._ID, issue.getId())
                    .withValue(IssueEntry.ISSUE_KEY, issue.getIssueKey())
                    .withValue(IssueEntry.PROJECT_ID, issue.getProjectId())
                    .withValue(IssueEntry.TYPE_ID, issue.getIssueType().getId())
                    .withValue(IssueEntry.SUMMARY, issue.getSummary())
                    .withValue(IssueEntry.DESCRIPTION, issue.getDescription())
                    .withValue(IssueEntry.PRIORITY, issue.getPriority())
                    .withValue(IssueEntry.STATUS, issue.getStatus())
                    .withValue(IssueEntry.MILESTONES, issue.getMilestones())
                    .withValue(IssueEntry.ASSIGNEE_ID, issue.getAssigneeId())
                    .withValue(IssueEntry.CREATED_USER_ID, issue.getCreatedUserId())
                    .withValue(IssueEntry.CREATED_DATE, issue.getCreatedDate())
                    .withValue(IssueEntry.UPDATED_USER_ID, issue.getUpdatedUserId())
                    .withValue(IssueEntry.UPDATED_DATE, issue.getUpdatedDate());

            operations.add(builder.build());
        }

        return operations;
    }
}
