package com.example.op.worker;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.util.Log;

import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.database.AppDatabase;
import com.example.database.HomeAddress;
import com.example.database.entity.PhoneLocalization;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import lombok.NonNull;
import lombok.SneakyThrows;

public class PhoneLocalizationWorker extends Worker {

    private static final String TAG = PhoneLocalizationWorker.class.getName();
    private AppDatabase database;
    private LocationCallback mLocationCallback;
    private LocationRequest mLocationRequest;

    public PhoneLocalizationWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
        database = AppDatabase.getDatabaseInstance(getApplicationContext());
        mLocationRequest = LocationRequest.create();
        mLocationCallback = new LocationCallback() {
            @SneakyThrows
            @Override
            public void onLocationResult(LocationResult locationResult) {
                processLocation(locationResult.getLastLocation());
            }
        };
    }

    @SuppressLint("MissingPermission")
    @Override
    public Result doWork() {
        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(getApplicationContext());
        fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY,  null).addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    processLocation(location);
                }
            }
        });
        return Result.success();
    }

    private void processLocation(Location location) {
        HomeAddress homeAddress = null;
        try {
            homeAddress = retrieveUserAddress(location);
        } catch (IOException exception) {
            exception.printStackTrace();
        }

        PhoneLocalization phoneLocalization = new PhoneLocalization(null, LocalTime.now(), LocalDate.now(), location.getLatitude(), location.getLongitude(), homeAddress);
        database.phoneLocalizationDao().insert(phoneLocalization);
        Log.i(TAG, "Insertion of phone localization into database: " + phoneLocalization);
    }

    private HomeAddress retrieveUserAddress(Location location) throws IOException {
        Geocoder geocoder = new Geocoder(getApplicationContext());
        List<Address> addressList = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
        String[] addressArray = addressList.get(0).getAddressLine(0).split(",");
        return new HomeAddress(addressArray[0], addressArray[1], addressArray[2]);
    }
}
