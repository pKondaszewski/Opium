package com.example.op.domain;

import androidx.annotation.NonNull;

import com.example.database.FitbitData;
import com.example.database.entity.FitbitSpO2Data;
import com.example.database.entity.FitbitStepsData;
import com.github.mikephil.charting.data.BarEntry;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class FitbitDataUseCase {

    private final Calendar cal;

    public FitbitDataUseCase() {
        cal = Calendar.getInstance();
    }

    public ArrayList<BarEntry> getFitbitStepsDaysBarData(@NonNull List<FitbitStepsData> data, LocalDate date, int numberOfDays) {
        Map<Integer, FitbitData> dayOfTheYearAndFitbitDataMap = data.stream()
                .collect(Collectors.toMap(
                        fitbitStepsData -> fitbitStepsData.getDate().getDayOfYear(),
                        fitbitStepsData -> fitbitStepsData));
        return getDayEntries(dayOfTheYearAndFitbitDataMap, date, numberOfDays, "steps");
    }

    public ArrayList<BarEntry> getFitbitAverageMonthBarData(@NonNull Map<Integer, Double> data) {
        return getAverageMonthEntries(data);
    }

    public ArrayList<BarEntry> getFitbitSpO2BarData(@NonNull List<FitbitSpO2Data> data, LocalDate date, int numberOfDays) {
        Map<Integer, FitbitData> dayOfTheYearAndFitbitDataMap = data.stream()
                .collect(Collectors.toMap(
                        fitbitSpO2Data -> fitbitSpO2Data.getDate().getDayOfYear(),
                        fitbitSpO2Data -> fitbitSpO2Data));
        return getDayEntries(dayOfTheYearAndFitbitDataMap, date, numberOfDays, "spO2");
    }

    private ArrayList<BarEntry> getDayEntries(Map<Integer, FitbitData> dayOfTheYearAndFitbitDataMap, LocalDate date, int numberOfDays, String dataType) {
        cal.set(Calendar.YEAR, date.getYear());
        int endDay = date.getDayOfYear();
        int startDay = endDay - numberOfDays;
        ArrayList<BarEntry> entries = new ArrayList<>();
        for (int i = startDay+1; i <= endDay; i++) {
            FitbitData fitbitData = dayOfTheYearAndFitbitDataMap.get(i);
            if (fitbitData == null) {
                entries.add(new BarEntry((float) i, null));
            } else {
                String stepsValue = getFitbitDataValue(fitbitData, dataType);
                entries.add(new BarEntry((float) i, Float.parseFloat(stepsValue)));
            }
        }
        return entries;
    }

    private ArrayList<BarEntry> getAverageMonthEntries(Map<Integer, Double> averageMonthMap) {
        ArrayList<BarEntry> entries = new ArrayList<>();
        averageMonthMap.forEach((month, averageValue) -> {
            if (averageValue == null) {
                entries.add(new BarEntry(month, Float.parseFloat("0")));
            } else {
                entries.add(new BarEntry(month, averageValue.floatValue()));
            }
        });
        return entries;
    }

    private String getFitbitDataValue(FitbitData fitbitData, String type) {
        if (Objects.equals(type, "steps")) {
            FitbitStepsData fitbitStepsData = (FitbitStepsData) fitbitData;
            return fitbitStepsData.getStepsValue();
        } else if (Objects.equals(type, "spO2")) {
            FitbitSpO2Data fitbitSpO2Data = (FitbitSpO2Data) fitbitData;
            return fitbitSpO2Data.getSpO2Value();
        } else {
            return "abc";
        }
    }
}
