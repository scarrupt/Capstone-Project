package com.codefactoring.android.backlogtracker.sync.fetchers;

import android.util.Log;

import com.codefactoring.android.backlogapi.BacklogApiClient;
import com.codefactoring.android.backlogapi.models.User;
import com.codefactoring.android.backlogtracker.sync.models.BacklogImage;
import com.codefactoring.android.backlogtracker.sync.models.UserDto;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import rx.Observable;
import rx.functions.Func1;

public class UserDataFetcher {

    public final String LOG_TAG = UserDataFetcher.class.getSimpleName();

    private final BacklogApiClient mBacklogApiClient;

    public UserDataFetcher(BacklogApiClient backlogApiClient) {
        mBacklogApiClient = backlogApiClient;
    }

    public List<UserDto> getUserList() {
        return mBacklogApiClient.getUserOperations().getUserList()
                .onErrorReturn(new Func1<Throwable, List<User>>() {
                    @Override
                    public List<User> call(Throwable throwable) {
                        Log.e(LOG_TAG, "Error on getUserList", throwable);
                        return new ArrayList<>();
                    }
                })
                .flatMapIterable(new Func1<List<User>, Iterable<User>>() {
                    @Override
                    public Iterable<User> call(List<User> users) {
                        return users;
                    }
                })
                .flatMap(new Func1<User, Observable<UserDto>>() {
                    @Override
                    public Observable<UserDto> call(User user) {
                        final UserDto userDto = new UserDto();
                        userDto.setId(user.getId());
                        userDto.setUserId(user.getUserId());
                        userDto.setName(user.getName());
                        userDto.setImage(getBacklogImage(user.getId()));
                        return Observable.just(userDto);
                    }
                })
                .toList()
                .toBlocking()
                .first();
    }

    private BacklogImage getBacklogImage(final long id) {
        return mBacklogApiClient.getUserOperations().getUserIcon(id)
                .flatMap(new Func1<ResponseBody, Observable<BacklogImage>>() {
                    @Override
                    public Observable<BacklogImage> call(ResponseBody response) {
                        final String subtype = response.contentType().subtype();
                        final byte[] bytes;
                        try {
                            bytes = response.bytes();
                        } catch (IOException ex) {
                            Log.e(LOG_TAG, "Error on reading image", ex);
                            return null;
                        }
                        return Observable.just(new BacklogImage(id + "." + subtype, bytes));
                    }
                })
                .onErrorReturn(new Func1<Throwable, BacklogImage>() {
                    @Override
                    public BacklogImage call(Throwable throwable) {
                        Log.e(LOG_TAG, "Error on get Project Icon", throwable);
                        return null;
                    }
                })
                .toBlocking()
                .first();
    }
}
