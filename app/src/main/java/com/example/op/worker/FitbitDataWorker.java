package com.example.op.worker;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.database.AppDatabase;
import com.example.database.entity.FitbitAccessToken;
import com.example.op.R;
import com.example.op.exception.UnauthorizedFitbitException;
import com.example.op.http.HttpClient;
import com.example.op.http.requests.FitbitRequest;
import com.example.op.utils.Authorization;
import com.example.op.utils.FitbitUrlBuilder;

import java.time.LocalDate;

import okhttp3.OkHttpClient;

public class FitbitDataWorker extends Worker {
    private static final String TAG = FitbitDataWorker.class.getName();
    private final FitbitRequest fitbitRequest, fitbitRequest1;
    private final LocalDate now = LocalDate.now();

    public FitbitDataWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
        String clientId = context.getString(R.string.client_id);
        String clientSecret = context.getString(R.string.client_secret);
        String scopes = context.getString(R.string.scopes);
        String redirectUrl = context.getString(R.string.redirect_url);
        Authorization authorization = new Authorization(clientId, clientSecret, scopes, redirectUrl);
        AppDatabase database = AppDatabase.getInstance(context);

        FitbitAccessToken fitbitAccessToken = database.fitbitAccessTokenDao().getNewestAccessToken().orElseThrow(
                () -> new UnauthorizedFitbitException(TAG, "There are no fitbit access token. Please authorize fitbit in settings activity.")
        );
        String accessToken = fitbitAccessToken.getAccessToken();
        String refreshToken = fitbitAccessToken.getRefreshToken();
        String authorizationToken = authorization.getAuthorizationToken();

        OkHttpClient httpClient = HttpClient.getHttpClient(
                context.getString(R.string.token_url), authorizationToken, refreshToken, context
        );

        fitbitRequest = new FitbitRequest(accessToken, httpClient, database);
        fitbitRequest1 = new FitbitRequest(accessToken, httpClient, database);
    }

    @Override
    public Result doWork() {
        try {
            fitbitRequest.getStepsByDay(FitbitUrlBuilder.stepsUrl(now));
            fitbitRequest1.getSpO2ByDay(FitbitUrlBuilder.spO2Url(now));
        } catch (InterruptedException exception) {
            Log.e(TAG, "There are unresolved exception during Fitbit worker", exception);
            return Result.failure();
        }
        return Result.success();
    }
}
