package com.codefactoring.android.backlogtracker.view.util;

import com.codefactoring.android.backlogapi.BacklogApiErrorConstants;
import com.codefactoring.android.backlogapi.BacklogApiException;
import com.codefactoring.android.backlogtracker.BuildConfig;
import com.codefactoring.android.backlogtracker.R;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.io.IOException;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class ErrorUtilsTest {

    @Test
    public void returnsDefaultErrorMessageWhenExceptionIsNotBacklogApiException() {
        final String errorMessage = ErrorUtils.getErrorMessage(RuntimeEnvironment.application, new IOException());
        assertThat(errorMessage, equalTo(getString(R.string.error_default)));
    }

    @Test
    public void returnsBacklogErrorMessageWhenExceptionIsBacklogApiException() {
        final BacklogApiException backlogApiException = mock(BacklogApiException.class);
        when(backlogApiException.getErrorCode()).thenReturn(BacklogApiErrorConstants.INTERNAL_ERROR);

        final String errorMessage = ErrorUtils.getErrorMessage(RuntimeEnvironment.application, backlogApiException);

        assertThat(errorMessage, equalTo(getString(R.string.error_server_internal)));
    }

    private String getString(int id) {
        return RuntimeEnvironment.application.getResources().getString(id);
    }
}