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

import java.time.Duration;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.concurrent.TimeUnit;

public class WorkerFactory {

    private static final String TAG = WorkerFactory.class.getName();
    private static final String FITBIT_DATA_REQUEST = "fitbitDataRequest";
    private static final String LOCALIZATION_REQUEST = "localizationRequest";
    private static final String MOVEMENT_REQUEST = "movementRequest";
    private static final String NOTIFICATION_REQUEST = "notificationRequest";
    private final AppDatabase database;
    private final Context context;
    private final PeriodicWorkRequest fitbitDataRequest, localizationRequest, movementRequest, notificationRequest;
    private final SharedPreferences sharPref;
    private final WorkManager workManager;

    public WorkerFactory(Context context) {
        this.context = context;
        database = AppDatabase.getDatabaseInstance(context);
        sharPref = context.getSharedPreferences(context.getString(com.example.database.R.string.opium_preferences), Context.MODE_PRIVATE);
        workManager = WorkManager.getInstance(context);

        fitbitDataRequest = new PeriodicWorkRequest.Builder(FitbitDataWorker.class, 15, TimeUnit.MINUTES)
                .build();

        localizationRequest = new PeriodicWorkRequest.Builder(PhoneLocalizationWorker.class, 15, TimeUnit.MINUTES)
                .build();

        movementRequest = new PeriodicWorkRequest.Builder(PhoneMovementWorker.class, 15, TimeUnit.MINUTES)
                .build();

        String dailyQuestionTime = sharPref.getString(context.getString(com.example.database.R.string.daily_question_time), "12:00");
        notificationRequest =
                new PeriodicWorkRequest.Builder(NotificationWorker.class, 15, TimeUnit.MINUTES)
                        .setInitialDelay(getInitialDelay(dailyQuestionTime))
                        .build();
    }

    public void enqueueWorks() {
        if (database.fitbitAccessTokenDao().getNewestAccessToken().isPresent() &&
                sharPref.getString(context.getString(com.example.database.R.string.fitbit_switch_state), "false").equals("true")) {
            workManager.enqueueUniquePeriodicWork(FITBIT_DATA_REQUEST, ExistingPeriodicWorkPolicy.KEEP, fitbitDataRequest);
        } else {
            workManager.cancelUniqueWork(FITBIT_DATA_REQUEST);
        }
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            workManager.enqueueUniquePeriodicWork(LOCALIZATION_REQUEST, ExistingPeriodicWorkPolicy.KEEP, this.localizationRequest);
        }
        workManager.enqueueUniquePeriodicWork(MOVEMENT_REQUEST, ExistingPeriodicWorkPolicy.KEEP, movementRequest);
        workManager.enqueueUniquePeriodicWork(NOTIFICATION_REQUEST, ExistingPeriodicWorkPolicy.KEEP, notificationRequest);
    }

    public void enqueueNewNotificationRequest() {
        workManager.cancelUniqueWork(NOTIFICATION_REQUEST);
        String dailyQuestionTime = sharPref.getString(context.getString(com.example.database.R.string.daily_question_time), "12:00");
        PeriodicWorkRequest notificationRequest =
                new PeriodicWorkRequest.Builder(NotificationWorker.class, 1, TimeUnit.DAYS)
                        .setInitialDelay(getInitialDelay(dailyQuestionTime))
                        .build();
        workManager.enqueueUniquePeriodicWork(NOTIFICATION_REQUEST, ExistingPeriodicWorkPolicy.KEEP, notificationRequest);
    }

    private Duration getInitialDelay(String delayTime) {
        String[] dailyQuestionTimeSplit = delayTime.split(":");
        LocalTime delay = LocalTime.of(Integer.parseInt(dailyQuestionTimeSplit[0]), Integer.parseInt(dailyQuestionTimeSplit[1]));
        LocalTime now = LocalTime.now(ZoneId.systemDefault());
        if (delay.isAfter(now)) {
            return Duration.between(now, delay);
        } else {
            return Duration.ofHours(24).minus(Duration.between(delay, now));
        }
    }
}
