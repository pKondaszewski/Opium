package com.example.op.domain.utils;

import static org.junit.Assert.assertEquals;

import com.example.op.utils.RequestSetup;

import org.junit.Test;

import okhttp3.Request;

public class RequestSetupTest {
    @Test
    public void buildPositiveTest() {
        String url = "http://url.com/";
        String accessToken = "accessToken";
        String contentType = "contentType";
        Request build = RequestSetup.build(url, accessToken, contentType);
        build.headers().get("Authorization");
        build.headers().get("ContentType");

        assertEquals(url, build.url().url().toString());
        assertEquals("Bearer " + accessToken, build.headers().get("Authorization"));
        assertEquals(contentType, build.headers().get("Content-Type"));
    }
}
