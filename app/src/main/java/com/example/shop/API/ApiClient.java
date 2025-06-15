package com.example.shop.API;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;


import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    private static final String BASE_URL = "http://10.0.2.2:8086/";
    private static Retrofit retrofit = null;

    public static Retrofit getClient(Context context) {
        if (retrofit == null) {
            synchronized (ApiClient.class) {
                if (retrofit == null) {
                    // Get JWT token from SharedPreferences
                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
                    String token = prefs.getString("jwt_token", "");

                    // Create OkHttpClient with interceptor for adding Authorization header
                    OkHttpClient client = new OkHttpClient.Builder()
                            .addInterceptor(chain -> {
                                Request original = chain.request();
                                Request.Builder builder = original.newBuilder();
                                if (!token.isEmpty()) {
                                    builder.header("Authorization", "Bearer " + token);
                                }
                                return chain.proceed(builder.build());
                            })
                            .build();

                    // Initialize Retrofit
                    retrofit = new Retrofit.Builder()
                            .baseUrl(BASE_URL)
                            .client(client)
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();
                }
            }
        }
        return retrofit;
    }

    public static ApiService getApiService(Context context) {
        return getClient(context).create(ApiService.class);
    }
}