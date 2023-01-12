package com.example.op.fragment.fitbit.spo2;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.database.AppDatabase;
import com.example.database.entity.FitbitSpO2Data;
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
import java.util.List;

public class FitbitDaysSpO2Fragment extends Fragment implements FitbitDataChartGenerator {
    private LocalDate date;
    private AppDatabase database;
    private BarChart barChart;
    private Context context;
    private FitbitDataUseCase fitbitDataUseCase;
    private String sharPrefYear;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        context = getContext();
        database = AppDatabase.getDatabaseInstance(context);
        fitbitDataUseCase = new FitbitDataUseCase();
        date = LocalDateUtils.extractFromSharPref(context);
        sharPrefYear = String.valueOf(date.getYear());
        return inflater.inflate(R.layout.fragment_week_chart_fitbit_spo2_data, parent, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        barChart = view.findViewById(R.id.bar_chart_fitbit_week_spo2);
    }

    @Override
    public void onResume() {
        super.onResume();
        generateFitbitDataChart();
    }

    @Override
    public void generateFitbitDataChart() {
        List<FitbitSpO2Data> spO2Data = database.fitbitSpO2DataDao().getAllFromYear(sharPrefYear);
        ArrayList<BarEntry> barEntries = spO2Data.isEmpty() ?
                new ArrayList<>() : fitbitDataUseCase.getFitbitSpO2BarData(spO2Data, date);
        BarData barData = ChartUtils.generateBarData(barEntries);
        barChart.setData(barData);
        ChartUtils.generateBarChart(barChart, 7, context);
    }
}
