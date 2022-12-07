package com.example.expertsystem;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.example.database.AppDatabase;
import com.example.expertsystem.engine.PhoneActivityEngine;
import com.example.expertsystem.engine.PhoneLocalizationEngine;
import com.example.expertsystem.extractor.PhoneActivityExtractor;

import java.time.LocalDate;
import java.util.List;

public class ExpertSystem {

    private AppDatabase database;
    private Context context;
    private SharedPreferences sharPref;

    public ExpertSystem(Context context) {
        this.context = context;
        database = AppDatabase.getDatabaseInstance(context);
        sharPref = PreferenceManager.getDefaultSharedPreferences(context);

        getPhoneActivityResults();
        //getFitbitResults();
        //getUserActivityResults();
    }

    public void getPhoneActivityResults() {
        PhoneActivityExtractor paExtractor = new PhoneActivityExtractor(database);
        List<Double> mostCommonCoordinates = paExtractor.extractMostCommonCoordinates(LocalDate.now());
        Integer amountOfNotedMovements = paExtractor.extractAmountOfNotedMovements();

        PhoneLocalizationEngine phoneLocalizationEngine = new PhoneLocalizationEngine(mostCommonCoordinates);
        Double phoneLocalizationEngineResult = phoneLocalizationEngine.process();

        PhoneActivityEngine phoneActivityEngine = new PhoneActivityEngine(amountOfNotedMovements, phoneLocalizationEngineResult);
        Double aDouble = phoneActivityEngine.process();
        System.out.println(aDouble);
    }
}
