package com.codefactoring.android.backlogtracker.sync.fetchers;

import android.util.Log;

import com.codefactoring.android.backlogapi.BacklogApiClient;
import com.codefactoring.android.backlogapi.models.Project;
import com.codefactoring.android.backlogtracker.sync.models.BacklogImage;
import com.codefactoring.android.backlogtracker.sync.models.ProjectDto;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import rx.Observable;
import rx.functions.Func1;

public class ProjectDataFetcher {

    public final String LOG_TAG = ProjectDataFetcher.class.getSimpleName();

    private final BacklogApiClient mBacklogApiClient;

    public ProjectDataFetcher(BacklogApiClient backlogApiClient) {
        mBacklogApiClient = backlogApiClient;
    }

    public List<ProjectDto> getProjectList() {
        return mBacklogApiClient.getProjectOperations().getProjectList()
                .onErrorReturn(new Func1<Throwable, List<Project>>() {
                    @Override
                    public List<Project> call(Throwable throwable) {
                        Log.e(LOG_TAG, "Error on getProjectList", throwable);
                        return new ArrayList<>();
                    }
                })
                .flatMapIterable(new Func1<List<Project>, Iterable<Project>>() {
                    @Override
                    public Iterable<Project> call(List<Project> projects) {
                        return projects;
                    }
                })
                .flatMap(new Func1<Project, Observable<ProjectDto>>() {
                    @Override
                    public Observable<ProjectDto> call(Project project) {
                        final ProjectDto projectDto = new ProjectDto();
                        projectDto.setId(project.getId());
                        projectDto.setProjectKey(project.getProjectKey());
                        projectDto.setName(project.getName());
                        projectDto.setImage(getProjectIcon(project.getId()));
                        return Observable.just(projectDto);
                    }
                })
                .toList()
                .toBlocking()
                .first();
    }

    private BacklogImage getProjectIcon(final long projectId) {
        return mBacklogApiClient.getProjectOperations().getProjectIcon(projectId)
                .flatMap(new Func1<ResponseBody, Observable<BacklogImage>>() {
                    @Override
                    public Observable<BacklogImage> call(ResponseBody response) {
                        final String subtype = response.contentType().subtype();
                        byte[] bytes = null;
                        try {
                            bytes = response.bytes();
                        } catch (IOException ex) {
                            Log.e(LOG_TAG, "Error on reading image", ex);
                        }
                        return Observable.just(new BacklogImage(projectId + "." + subtype, bytes));
                    }
                })
                .onErrorReturn(new Func1<Throwable, BacklogImage>() {
                    @Override
                    public BacklogImage call(Throwable throwable) {
                        Log.e(LOG_TAG, "Error on get Project Icon", throwable);
                        return null;
                    }
                })
                .toBlocking()
                .first();
    }
}
