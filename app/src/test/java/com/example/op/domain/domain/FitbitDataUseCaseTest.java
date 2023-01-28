package com.example.op.domain.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.example.database.entity.FitbitSpO2Data;
import com.example.database.entity.FitbitStepsData;
import com.example.op.domain.FitbitDataUseCase;
import com.github.mikephil.charting.data.BarEntry;

import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FitbitDataUseCaseTest {
    private FitbitDataUseCase fitbitDataUseCase;
    private FitbitSpO2Data fitbitSpO2Data;
    private FitbitStepsData fitbitStepsData;
    private LocalDate localDate;
    private String fitbitActivityValue;

    @Before
    public void init() {
        fitbitDataUseCase = new FitbitDataUseCase();
        localDate = LocalDate.now();
        fitbitActivityValue = "100";

        fitbitSpO2Data = new FitbitSpO2Data(1, localDate, LocalTime.now(), fitbitActivityValue);
        fitbitStepsData = new FitbitStepsData(1, localDate, LocalTime.now(), fitbitActivityValue);
    }

    @Test
    public void getFitbitSpO2BarDataDailyEmptyResult() {
        ArrayList<BarEntry> fitbitSpO2BarData = fitbitDataUseCase.getFitbitSpO2BarData(List.of(), localDate);
        long count = fitbitSpO2BarData.stream().filter(barEntry -> barEntry.getY() != 0).count();
        assertEquals(0, count);
    }

    @Test
    public void getFitbitSpO2BarDataDailyPositiveSingleResult() {
        ArrayList<BarEntry> fitbitSpO2BarData = fitbitDataUseCase.getFitbitSpO2BarData(List.of(fitbitSpO2Data), localDate);
        boolean anyMatch = fitbitSpO2BarData.stream().anyMatch(barEntry1 -> barEntry1.getY() == Float.parseFloat(fitbitActivityValue));
        assertTrue(anyMatch);
    }

    @Test
    public void getFitbitStepsBarDataDailyEmptyResult() {
        ArrayList<BarEntry> fitbitStepsDaysBarData = fitbitDataUseCase.getFitbitStepsDaysBarData(List.of(), localDate);
        long count = fitbitStepsDaysBarData.stream().filter(barEntry -> barEntry.getY() != 0).count();
        assertEquals(0, count);
    }

    @Test
    public void getFitbitStepsBarDataDailyPositiveSingleResult() {
        ArrayList<BarEntry> fitbitStepsDaysBarData = fitbitDataUseCase.getFitbitStepsDaysBarData(List.of(fitbitStepsData), localDate);
        boolean anyMatch = fitbitStepsDaysBarData.stream().anyMatch(barEntry -> barEntry.getY() == Float.parseFloat(fitbitActivityValue));
        assertTrue(anyMatch);
    }

    @Test
    public void getFitbitStepsBarDataMonthlyPositiveSingleResult() {
        ArrayList<BarEntry> fitbitStepsDaysBarData = fitbitDataUseCase.getFitbitAverageMonthBarData(Map.of(1, 5.0));
        boolean contains = fitbitStepsDaysBarData.stream().anyMatch(barEntry -> barEntry.getY() == 5f);
        assertEquals(1, fitbitStepsDaysBarData.size());
        assertTrue(contains);
    }
}
