package com.codefactoring.android.backlogtracker.sync.handlers;

import android.content.ContentProviderOperation;
import android.content.ContentValues;
import android.content.Context;

import com.codefactoring.android.backlogtracker.BuildConfig;
import com.codefactoring.android.backlogtracker.provider.BacklogContract.UserEntry;
import com.codefactoring.android.backlogtracker.sync.models.UserDto;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.Shadows;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowContentProviderOperation;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.robolectric.shadows.ShadowContentProviderOperation.TYPE_DELETE;
import static org.robolectric.shadows.ShadowContentProviderOperation.TYPE_INSERT;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class UserDataHandlerTest {

    private static final int INDEX_TYPE_DELETE = 0;
    private static final int INDEX_TYPE_INSERT = 1;

    private Context mContext;

    @Before
    public void setUp() {
        mContext = RuntimeEnvironment.application;
    }

    @Test
    public void createsOperationDeleteAllUsersAtFirst() {
        final ArrayList<ContentProviderOperation> operations = new UserDataHandler(mContext)
                .makeContentProviderOperations(new ArrayList<UserDto>());

        final ContentProviderOperation operation = operations.get(INDEX_TYPE_DELETE);

        assertThat(operation.getUri(), equalTo(UserEntry.CONTENT_URI));
        final ShadowContentProviderOperation shadowOperation = Shadows.shadowOf(operation);
        assertThat(shadowOperation.getType(), equalTo(TYPE_DELETE));
    }

    @Test
    public void createsOperationInsertUser() {
        final List<UserDto> users = new ArrayList<>();
        final UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setUserId("admin");
        userDto.setName("admin");
        users.add(userDto);

        final ArrayList<ContentProviderOperation> operations = new UserDataHandler(mContext)
                .makeContentProviderOperations(users);

        final ContentProviderOperation operation = operations.get(INDEX_TYPE_INSERT);
        assertThat(operation.getUri(), equalTo(UserEntry.CONTENT_URI));

        final ShadowContentProviderOperation shadowOperation = Shadows.shadowOf(operation);
        assertThat(shadowOperation.getType(), equalTo(TYPE_INSERT));

        final ContentValues contentValues = shadowOperation.getContentValues();
        assertThat(contentValues.getAsLong(UserEntry._ID), equalTo(userDto.getId()));
        assertThat(contentValues.getAsString(UserEntry.USER_ID), equalTo(userDto.getUserId()));
        assertThat(contentValues.getAsString(UserEntry.NAME), equalTo(userDto.getName()));
    }
}