package com.example.op.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.example.op.R;
import com.example.op.activity.MainActivity;
import com.example.op.database.AppDatabase;
import com.example.op.database.entity.FitbitAccessToken;
import com.example.op.exception.UnauthorizedFitbitException;
import com.example.op.receiver.FitbitDataReceiver;
import com.example.op.utils.Authorization;

public class FitbitDataService extends Service {

    public static final String A = "com.example.op.service.A";
    public static final String B = "com.example.op.service.B";
    public static final String C = "com.example.op.service.C";

    private static final String TAG = "FitbitDataService";

    @Override
    public void onCreate() {
        String clientId = getString(R.string.clientId);
        String clientSecret = getString(R.string.clientSecret);
        String scopes = getString(R.string.scopes);
        String redirectUrl = getString(R.string.redirectUrl);
        Authorization authorization = new Authorization(clientId, clientSecret, scopes, redirectUrl);

        AppDatabase database = AppDatabase.getDatabaseInstance(getApplicationContext());

        FitbitAccessToken fitbitAccessToken = database.fitbitAccessTokenDao().getNewestAccessToken().orElseThrow(
                () -> new UnauthorizedFitbitException(TAG, "There are no fitbit access token. Please authorize fitbit in settings activity.")
        );
        String accessToken = fitbitAccessToken.getAccessToken();
        String refreshToken = fitbitAccessToken.getRefreshToken();
        String authorizationString = authorization.getAuthorizationToken();

        Intent intent = new Intent(getApplicationContext(), FitbitDataReceiver.class);
        intent.putExtra(A, accessToken);
        intent.putExtra(B, refreshToken);
        intent.putExtra(C, authorizationString);

        PendingIntent pendingIntent2 = PendingIntent.getBroadcast(this, FitbitDataReceiver.REQUEST_CODE,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarm2 = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        alarm2.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(),
                AlarmManager.INTERVAL_FIFTEEN_MINUTES, pendingIntent2);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
