package com.codefactoring.android.backlogtracker.sync.handlers;

import android.content.ContentProviderOperation;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.codefactoring.android.backlogtracker.BuildConfig;
import com.codefactoring.android.backlogtracker.sync.models.ProjectDto;
import com.google.common.collect.Lists;

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
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.robolectric.shadows.ShadowContentProviderOperation.TYPE_DELETE;
import static org.robolectric.shadows.ShadowContentProviderOperation.TYPE_INSERT;
import static org.robolectric.shadows.ShadowContentProviderOperation.TYPE_UPDATE;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class ProjectDataHandlerTest {

    private Context mContext;

    @Before
    public void setUp() {
        mContext = RuntimeEnvironment.application;
    }

    @Test
    public void createsOperationDeleteRemovedProject() {
        final ProjectDataHandler projectDataHandler = new ProjectDataHandler(mContext) {
            @Override
            protected Cursor query() {
                final Cursor cursor = mock(Cursor.class);
                when(cursor.getCount()).thenReturn(1);
                when(cursor.moveToFirst()).thenReturn(true);
                when(cursor.getString(eq(COL_PROJECT_KEY))).thenReturn("removedId");
                return cursor;
            }
        };

        final ArrayList<ContentProviderOperation> operations = projectDataHandler.makeContentProviderOperations(new ArrayList<ProjectDto>());

        final ContentProviderOperation operation = operations.get(0);

        assertThat(operation.getUri(), equalTo(CONTENT_URI));
        final ShadowContentProviderOperation shadowOperation = Shadows.shadowOf(operation);
        assertThat(shadowOperation.getType(), equalTo(TYPE_DELETE));
    }

    @Test
    public void createsOperationUpdateProject(){

        final String existingKey = "existingKey";

        final ProjectDataHandler projectDataHandler = new ProjectDataHandler(mContext) {
            @Override
            protected Cursor query() {
                final Cursor cursor = mock(Cursor.class);
                when(cursor.getCount()).thenReturn(1);
                when(cursor.moveToFirst()).thenReturn(true);
                when(cursor.getString(eq(COL_PROJECT_KEY))).thenReturn(existingKey);
                return cursor;
            }
        };

        final ProjectDto projectDto = new ProjectDto();
        projectDto.setProjectKey(existingKey);
        projectDto.setName("updatedName");

        final ArrayList<ContentProviderOperation> operations = projectDataHandler
                .makeContentProviderOperations(Lists.newArrayList(projectDto));

        final ContentProviderOperation operation = operations.get(0);

        assertThat(operation.getUri(), equalTo(ProjectEntry.CONTENT_URI));
        final ShadowContentProviderOperation shadowOperation = Shadows.shadowOf(operation);
        assertThat(shadowOperation.getType(), equalTo(TYPE_UPDATE));
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

        final ContentProviderOperation operation = operations.get(0);
        assertThat(operation.getUri(), equalTo(CONTENT_URI));

        final ShadowContentProviderOperation shadowOperation = Shadows.shadowOf(operation);
        assertThat(shadowOperation.getType(), equalTo(TYPE_INSERT));

        final ContentValues contentValues = shadowOperation.getContentValues();
        assertThat(contentValues.getAsLong(ProjectEntry._ID), equalTo(projectDto.getId()));
        assertThat(contentValues.getAsString(ProjectEntry.PROJECT_KEY), equalTo(projectDto.getProjectKey()));
        assertThat(contentValues.getAsString(ProjectEntry.NAME), equalTo(projectDto.getName()));
    }
}