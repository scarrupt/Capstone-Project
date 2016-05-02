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

import static com.codefactoring.android.backlogapi.BacklogApiConstants.STATUS_ISSUE_OPEN;
import static com.codefactoring.android.backlogtracker.provider.BacklogContract.CONTENT_AUTHORITY;
import static com.codefactoring.android.backlogtracker.provider.BacklogContract.CommentEntry;
import static com.codefactoring.android.backlogtracker.provider.BacklogContract.IssueEntry;
import static com.codefactoring.android.backlogtracker.provider.BacklogContract.IssuePreviewEntry;
import static com.codefactoring.android.backlogtracker.provider.BacklogContract.IssueStatsEntry;
import static com.codefactoring.android.backlogtracker.provider.BacklogContract.IssueTypeEntry;
import static com.codefactoring.android.backlogtracker.provider.BacklogContract.ProjectEntry;
import static com.codefactoring.android.backlogtracker.provider.BacklogContract.UserEntry;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class BacklogProviderTest {

    private static final long PROJECT_ID = 1L;
    private static final long ISSUE_ID = 1L;

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

        final int count = shadowContentResolver.delete(IssueTypeEntry.CONTENT_URI, null, null);
        assertThat(count, equalTo(1));
    }

    private Uri insertSampleIssueType() {
        final ContentValues contentValues = new ContentValues();
        contentValues.put(IssueTypeEntry._ID, 1);
        contentValues.put(IssueTypeEntry.PROJECT_ID, 1);
        contentValues.put(IssueTypeEntry.NAME, "bug");
        contentValues.put(IssueTypeEntry.COLOR, "#7ea800");

        final Uri uri = IssueTypeEntry.buildIssueTypeFromProjectIdUri(PROJECT_ID);

        return shadowContentResolver.insert(uri, contentValues);
    }

    /*
     * Issue Tests
     */


    @Test
    public void returnsIssueFromIssueKey() {
        insertSampleIssueType();
        insertSampleUser();
        insertSampleIssue();

        Uri issueIdUri = IssueEntry.buildIssueUriFromIssueId("1");

        Cursor cursor = shadowContentResolver.query(issueIdUri, null, null, null, null);

        assertThat(cursor.getCount(), equalTo(1));
    }

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

    /*
     * Issues Summaries Test
     */
    @Test
    public void returnsIssuePreviewsMatchingProjectIdParameter() {
        insertSampleIssue();
        final Uri uri = IssuePreviewEntry.buildIssuePreviewsWithProjectId("1");
        Uri filteredUri = IssuePreviewEntry.addStatusQueryParameterToUri(uri, STATUS_ISSUE_OPEN);
        Cursor cursor = shadowContentResolver.query(filteredUri, null, null, null, null);
        assertThat(cursor.getCount(), equalTo(1));
    }

    @Test
    public void returnsIssueStatsMatchingProjectIdParameter() {
        insertSampleIssue();
        final Uri uri = IssueStatsEntry.buildIssueStatsUriWithProjectId("1");
        Cursor cursor = shadowContentResolver.query(uri, null, null, null, null);
        assertThat(cursor.getCount(), equalTo(3));
    }

    @Test
    public void returnsLast10OpenedIssues() {
        insertSampleIssue();
        final Uri uri = IssueEntry.buildLast10OpenedIssueUri();
        final Cursor cursor = shadowContentResolver.query(uri, null, null, null, null);
        assertThat(cursor.getCount(), equalTo(1));
    }

    @Test
    public void returnsCommentsMatchingIssueId() {
        insertSampleUser();
        insertSampleComment();
        final Uri uri = CommentEntry.buildCommentUriFromIssueIdUri(1L);
        final Cursor cursor = shadowContentResolver.query(uri, null, null, null, null);
        assertThat(cursor.getCount(), equalTo(1));
    }

    @Test
    public void returnsIssuesOpenedAfterPreviousSync() {
        insertSampleProject();
        insertSampleIssue();
        final Uri uri = IssueEntry.buildIssueUriWithStatusAndCreatedDate(STATUS_ISSUE_OPEN, "2000-01-01T00:00:00Z");
        final Cursor cursor = shadowContentResolver.query(uri, null, null, null, null);
        assertThat(cursor.getCount(), equalTo(1));
    }

    /*
     * Comments
     */

    @Test
    public void insertsNewComment() {
        final Uri uri = insertSampleComment();

        assertThat(ContentUris.parseId(uri), equalTo(1L));
    }

    @Test
    public void deletesExistingComment() {
        insertSampleComment();

        final int count = shadowContentResolver.delete(CommentEntry.CONTENT_URI, null, null);
        assertThat(count, equalTo(1));
    }

    private Uri insertSampleComment() {
        final ContentValues contentValues = new ContentValues();
        contentValues.put(CommentEntry._ID, 1);
        contentValues.put(CommentEntry.ISSUE_ID, 1);
        contentValues.put(CommentEntry.CONTENT, "test");
        contentValues.put(CommentEntry.CREATED_USER_ID, 1);
        contentValues.put(CommentEntry.CREATED, "2013-08-05T06:15:06Z");
        contentValues.put(CommentEntry.UPDATED, "2013-08-05T06:15:06Z");

        final Uri uri = CommentEntry.buildCommentUriFromIssueIdUri(ISSUE_ID);

        return shadowContentResolver.insert(uri, contentValues);
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
        contentValues.put(IssueEntry.STATUS, "Open");
        contentValues.put(IssueEntry.MILESTONES, "milestones");
        contentValues.put(IssueEntry.ASSIGNEE_ID, 1);
        contentValues.put(IssueEntry.CREATED_USER_ID, 1);
        contentValues.put(IssueEntry.CREATED_DATE, "2013-02-07T08:09:49Z");
        contentValues.put(IssueEntry.UPDATED_USER_ID, 1);
        contentValues.put(IssueEntry.UPDATED_DATE, "2013-02-07T08:09:49Z");

        return shadowContentResolver.insert(IssueEntry.CONTENT_URI, contentValues);
    }
}