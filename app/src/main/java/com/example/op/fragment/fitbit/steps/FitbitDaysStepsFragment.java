package com.example.op.fragment.fitbit.steps;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.database.AppDatabase;
import com.example.database.entity.FitbitStepsData;
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

public class FitbitDaysStepsFragment extends Fragment implements FitbitDataChartGenerator {
    private AppDatabase database;
    private BarChart barChart;
    private Context context;
    private LocalDate date;
    private FitbitDataUseCase fitbitDataUseCase;
    private String sharPrefYear;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        context = getContext();
        database = AppDatabase.getInstance(context);
        fitbitDataUseCase = new FitbitDataUseCase();
        date = LocalDateUtils.extractFromSharPref(context);
        sharPrefYear = String.valueOf(date.getYear());
        return inflater.inflate(R.layout.fragment_days_chart_fitbit_steps_data, parent, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        barChart = view.findViewById(R.id.bar_chart_fitbit_days_steps);
    }

    @Override
    public void onResume() {
        super.onResume();
        generateFitbitDataChart();
    }

    @Override
    public void generateFitbitDataChart() {
        List<FitbitStepsData> stepsData = database.fitbitStepsDataDao().getAllFromYear(sharPrefYear);
        ArrayList<BarEntry> barEntries = stepsData.isEmpty() ?
                new ArrayList<>() : fitbitDataUseCase.getFitbitStepsDaysBarData(stepsData, date);
        BarData barData = ChartUtils.generateBarData(barEntries);
        barChart.setData(barData);
        ChartUtils.generateBarChart(barChart, 7, context);
    }
}
