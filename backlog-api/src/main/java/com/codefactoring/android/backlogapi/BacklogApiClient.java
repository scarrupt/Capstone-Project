package com.codefactoring.android.backlogapi;

import com.codefactoring.android.backlogapi.operations.UserOperations;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.GsonConverterFactory;
import retrofit2.Retrofit;
import retrofit2.RxJavaCallAdapterFactory;

import static com.codefactoring.android.backlogapi.BacklogApiConstants.DATE_FORMAT_PATTERN;

public class BacklogApiClient {

    private static final String BACKLOG_API_ENDPOINT = "/api/v2/";

    private static final String API_KEY_PARAMETER = "apiKey";

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
        return new OkHttpClient.Builder().addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                final Request request = chain.request();
                final HttpUrl urlWithApiKey = request.url().newBuilder().addQueryParameter(API_KEY_PARAMETER, apiKey).build();
                return chain.proceed(request.newBuilder().url(urlWithApiKey).build());
            }
        }).build();
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
