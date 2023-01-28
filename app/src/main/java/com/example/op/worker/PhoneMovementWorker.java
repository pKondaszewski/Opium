package com.example.op.worker;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.database.AppDatabase;
import com.example.database.entity.PhoneMovement;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class PhoneMovementWorker extends Worker implements SensorEventListener {
    private static final String TAG = PhoneMovementWorker.class.getName();
    private AppDatabase database;
    private LocalDateTime lastLocalDateTime;
    private SensorManager sensorManager;
    private float deviceAcceleration;
    private float currentAcceleration;
    private float lastAcceleration;


    public PhoneMovementWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    @Override
    public Result doWork() {
        database = AppDatabase.getInstance(getApplicationContext());
        lastLocalDateTime = LocalDateTime.now();
        deviceAcceleration = 0.00f;
        currentAcceleration = SensorManager.GRAVITY_EARTH;
        lastAcceleration = currentAcceleration;
        sensorManager = (SensorManager) getApplicationContext().getSystemService(Context.SENSOR_SERVICE);
        Sensor accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        sensorManager.registerListener(this, accelerometerSensor,
                SensorManager.SENSOR_DELAY_UI);
        return Result.success();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float[] mGravity = event.values.clone();
            float x = mGravity[0];
            float y = mGravity[1];
            float z = mGravity[2];
            lastAcceleration = currentAcceleration;
            currentAcceleration = (float) Math.sqrt(x*x + y*y + z*z);
            float delta = currentAcceleration - lastAcceleration;
            deviceAcceleration = deviceAcceleration * 0.9f + delta;
            if (deviceAcceleration > 1 && LocalDateTime.now().isAfter(lastLocalDateTime)) {
                PhoneMovement phoneMovement = new PhoneMovement(null, LocalDate.now(), LocalTime.now());
                database.phoneMovementDao().insert(phoneMovement);
                Log.i(TAG, "Insertion of phone movement into database. Datetime value: " +
                        phoneMovement.getDateOfMovement() + " " + phoneMovement.getTimeOfMovement());
                sensorManager.unregisterListener(this);
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}
}
