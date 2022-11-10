package com.example.op.http;

import android.content.Context;

import com.example.database.AppDatabase;
import com.example.op.http.requests.FitbitAuthorization;

import java.net.HttpURLConnection;
import java.util.concurrent.CountDownLatch;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class HttpClient {

    private static volatile OkHttpClient client;
    private static AppDatabase database;

    public static OkHttpClient getHttpClient(String tokenUrl, String authorizationString,
                                             String refreshToken, Context context) {
        if (client == null) {
            synchronized (HttpClient.class) {
                if (client == null) {
                    client = new OkHttpClient()
                            .newBuilder()
                            .addInterceptor(chain -> {
                                Request request = chain.request();
                                Response response = chain.proceed(request);
                                if (response.code() == HttpURLConnection.HTTP_UNAUTHORIZED) {
                                    response.close();
                                    database = AppDatabase.getDatabaseInstance(context);

                                    CountDownLatch countDownLatch = new CountDownLatch(1);
                                    FitbitAuthorization.refreshAccessToken(
                                            tokenUrl, authorizationString, refreshToken, database, countDownLatch
                                    );
                                    try {
                                        countDownLatch.await();
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                    return chain.proceed(
                                            chain
                                            .request()
                                            .newBuilder()
                                            .header("Authorization", "Bearer " + database.fitbitAccessTokenDao().getNewestAccessToken().get())
                                            .build());
                                }
                                return response;
                            })
                            .build();

                }
            }
        }
        return client;
    }
}
