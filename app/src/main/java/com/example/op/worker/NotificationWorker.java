package com.example.op.worker;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.op.service.NotificationService;

import lombok.NonNull;

public class NotificationWorker extends Worker {
    private final Context context;
    private final Intent intent;
    private final SharedPreferences sharPref;

    public NotificationWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
        this.context = context;
        sharPref = context.getSharedPreferences(context.getString(com.example.database.R.string.opium_preferences), Context.MODE_PRIVATE);
        intent = new Intent(getApplicationContext(), NotificationService.class);
    }

    @Override
    public Result doWork() {
        sharPref.edit().putString(context.getString(com.example.database.R.string.is_repeatable), "true").apply();
        context.startService(intent);
        return Result.success();
    }
}
