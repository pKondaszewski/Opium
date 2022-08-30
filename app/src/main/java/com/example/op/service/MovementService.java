package com.example.op.service;

import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;

import androidx.annotation.Nullable;

public class MovementService extends Service implements SensorEventListener {

    int licznik = 0;

    private SensorManager sensorManager;
    private Sensor accelerometerSensor;

    private float[] mGravity;
    private float deviceAcceleration;
    private float currentAcceleration;
    private float lastAcceleration;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        deviceAcceleration = 0.00f;
        currentAcceleration = SensorManager.GRAVITY_EARTH;
        lastAcceleration = currentAcceleration;
        sensorManager.registerListener(this, accelerometerSensor,
                SensorManager.SENSOR_DELAY_UI);
        return Service.START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            mGravity = event.values.clone();
            // Shake detection
            float x = mGravity[0];
            float y = mGravity[1];
            float z = mGravity[2];
            lastAcceleration = currentAcceleration;
            currentAcceleration = (float) Math.sqrt(x*x + y*y + z*z);
            float delta = currentAcceleration - lastAcceleration;
            deviceAcceleration = deviceAcceleration * 0.9f + delta;
            // Make this higher or lower according to how much
            // motion you want to detect
            if (deviceAcceleration > 1) {
                //System.out.println(x + " " + y + " " + z);
            }
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // required method
    }
}
