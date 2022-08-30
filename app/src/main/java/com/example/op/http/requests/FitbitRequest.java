package com.example.op.http.requests;

import com.example.op.database.AppDatabase;
import com.example.op.database.entity.FitbitSpO2Data;
import com.example.op.database.entity.FitbitStepsData;
import com.example.op.utils.JSONDataExtractor;
import com.example.op.utils.RequestSetup;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;

import lombok.SneakyThrows;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class FitbitRequest {

    private final String accessToken;
    private final OkHttpClient httpClient;
    private final AppDatabase database;

    public FitbitRequest(String accessToken, OkHttpClient httpClient, AppDatabase database) {
        this.accessToken = accessToken;
        this.httpClient = httpClient;
        this.database = database;
    }

    public void getStepsByDay(String url) throws InterruptedException {
        Request request = RequestSetup.build(
                url, accessToken, "application/x-www-form-urlencoded"
        );
        Call call = httpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
            }

            @SneakyThrows
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String string = response.body().string();
                System.out.println(string);
                String stepsByDayValue = JSONDataExtractor.steps(string);
                FitbitStepsData uploadedFitbitStepsData = new FitbitStepsData(null, LocalDate.now(), LocalTime.now(), stepsByDayValue);
                FitbitStepsData recentDatabaseFitbitStepsData = database.fitbitStepsDataDao().getNewestFitbitStepsData(LocalDate.now()).orElse(null);
                if (!uploadedFitbitStepsData.equals(recentDatabaseFitbitStepsData)) {
                    database.fitbitStepsDataDao().insert(uploadedFitbitStepsData);
                }
            }
        });
    }

    public void getSpO2ByDay(String url) throws InterruptedException {
        Request request = RequestSetup.build(
                url, accessToken, "application/json"
        );
        Call call = httpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
            }

            @SneakyThrows
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String string = response.body().string();
                System.out.println(string);
                String spO2ByDayValue = JSONDataExtractor.spO2(string);
                FitbitSpO2Data uploadedFitbitSpO2Data = new FitbitSpO2Data(null, LocalDate.now(), LocalTime.now(), spO2ByDayValue);
                FitbitStepsData recentDatabaseFitbitSpO2Data = database.fitbitStepsDataDao().getNewestFitbitStepsData(LocalDate.now()).orElse(null);

                if (!uploadedFitbitSpO2Data.equals(recentDatabaseFitbitSpO2Data)) {
                    database.fitbitSpO2DataDao().insert(uploadedFitbitSpO2Data);
                }
            }
        });
    }
}
