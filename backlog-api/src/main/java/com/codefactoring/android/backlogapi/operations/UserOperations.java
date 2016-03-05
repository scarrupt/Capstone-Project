package com.codefactoring.android.backlogapi.operations;

import com.codefactoring.android.backlogapi.models.User;

import retrofit2.http.GET;
import rx.Observable;

public interface UserOperations {

    @GET("users/myself")
    Observable<User> getOwnUser();
}
