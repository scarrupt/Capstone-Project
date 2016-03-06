package com.codefactoring.android.backlogapi.interceptors;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

import static com.codefactoring.android.backlogapi.BacklogApiConstants.API_KEY_PARAMETER;

public class ApiKeyRequestInterceptor implements Interceptor {

    private final String mApiKey;

    public ApiKeyRequestInterceptor(String apiKey) {
        mApiKey = apiKey;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        final Request request = chain.request();
        final HttpUrl urlWithApiKey = request.url()
                .newBuilder()
                .addQueryParameter(API_KEY_PARAMETER, mApiKey)
                .build();
        return chain.proceed(request.newBuilder().url(urlWithApiKey).build());
    }
}
