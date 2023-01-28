package com.example.op.http;

import android.content.Context;
import android.util.Log;

import com.example.database.AppDatabase;
import com.example.op.http.requests.FitbitAuthorization;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.concurrent.CountDownLatch;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class HttpClient {
    private static final String TAG = HttpClient.class.getName();
    private static volatile OkHttpClient client;
    private static AppDatabase database;

    public static OkHttpClient getHttpClient(String tokenUrl, String authorizationString,
                                             String refreshToken, Context context) {
        if (client == null) {
            synchronized (HttpClient.class) {
                if (client == null) {
                    client = new OkHttpClient().newBuilder().addInterceptor(chain -> {
                        Request request = chain.request();
                        Response response = chain.proceed(request);
                        if (response.code() == HttpURLConnection.HTTP_UNAUTHORIZED) {
                            response.close();
                            database = AppDatabase.getInstance(context);
                            CountDownLatch countDownLatch = new CountDownLatch(1);
                            FitbitAuthorization.refreshAccessToken(
                                    tokenUrl, authorizationString, refreshToken, database, countDownLatch);
                            countDown(countDownLatch);
                            return chainResult(chain);
                        }
                        return response;
                    })
                    .build();

                }
            }
        }
        return client;
    }

    private static void countDown(CountDownLatch countDownLatch) {
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            Log.e(TAG, "There are unresolved interrupted exception", e);
        }
    }

    private static Response chainResult(Interceptor.Chain chain) throws IOException {
        return chain.proceed(chain
                .request()
                .newBuilder()
                .header("Authorization", "Bearer " + database.fitbitAccessTokenDao().getNewestAccessToken().get())
                .build());
    }
}
