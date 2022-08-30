package com.example.op.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class UpdateFitbitDataService extends Service {

    String authorizationToken;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand (Intent intent, int flags, int startId) {
        Toast.makeText(this, "toast mordo", Toast.LENGTH_SHORT).show();

        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
