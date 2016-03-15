package com.codefactoring.android.backlogtracker.sync.fetchers;

import android.util.Log;

import com.codefactoring.android.backlogapi.BacklogApiClient;
import com.codefactoring.android.backlogapi.models.Issue;
import com.codefactoring.android.backlogapi.models.User;
import com.codefactoring.android.backlogtracker.sync.models.IssueDto;
import com.codefactoring.android.backlogtracker.sync.models.IssueTypeDto;
import com.google.common.base.Joiner;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import rx.Observable;
import rx.functions.Func1;

import static com.codefactoring.android.backlogapi.BacklogApiConstants.DATE_FORMAT_PATTERN;

public class IssueDataFetcher {

    private final String LOG_TAG = IssueDataFetcher.class.getSimpleName();

    final BacklogApiClient mBacklogApiClient;

    public IssueDataFetcher(BacklogApiClient backlogApiClient) {
        mBacklogApiClient = backlogApiClient;
    }

    public List<IssueDto> getIssueList(long projectId) {
        return mBacklogApiClient
                .getIssueOperations()
                .getIssueList(projectId)
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
                        issueDto.setMilestones(Joiner.on(",").join(issue.getMilestone()));
                        issueDto.setCreatedUserId(issue.getCreatedUser().getId());
                        issueDto.setCreatedDate(formatDate(issue.getCreated()));
                        issueDto.setUpdatedUserId(getUserIdOrNull(issue.getUpdatedUser()));
                        issueDto.setUpdatedDate(formatDate(issue.getUpdated()));

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

    private String formatDate(Date date) {
        if (date == null) {
            return null;
        } else {
            final SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_FORMAT_PATTERN, Locale.getDefault());
            return simpleDateFormat.format(date);
        }
    }
}
