package com.codefactoring.android.backlogtracker.gcm;


import com.codefactoring.android.backlogapi.interceptors.HttpStatusNotFoundResponseInterceptor;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.codefactoring.android.backlogapi.BacklogApiConstants.DATE_FORMAT_PATTERN;

public class UpdateApiClient {

    private static final String BASE_URL = "http://10.0.2.2:8080/_ah/api/";

    private final RegisterOperations registerOperations;

    public UpdateApiClient() {

        final Gson gson = provideGson();

        final HttpUrl baseUrl = HttpUrl.parse(BASE_URL);

        final OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new HttpStatusNotFoundResponseInterceptor()).build();

        final Retrofit retrofit = provideRetrofit(gson, baseUrl, client);

        this.registerOperations = retrofit.create(RegisterOperations.class);
    }

    private Retrofit provideRetrofit(Gson gson, HttpUrl baseUrl, OkHttpClient client) {
        return new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
    }

    private Gson provideGson() {
        return new GsonBuilder()
                .setDateFormat(DATE_FORMAT_PATTERN)
                .create();
    }

    public RegisterOperations getRegisterOperations() {
        return registerOperations;
    }
}