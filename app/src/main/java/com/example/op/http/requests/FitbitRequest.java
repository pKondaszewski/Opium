package com.example.op.http.requests;

import android.util.Log;

import com.example.database.AppDatabase;
import com.example.database.entity.FitbitSpO2Data;
import com.example.database.entity.FitbitStepsData;
import com.example.op.utils.JsonManipulator;
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

    private static final String TAG = FitbitRequest.class.getName();

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
                String stepsByDayValue = JsonManipulator.extractSteps(string);
                FitbitStepsData uploadedFitbitStepsData = new FitbitStepsData(null, LocalDate.now(), LocalTime.now(), stepsByDayValue);
                FitbitStepsData recentDatabaseFitbitStepsData = database.fitbitStepsDataDao().getNewestFitbitStepsDataByDate(LocalDate.now()).orElse(null);
                if (!uploadedFitbitStepsData.equals(recentDatabaseFitbitStepsData)) {
                    database.fitbitStepsDataDao().insert(uploadedFitbitStepsData);
                    Log.i(TAG, "Insertion of fitbit steps data into database: " + uploadedFitbitStepsData);
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
                String spO2ByDayValue = JsonManipulator.extractSpo2(string);
                FitbitSpO2Data uploadedFitbitSpO2Data = new FitbitSpO2Data(null, LocalDate.now(), LocalTime.now(), spO2ByDayValue);
                FitbitStepsData recentDatabaseFitbitSpO2Data = database.fitbitStepsDataDao().getNewestFitbitStepsDataByDate(LocalDate.now()).orElse(null);
                if (!uploadedFitbitSpO2Data.equals(recentDatabaseFitbitSpO2Data)) {
                    database.fitbitSpO2DataDao().insert(uploadedFitbitSpO2Data);
                    Log.i(TAG, "Insertion of fitbit spO2 data into database: " + uploadedFitbitSpO2Data);
                }
            }
        });
    }
}
