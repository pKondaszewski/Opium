package com.example.op.service;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.example.op.database.AppDatabase;
import com.example.op.database.entity.PhoneLocalization;
import com.example.op.receiver.FitbitDataReceiver;
import com.example.op.utils.UserAddress;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import lombok.SneakyThrows;

public class LocalizationService extends Service {

    @SuppressLint("MissingPermission")
    @Override
    public void onCreate() {
        AppDatabase database = AppDatabase.getDatabaseInstance(getApplicationContext());
        LocationRequest mLocationRequest = LocationRequest.create();
        mLocationRequest.setInterval(15 * 60 * 1_000);
        LocationCallback mLocationCallback = new LocationCallback() {
            @SneakyThrows
            @Override
            public void onLocationResult(LocationResult locationResult) {
                Location location = locationResult.getLastLocation();
                Geocoder geocoder = new Geocoder(getApplicationContext());

                List<Address> addressList = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                String[] addressArray = addressList.get(0).getAddressLine(0).split(",");
                UserAddress userAddress = new UserAddress(addressArray[0], addressArray[1], addressArray[2]);

                PhoneLocalization phoneLocalization = new PhoneLocalization(null, LocalTime.now(), LocalDate.now(), location.getLatitude(), location.getLongitude(), userAddress);
                database.phoneLocalizationDao().insert(phoneLocalization);
            }
        };
        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        fusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, null);
        fusedLocationClient.getLastLocation();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
