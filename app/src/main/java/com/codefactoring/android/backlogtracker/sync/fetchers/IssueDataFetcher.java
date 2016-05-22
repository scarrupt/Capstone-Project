package com.codefactoring.android.backlogtracker.sync.fetchers;

import android.util.Log;

import com.codefactoring.android.backlogapi.BacklogApiClient;
import com.codefactoring.android.backlogapi.models.Issue;
import com.codefactoring.android.backlogapi.models.Milestone;
import com.codefactoring.android.backlogapi.models.User;
import com.codefactoring.android.backlogtracker.sync.models.IssueDto;
import com.codefactoring.android.backlogtracker.sync.models.IssueTypeDto;
import com.codefactoring.android.backlogtracker.sync.utils.SyncUtils;
import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.functions.Func1;

import static com.codefactoring.android.backlogapi.BacklogApiConstants.STATUS_ISSUE_ID_IN_PROGRESS;
import static com.codefactoring.android.backlogapi.BacklogApiConstants.STATUS_ISSUE_ID_OPEN;
import static com.codefactoring.android.backlogapi.BacklogApiConstants.STATUS_ISSUE_ID_RESOLVED;

public class IssueDataFetcher {

    private final String LOG_TAG = IssueDataFetcher.class.getSimpleName();

    final BacklogApiClient mBacklogApiClient;

    public IssueDataFetcher(BacklogApiClient backlogApiClient) {
        mBacklogApiClient = backlogApiClient;
    }

    public List<IssueDto> getIssueList(long projectId) {
        return mBacklogApiClient
                .getIssueOperations()
                .getIssueList(projectId, Lists.newArrayList(
                        STATUS_ISSUE_ID_OPEN,
                        STATUS_ISSUE_ID_IN_PROGRESS,
                        STATUS_ISSUE_ID_RESOLVED))
                .onErrorReturn(new Func1<Throwable, List<Issue>>() {
                    @Override
                    public List<Issue> call(Throwable throwable) {
                        Log.e(LOG_TAG, "Error on getIssueList", throwable);
                        return new ArrayList<>();
                    }
                })
                .flatMapIterable(new Func1<List<Issue>, Iterable<Issue>>() {
                    @Override
                    public Iterable<Issue> call(List<Issue> issues) {
                        return issues;
                    }
                })
                .flatMap(new Func1<Issue, Observable<IssueDto>>() {
                    @Override
                    public Observable<IssueDto> call(Issue issue) {
                        final IssueDto issueDto = new IssueDto();
                        issueDto.setId(issue.getId());
                        issueDto.setProjectId(issue.getProjectId());
                        issueDto.setIssueKey(issue.getIssueKey());
                        issueDto.setSummary(issue.getSummary());
                        issueDto.setDescription(issue.getDescription());
                        issueDto.setPriority(issue.getPriority().getName());
                        issueDto.setStatus(issue.getStatus().getName());
                        issueDto.setAssigneeId(getUserIdOrNull(issue.getAssignee()));
                        issueDto.setMilestones(Joiner.on(",").join(Lists.transform(issue.getMilestone(),
                                new Function<Milestone, String>() {
                                    @Override
                                    public String apply(Milestone milestone) {
                                        return milestone.getName();
                                    }
                                }
                        )));

                        issueDto.setCreatedUserId(issue.getCreatedUser().getId());
                        issueDto.setCreatedDate(SyncUtils.formatDate(issue.getCreated()));
                        issueDto.setUpdatedUserId(getUserIdOrNull(issue.getUpdatedUser()));
                        issueDto.setUpdatedDate(SyncUtils.formatDate(issue.getUpdated()));
                        issueDto.setUrl(mBacklogApiClient.getBaseURL() + issue.getIssueKey());

                        final IssueTypeDto issueTypeDto = new IssueTypeDto();
                        issueTypeDto.setId(issue.getIssueType().getId());
                        issueTypeDto.setProjectId(issue.getIssueType().getProjectId());
                        issueTypeDto.setName(issue.getIssueType().getName());
                        issueTypeDto.setColor(issue.getIssueType().getColor());
                        issueDto.setIssueType(issueTypeDto);
                        return Observable.just(issueDto);
                    }
                })
                .toList()
                .toBlocking()
                .first();
    }

    private Long getUserIdOrNull(User user) {
        return user == null ? null : user.getId();
    }
}