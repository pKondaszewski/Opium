package com.example.op.activity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.op.R;
import com.example.op.database.AppDatabase;
import com.example.op.database.entity.DailyFeelings;
import com.example.op.database.entity.FitbitSpO2Data;
import com.example.op.database.entity.FitbitStepsData;
import com.example.op.database.entity.PhoneLocalization;
import com.example.op.utils.UserAddress;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

import okhttp3.OkHttpClient;

public class TreatmentHistoryActivity extends AppCompatActivity implements CalendarView.OnDateChangeListener, MenuItem.OnMenuItemClickListener {

    CalendarView calendarView;
    TextView descriptionOfTheDayTextView;
    AppDatabase database;
    Button showCurrentDateButton;
    MenuItem showCurrentDateMenuItem;
    OkHttpClient httpClient;
    DailyFeelings dailyFeelings;
    FitbitStepsData fitbitStepsData;
    FitbitSpO2Data fitbitSpO2Data;
    PhoneLocalization phoneLocalization;

    Calendar calendar;
    LocalDate localDate;

    String authorizationToken;

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        getMenuInflater().inflate(R.menu.menu_treatment_history, menu);
        for (int i = 0; i < menu.size(); i++) {
            menu.getItem(i).setOnMenuItemClickListener(this);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_treatment_history);

        database = AppDatabase.getDatabaseInstance(this);
        calendar = Calendar.getInstance();
        localDate = LocalDate.now();

        calendarView = (CalendarView) findViewById(R.id.calendarView);
        calendarView.setOnDateChangeListener(this);

        descriptionOfTheDayTextView = (TextView) findViewById(R.id.descriptionOfTheDay);
    }

    @Override
    public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
        LocalDate pickedDate = LocalDate.of(year, month+1, dayOfMonth);
        setupDataObjectsByDate(pickedDate);
        String descriptionOfTheDay = createDescriptionOfTheDay();

        descriptionOfTheDayTextView.setText(descriptionOfTheDay);
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        if (item.getItemId() == R.id.showCurrentDateMenuItem) {
            calendarView.setDate(calendar.toInstant().toEpochMilli(), true, false);
            onSelectedDayChange(calendarView, localDate.getYear(), localDate.getMonthValue(), localDate.getDayOfMonth());
        } else if (item.getItemId() == R.id.editPickedDateMenuItem) {

        }
        return false;
    }

    /**
     * Extract data from database and initialize data objects with it. If data for specific date is
     * empty, then data object is initialized as empty.
     * @param date selected date by the user from calendarView
     */
    private void setupDataObjectsByDate(LocalDate date) {
        dailyFeelings = database.dailyFeelingsDao().getByDate(date)
                .orElse(new DailyFeelings());
        fitbitStepsData = database.fitbitStepsDataDao().getNewestFitbitStepsData(date)
                .orElse(new FitbitStepsData());
        fitbitSpO2Data = database.fitbitSpO2DataDao().getNewestFitbitSpO2Data(date)
                .orElse(new FitbitSpO2Data());
        phoneLocalization = database.phoneLocalizationDao().getNewestLocation()
                .orElse(new PhoneLocalization());
    }

    /**
     * Create string description with data objects field's.
     * @return String description of the day
     */
    private String createDescriptionOfTheDay()  {
        return  "Mood: " + dailyFeelings.getMood() + '\n' +
                "Ailments: " + String.join(", ", dailyFeelings.getAilments()) + '\n' +
                "Note: " + dailyFeelings.getNote() + '\n' +
                "Steps: " + fitbitStepsData.getStepsValue() + '\n' +
                "SpO2: " + fitbitSpO2Data.getSpO2Value() + '\n' +
                "Latitude: " + phoneLocalization.getLatitude() + '\n' +
                "Longitude: " + phoneLocalization.getLongitude() + '\n' +
                "UserAddress: " + phoneLocalization.getUserAddress();
    }
}
