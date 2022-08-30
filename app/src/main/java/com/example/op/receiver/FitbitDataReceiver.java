package com.example.op.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.op.R;
import com.example.op.activity.MainActivity;
import com.example.op.database.AppDatabase;
import com.example.op.http.HttpClient;
import com.example.op.http.requests.FitbitRequest;
import com.example.op.service.FitbitDataService;
import com.example.op.utils.FitbitUrlBuilder;

import java.time.LocalDate;

import okhttp3.OkHttpClient;

public class FitbitDataReceiver extends BroadcastReceiver {
    public static final int REQUEST_CODE = 12345;
    @Override
    public void onReceive(Context context, Intent intent) {
        String accessToken = intent.getStringExtra(FitbitDataService.A);
        String refreshToken = intent.getStringExtra(FitbitDataService.B);
        String authorizationToken = intent.getStringExtra(FitbitDataService.C);

        OkHttpClient httpClient = HttpClient.getHttpClient(
                context.getString(R.string.tokenUrl), authorizationToken, refreshToken, context
        );

        AppDatabase database = AppDatabase.getDatabaseInstance(context);
        FitbitRequest fitbitRequest = new FitbitRequest(accessToken, httpClient, database);
        FitbitRequest fitbitRequest1 = new FitbitRequest(accessToken, httpClient, database);

        try {
            fitbitRequest.getStepsByDay(FitbitUrlBuilder.stepsUrl(LocalDate.now()));
            fitbitRequest1.getSpO2ByDay(FitbitUrlBuilder.spO2Url(LocalDate.of(2022,7,5)));
        } catch (InterruptedException exception) {
            exception.printStackTrace();
        }
    }
}
