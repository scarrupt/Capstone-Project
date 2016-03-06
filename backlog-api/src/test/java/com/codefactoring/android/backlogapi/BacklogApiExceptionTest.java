package com.codefactoring.android.backlogapi;

import com.codefactoring.android.backlogapi.models.BacklogError;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.beans.SamePropertyValuesAs.samePropertyValuesAs;

public class BacklogApiExceptionTest {

    private static final String EMPTY_ERRORS = "{\"errors\":[]}";

    private static final String NO_JSON = "No JSON";

    private static final String BACKLOG_ERROR_RESPONSE_BODY = "{\n" +
            "    \"errors\":[\n" +
            "        {\n" +
            "            \"message\": \"No project.\",\n" +
            "            \"code\": 6,\n" +
            "            \"moreInfo\": \"\"\n" +
            "        }\n" +
            "    ] \n" +
            "}";


    @Test
    public void returnsBacklogErrorWhenHttpExceptionIs404() {
        final BacklogError backlogError = new BacklogApiException(BACKLOG_ERROR_RESPONSE_BODY)
                .getBacklogError();

        final BacklogError expectedBacklogError = new BacklogError("No project.", 6, "");

        assertThat(expectedBacklogError, samePropertyValuesAs(backlogError));
    }

    @Test
    public void returnsNullWhenBacklogErrorHasNoContent() {
        final BacklogError backlogError = new BacklogApiException("").getBacklogError();

        assertThat(backlogError, nullValue());
    }

    @Test
    public void returnsNullWhenBacklogErrorContentIsNotJSON() {
        final BacklogError backlogError = new BacklogApiException(NO_JSON).getBacklogError();

        assertThat(backlogError, nullValue());
    }

    @Test
    public void returnsNullWhenBacklogErrorContentErrorsIsEmpty() {
        final BacklogError backlogError = new BacklogApiException(EMPTY_ERRORS).getBacklogError();

        assertThat(backlogError, nullValue());
    }

    @Test
    public void returnsBacklogErrorCodeFromBacklogError() {
        final BacklogApiException backlogApiException = new BacklogApiException(BACKLOG_ERROR_RESPONSE_BODY);

        assertThat(backlogApiException.getErrorCode(), equalTo(6));
    }
}