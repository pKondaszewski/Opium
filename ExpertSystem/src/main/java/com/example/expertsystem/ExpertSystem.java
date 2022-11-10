package com.example.expertsystem;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.example.database.AppDatabase;
import com.example.database.entity.ControlTextUserAnswer;
import com.example.database.entity.DailyFeelings;
import com.example.database.entity.FitbitSpO2Data;
import com.example.database.entity.FitbitStepsData;
import com.example.database.entity.PhoneLocalization;
import com.example.database.entity.PhoneMovement;

import lombok.AllArgsConstructor;

public class ExpertSystem {

    private AppDatabase database;
    private Context context;
    private SharedPreferences sharPref;

    public ExpertSystem(Context context) {
        this.context = context;
        database = AppDatabase.getDatabaseInstance(context);
        sharPref = PreferenceManager.getDefaultSharedPreferences(context);

        getPhoneActivityResults();
    }

    public void getPhoneActivityResults() {
        PhoneActivity phoneActivity = new PhoneActivity(database);
    }

}
