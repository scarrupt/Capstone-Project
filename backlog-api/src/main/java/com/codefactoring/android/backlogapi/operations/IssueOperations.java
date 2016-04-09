package com.codefactoring.android.backlogapi.operations;

import com.codefactoring.android.backlogapi.models.Comment;
import com.codefactoring.android.backlogapi.models.Issue;

import java.util.List;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

public interface IssueOperations {

    @GET("issues")
    Observable<List<Issue>> getIssueList(@Query("projectId[]") long projectId);

    @GET("issues/{issueId}/comments")
    Observable<List<Comment>> getCommentList(@Query("issueId") long issueId);
}
