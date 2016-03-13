package com.codefactoring.android.backlogapi.operations;

import com.codefactoring.android.backlogapi.models.User;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Path;
import rx.Observable;

public interface UserOperations {

    @GET("users/myself")
    Observable<User> getOwnUser();

    @GET("users")
    Observable<List<User>> getUserList();

    @GET("users/{userId}/icon")
    Observable<ResponseBody> getUserIcon(@Path("userId") long id);
}
