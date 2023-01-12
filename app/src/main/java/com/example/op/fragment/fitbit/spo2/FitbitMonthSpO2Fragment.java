package com.example.op.fragment.fitbit.spo2;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.database.AppDatabase;
import com.example.op.R;
import com.example.op.domain.FitbitDataUseCase;
import com.example.op.fragment.fitbit.utils.ChartUtils;
import com.example.op.fragment.fitbit.utils.FitbitDataChartGenerator;
import com.example.op.utils.LocalDateUtils;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarEntry;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.TreeMap;

public class FitbitMonthSpO2Fragment extends Fragment implements FitbitDataChartGenerator {
    private AppDatabase database;
    private BarChart barChart;
    private Context context;
    private FitbitDataUseCase fitbitDataUseCase;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        context = getContext();
        database = AppDatabase.getDatabaseInstance(context);
        fitbitDataUseCase = new FitbitDataUseCase();
        return inflater.inflate(R.layout.fragment_month_chart_fitbit_spo2_data, parent, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        barChart = view.findViewById(R.id.bar_chart_fitbit_month_spo2);
        generateFitbitDataChart();
    }

    @Override
    public void generateFitbitDataChart() {
        TreeMap<Integer, Double> averageMonthValues = getAverageMonthValues();
        ArrayList<BarEntry> barEntries = averageMonthValues.isEmpty() ?
                new ArrayList<>() : fitbitDataUseCase.getFitbitAverageMonthBarData(averageMonthValues);
        BarData barData = ChartUtils.generateBarData(barEntries);
        barChart.setData(barData);
        ChartUtils.generateBarChart(barChart, 1, context);
    }

    private TreeMap<Integer, Double> getAverageMonthValues() {
        TreeMap<Integer, Double> averageMonthValues = new TreeMap<>();
        LocalDate date = LocalDateUtils.extractFromSharPref(context);
        int months = LocalDateUtils.extractNumberOfPastMonthsFromYear(date);
        String yearAsString = String.valueOf(date.getYear());
        for (int i = 0; i <= months; i++) {
            String monthAsString = LocalDateUtils.monthFromInt(i);
            Double avgResult = database.fitbitSpO2DataDao().getAverageFromMonth(monthAsString, yearAsString);
            averageMonthValues.put(i, avgResult);
        }
        return averageMonthValues;
    }
}
