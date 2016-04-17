package com.codefactoring.android.backlogtracker.sync.fetchers;

import android.util.Log;

import com.codefactoring.android.backlogapi.BacklogApiClient;
import com.codefactoring.android.backlogapi.models.ChangeLog;
import com.codefactoring.android.backlogapi.models.Comment;
import com.codefactoring.android.backlogtracker.sync.models.CommentDto;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.functions.Func1;

public class CommentDataFetcher {

    private final String LOG_TAG = CommentDataFetcher.class.getSimpleName();

    final BacklogApiClient mBacklogApiClient;

    public CommentDataFetcher(BacklogApiClient backlogApiClient) {
        mBacklogApiClient = backlogApiClient;
    }

    public List<CommentDto> getCommentList(final long issueId) {
        return mBacklogApiClient.getIssueOperations().getCommentList(issueId)
                .onErrorReturn(new Func1<Throwable, List<Comment>>() {
                    @Override
                    public List<Comment> call(Throwable throwable) {
                        Log.e(LOG_TAG, "Error on getCommentList", throwable);
                        return new ArrayList<>();
                    }
                })
                .flatMapIterable(new Func1<List<Comment>, Iterable<Comment>>() {
                    @Override
                    public Iterable<Comment> call(List<Comment> comments) {
                        return comments;
                    }
                })
                .flatMap(new Func1<Comment, Observable<CommentDto>>() {
                    @Override
                    public Observable<CommentDto> call(Comment comment) {
                        final CommentDto commentDto = new CommentDto();
                        commentDto.setId(comment.getId());
                        commentDto.setIssueId(issueId);
                        commentDto.setCreatedUserId(comment.getCreatedUser().getId());
                        commentDto.setContent(
                                comment.getContent() == null || comment.getContent().isEmpty()
                                        ? transformToString(comment.getChangeLog())
                                        : comment.getContent());
                        commentDto.setCreated(comment.getCreated());
                        commentDto.setUpdated(comment.getUpdated());
                        return Observable.just(commentDto);
                    }
                })
                .toList()
                .toBlocking()
                .first();
    }

    private String transformToString(ChangeLog[] changeLogs) {
        final StringBuilder sb = new StringBuilder();

        for (ChangeLog changeLog : changeLogs) {
            sb.append(changeLog.getField())
                    .append(": ")
                    .append(changeLog.getOldValue())
                    .append(" -> ")
                    .append(changeLog.getNewValue())
                    .append("\n");
        }

        return sb.toString();
    }
}
