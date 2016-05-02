package com.codefactoring.android.backlogtracker.sync.fetchers;

import com.codefactoring.android.backlogapi.BacklogApiClient;
import com.codefactoring.android.backlogtracker.sync.models.CommentDto;

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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;

public class CommentDataFetcherTest {

    private static final String PARAM_API_KEY = "apiKey";

    private static final String PARAM_ERROR_GET_COMMENT_LIST_API_KEY = "errorGetCommentListApiKey";

    private final MockWebServer server = new MockWebServer();

    private final BacklogApiClient mBacklogApiClient = new BacklogApiClient(new BacklogTestConfig());

    private CommentDataFetcher mCommentDataFetcher;

    @Before
    public void setUp() throws IOException {
        mCommentDataFetcher = new CommentDataFetcher(mBacklogApiClient);
        server.setDispatcher(new CommentRequestDispatcher());
        server.start();
    }

    @Test
    public void returnsCommentListFromGetCommentListEndpoint() {
        final HttpUrl baseUrl = server.url("/api/v2/issues/1/comments");

        mBacklogApiClient.connectWith(baseUrl.toString(), PARAM_API_KEY);

        final List<CommentDto> commentList = mCommentDataFetcher.getCommentList(1L);

        assertThat(commentList, Matchers.hasSize(1));
    }

    @Test
    public void returnsEmptyCommentListOnGetCommentListError() {
        final HttpUrl baseUrl = server.url("/api/v2/issues/1/comments");

        mBacklogApiClient.connectWith(baseUrl.toString(), PARAM_ERROR_GET_COMMENT_LIST_API_KEY);

        final List<CommentDto> commentList = mCommentDataFetcher.getCommentList(1L);

        assertThat(commentList, empty());
    }

    private static class CommentRequestDispatcher extends Dispatcher {

        private static final String PARAM_API_KEY = "apiKey";

        @Override
        public MockResponse dispatch(RecordedRequest request) throws InterruptedException {
            switch (request.getPath()) {
                case "/api/v2/issues/1/comments?apiKey=" + PARAM_API_KEY:
                    return new MockResponse()
                            .setResponseCode(200)
                            .setBody("[\n" +
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
                                    "]");
                case "/api/v2/issues/1/comments?apiKey=" + PARAM_ERROR_GET_COMMENT_LIST_API_KEY:
                    return new MockResponse()
                            .setResponseCode(404);
                default:
                    throw new IllegalArgumentException();
            }
        }
    }
}