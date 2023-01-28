package com.example.op.domain;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.database.FitbitData;
import com.example.database.entity.FitbitSpO2Data;
import com.example.database.entity.FitbitStepsData;
import com.example.op.utils.LocalDateUtils;
import com.github.mikephil.charting.data.BarEntry;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class FitbitDataUseCase {
    private final String TAG = FitbitDataUseCase.class.getName();

    public ArrayList<BarEntry> getFitbitStepsDaysBarData(@NonNull List<FitbitStepsData> data, LocalDate date) {
        Map<Integer, FitbitData> dayOfTheYearAndFitbitDataMap = data.stream()
                .collect(Collectors.toMap(
                        fitbitStepsData -> fitbitStepsData.getDate().getDayOfYear(),
                        fitbitStepsData -> fitbitStepsData));
        return getDayEntries(dayOfTheYearAndFitbitDataMap, date, "steps");
    }

    public ArrayList<BarEntry> getFitbitAverageMonthBarData(@NonNull Map<Integer, Double> data) {
        return getAverageMonthEntries(data);
    }

    public ArrayList<BarEntry> getFitbitSpO2BarData(@NonNull List<FitbitSpO2Data> data, LocalDate date) {
        Map<Integer, FitbitData> dayOfTheYearAndFitbitDataMap = data.stream()
                .collect(Collectors.toMap(
                        fitbitSpO2Data -> fitbitSpO2Data.getDate().getDayOfYear(),
                        fitbitSpO2Data -> fitbitSpO2Data));
        return getDayEntries(dayOfTheYearAndFitbitDataMap, date, "spO2");
    }

    private ArrayList<BarEntry> getDayEntries(Map<Integer, FitbitData> dayOfTheYearAndFitbitDataMap, LocalDate date, String dataType) {
        int days = LocalDateUtils.extractNumberOfPastDaysFromYear(date);
        ArrayList<BarEntry> entries = new ArrayList<>();
        for (int i = 1; i <= days; i++) {
            FitbitData fitbitData = dayOfTheYearAndFitbitDataMap.get(i);
            if (fitbitData == null) {
                entries.add(new BarEntry((float) i, null));
            } else {
                String extractedValue = getFitbitDataValue(fitbitData, dataType);
                float parsedValue = Objects.nonNull(extractedValue) ? Float.parseFloat(extractedValue) : 0.0f;
                entries.add(new BarEntry((float) i, parsedValue));
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
            Log.e(TAG, "Can't retrieve data from unknown type of fitbit data: " + type);
            return null;
        }
    }
}
