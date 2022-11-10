package com.example.op.worker;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.op.R;

import java.time.LocalTime;

public class UserActivityRangeWorker extends Worker {

    public UserActivityRangeWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        Context context = getApplicationContext();
        WorkManager instance = WorkManager.getInstance(context);
        SharedPreferences sharPref = context.getSharedPreferences(context.getString(R.string.opium_preferences), Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = sharPref.edit();

        String time1 = sharPref.getString("time", null);
        LocalTime localTime = time1 == null ? LocalTime.of(8, 0) : LocalTime.of(22, 0);

        // TODO: nad tym trzeba sie zastanowic, ale to ma potencjał.
        //  Worker do obsługi innych workerów aby działały w dynamicznych przedziałach czasu.

        Data time;
        if (true) {
            time = new Data.Builder()
                    .putString("time", "8:00")
                    .build();
            instance.cancelAllWork();
        } else {
            time = new Data.Builder()
                    .putString("time", "22:00")
                    .build();
        }
        instance.cancelAllWork();
        instance.enqueue(new OneTimeWorkRequest.Builder(UserActivityRangeWorker.class)
                        .setInputData(time)
                        .build()
        );
        return null;
    }
}
