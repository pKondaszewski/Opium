package com.example.op.http.requests;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.database.AppDatabase;
import com.example.database.entity.FitbitSpO2Data;
import com.example.database.entity.FitbitStepsData;
import com.example.op.utils.JsonManipulator;
import com.example.op.utils.RequestSetup;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;

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

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String string = Objects.requireNonNull(response.body()).string();
                String uploadedStepsValue = JsonManipulator.extractSteps(string);
                FitbitStepsData uploadedFitbitStepsData = new FitbitStepsData(null, LocalDate.now(), LocalTime.now(), uploadedStepsValue);
                FitbitStepsData recentDatabaseFitbitStepsData = database.fitbitStepsDataDao().getMaxFitbitStepsDataByDate(LocalDate.now()).orElse(null);
                if (recentDatabaseFitbitStepsData == null) {
                    return;
                }
                String recentStepsValue = recentDatabaseFitbitStepsData.getStepsValue();
                if (!uploadedStepsValue.equals(recentStepsValue)) {
                    database.fitbitStepsDataDao().insert(uploadedFitbitStepsData);
                    Log.i(TAG, String.format("Insertion of fitbit steps data into database. Date: %s. Time: %s. Steps value:  %s",
                            uploadedFitbitStepsData.getDate(), uploadedFitbitStepsData.getTime(), uploadedFitbitStepsData.getStepsValue()));
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
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String string = Objects.requireNonNull(response.body()).string();
                String uploadedSpO2Value = JsonManipulator.extractSpo2(string);
                FitbitSpO2Data uploadedFitbitSpO2Data = new FitbitSpO2Data(null, LocalDate.now(), LocalTime.now(), uploadedSpO2Value);
                FitbitSpO2Data recentFitbitSpO2Data = database.fitbitSpO2DataDao().getMaxFitbitSpO2DataByDate(LocalDate.now()).orElse(null);
                if (recentFitbitSpO2Data == null) {
                    return;
                }
                String recentSpO2Value = recentFitbitSpO2Data.getSpO2Value();
                if (!uploadedSpO2Value.equals(recentSpO2Value)) {
                    database.fitbitSpO2DataDao().insert(uploadedFitbitSpO2Data);
                    Log.i(TAG, String.format("Insertion of fitbit spO2 data into database. Date: %s. Time: %s. SpO2 value:  %s",
                            uploadedFitbitSpO2Data.getDate(), uploadedFitbitSpO2Data.getTime(), uploadedFitbitSpO2Data.getSpO2Value()));
                }
            }
        });
    }
}
