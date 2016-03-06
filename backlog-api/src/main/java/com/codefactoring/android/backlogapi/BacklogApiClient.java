package com.codefactoring.android.backlogapi;

import com.codefactoring.android.backlogapi.interceptors.ApiKeyRequestInterceptor;
import com.codefactoring.android.backlogapi.operations.UserOperations;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import retrofit2.GsonConverterFactory;
import retrofit2.Retrofit;
import retrofit2.RxJavaCallAdapterFactory;

import static com.codefactoring.android.backlogapi.BacklogApiConstants.BACKLOG_API_ENDPOINT;
import static com.codefactoring.android.backlogapi.BacklogApiConstants.DATE_FORMAT_PATTERN;

public class BacklogApiClient {

    private UserOperations mUserOperations;

    public BacklogApiClient(String spaceUrl, final String apiKey) {
        final Gson gson = provideGson();

        final HttpUrl baseUrl = HttpUrl.parse(spaceUrl);

        final OkHttpClient client = provideOkHttpClient(apiKey);

        final Retrofit retrofit = provideRetrofit(gson, baseUrl, client);

        mUserOperations = retrofit.create(UserOperations.class);
    }

    private Retrofit provideRetrofit(Gson gson, HttpUrl baseUrl, OkHttpClient client) {
        return new Retrofit.Builder()
                .baseUrl(baseUrl.newBuilder().encodedPath(BACKLOG_API_ENDPOINT).build())
                        .client(client)
                        .addConverterFactory(GsonConverterFactory.create(gson))
                        .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                        .build();
    }

    private OkHttpClient provideOkHttpClient(final String apiKey) {
        return new OkHttpClient.Builder()
                .addInterceptor(new ApiKeyRequestInterceptor(apiKey))
                .build();
    }

    private Gson provideGson() {
        return new GsonBuilder()
                .setDateFormat(DATE_FORMAT_PATTERN)
                .create();
    }

    public UserOperations getUserOperations() {
        return mUserOperations;
    }
}
