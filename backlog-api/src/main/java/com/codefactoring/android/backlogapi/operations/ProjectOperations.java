package com.codefactoring.android.backlogapi.operations;

import com.codefactoring.android.backlogapi.models.Project;

import java.util.List;

import retrofit2.http.GET;
import rx.Observable;

public interface ProjectOperations {

    @GET("projects")
    Observable<List<Project>> getProjectList();
}
