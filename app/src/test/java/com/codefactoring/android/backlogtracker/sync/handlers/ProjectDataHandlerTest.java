package com.codefactoring.android.backlogtracker.sync.handlers;

import android.content.ContentProviderOperation;
import android.content.ContentValues;
import android.content.Context;

import com.codefactoring.android.backlogtracker.BuildConfig;
import com.codefactoring.android.backlogtracker.sync.models.ProjectDto;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.Shadows;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowContentProviderOperation;

import java.util.ArrayList;
import java.util.List;

import static com.codefactoring.android.backlogtracker.provider.BacklogContract.ProjectEntry;
import static com.codefactoring.android.backlogtracker.provider.BacklogContract.ProjectEntry.CONTENT_URI;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.robolectric.shadows.ShadowContentProviderOperation.TYPE_DELETE;
import static org.robolectric.shadows.ShadowContentProviderOperation.TYPE_INSERT;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class ProjectDataHandlerTest {

    private static final int INDEX_TYPE_DELETE = 0;
    private static final int INDEX_TYPE_INSERT = 1;

    private Context mContext;

    @Before
    public void setUp() {
        mContext = RuntimeEnvironment.application;
    }

    @Test
    public void createsOperationDeleteAllProjectsAtFirst() {
        final ArrayList<ContentProviderOperation> operations = new ProjectDataHandler(mContext)
                .makeContentProviderOperations(new ArrayList<ProjectDto>());

        final ContentProviderOperation operation = operations.get(INDEX_TYPE_DELETE);

        assertThat(operation.getUri(), equalTo(CONTENT_URI));
        final ShadowContentProviderOperation shadowOperation = Shadows.shadowOf(operation);
        assertThat(shadowOperation.getType(), equalTo(TYPE_DELETE));
    }

    @Test
    public void createsOperationInsertProject() {
        final List<ProjectDto> projects = new ArrayList<>();
        final ProjectDto projectDto = new ProjectDto();
        projectDto.setId(1L);
        projectDto.setProjectKey("TEST");
        projectDto.setName("Test project");
        projects.add(projectDto);

        final ArrayList<ContentProviderOperation> operations = new ProjectDataHandler(mContext)
                .makeContentProviderOperations(projects);

        final ContentProviderOperation operation = operations.get(INDEX_TYPE_INSERT);
        assertThat(operation.getUri(), equalTo(CONTENT_URI));

        final ShadowContentProviderOperation shadowOperation = Shadows.shadowOf(operation);
        assertThat(shadowOperation.getType(), equalTo(TYPE_INSERT));

        final ContentValues contentValues = shadowOperation.getContentValues();
        assertThat(contentValues.getAsLong(ProjectEntry._ID), equalTo(projectDto.getId()));
        assertThat(contentValues.getAsString(ProjectEntry.PROJECT_KEY), equalTo(projectDto.getProjectKey()));
        assertThat(contentValues.getAsString(ProjectEntry.NAME), equalTo(projectDto.getName()));
    }
}