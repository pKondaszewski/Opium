package com.example.op.worker;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;
import androidx.work.Data;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.ExistingWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.example.database.AppDatabase;
import com.example.op.R;

import java.util.concurrent.TimeUnit;

public class WorkerFactory {

    private final static String TAG = WorkerFactory.class.getName();
    private final AppDatabase database;
    private final Context context;
    private final PeriodicWorkRequest fitbitDataRequest, localizationRequest, movementRequest, notificationRequest;
    private final SharedPreferences sharPref;
    private final WorkManager workManager;

    public WorkerFactory(Context context) {
        this.context = context;
        database = AppDatabase.getDatabaseInstance(context);
        sharPref = context.getSharedPreferences(context.getString(R.string.opium_preferences), Context.MODE_PRIVATE);
        workManager = WorkManager.getInstance(context);

        Data repeatable = new Data.Builder()
                .putBoolean(context.getString(R.string.is_repeatable), true)
                .build();

        fitbitDataRequest = new PeriodicWorkRequest.Builder(FitbitDataWorker.class, 15, TimeUnit.MINUTES)
                .setInputData(repeatable)
                .addTag("tag")
                .build();

        localizationRequest = new PeriodicWorkRequest.Builder(PhoneLocalizationWorker.class, 15, TimeUnit.MINUTES)
                .setInputData(repeatable)
                .addTag("tag")
                .build();

        movementRequest = new PeriodicWorkRequest.Builder(PhoneMovementWorker.class, 15, TimeUnit.MINUTES)
                .setInputData(repeatable)
                .addTag("tag")
                .build();

        //TODO: Initial delay zeby zapytanie bylo o okreslonej godzinie przez uzytkownika np. 12:00
        notificationRequest =
                new PeriodicWorkRequest.Builder(NotificationWorker.class, 15, TimeUnit.MINUTES)
                        .setInputData(repeatable)
                        .addTag("tag")
                        .build();
    }

    public void enqueueWorks() {
        if (database.fitbitAccessTokenDao().getNewestAccessToken().isPresent() &&
                sharPref.getBoolean(context.getString(R.string.fitbit_switch_state), false)) {
            workManager.enqueueUniquePeriodicWork("fitbitDataRequest", ExistingPeriodicWorkPolicy.KEEP, fitbitDataRequest);
        } else {
            workManager.cancelUniqueWork("fitbitDataRequest");
        }
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            workManager.enqueueUniquePeriodicWork("localizationRequest", ExistingPeriodicWorkPolicy.KEEP, this.localizationRequest);
        }
        workManager.enqueueUniquePeriodicWork("movementRequest", ExistingPeriodicWorkPolicy.KEEP, movementRequest);
        workManager.enqueueUniquePeriodicWork("notificationRequest", ExistingPeriodicWorkPolicy.KEEP, notificationRequest);
    }
}
