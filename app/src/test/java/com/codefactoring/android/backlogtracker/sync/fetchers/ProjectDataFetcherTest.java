package com.codefactoring.android.backlogtracker.sync.fetchers;

import com.codefactoring.android.backlogapi.BacklogApiClient;
import com.codefactoring.android.backlogtracker.sync.models.ProjectDto;

import org.hamcrest.Matchers;
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
import static org.hamcrest.Matchers.nullValue;

public class ProjectDataFetcherTest {

    private static final String PARAM_API_KEY = "apiKey";

    private static final String PARAM_ERROR_GET_PROJECT_IMAGE_API_KEY = "errorGetProjectImageApiKey";

    private static final String PARAM_ERROR_GET_PROJECT_LIST_API_KEY = "errorGetProjectListApiKey";

    private final MockWebServer server = new MockWebServer();

    private BacklogApiClient mBacklogApiClient = new BacklogApiClient(new BacklogTestConfig());

    private ProjectDataFetcher mProjectDataFetcher;

    @Before
    public void setUp() throws IOException {
        mProjectDataFetcher = new ProjectDataFetcher(mBacklogApiClient);
        server.setDispatcher(new ProjectRequestDispatcher());
        server.start();
    }

    @Test
    public void returnsProjectListWithIconFromGetProjectListEndpoint() {
        final HttpUrl baseUrl = server.url("/api/v2/projects/");

        mBacklogApiClient.connectWith(baseUrl.toString(), PARAM_API_KEY);

        final List<ProjectDto> projectList = mProjectDataFetcher.getProjectList();

        assertThat(projectList, Matchers.hasSize(1));
    }

    @Test
    public void returnsEmptyProjectListOnGetProjectListError() {
        final HttpUrl baseUrl = server.url("/api/v2/projects/");

        mBacklogApiClient.connectWith(baseUrl.toString(), PARAM_ERROR_GET_PROJECT_LIST_API_KEY);

        final List<ProjectDto> projectList = mProjectDataFetcher.getProjectList();

        assertThat(projectList, empty());
    }

    @Test
    public void returnsNullProjectThumbnailOnGetProjectImageError() {
        final HttpUrl baseUrl = server.url("/api/v2/projects/");

        mBacklogApiClient.connectWith(baseUrl.toString(), PARAM_ERROR_GET_PROJECT_IMAGE_API_KEY);

        final List<ProjectDto> projectList = mProjectDataFetcher.getProjectList();

        assertThat(projectList, contains(hasProperty("image", nullValue())));
    }


    private static class ProjectRequestDispatcher extends Dispatcher {

        @Override
        public MockResponse dispatch(RecordedRequest request) throws InterruptedException {
            switch (request.getPath()) {
                case "/api/v2/projects?apiKey=" + PARAM_API_KEY:
                case "/api/v2/projects?apiKey=" + PARAM_ERROR_GET_PROJECT_IMAGE_API_KEY:
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

                case "/api/v2/projects/1/image?apiKey=" + PARAM_API_KEY:
                    return new MockResponse()
                            .setResponseCode(200)
                            .addHeader("Content-Type", "application/octet-stream")
                            .addHeader("Content-Disposition", "attachment;filename=\"logo_mark.png\"")
                            .setBody(new Buffer().write(new byte[]{1}));
                case "/api/v2/projects?apiKey=" + PARAM_ERROR_GET_PROJECT_LIST_API_KEY:
                    return new MockResponse()
                            .setResponseCode(404);
                case "/api/v2/projects/1/image?apiKey=" + PARAM_ERROR_GET_PROJECT_IMAGE_API_KEY:
                    return new MockResponse()
                            .setResponseCode(404);
                default:
                    throw new IllegalArgumentException();
            }
        }
    }
}