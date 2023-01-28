package com.example.op.fragment.fitbit.steps;

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
import java.util.Objects;
import java.util.TreeMap;

public class FitbitMonthsStepsFragment extends Fragment implements FitbitDataChartGenerator {

    private static final LocalDate now = LocalDate.now();
    private static final int months = Math.min(now.getMonthValue(), 6);
    private AppDatabase database;
    private Context context;
    private BarChart barChart;
    private FitbitDataUseCase fitbitDataUseCase;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        context = getContext();
        database = AppDatabase.getInstance(context);
        fitbitDataUseCase = new FitbitDataUseCase();
        return inflater.inflate(R.layout.fragment_months_chart_fitbit_steps_data, parent, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        barChart = view.findViewById(R.id.bar_chart_fitbit_months_steps);
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
        for (int i = 1; i <= months; i++) {
            String monthAsString = LocalDateUtils.monthFromInt(i);
            Double avgResult = database.fitbitStepsDataDao().getAverageFromMonth(monthAsString, yearAsString);
            if (Objects.nonNull(avgResult)) {
                averageMonthValues.put(i, avgResult);
            }
        }
        return averageMonthValues;
    }
}
