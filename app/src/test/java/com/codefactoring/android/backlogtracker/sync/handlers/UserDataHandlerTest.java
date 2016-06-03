package com.codefactoring.android.backlogtracker.sync.handlers;

import android.content.ContentProviderOperation;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.codefactoring.android.backlogtracker.BuildConfig;
import com.codefactoring.android.backlogtracker.provider.BacklogContract.UserEntry;
import com.codefactoring.android.backlogtracker.sync.models.UserDto;
import com.google.common.collect.Lists;

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
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.robolectric.shadows.ShadowContentProviderOperation.TYPE_DELETE;
import static org.robolectric.shadows.ShadowContentProviderOperation.TYPE_INSERT;
import static org.robolectric.shadows.ShadowContentProviderOperation.TYPE_UPDATE;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class UserDataHandlerTest {

    private Context mContext;

    @Before
    public void setUp() {
        mContext = RuntimeEnvironment.application;
    }

    @Test
    public void createsOperationDeleteRemoveUser(){

        final UserDataHandler userDataHandler = new UserDataHandler(mContext) {
            @Override
            protected Cursor query() {
                final Cursor cursor = mock(Cursor.class);
                when(cursor.getCount()).thenReturn(1);
                when(cursor.moveToFirst()).thenReturn(true);
                when(cursor.getString(eq(COL_USER_ID))).thenReturn("removedUserId");
                return cursor;
            }
        };

        final ArrayList<ContentProviderOperation> operations = userDataHandler
                .makeContentProviderOperations(new ArrayList<UserDto>());

        final ContentProviderOperation operation = operations.get(0);

        assertThat(operation.getUri(), equalTo(UserEntry.CONTENT_URI));
        final ShadowContentProviderOperation shadowOperation = Shadows.shadowOf(operation);
        assertThat(shadowOperation.getType(), equalTo(TYPE_DELETE));
    }

    @Test
    public void createsOperationUpdateUser(){

        final String existingUserId = "existingUserId";

        final UserDataHandler userDataHandler = new UserDataHandler(mContext) {
            @Override
            protected Cursor query() {
                final Cursor cursor = mock(Cursor.class);
                when(cursor.getCount()).thenReturn(1);
                when(cursor.moveToFirst()).thenReturn(true);
                when(cursor.getString(eq(COL_USER_ID))).thenReturn(existingUserId);
                return cursor;
            }
        };

        final UserDto userDto = new UserDto();
        userDto.setUserId(existingUserId);
        userDto.setName("updatedName");

        final ArrayList<ContentProviderOperation> operations = userDataHandler
                .makeContentProviderOperations(Lists.newArrayList(userDto));

        final ContentProviderOperation operation = operations.get(0);

        assertThat(operation.getUri(), equalTo(UserEntry.CONTENT_URI));
        final ShadowContentProviderOperation shadowOperation = Shadows.shadowOf(operation);
        assertThat(shadowOperation.getType(), equalTo(TYPE_UPDATE));
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

        final ContentProviderOperation operation = operations.get(0);
        assertThat(operation.getUri(), equalTo(UserEntry.CONTENT_URI));

        final ShadowContentProviderOperation shadowOperation = Shadows.shadowOf(operation);
        assertThat(shadowOperation.getType(), equalTo(TYPE_INSERT));

        final ContentValues contentValues = shadowOperation.getContentValues();
        assertThat(contentValues.getAsLong(UserEntry._ID), equalTo(userDto.getId()));
        assertThat(contentValues.getAsString(UserEntry.USER_ID), equalTo(userDto.getUserId()));
        assertThat(contentValues.getAsString(UserEntry.NAME), equalTo(userDto.getName()));
    }
}