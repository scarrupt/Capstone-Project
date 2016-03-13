package com.codefactoring.android.backlogtracker.sync.fetchers;

import com.codefactoring.android.backlogapi.BacklogApiClient;
import com.codefactoring.android.backlogtracker.sync.models.UserDto;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

import okhttp3.HttpUrl;
import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import okio.Buffer;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.nullValue;

public class UserDataFetcherTest {

    private static final String PARAM_API_KEY = "apiKey";

    private static final String PARAM_ERROR_GET_USER_ICON_API_KEY = "errorGetUserIconApiKey";

    private static final String PARAM_ERROR_GET_USER_LIST_API_KEY = "errorGetUserListApiKey";

    private static final String GET_USER_LIST_END_POINT = "/api/v2/users";

    private final MockWebServer server = new MockWebServer();

    private BacklogApiClient mBacklogApiClient = new BacklogApiClient(new BacklogTestConfig());

    private UserDataFetcher mUserDataFetcher;

    @Before
    public void setUp() throws IOException {
        mUserDataFetcher = new UserDataFetcher(mBacklogApiClient);
        server.setDispatcher(new UserRequestDispatcher());
        server.start();
    }

    @Test
    public void returnsUserListWithIconFromGetUserListEndpoint() {
        final HttpUrl baseUrl = server.url(GET_USER_LIST_END_POINT);

        mBacklogApiClient.connectWith(baseUrl.toString(), PARAM_API_KEY);

        final List<UserDto> userList = mUserDataFetcher.getUserList();

        assertThat(userList, hasSize(1));
    }

    @Test
    public void returnsEmptyUserListOnGetUserListError() {
        final HttpUrl baseUrl = server.url(GET_USER_LIST_END_POINT);

        mBacklogApiClient.connectWith(baseUrl.toString(), PARAM_ERROR_GET_USER_LIST_API_KEY);

        final List<UserDto> userList = mUserDataFetcher.getUserList();

        assertThat(userList, empty());
    }

    @Test
    public void returnsNullUserThumbnailOnGetUserIconError() {
        final HttpUrl baseUrl = server.url(GET_USER_LIST_END_POINT);

        mBacklogApiClient.connectWith(baseUrl.toString(), PARAM_ERROR_GET_USER_ICON_API_KEY);

        final List<UserDto> userList = mUserDataFetcher.getUserList();

        assertThat(userList, contains(hasProperty("image", nullValue())));
    }


    private static class UserRequestDispatcher extends Dispatcher {

        @Override
        public MockResponse dispatch(RecordedRequest request) throws InterruptedException {
            switch (request.getPath()) {
                case "/api/v2/users?apiKey=" + PARAM_API_KEY:
                case "/api/v2/users?apiKey=" + PARAM_ERROR_GET_USER_ICON_API_KEY:
                    return new MockResponse()
                            .setResponseCode(200)
                            .setBody("[\n" +
                                    "    {\n" +
                                    "        \"id\": 1,\n" +
                                    "        \"projectKey\": \"TEST\",\n" +
                                    "        \"name\": \"test\",\n" +
                                    "        \"chartEnabled\": false,\n" +
                                    "        \"subtaskingEnabled\": false,\n" +
                                    "        \"projectLeaderCanEditProjectLeader\": false,\n" +
                                    "        \"textFormattingRule\": \"markdown\",\n" +
                                    "        \"archived\":false\n" +
                                    "    }\n" +
                                    "]");
                case "/api/v2/users?apiKey=" + PARAM_ERROR_GET_USER_LIST_API_KEY:
                    return new MockResponse()
                            .setResponseCode(404);
                case "/api/v2/users/1/icon?apiKey=" + PARAM_API_KEY:
                    return new MockResponse()
                            .setResponseCode(200)
                            .addHeader("Content-Type", "application/octet-stream")
                            .addHeader("Content-Disposition", "attachment;filename=\"person_168.gif\"")
                            .setBody(new Buffer().write(new byte[]{1}));
                case "/api/v2/users/1/icon?apiKey=" + PARAM_ERROR_GET_USER_ICON_API_KEY:
                    return new MockResponse()
                            .setResponseCode(404);
                default:
                    throw new IllegalArgumentException();
            }
        }
    }
}