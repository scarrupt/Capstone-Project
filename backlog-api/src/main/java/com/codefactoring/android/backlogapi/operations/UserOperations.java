package com.codefactoring.android.backlogapi.operations;

import com.codefactoring.android.backlogapi.models.User;

import java.util.List;

import retrofit2.http.GET;
import rx.Observable;

public interface UserOperations {

    @GET("users/myself")
    Observable<User> getOwnUser();

    @GET("users")
    Observable<List<User>> getUserList();
}
