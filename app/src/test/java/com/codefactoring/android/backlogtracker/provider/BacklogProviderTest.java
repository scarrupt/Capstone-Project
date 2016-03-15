package com.codefactoring.android.backlogtracker.provider;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

import com.codefactoring.android.backlogtracker.BuildConfig;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.Shadows;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowContentResolver;

import static com.codefactoring.android.backlogtracker.provider.BacklogContract.*;
import static com.codefactoring.android.backlogtracker.provider.BacklogContract.CONTENT_AUTHORITY;
import static com.codefactoring.android.backlogtracker.provider.BacklogContract.ProjectEntry;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class BacklogProviderTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private ShadowContentResolver shadowContentResolver;

    @Before
    public void setUp() {
        final BacklogProvider backlogProvider = new BacklogProvider();
        final ContentResolver contentResolver = RuntimeEnvironment.application.getContentResolver();
        this.shadowContentResolver = Shadows.shadowOf(contentResolver);
        backlogProvider.onCreate();
        ShadowContentResolver.registerProvider(CONTENT_AUTHORITY, backlogProvider);
    }

    /*
     * Project Tests
     */

    @Test
    public void noResultShouldBeReturnedWhenNoProjectExist() {
        Cursor cursor = shadowContentResolver.query(ProjectEntry.CONTENT_URI, null, null, null, null);
        assertThat(cursor.getCount(), equalTo(0));
    }

    @Test
    public void insertsNewProject() {
        final Uri uri = insertSampleProject();

        assertThat(ContentUris.parseId(uri), equalTo(1L));
    }

    @Test
    public void updatesExistingProject() {
        final ContentValues contentValues = new ContentValues();
        contentValues.put(ProjectEntry._ID, 1);
        contentValues.put(ProjectEntry.PROJECT_KEY, "TEST");
        contentValues.put(ProjectEntry.NAME, "test");
        contentValues.put(ProjectEntry.THUMBNAIL_URL, "/thumbnails/projects/TEST.png");

        final Uri projectUri = shadowContentResolver.insert(ProjectEntry.CONTENT_URI, contentValues);
        final long projectRowId = ContentUris.parseId(projectUri);

        final int count = this.shadowContentResolver.update(ProjectEntry.CONTENT_URI, contentValues,
                ProjectEntry._ID + "= ?", new String[]{Long.toString(projectRowId)});
        assertThat(count, equalTo(1));
    }

    @Test
    public void deletesExistingProject() {
        insertSampleProject();

        final int count = shadowContentResolver.delete(ProjectEntry.CONTENT_URI, null, null);
        assertThat(count, equalTo(1));
    }

    @Test
    public void insertsMultipleProjects() {
        final ContentValues[] contentValuesArray = new ContentValues[5];

        for (int i = 0; i < contentValuesArray.length; i++) {
            final ContentValues contentValues = new ContentValues();
            contentValues.put(ProjectEntry._ID, i);
            contentValues.put(ProjectEntry.PROJECT_KEY, "TEST");
            contentValues.put(ProjectEntry.NAME, "test");
            contentValues.put(ProjectEntry.THUMBNAIL_URL, "/thumbnails/projects/TEST.png");
            contentValuesArray[i] = contentValues;
        }

        final int count = shadowContentResolver.bulkInsert(ProjectEntry.CONTENT_URI, contentValuesArray);

        assertThat(count, equalTo(contentValuesArray.length));
    }

    @Test
    public void returnsContentTypeForProjectsPath() {
        final String type = shadowContentResolver.getType(ProjectEntry.CONTENT_URI);
        assertThat(type, equalTo(ProjectEntry.CONTENT_TYPE));
    }

    private Uri insertSampleProject() {
        final ContentValues contentValues = new ContentValues();
        contentValues.put(ProjectEntry._ID, 1);
        contentValues.put(ProjectEntry.PROJECT_KEY, "TEST");
        contentValues.put(ProjectEntry.NAME, "test");
        contentValues.put(ProjectEntry.THUMBNAIL_URL, "/thumbnails/projects/TEST.png");

        return shadowContentResolver.insert(ProjectEntry.CONTENT_URI, contentValues);
    }

    /*
     * User Tests
     */
    @Test
    public void insertsNewUser() {
        final Uri uri = insertSampleUser();

        assertThat(ContentUris.parseId(uri), equalTo(1L));
    }

    @Test
    public void deletesExistingUser() {
        insertSampleUser();

        final int count = shadowContentResolver.delete(UserEntry.CONTENT_URI, null, null);
        assertThat(count, equalTo(1));
    }

    private Uri insertSampleUser() {
        final ContentValues contentValues = new ContentValues();
        contentValues.put(UserEntry._ID, 1);
        contentValues.put(UserEntry.NAME, "Hiroki Nakamura");
        contentValues.put(UserEntry.USER_ID, "hiroki_nakamura");
        contentValues.put(UserEntry.THUMBNAIL_URL, "/thumbnails/users/hiroki_nakamura.png");

        return shadowContentResolver.insert(UserEntry.CONTENT_URI, contentValues);
    }

    /*
     * Issue Type Tests
     */
    @Test
    public void insertsNewIssueType() {
        final Uri uri = insertSampleIssueType();

        assertThat(ContentUris.parseId(uri), equalTo(1L));
    }

    @Test
    public void deletesExistingIssueType() {
        insertSampleIssueType();

        final int count = shadowContentResolver.delete(IssueTypeEntry.buildIssueTypeUri("1"), null, null);
        assertThat(count, equalTo(1));
    }

    private Uri insertSampleIssueType() {
        final ContentValues contentValues = new ContentValues();
        contentValues.put(IssueTypeEntry._ID, 1);
        contentValues.put(IssueTypeEntry.PROJECT_ID, 1);
        contentValues.put(IssueTypeEntry.NAME, "bug");
        contentValues.put(IssueTypeEntry.COLOR, "#7ea800");

        final Uri uri = IssueTypeEntry.buildIssueTypeUri("1");

        return shadowContentResolver.insert(uri, contentValues);
    }

    /*
     * Issue Tests
     */

    @Test
    public void insertsNewIssue() {
        final Uri uri = insertSampleIssue();

        assertThat(ContentUris.parseId(uri), equalTo(1L));
    }

    @Test
    public void deletesExistingIssue() {
        insertSampleIssue();

        final int count = shadowContentResolver.delete(IssueEntry.CONTENT_URI, null, null);
        assertThat(count, equalTo(1));
    }

    private Uri insertSampleIssue() {
        final ContentValues contentValues = new ContentValues();
        contentValues.put(IssueEntry._ID, 1);
        contentValues.put(IssueEntry.PROJECT_ID, 1);
        contentValues.put(IssueEntry.TYPE_ID, 1);
        contentValues.put(IssueEntry.ISSUE_KEY, "ISSUE_KEY");
        contentValues.put(IssueEntry.SUMMARY, "summary");
        contentValues.put(IssueEntry.DESCRIPTION, "description");
        contentValues.put(IssueEntry.PRIORITY, "priority");
        contentValues.put(IssueEntry.STATUS, "status");
        contentValues.put(IssueEntry.MILESTONES, "milestones");
        contentValues.put(IssueEntry.ASSIGNEE_ID, 1);
        contentValues.put(IssueEntry.CREATED_USER_ID, 1);
        contentValues.put(IssueEntry.CREATED_DATE, "2013-02-07T08:09:49Z");
        contentValues.put(IssueEntry.UPDATED_USER_ID, 1);
        contentValues.put(IssueEntry.UPDATED_DATE, "2013-02-07T08:09:49Z");

        return shadowContentResolver.insert(IssueEntry.CONTENT_URI, contentValues);
    }
}