package com.codefactoring.android.backlogapi;

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

    private final MockWebServer server = new MockWebServer();

    private TestSubscriber<User> subscriber = new TestSubscriber<>();

    @Test
    public void addsApiKeyAsRequestParameter() throws IOException, InterruptedException {

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
                .connectWith(baseUrl.toString(), "apiKey");
        backlogApi.getUserOperations().getOwnUser().subscribe(subscriber);

        List<User> users = new ArrayList<>();
        final User expectedUser = new User();
        expectedUser.setUserId("admin");
        users.add(expectedUser);

        subscriber.assertReceivedOnNext(users);
    }

    @Test
    public void throwsBacklogApiExceptionOn404Error() throws IOException, InterruptedException {
        final TestSubscriber<User> subscriber = new TestSubscriber<>();

        server.enqueue(new MockResponse().setResponseCode(404));
        server.start();
        final HttpUrl baseUrl = server.url("/api/v2/test");

        final BacklogApiClient backlogApi = new BacklogApiClient(new BacklogTestConfig())
                .connectWith(baseUrl.toString(), "apiKey");

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