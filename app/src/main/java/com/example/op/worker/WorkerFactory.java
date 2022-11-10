package com.example.op.worker;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;
import androidx.work.Data;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.ExistingWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import com.example.database.AppDatabase;
import com.example.op.R;
import com.google.common.util.concurrent.ListenableFuture;

import java.util.List;
import java.util.concurrent.TimeUnit;

import lombok.SneakyThrows;

public class WorkerFactory {

    private final AppDatabase database;
    private final Context context;
    private final PeriodicWorkRequest fitbitDataRequest, localizationRequest, movementRequest, notificationRequest;
    private final WorkManager workManager;
    private final OneTimeWorkRequest userActivityRangeRequest;

    public WorkerFactory(Context context) {
        this.context = context;
        database = AppDatabase.getDatabaseInstance(context);
        workManager = WorkManager.getInstance(context);

        Data repeatable = new Data.Builder()
                .putBoolean(context.getString(R.string.is_repeatable), true)
                .build();

        userActivityRangeRequest =
                new OneTimeWorkRequest.Builder(UserActivityRangeWorker.class)
                        //.setInputData()
                        .build();

//        String fitbitDataStartTime = sharPref.getString(getString(R.string.fitbit_data_interval_start_time), "00:00");
//        Duration intervalStart = extractInitialDelayValue(fitbitDataStartTime);
        fitbitDataRequest = new PeriodicWorkRequest.Builder(FitbitDataWorker.class, 15, TimeUnit.MINUTES)
                .setInputData(repeatable)
//                .setInitialDelay(intervalStart)
                .addTag("tag")
                .build();

        localizationRequest = new PeriodicWorkRequest.Builder(PhoneLocalizationWorker.class, 15, TimeUnit.MINUTES)
                .setInputData(repeatable)
                .addTag("tag")
                .build();


        //String phoneMovementStartTime = sharPref.getString(getString(R.string.phone_movement_interval_start_time), "12:00");
        //intervalStart = extractInitialDelayValue(phoneMovementStartTime);
        movementRequest = new PeriodicWorkRequest.Builder(PhoneMovementWorker.class, 15, TimeUnit.MINUTES)
                .setInputData(repeatable)
                //.setInitialDelay(intervalStart)
                .addTag("tag")
                .build();

        //String notificationStartTime = sharPref.getString(getString(R.string.notification_interval_start_time), "12:00");
        //intervalStart = extractInitialDelayValue(notificationStartTime);
        notificationRequest =
                new PeriodicWorkRequest.Builder(NotificationWorker.class, 15, TimeUnit.MINUTES)
                        .setInputData(repeatable)
                        //.setInitialDelay(intervalStart)
                        .addTag("tag")
                        .build();
    }

    public void enqueueWorks() {

        if (database.fitbitAccessTokenDao().getNewestAccessToken().isPresent()) {
            workManager.enqueueUniquePeriodicWork("fitbitDataRequest", ExistingPeriodicWorkPolicy.KEEP, localizationRequest);
        }
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            workManager.enqueueUniquePeriodicWork("localizationRequest", ExistingPeriodicWorkPolicy.KEEP, this.localizationRequest);
        }
        workManager.enqueueUniquePeriodicWork("movementRequest", ExistingPeriodicWorkPolicy.KEEP, movementRequest);
        workManager.enqueueUniquePeriodicWork("notificationRequest", ExistingPeriodicWorkPolicy.KEEP, notificationRequest);
        workManager.enqueueUniqueWork("userActivityRangeRequest", ExistingWorkPolicy.KEEP, userActivityRangeRequest);
    }
}
