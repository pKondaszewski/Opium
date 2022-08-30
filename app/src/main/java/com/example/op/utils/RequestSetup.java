package com.example.op.utils;

import okhttp3.Request;

public class RequestSetup {

    public static Request build(String url, String accessToken, String contentType) {
        return new Request.Builder()
                .url(url)
                .get()
                .addHeader("Authorization", "Bearer " + accessToken)
                .addHeader("Content-Type", contentType)
                .build();
    }
}
