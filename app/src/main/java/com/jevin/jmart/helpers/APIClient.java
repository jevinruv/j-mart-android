package com.jevin.jmart.helpers;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class APIClient {

    public static final String BASE_URL = "http://j-mart.herokuapp.com/api/";
//    public static final String BASE_URL = "http://10.0.3.2:8080";
//    public static final String BASE_URL = "http://192.168.1.5:8080";

    private static Retrofit retrofit = null;
    private static OkHttpClient client = null;

    public static Retrofit getClient() {

        if (retrofit == null) {

            getOkHttpClient();

            retrofit = new Retrofit.Builder()
                    .client(client)
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    private static OkHttpClient getOkHttpClient() {

        String authToken = SharedPreferencesManager.getAuthToken(MyApplication.getAppContext());

        client = new OkHttpClient.Builder().addInterceptor(chain -> {

            Request newRequest = chain.request().newBuilder()
                    .addHeader("Authorization", "Bearer " + authToken)
                    .build();
            return chain.proceed(newRequest);
        }).build();

        return client;
    }

    public static Retrofit getClientForAuth() {

        Retrofit retrofitForAuth = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofitForAuth;
    }
}
