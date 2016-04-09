package com.codefactoring.android.backlogapi;

import com.codefactoring.android.backlogapi.models.Comment;
import com.codefactoring.android.backlogapi.models.Issue;
import com.codefactoring.android.backlogapi.models.Project;
import com.codefactoring.android.backlogapi.models.User;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.HttpUrl;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import rx.observers.TestSubscriber;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class BacklogApiClientTest {

    private static final String PARAM_API_KEY = "apiKey";
    private static final long PROJECT_ID = 1L;
    private static final long KEY_ID = 1L;

    private final MockWebServer server = new MockWebServer();

    @Test
    public void addsApiKeyAsRequestParameter() throws IOException, InterruptedException {
        final TestSubscriber<User> subscriber = new TestSubscriber<>();

        server.enqueue(new MockResponse().setBody("{}"));
        server.start();
        final HttpUrl baseUrl = server.url("/api/v2/test");

        final BacklogApiClient backlogApi = new BacklogApiClient(new BacklogTestConfig())
                .connectWith(baseUrl.toString(), "apiKey");

        backlogApi.getUserOperations().getOwnUser().subscribe(subscriber);

        final RecordedRequest recordedRequest = server.takeRequest();

        assertThat(recordedRequest.getPath(), containsString("?apiKey=apiKey"));
    }

    @Test
    public void returnsCurrentUserWhenGetOwnUserEndpointIsCalled() throws IOException {
        final TestSubscriber<User> subscriber = new TestSubscriber<>();

        final String userJson = "{\n" +
                "    \"id\": 1,\n" +
                "    \"userId\": \"admin\",\n" +
                "    \"name\": \"admin\",\n" +
                "    \"roleType\": 1,\n" +
                "    \"lang\": \"ja\",\n" +
                "    \"mailAddress\": \"eguchi@nulab.example\"\n" +
                "}";

        server.enqueue(new MockResponse().setBody(userJson));

        server.start();

        final HttpUrl baseUrl = server.url("/api/v2/users/myself");

        final BacklogApiClient backlogApi = new BacklogApiClient(new BacklogTestConfig())
                .connectWith(baseUrl.toString(), PARAM_API_KEY);
        backlogApi.getUserOperations().getOwnUser().subscribe(subscriber);

        List<User> users = new ArrayList<>();
        final User expectedUser = new User();
        expectedUser.setUserId("admin");
        users.add(expectedUser);

        subscriber.assertReceivedOnNext(users);
    }

    @Test
    public void returnsProjectListWhenGetProjectListEndpointIsCalled() throws IOException {
        final TestSubscriber<List<Project>> subscriber = new TestSubscriber<>();

        final String projectListJson = "[\n" +
                "    {\n" +
                "        \"id\": 1,\n" +
                "        \"projectKey\": \"TEST\",\n" +
                "        \"name\": \"test\",\n" +
                "        \"chartEnabled\": false,\n" +
                "        \"subtaskingEnabled\": false,\n" +
                "        \"projectLeaderCanEditProjectLeader\": false,\n" +
                "        \"textFormattingRule\": \"markdown\",\n" +
                "        \"archived\":false\n" +
                "    }" +
                "]";

        server.enqueue(new MockResponse().setBody(projectListJson));

        server.start();

        final HttpUrl baseUrl = server.url("/api/v2/projects/");

        final BacklogApiClient backlogApi = new BacklogApiClient(new BacklogTestConfig())
                .connectWith(baseUrl.toString(), PARAM_API_KEY);
        backlogApi.getProjectOperations().getProjectList().subscribe(subscriber);

        final List<Project> projects = new ArrayList<>();
        final Project expectedProject = new Project();
        expectedProject.setProjectKey("TEST");
        projects.add(expectedProject);

        List<List<Project>> items = new ArrayList<>();
        items.add(projects);

        subscriber.assertReceivedOnNext(items);
    }

    @Test
    public void returnsUserListWhenGetUserListEndpointIsCalled() throws IOException {
        final TestSubscriber<List<User>> subscriber = new TestSubscriber<>();

        final String userListJson = "[\n" +
                "    {\n" +
                "        \"id\": 1,\n" +
                "        \"userId\": \"admin\",\n" +
                "        \"name\": \"admin\",\n" +
                "        \"roleType\": 1,\n" +
                "        \"lang\": \"ja\",\n" +
                "        \"mailAddress\": \"eguchi@nulab.example\"\n" +
                "    }\n" +
                "]";

        server.enqueue(new MockResponse().setBody(userListJson));

        server.start();

        final HttpUrl baseUrl = server.url("/api/v2/users/");

        final BacklogApiClient backlogApi = new BacklogApiClient(new BacklogTestConfig())
                .connectWith(baseUrl.toString(), PARAM_API_KEY);
        backlogApi.getUserOperations().getUserList().subscribe(subscriber);

        final List<User> users = new ArrayList<>();
        final User expectedUser = new User();
        expectedUser.setUserId("admin");
        users.add(expectedUser);

        List<List<User>> items = new ArrayList<>();
        items.add(users);

        subscriber.assertReceivedOnNext(items);
    }

    @Test
    public void returnsIssueListWhenGetIssueListEndpointIsCalled() throws IOException {
        final TestSubscriber<List<Issue>> subscriber = new TestSubscriber<>();

        final String issueListJson = "[\n" +
                "    {\n" +
                "        \"id\": 1,\n" +
                "        \"projectId\": 1,\n" +
                "        \"issueKey\": \"BLG-1\",\n" +
                "        \"keyId\": 1,\n" +
                "        \"issueType\": {\n" +
                "            \"id\": 2,\n" +
                "            \"projectId\" :1,\n" +
                "            \"name\": \"Task\",\n" +
                "            \"color\": \"#7ea800\",\n" +
                "            \"displayOrder\": 0\n" +
                "        },\n" +
                "        \"summary\": \"first issue\",\n" +
                "        \"description\": \"\",\n" +
                "        \"resolutions\": null,\n" +
                "        \"priority\": {\n" +
                "            \"id\": 3,\n" +
                "            \"name\": \"Normal\"\n" +
                "        },\n" +
                "        \"status\": {\n" +
                "            \"id\": 1,\n" +
                "            \"name\": \"Open\"\n" +
                "        },\n" +
                "        \"assignee\": {\n" +
                "            \"id\": 2,\n" +
                "            \"name\": \"eguchi\",\n" +
                "            \"roleType\" :2,\n" +
                "            \"lang\": null,\n" +
                "            \"mailAddress\": \"eguchi@nulab.example\"\n" +
                "        },\n" +
                "        \"category\": [],\n" +
                "        \"versions\": [],\n" +
                "        \"milestone\": [\n" +
                "            {\n" +
                "                \"id\": 30,\n" +
                "                \"projectId\": 1,\n" +
                "                \"name\": \"wait for release\",\n" +
                "                \"description\": \"\",\n" +
                "                \"startDate\": null,\n" +
                "                \"releaseDueDate\": null,\n" +
                "                \"archived\": false,\n" +
                "                \"displayOrder\": 0\n" +
                "            }\n" +
                "        ],\n" +
                "        \"startDate\": null,\n" +
                "        \"dueDate\": null,\n" +
                "        \"estimatedHours\": null,\n" +
                "        \"actualHours\": null,\n" +
                "        \"parentIssueId\": null,\n" +
                "        \"createdUser\": {\n" +
                "            \"id\": 1,\n" +
                "            \"userId\": \"admin\",\n" +
                "            \"name\": \"admin\",\n" +
                "            \"roleType\": 1,\n" +
                "            \"lang\": \"ja\",\n" +
                "            \"mailAddress\": \"eguchi@nulab.example\"\n" +
                "        },\n" +
                "        \"created\": \"2012-07-23T06:10:15Z\",\n" +
                "        \"updatedUser\": {\n" +
                "            \"id\": 1,\n" +
                "            \"userId\": \"admin\",\n" +
                "            \"name\": \"admin\",\n" +
                "            \"roleType\": 1,\n" +
                "            \"lang\": \"ja\",\n" +
                "            \"mailAddress\": \"eguchi@nulab.example\"\n" +
                "        },\n" +
                "        \"updated\": \"2013-02-07T08:09:49Z\",\n" +
                "        \"customFields\": [],\n" +
                "        \"attachments\": [\n" +
                "            {\n" +
                "                \"id\": 1,\n" +
                "                \"name\": \"IMGP0088.JPG\",\n" +
                "                \"size\": 85079\n" +
                "            }\n" +
                "        ],\n" +
                "        \"sharedFiles\": [\n" +
                "            {\n" +
                "                \"id\": 454403,\n" +
                "                \"type\": \"file\",\n" +
                "                \"dir\": \"/userIcon/\",\n" +
                "                \"name\": \"01_male clerk.png\",\n" +
                "                \"size\": 2735,\n" +
                "                \"createdUser\": {\n" +
                "                    \"id\": 5686,\n" +
                "                    \"userId\": \"takada\",\n" +
                "                    \"name\": \"takada\",\n" +
                "                    \"roleType\":2,\n" +
                "                    \"lang\":\"ja\",\n" +
                "                    \"mailAddress\":\"takada@nulab.example\"\n" +
                "                },\n" +
                "                \"created\": \"2009-02-27T03:26:15Z\",\n" +
                "                \"updatedUser\": {\n" +
                "                    \"id\": 5686,\n" +
                "                    \"userId\": \"takada\",\n" +
                "                    \"name\": \"takada\",\n" +
                "                    \"roleType\": 2,\n" +
                "                    \"lang\": \"ja\",\n" +
                "                    \"mailAddress\": \"takada@nulab.example\"\n" +
                "                },\n" +
                "                \"updated\":\"2009-03-03T16:57:47Z\"\n" +
                "            }\n" +
                "        ],\n" +
                "        \"stars\": [\n" +
                "            {\n" +
                "                \"id\": 10,\n" +
                "                \"comment\": null,\n" +
                "                \"url\": \"https://xx.backlogtool.com/view/BLG-1\",\n" +
                "                \"title\": \"[BLG-1] first issue | Show issue - Backlog\",\n" +
                "                \"presenter\": {\n" +
                "                    \"id\": 2,\n" +
                "                    \"userId\": \"eguchi\",\n" +
                "                    \"name\": \"eguchi\",\n" +
                "                    \"roleType\": 2,\n" +
                "                    \"lang\": \"ja\",\n" +
                "                    \"mailAddress\": \"eguchi@nulab.example\"\n" +
                "                },\n" +
                "                \"created\":\"2013-07-08T10:24:28Z\"\n" +
                "            }\n" +
                "        ]\n" +
                "    }\n" +
                "]";

        server.enqueue(new MockResponse().setBody(issueListJson));

        server.start();

        final HttpUrl baseUrl = server.url("/api/v2/issues");

        final BacklogApiClient backlogApi = new BacklogApiClient(new BacklogTestConfig())
                .connectWith(baseUrl.toString(), PARAM_API_KEY);
        backlogApi.getIssueOperations().getIssueList(PROJECT_ID).subscribe(subscriber);

        final List<Issue> issues = new ArrayList<>();
        final Issue expectedIssue = new Issue();
        expectedIssue.setKeyId(KEY_ID);
        issues.add(expectedIssue);

        List<List<Issue>> items = new ArrayList<>();
        items.add(issues);

        subscriber.assertReceivedOnNext(items);
    }

    @Test
    public void returnsCommentListWhenGetCommentListEndpointIsCalled() throws IOException {
        final TestSubscriber<List<Comment>> subscriber = new TestSubscriber<>();

        final String commentListJson = "[\n" +
                    " {\n" +
                    "    \"id\": 6586,\n" +
                    "    \"content\": \"test\",\n" +
                    "    \"changeLog\": null,\n" +
                    "    \"createdUser\": {\n" +
                    "        \"id\": 1,\n" +
                    "        \"userId\": \"admin\",\n" +
                    "        \"name\": \"admin\",\n" +
                    "        \"roleType\": 1,\n" +
                    "        \"lang\": \"ja\",\n" +
                    "        \"mailAddress\": \"eguchi@nulab.example\"\n" +
                    "    },\n" +
                    "    \"created\": \"2013-08-05T06:15:06Z\",\n" +
                    "    \"updated\": \"2013-08-05T06:15:06Z\",\n" +
                    "    \"stars\": [],\n" +
                    "    \"notifications\": []\n" +
                    "}" +
                "]";

        server.enqueue(new MockResponse().setBody(commentListJson));

        server.start();

        final HttpUrl baseUrl = server.url("/api/v2/issues/");

        final BacklogApiClient backlogApi = new BacklogApiClient(new BacklogTestConfig())
                .connectWith(baseUrl.toString(), PARAM_API_KEY);
        backlogApi.getIssueOperations().getCommentList(1).subscribe(subscriber);

        final List<Comment> comments = new ArrayList<>();
        final Comment expectedComment = new Comment();
        expectedComment.setId(6586);
        comments.add(expectedComment);

        List<List<Comment>> items = new ArrayList<>();
        items.add(comments);

        subscriber.assertReceivedOnNext(items);
    }

    @Test
    public void throwsBacklogApiExceptionOn404Error() throws IOException, InterruptedException {
        final TestSubscriber<User> subscriber = new TestSubscriber<>();

        server.enqueue(new MockResponse().setResponseCode(404));
        server.start();
        final HttpUrl baseUrl = server.url("/api/v2/test");

        final BacklogApiClient backlogApi = new BacklogApiClient(new BacklogTestConfig())
                .connectWith(baseUrl.toString(), PARAM_API_KEY);

        backlogApi.getUserOperations().getOwnUser().subscribe(subscriber);

        server.takeRequest();

        subscriber.assertError(BacklogApiException.class);
    }
}