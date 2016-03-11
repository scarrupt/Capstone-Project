package com.codefactoring.android.backlogapi.interceptors;

import com.codefactoring.android.backlogapi.BacklogApiException;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class HttpStatusNotFoundResponseInterceptor implements Interceptor {

    private static final int HTTP_STATUS_NOT_FOUND = 404;

    @Override
    public Response intercept(Chain chain) throws IOException {
        final Request request = chain.request();
        final Response response = chain.proceed(request);

        if (HTTP_STATUS_NOT_FOUND == response.code()) {
            throw new BacklogApiException(response.body() == null ? "" : response.body().string());
        }

        return response;
    }
}
