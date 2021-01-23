package com.example.android.whatscooking.network;

import android.content.Context;

import com.example.android.whatscooking.Config;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    private static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";
    private static RetrofitClient instance;
    final Retrofit retrofit;

    private RetrofitClient(Context context) {
        final OkHttpClient.Builder client = new OkHttpClient.Builder();
        client.connectTimeout(15, TimeUnit.SECONDS);
        client.readTimeout(15, TimeUnit.SECONDS);
        client.writeTimeout(15, TimeUnit.SECONDS);

        final Gson gson = new GsonBuilder()
                .serializeNulls()
                .setDateFormat(DATE_FORMAT)
                .create();
        retrofit = new Retrofit.Builder()
                .client(client.build()).baseUrl(Config.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
    }

    public static RetrofitClient getInstance(Context context) {
        if(instance == null) {
            instance = new RetrofitClient(context);
        }
        return instance;
    }

    public Retrofit getRetrofit() {
        return retrofit;
    }
}