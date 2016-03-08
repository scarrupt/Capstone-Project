package com.codefactoring.android.backlogapi;

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

    private class BacklogTestConfig extends BacklogToolConfig {
        @Override
        public String getBaseURL(String spaceUrl) {
            return spaceUrl;
        }
    }
}