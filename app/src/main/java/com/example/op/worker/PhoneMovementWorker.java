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
import com.example.op.R;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class PhoneMovementWorker extends Worker implements SensorEventListener {

    private static final String TAG = PhoneMovementWorker.class.getName();
    private AppDatabase database;
    private Context context;
    private LocalDateTime lastLocalDateTime;
    private Sensor accelerometerSensor;
    private SensorManager sensorManager;
    private float[] mGravity;
    private float deviceAcceleration;
    private float currentAcceleration;
    private float lastAcceleration;


    public PhoneMovementWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    @Override
    public Result doWork() {
        context = getApplicationContext();
        database = AppDatabase.getDatabaseInstance(getApplicationContext());

        lastLocalDateTime = LocalDateTime.now();

        sensorManager = (SensorManager) getApplicationContext().getSystemService(Context.SENSOR_SERVICE);
        accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        deviceAcceleration = 0.00f;
        currentAcceleration = SensorManager.GRAVITY_EARTH;
        lastAcceleration = currentAcceleration;
        sensorManager.registerListener(this, accelerometerSensor,
                SensorManager.SENSOR_DELAY_UI);
        return Result.success();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            mGravity = event.values.clone();
            float x = mGravity[0];
            float y = mGravity[1];
            float z = mGravity[2];
            lastAcceleration = currentAcceleration;
            currentAcceleration = (float) Math.sqrt(x*x + y*y + z*z);
            float delta = currentAcceleration - lastAcceleration;
            deviceAcceleration = deviceAcceleration * 0.9f + delta;
            // Make this higher or lower according to how much
            // motion you want to detect

            if (deviceAcceleration > 1)
                System.out.println(deviceAcceleration);

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
