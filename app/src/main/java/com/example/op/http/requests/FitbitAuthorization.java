package com.example.op.http.requests;

import android.net.Uri;

import androidx.annotation.NonNull;

import com.example.database.AppDatabase;
import com.example.database.entity.FitbitAccessToken;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class FitbitAuthorization {

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final OkHttpClient client = new OkHttpClient().newBuilder().build();

    public static void exchangeCodeForAccessToken(String code, String redirectUrl, String tokenUrl, String authorizationString, AppDatabase database) {
        RequestBody formBody = new FormBody.Builder()
                .add("grant_type", "authorization_code")
                .add("code", code)
                .add("redirect_uri", Uri.parse(redirectUrl).toString())
                .build();

        Request request = new Request.Builder()
                .url(tokenUrl)
                .post(formBody)
                .addHeader("Authorization", authorizationString)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .build();

        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                FitbitAccessToken accessToken = objectMapper.readValue(response.body().string(), FitbitAccessToken.class);
                database.fitbitAccessTokenDao().insert(accessToken);
            }
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {

            }
        });
    }

    public static void refreshAccessToken(String tokenUrl, String authorizationToken, String refreshToken, AppDatabase database, CountDownLatch countDownLatch) {
        RequestBody formBody = new FormBody.Builder()
                .add("grant_type", "refresh_token")
                .add("refresh_token", refreshToken)
                .build();

        Request request = new Request.Builder()
                .url(tokenUrl)
                .post(formBody)
                .addHeader("Authorization", authorizationToken)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .build();

        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onResponse(@NonNull Call call, @NonNull okhttp3.Response response) throws IOException {
                FitbitAccessToken accessToken = objectMapper.readValue(response.body().string(), FitbitAccessToken.class);
                database.fitbitAccessTokenDao().insert(accessToken);
                countDownLatch.countDown();
            }
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
            }
        });
    }
}
