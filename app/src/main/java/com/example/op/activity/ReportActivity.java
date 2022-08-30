package com.example.op.activity;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.op.R;
import com.example.op.database.AppDatabase;
import com.example.op.database.entity.FitbitSpO2Data;
import com.example.op.database.entity.FitbitStepsData;
import com.example.op.database.entity.PhoneLocalization;

import java.time.LocalDate;

import lombok.SneakyThrows;

public class ReportActivity extends AppCompatActivity {
    @SneakyThrows
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        AppDatabase database = AppDatabase.getDatabaseInstance(this);
        LocalDate presentDate = LocalDate.now();

        TextView dateValueTextView = findViewById(R.id.dateValueTextView);
        TextView nameValueTextView = findViewById(R.id.nameValueTextView);
        TextView phoneMovementValueTextView = findViewById(R.id.phoneMovementValueTextView);
        TextView fitbitStepsValueTextView = findViewById(R.id.fitbitStepsValueTextView);
        TextView fitbitSp02ValueTextView = findViewById(R.id.fitbitSp02ValueTextView);
        TextView phoneLocalizationValueTextView = findViewById(R.id.phoneLocalizationValueTextView);

        dateValueTextView.setText(presentDate.toString());
        nameValueTextView.setText("Janina Kowalska");
        phoneMovementValueTextView.setText("true");

        FitbitStepsData fitbitStepsData = database.fitbitStepsDataDao().getNewestFitbitStepsData(presentDate)
                .orElse(new FitbitStepsData());
        fitbitStepsValueTextView.setText(fitbitStepsData.getStepsValue());

        FitbitSpO2Data fitbitSpO2Data = database.fitbitSpO2DataDao().getNewestFitbitSpO2Data(presentDate)
                .orElse(new FitbitSpO2Data());
        fitbitSp02ValueTextView.setText(fitbitSpO2Data.getSpO2Value());

        PhoneLocalization phoneLocalization = database.phoneLocalizationDao().getNewestLocation().orElse(
                new PhoneLocalization());
        Geocoder geocoder = new Geocoder(this);
        String homeAddress = null;
        for (Address address : geocoder.getFromLocation(phoneLocalization.getLatitude(), phoneLocalization.getLongitude(), 1)) {
            homeAddress = address.getAddressLine(0);
        }
        phoneLocalizationValueTextView.setText(String.format("%s, %s \n%s", phoneLocalization.getLatitude(), phoneLocalization.getLongitude(), homeAddress));

    }

}
