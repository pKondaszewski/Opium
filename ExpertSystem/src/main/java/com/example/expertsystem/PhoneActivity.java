package com.example.expertsystem;

import com.example.database.AppDatabase;
import com.example.database.entity.PhoneLocalization;
import com.example.database.entity.PhoneMovement;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class PhoneActivity {
    private final AppDatabase database;

    public PhoneActivity(AppDatabase database) {
        this.database = database;

        List<Double> medianValuesOfCoordinates = extractMedianValuesOfCoordinates();
        Integer amountOfNotedMovements = extractAmountOfNotedMovements();
        analyzePhoneActivity(medianValuesOfCoordinates, amountOfNotedMovements);
    }

    private List<Double> extractMedianValuesOfCoordinates() {
        ArrayList<Double> latitudeList = new ArrayList<>();
        ArrayList<Double> longitudeList = new ArrayList<>();

        List<PhoneLocalization> allByDate = database.phoneLocalizationDao().getAllByDate(LocalDate.now());
        allByDate.forEach(phoneLocalization -> {
            latitudeList.add(phoneLocalization.getLatitude());
            longitudeList.add(phoneLocalization.getLongitude());
        });
        Collections.sort(latitudeList);
        Collections.sort(longitudeList);

        int middleOfTheList = allByDate.size() / 2;

        Double latitudeMedian = latitudeList.get(middleOfTheList);
        Double longitudeMedian = longitudeList.get(middleOfTheList);

        return Arrays.asList(latitudeMedian, longitudeMedian);
    }

    private Integer extractAmountOfNotedMovements() {
        List<PhoneMovement> allByDate = database.phoneMovementDao().getAllByDate(LocalDate.now());
        int size = allByDate.size();
        if (size > 4) {
            return 4;
        } else if (size > 3) {
            return 3;
        } else if (size > 2) {
            return 2;
        } else if (size > 1) {
            return 1;
        } else {
            return 0;
        }
    }

    private void analyzePhoneActivity(List<Double> medianValuesOfCoordinates, Integer amountOfNotedMovements) {

    }
}
