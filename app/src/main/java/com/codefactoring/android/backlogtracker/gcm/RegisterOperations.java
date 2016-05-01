package com.codefactoring.android.backlogtracker.gcm;

import okhttp3.ResponseBody;
import retrofit2.http.Body;
import retrofit2.http.POST;
import rx.Observable;

public interface RegisterOperations {

    @POST("registration/v1/registerDevice")
    Observable<ResponseBody> register(@Body Registration registration);
}
