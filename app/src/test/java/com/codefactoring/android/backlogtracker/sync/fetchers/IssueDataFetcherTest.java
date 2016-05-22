package com.codefactoring.android.backlogtracker.sync.fetchers;

import com.codefactoring.android.backlogapi.BacklogApiClient;
import com.codefactoring.android.backlogtracker.sync.models.IssueDto;

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
import static org.hamcrest.Matchers.hasSize;

public class IssueDataFetcherTest {

    private static final String PARAM_API_KEY = "apiKey";

    private static final String PARAM_ERROR_GET_ISSUE_LIST_API_KEY = "errorGetIssueListApiKey";

    private static final String GET_ISSUE_LIST_END_POINT = "/api/v2/issues?projectId[]=1";

    private static final long PROJECT_ID = 1L;

    private final MockWebServer server = new MockWebServer();

    private final BacklogApiClient mBacklogApiClient = new BacklogApiClient(new BacklogTestConfig());

    private IssueDataFetcher mIssueDataFetcher;

    @Before
    public void setUp() throws IOException {
        mIssueDataFetcher = new IssueDataFetcher(mBacklogApiClient);
        server.setDispatcher(new IssueRequestDispatcher());
        server.start();
    }

    @Test
    public void returnsIssueListFromGetIssueListEndpoint() {
        final HttpUrl baseUrl = server.url(GET_ISSUE_LIST_END_POINT);

        mBacklogApiClient.connectWith(baseUrl.toString(), PARAM_API_KEY);

        final List<IssueDto> issueList = mIssueDataFetcher.getIssueList(PROJECT_ID);

        assertThat(issueList, hasSize(1));
    }

    @Test
    public void returnsEmptyIssueListOnGetIssueListError() {
        final HttpUrl baseUrl = server.url(GET_ISSUE_LIST_END_POINT);

        mBacklogApiClient.connectWith(baseUrl.toString(), PARAM_ERROR_GET_ISSUE_LIST_API_KEY);

        final List<IssueDto> issueList = mIssueDataFetcher.getIssueList(PROJECT_ID);

        assertThat(issueList, empty());
    }

    private static class IssueRequestDispatcher extends Dispatcher {

        @Override
        public MockResponse dispatch(RecordedRequest request) throws InterruptedException {
            switch (request.getPath()) {
                case "/api/v2/issues?projectId[]=1&statusId[]=1&statusId[]=2&statusId[]=3&apiKey=" + PARAM_API_KEY:
                    return new MockResponse()
                            .setResponseCode(200)
                            .setBody("[\n" +
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
                                    "]");
                case "/api/v2/issues?projectId[]=1&statusId[]=1&statusId[]=2&statusId[]=3&apiKey=" + PARAM_ERROR_GET_ISSUE_LIST_API_KEY:
                    return new MockResponse()
                            .setResponseCode(404);
                default:
                    throw new IllegalArgumentException();
            }
        }
    }
}