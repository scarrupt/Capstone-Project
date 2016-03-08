package com.codefactoring.android.backlogapi.operations;

import com.codefactoring.android.backlogapi.models.Project;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Path;
import rx.Observable;

public interface ProjectOperations {

    @GET("projects")
    Observable<List<Project>> getProjectList();

    @GET("projects/{projectId}/image")
    Observable<ResponseBody> getProjectIcon(@Path("projectId")long projectId);
}
