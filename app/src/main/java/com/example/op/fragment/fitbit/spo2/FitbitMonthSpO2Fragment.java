package com.example.op.fragment.fitbit.spo2;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.database.AppDatabase;
import com.example.op.R;
import com.example.op.domain.FitbitDataUseCase;
import com.example.op.fragment.fitbit.utils.FitbitDataChartGenerator;
import com.example.op.fragment.fitbit.utils.ChartUtils;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarEntry;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.TreeMap;

public class FitbitMonthSpO2Fragment extends Fragment implements FitbitDataChartGenerator {

    private static final LocalDate now = LocalDate.now();
    private static final int months = Math.min(now.getMonthValue(), 6);
    private AppDatabase database;
    private BarChart barChart;
    private FitbitDataUseCase fitbitDataUseCase;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        database = AppDatabase.getDatabaseInstance(getContext());
        fitbitDataUseCase = new FitbitDataUseCase();
        return inflater.inflate(R.layout.fragment_month_chart_fitbit_spo2_data, parent, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        barChart = view.findViewById(R.id.bar_chart_fitbit_month_spo2);
        generateFitbitDataChart(months);
    }

    @Override
    public void generateFitbitDataChart(int limit) {
        TreeMap<Integer, Double> averageMonthValues = getAverageMonthValues(limit);
        ArrayList<BarEntry> barEntries = averageMonthValues.isEmpty() ?
                new ArrayList<>() : fitbitDataUseCase.getFitbitAverageMonthBarData(averageMonthValues);
        BarData barData = ChartUtils.generateBarData(barEntries);
        barChart.setData(barData);
        ChartUtils.generateBarChart(barChart, 1, getContext());
    }

    private TreeMap<Integer, Double> getAverageMonthValues(int limit) {
        TreeMap<Integer, Double> averageMonthValues = new TreeMap<>();
        LocalDate date = LocalDate.now();
        for (int i = 0; i < limit; i++) {
            Double averageBetweenTwoDates = database.fitbitSpO2DataDao().getAverageBetweenTwoDates(date.minusMonths(1), date);
            averageMonthValues.put(date.getMonthValue(), averageBetweenTwoDates);
            date = date.minusMonths(1);
        }
        return averageMonthValues;
    }
}
