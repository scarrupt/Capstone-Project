package com.codefactoring.android.backlogtracker.view.account;

import android.content.Context;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;
import android.view.View;
import android.widget.EditText;

import com.codefactoring.android.backlogtracker.R;
import com.codefactoring.android.backlogtracker.TestApplication;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import javax.inject.Inject;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class AccountActivityTest {

    private static final String VALID_SPACE_KEY = "spaceKey";

    private static final String TOO_SHORT_SPACE_KEY = "ts";

    private static final String TOO_LONG_SPACE_KEY = "toooooooLong";

    private static final String VALID_API_KEY = "apiKey";

    private static final String ERROR_FIELD_IS_REQUIRED = "This field is required";

    private static final String ERROR_SPACE_KEY_LENGTH = "Space key must between 3 and 10 characters long";

    private final MockWebServer server = new MockWebServer();

    private TestApplication mTestApplication;

    @Inject
    Context mContext;

    @Rule
    public final ActivityTestRule<AccountActivity> activityTestRule =
            new ActivityTestRule<>(AccountActivity.class);

    @Before
    public void setup() throws IOException {
        mTestApplication =  ((TestApplication) activityTestRule
                .getActivity()
                .getApplication());
        mTestApplication.getApplicationComponent().inject(this);
        server.start();
    }

    @After
    public void tearDown() throws IOException {
        server.shutdown();
    }

    @Test
    public void displaysFieldRequiredErrorWhenSpaceKeyIsEmpty() {
        onView(withId(R.id.text_api_key)).perform(typeText(VALID_API_KEY));

        onView(withId(R.id.button_next)).perform(click());

        onView(withId(R.id.text_space_key)).check(matches(withError(ERROR_FIELD_IS_REQUIRED)));
    }

    @Test
    public void displaysFieldLengthErrorWhenSpaceKeyLengthIsLessThan3() {
        onView(withId(R.id.text_space_key)).perform(typeText(TOO_SHORT_SPACE_KEY));
        onView(withId(R.id.text_api_key)).perform(typeText(VALID_API_KEY));


        onView(withId(R.id.button_next)).perform(click());

        onView(withId(R.id.text_space_key)).check(matches(withError(ERROR_SPACE_KEY_LENGTH)));
    }

    @Test
    public void displaysLengthErrorWhenSpaceKeyLengthIsGreaterThan10() {
        onView(withId(R.id.text_space_key)).perform(typeText(TOO_LONG_SPACE_KEY));
        onView(withId(R.id.text_api_key)).perform(typeText(VALID_API_KEY));

        onView(withId(R.id.button_next)).perform(click());

        onView(withId(R.id.text_space_key)).check(matches(withError(ERROR_SPACE_KEY_LENGTH)));
    }

    @Test
    public void displaysFieldRequiredErrorWhenApiKeyIsEmpty() {
        onView(withId(R.id.text_space_key)).perform(typeText(VALID_SPACE_KEY));

        onView(withId(R.id.button_next)).perform(click());

        onView(withId(R.id.text_api_key)).check(matches(withError(ERROR_FIELD_IS_REQUIRED)));
    }

    @Test
    public void displaysFieldRequiredErrorForEachFieldWhenEmpty() {
        onView(withId(R.id.button_next)).perform(click());

        onView(withId(R.id.text_space_key)).check(matches(withError(ERROR_FIELD_IS_REQUIRED)));
        onView(withId(R.id.text_api_key)).check(matches(withError(ERROR_FIELD_IS_REQUIRED)));
    }

    @Test
    public void displaysAlertDialogWhenApiCallReturnsException() throws Exception {
        server.enqueue(new MockResponse().setStatus("404"));

        mTestApplication.getBacklogTestConfig().setUrl(server
                .url("/api/v2/users/myself")
                .toString());

        onView(withId(R.id.text_space_key)).perform(typeText(VALID_SPACE_KEY));
        onView(withId(R.id.text_api_key)).perform(typeText(VALID_API_KEY));
        onView(withId(R.id.button_next)).perform(click());

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new Exception();
        }

        onView(withText(mContext.getString(R.string.error_default))).check(matches(isDisplayed()));
    }

    private static Matcher<View> withError(final String expected) {
        return new TypeSafeMatcher<View>() {

            @Override
            public boolean matchesSafely(View view) {
                if (!(view instanceof EditText)) {
                    return false;
                }
                EditText editText = (EditText) view;
                return editText.getError().toString().equals(expected);
            }

            @Override
            public void describeTo(Description description) {

            }
        };
    }
}